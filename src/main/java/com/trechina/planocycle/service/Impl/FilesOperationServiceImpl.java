package com.trechina.planocycle.service.Impl;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.trechina.planocycle.entity.dto.GetPtsCsvCgiDto;
import com.trechina.planocycle.entity.dto.ShelfPtsDto;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.service.FilesOperationService;
import com.trechina.planocycle.service.ShelfPtsService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.cgiUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class FilesOperationServiceImpl implements FilesOperationService {
    private Logger logger  = LoggerFactory.getLogger(this.getClass());
    @Autowired
    HttpSession session;
    @Autowired
    private ShelfPtsService shelfPtsService;
    @Autowired
    private cgiUtils cgiUtil;
    /**
     * 单文件上传
     * @param multipartFile
     * @param path
     * @return
     */
    @Override
    public Map<String, Object>  CsvUpload(MultipartFile multipartFile,String path,String companyCd,String filename,
                                          String projectIds,String bucketNames) {
        try {
                String fileName = filename;
                if (!multipartFile.isEmpty()){
                    String names ="";
                    if (multipartFile!=null && multipartFile.getOriginalFilename()!=null){
                        names = multipartFile.getOriginalFilename();
                    }
                    if(names != null && names.indexOf(".csv")>-1){
                        // 不存在会创建路径
                        judgeExistsPath(path);
                        //存放文件
                        excelPutPath(multipartFile, path + fileName);
                        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
                        fileSaveRemote(path, companyCd, fileName,resourceBundle.getString("ProductCsvPath"),
                                projectIds,bucketNames);
                        return ResultMaps.result(ResultEnum.SUCCESS);
                    }
                    else {
                        return ResultMaps.result(ResultEnum.FAILURE);
                    }

                }
                else {
                    return ResultMaps.result(ResultEnum.FAILURE);
                }
        } catch (IOException | NullPointerException e) {
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String,Object> CsvUploadMulti(MultipartFile[] multipartFileList,String path,String companyCd,String projectIds,
                                             String bucketNames) {
        try {
            if (multipartFileList !=null && multipartFileList.length>0){
                // 不存在会创建路径
                judgeExistsPath(path);
                //存放文件 // 文件内容check
                for (MultipartFile file : multipartFileList) {
                    String filenames = "";
                    if (file!=null && file.getOriginalFilename()!=null){
                        filenames = (""+file.getOriginalFilename()).replaceAll(" ","*");
                        if (filenames.indexOf(".csv")>-1) {
                            excelPutPath(file, path + filenames);
                            File file1 = new File(path + filenames);
                            logger.info("文件存放完成");
                            if(!file1.setReadable(true)){
                                logger.info("设置读文件失败");
                            }

                            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file1), "Shift_Jis");
                            try(BufferedReader bufferedReader = new BufferedReader(inputStreamReader);){
                                List arrList = new ArrayList<>();
                                String line = "";
                                int lineNum = 1;
                                int titleNum = 0;
                                while ((line = bufferedReader.readLine()) != null) {
                                    if (lineNum ==1){
                                        logger.info("第一行信息"+line);
                                        String[] arr = line.split(",");
                                        arrList.add(arr);
                                        for (int i = 0; i < arr.length; i++) {
                                            logger.info("分隔后"+arr[i]);
                                        }
                                        if (arr[1].equals("V3.0") ==false && arr[1].equals("V2.0") ==false){
                                            logger.info("不包含3.0和2.0");
                                            return ResultMaps.result(ResultEnum.FILEVERSIONFAILURE);
                                        }
                                        lineNum+=1;
                                    }
                                    if (line.indexOf("台番号") > -1) {
                                        titleNum += 1;
                                    }
                                }
                                if (titleNum != 3) {
                                    logger.info("没有三组台番号");
                                    return ResultMaps.result(ResultEnum.FILECONTENTFAILURE);
                                }

                                inputStreamReader.close();
                                logger.info("check完成，开始链接服务器");

                            }

                            ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
                            fileSaveRemote(path, companyCd, filenames,resourceBundle.getString("PtsCsvPath"),
                                    projectIds,bucketNames);
                            try {
                                // 保存pts信息
                                ShelfPtsDto shelfPtsDto = new ShelfPtsDto();
                                shelfPtsDto.setCompanyCd(companyCd);
                                shelfPtsDto.setFileName(filenames);

                                Map<String, Object> res = shelfPtsService.setShelfPtsInfo(shelfPtsDto,0);

                                // 返回id
                                String uuid = UUID.randomUUID().toString();
                                String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
                                GetPtsCsvCgiDto getPtsCsvCgiDto = new GetPtsCsvCgiDto();
                                getPtsCsvCgiDto.setCompany(companyCd);
                                getPtsCsvCgiDto.setMode("pts_shori");
                                getPtsCsvCgiDto.setPtsIdCsvNm(res.get("data") + "@" + filenames);
                                getPtsCsvCgiDto.setGuid(uuid);
                                logger.info("调用pts_shori的参数" + getPtsCsvCgiDto);

                                ResourceBundle resource = ResourceBundle.getBundle("pathConfig");
                                String paths = resource.getString("PtsUploadData");
                                String result = null;
                                result = cgiUtil.postCgi(paths, getPtsCsvCgiDto, tokenInfo);
                                logger.info("taskid返回pts文件处理：" + result);
                                String queryPath = resourceBundle.getString("TaskQuery");
                                // 带着taskid，再次请求cgi获取运行状态/数据
                                Map<String, Object> Data = cgiUtil.postCgiLoop(queryPath, result, tokenInfo);
                                logger.info("调用pts_shori的结果" + Data);

                            } catch (IOException e) {
                                logger.info("报错:"+e);
                                logger.info("pts文件处理报错" + e);
                                return ResultMaps.result(ResultEnum.FAILURE);
                            }
                        }
                    }
                }
                return ResultMaps.result(ResultEnum.SUCCESS);

            }

            else {
                return ResultMaps.result(ResultEnum.FAILURE);
            }
        } catch (IOException e) {
            logger.info("报错:"+e);
            logger.info("上传文件报错："+e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }

    /**
     * csv转excel，并下载
     *
     * @param multipartFile
     * @return
     */
    @Override
    public Map<String, Object> csvConvertExcelDowlLoad(MultipartFile multipartFile, String productDownPath, HttpServletResponse response) {
        logger.info("a"+multipartFile.getSize());
        // 不存在会创建路径
        judgeExistsPath(productDownPath);
        String files = productDownPath + "productFile.csv";
        String ExcelFiles = productDownPath+"productExcel.xlsx";
        //存放文件
        try {
            excelPutPath(multipartFile, files);
            //读取csv写入excel
            try(DataInputStream in = new DataInputStream(new FileInputStream(files));){
                InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF-8");
                try(BufferedReader reader = new BufferedReader(inputStreamReader);) {
                    String line  = null;
                    try(XSSFWorkbook workbook = new XSSFWorkbook();){
                        XSSFSheet sheet = workbook.createSheet("商品力点数表");
                        XSSFRow row = null;
                        XSSFCell cell = null;
                        for(int i=0;(line=reader.readLine())!=null;i++){
                            row = sheet.createRow(i);
                            if (i==0){
                                line = line.replaceAll("\"","");
                                line = line.substring(1,line.length());
                            }
                            String item[]  = line.split(",");
                            for (int j = 0; j < item.length; j++) {
                                cell=row.createCell(j);
                                cell.setCellValue(item[j]);
                            }
                        }
                        FileOutputStream fos = new FileOutputStream(ExcelFiles);
                        workbook.write(fos);
                        try {
                            response.setContentType("application/Octet-stream");
                            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode("商品力点数表", "UTF-8")+".xlsx");
                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
                            logger.info("csv转excel报错"+e);
                        }
                        File fileExcel = new File(ExcelFiles);
                        OutputStream outputStream = response.getOutputStream();
                        try(InputStream is = new FileInputStream(fileExcel);){
                            // 构造一个长度为1024的字节数组
                            byte[] buffer = new byte[1024];
                            int len = 0;
                            while ((len = is.read(buffer)) != -1){
                                outputStream.write(buffer,0,len);
                                outputStream.flush();
                            }
                            outputStream.close();
                            fos.close();
                            inputStreamReader.close();
                            File fileCsv = new File(files);
                            if (!fileCsv.delete()){
                                logger.info("文件删除失败");
                            }
                            if (!fileExcel.delete()){
                                logger.info("文件删除失败");
                            }
                        }

                    }

                }
            }
        } catch (IOException e) {
            logger.info("csv转excel报错2:"+e);
        }
        return null;
    }

    /**
     * 文件保存到服务器
     * @param path
     * @param companyCd
     * @param fileName
     * @throws IOException
     */
    private void fileSaveRemote(String path, String companyCd, String fileName,String remotePaths,String projectIds,
                                String bucketNames) throws IOException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String remotePath = remotePaths;
        remotePath = remotePath.replace("COMPANYCD", companyCd);
        //存放文件--物理机版
//                        logger.info("开始链接服务器");
//                        sshFtpUtils ssh = new sshFtpUtils();
//                        logger.info("远程服务器地址" + remotePath);
//                        logger.info("打印pushfile参数" + fileName);
//                        ssh.pushFile(path + fileName, remotePath);
        // 存放文件--数据桶版本
        // The ID of your GCP project
        String projectId = projectIds;
        // The ID of your GCS bucket
        String bucketName = bucketNames;
        // The ID of your GCS object
        String objectName = fileName;
        // The path to your file to upload
        String filePath = path + fileName;
        File jsonKey = new File("/secrets/.gcp-credientials.json");
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).setCredentials(GoogleCredentials.fromStream(new FileInputStream(jsonKey))).build().getService();
        BlobId blobId = BlobId.of(bucketName, remotePath+objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));
        File delFile = new File(path+fileName);
        if(!delFile.delete()){
            logger.info("删除文件失败");
        }
    }

    /**
     * 判断文件路径是否存在
     */
    public  boolean judgeExistsPath(String csvPath) {
        boolean res = true;
        if (!new File(csvPath).exists()) {
            res = new File(csvPath).mkdirs();
        }
        return res;
    }

    /**
     * 文件存放
     */
    public static void excelPutPath(MultipartFile file, String csvPath) throws IOException {
        File resultFile = new File(csvPath);
        file.transferTo(resultFile);
    }

}

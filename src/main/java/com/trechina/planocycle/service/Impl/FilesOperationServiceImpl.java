package com.trechina.planocycle.service.Impl;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.trechina.planocycle.entity.dto.GetPtsCsvCgiDto;
import com.trechina.planocycle.entity.dto.ShelfPtsDto;
import com.trechina.planocycle.entity.po.ShelfPtsDataJandata;
import com.trechina.planocycle.entity.po.ShelfPtsDataTaimst;
import com.trechina.planocycle.entity.po.ShelfPtsDataTanamst;
import com.trechina.planocycle.entity.po.ShelfPtsDataVersion;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.ShelfPtsDataJandataMapper;
import com.trechina.planocycle.mapper.ShelfPtsDataTaimstMapper;
import com.trechina.planocycle.mapper.ShelfPtsDataTanamstMapper;
import com.trechina.planocycle.mapper.ShelfPtsDataVersionMapper;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class FilesOperationServiceImpl implements FilesOperationService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    HttpSession session;
    @Autowired
    private ShelfPtsService shelfPtsService;
    @Autowired
    private cgiUtils cgiUtil;
    @Autowired
    private ShelfPtsDataVersionMapper shelfPtsDataVersionMapper;
    @Autowired
    private ShelfPtsDataTaimstMapper shelfPtsDataTaimstMapper;
    @Autowired
    private ShelfPtsDataTanamstMapper shelfPtsDataTanamstMapper;
    @Autowired
    private ShelfPtsDataJandataMapper shelfPtsDataJandataMapper;

    /**
     * 单文件上传
     *
     * @param multipartFile
     * @param path
     * @return
     */
    @Override
    public Map<String, Object> csvUpload(MultipartFile multipartFile, String path, String companyCd, String filename,
                                         String projectIds, String bucketNames) {
        try {
            String fileName = filename;
            if (!multipartFile.isEmpty()) {
                String names = "";
                if (multipartFile.getOriginalFilename() != null) {
                    names = multipartFile.getOriginalFilename();
                }
                if (names != null && names.indexOf(".csv") > -1) {
                    // 不存在会创建路径
                    judgeExistsPath(path);
                    //存放文件
                    excelPutPath(multipartFile, path + fileName);
                    ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
                    fileSaveRemote(path, companyCd, fileName, resourceBundle.getString("ProductCsvPath"),
                            projectIds, bucketNames);
                    return ResultMaps.result(ResultEnum.SUCCESS);
                } else {
                    return ResultMaps.result(ResultEnum.FAILURE);
                }

            } else {
                return ResultMaps.result(ResultEnum.FAILURE);
            }
        } catch (IOException | NullPointerException e) {
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }

    private List<ShelfPtsDataTaimst> convertArrayToTaimstList(List<String[]> datas, Integer ptsCd, String companyCd, String authorCd, Date createTime) {
        List<ShelfPtsDataTaimst> result = new ArrayList<>();
        ShelfPtsDataTaimst ptsDataTaimst = null;
        for (String[] data : datas) {
            ptsDataTaimst = new ShelfPtsDataTaimst();
            ptsDataTaimst.setPtsCd(ptsCd);
            ptsDataTaimst.setCompanyCd(companyCd);

            ptsDataTaimst.setTaiCd(stringToInteger(data[0]));
            ptsDataTaimst.setTaiHeight(stringToInteger(data[1]));
            ptsDataTaimst.setTaiWidth(stringToInteger(data[2]));
            ptsDataTaimst.setTaiDepth(stringToInteger(data[3]));
            ptsDataTaimst.setTaiName(data[4]);

            ptsDataTaimst.setAuthorCd(authorCd);
            ptsDataTaimst.setCreateTime(createTime);

            result.add(ptsDataTaimst);
        }
        return result;
    }


    private List<ShelfPtsDataTanamst> convertArrayToTanamstList(List<String[]> datas, Integer ptsCd, String companyCd, String authorCd, Date createTime) {
        List<ShelfPtsDataTanamst> result = new ArrayList<>();
        ShelfPtsDataTanamst ptsDataTanamst = null;
        for (String[] data : datas) {
            ptsDataTanamst = new ShelfPtsDataTanamst();
            ptsDataTanamst.setPtsCd(ptsCd);
            ptsDataTanamst.setCompanyCd(companyCd);

            ptsDataTanamst.setTaiCd(stringToInteger(data[0]));
            ptsDataTanamst.setTanaCd(stringToInteger(data[1]));
            ptsDataTanamst.setTanaHeight(stringToInteger(data[2]));
            ptsDataTanamst.setTanaWidth(stringToInteger(data[3]));
            ptsDataTanamst.setTanaDepth(stringToInteger(data[4]));
            ptsDataTanamst.setTanaThickness(stringToInteger(data[5]));
            ptsDataTanamst.setTanaType(stringToInteger(data[6]));

            ptsDataTanamst.setAuthorCd(authorCd);
            ptsDataTanamst.setCreateTime(createTime);

            result.add(ptsDataTanamst);
        }
        return result;
    }

    private List<ShelfPtsDataJandata> convertArrayToJanList(List<String[]> datas, Integer ptsCd, String companyCd, String authorCd, Date createTime) {
        List<ShelfPtsDataJandata> result = new ArrayList<>();
        ShelfPtsDataJandata ptsDataJandata = null;
        for (String[] data : datas) {
            ptsDataJandata = new ShelfPtsDataJandata();
            ptsDataJandata.setPtsCd(ptsCd);
            ptsDataJandata.setCompanyCd(companyCd);

            ptsDataJandata.setTaiCd(stringToInteger(data[0]));
            ptsDataJandata.setTanaCd(stringToInteger(data[01]));
            ptsDataJandata.setTanapositionCd(stringToInteger(data[2]));
            // Jan为空的时候，数据库存null
            ptsDataJandata.setJan("".equals(data[3]) ? null : data[3]);
            ptsDataJandata.setFaceCount(stringToInteger(data[4]));
            ptsDataJandata.setFaceMen(stringToInteger(data[5]));
            ptsDataJandata.setFaceKaiten(stringToInteger(data[6]));
            ptsDataJandata.setTumiagesu(stringToInteger(data[7]));
            ptsDataJandata.setZaikosu(stringToInteger(data[8]));
            ptsDataJandata.setFaceDisplayflg(stringToInteger(data[9]));
            ptsDataJandata.setFacePosition(stringToInteger(data[10]));
            ptsDataJandata.setDepthDisplayNum(stringToInteger(data[11]));

            ptsDataJandata.setAuthorCd(authorCd);
            ptsDataJandata.setCreateTime(createTime);

            result.add(ptsDataJandata);
        }
        return result;
    }

    private Integer stringToInteger(String str) {
        return str == null ? null : Integer.valueOf(str);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> CsvUploadMulti(MultipartFile[] multipartFileList, String path, String companyCd, String projectIds,
                                              String bucketNames) {
        try {
            if (multipartFileList != null && multipartFileList.length > 0) {
                // 不存在会创建路径
                judgeExistsPath(path);
                String authorCd = session.getAttribute("aud").toString();
                Date now = Calendar.getInstance().getTime();
                //存放文件 // 文件内容check
                String[] arr1;
                String[] arr2;
                String[] arr3;

                for (MultipartFile file : multipartFileList) {
                    String filenames = "";
                    if (file != null && file.getOriginalFilename() != null) {
                        filenames = ("" + file.getOriginalFilename()).replace(" ", "*");
                        if (filenames.indexOf(".csv") > -1) {
                            excelPutPath(file, path + filenames);
                            File file1 = new File(path + filenames);
                            logger.info("文件存放完成");
                            if (!file1.setReadable(true)) {
                                logger.info("设置读文件失败");
                            }

                            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file1), "Shift_Jis");
                            //
                            ShelfPtsDataVersion ptsDataVersion = new ShelfPtsDataVersion();
                            ptsDataVersion.setCompanyCd(companyCd);
                            List<String[]> arrList1 = new ArrayList<>();
                            List<String[]> arrList2 = new ArrayList<>();
                            List<String[]> arrList3 = new ArrayList<>();
                            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader);) {
                                String line = "";
                                int lineNum = 1;
                                int titleNum = 0;
                                while ((line = bufferedReader.readLine()) != null) {
                                    if (lineNum == 1) {
                                        logger.info("第一行信息{}", line);
                                        String[] arr = line.split(",");
                                        if (!arr[1].equals("V3.0") && !arr[1].equals("V2.0")) {
                                            logger.info("不包含3.0和2.0");
                                            return ResultMaps.result(ResultEnum.FILEVERSIONFAILURE);
                                        }
                                        ptsDataVersion.setCommoninfo(arr[0]);
                                        ptsDataVersion.setVersioninfo(arr[1]);
                                        ptsDataVersion.setOutflg(arr[2]);
                                        lineNum += 1;
                                    } else if (lineNum == 2) {
                                        ptsDataVersion.setModename(line);
                                        lineNum += 1;
                                    }
                                    // 数据分段
                                    if (line.indexOf("台番号") > -1) {
                                        titleNum += 1;
                                        if (titleNum == 1) {
                                            ptsDataVersion.setTaiHeader(line);
                                        } else if (titleNum == 2) {
                                            ptsDataVersion.setTanaHeader(line);
                                        } else if (titleNum == 3) {
                                            ptsDataVersion.setJanHeader(line);
                                        }
                                    } else {
                                        String[] lineArr = line.split(",");
                                        if (titleNum == 1) {
                                            arr1 = new String[5];
                                            Arrays.fill(arr1, null);
                                            System.arraycopy(lineArr, 0, arr1, 0, lineArr.length);
                                            arrList1.add(arr1);
                                        } else if (titleNum == 2) {
                                            arr2 = new String[7];
                                            Arrays.fill(arr2, null);
                                            System.arraycopy(lineArr, 0, arr2, 0, lineArr.length);
                                            arrList2.add(arr2);
                                        } else if (titleNum == 3) {
                                            arr3 = new String[12];
                                            Arrays.fill(arr3, null);
                                            System.arraycopy(lineArr, 0, arr3, 0, lineArr.length);
                                            arrList3.add(arr3);
                                        }
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

                            //

                            // :TODO 屏蔽掉了往GCP上传文件的部分
//                            fileSaveRemote(path, companyCd, filenames, resourceBundle.getString("PtsCsvPath"),
//                                    projectIds, bucketNames);
                            // 保存pts信息
                            ShelfPtsDto shelfPtsDto = new ShelfPtsDto();
                            shelfPtsDto.setCompanyCd(companyCd);
                            shelfPtsDto.setFileName(filenames);

                            Map<String, Object> res = shelfPtsService.setShelfPtsInfo(shelfPtsDto, 0);
                            Integer ptsId = (Integer) res.get("data");
                            ptsDataVersion.setPtsCd(ptsId);
                            //
                            shelfPtsDataVersionMapper.insert(ptsDataVersion);
                            List<ShelfPtsDataTaimst> dataTaimstList = convertArrayToTaimstList(arrList1, ptsId, companyCd, authorCd, now);

                            List<ShelfPtsDataTanamst> dataTanamstList = convertArrayToTanamstList(arrList2, ptsId, companyCd, authorCd, now);

                            List<ShelfPtsDataJandata> dataJandataList = convertArrayToJanList(arrList3, ptsId, companyCd, authorCd, now);

                            shelfPtsDataTaimstMapper.insertAll(dataTaimstList);

                            shelfPtsDataTanamstMapper.insertAll(dataTanamstList);

                            shelfPtsDataJandataMapper.insertAll(dataJandataList);

                            // 返回id
                            String uuid = UUID.randomUUID().toString();
                            String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
                            GetPtsCsvCgiDto getPtsCsvCgiDto = new GetPtsCsvCgiDto();
                            getPtsCsvCgiDto.setCompany(companyCd);
                            getPtsCsvCgiDto.setMode("pts_shori");
                            getPtsCsvCgiDto.setPtsIdCsvNm(ptsId + "@" + filenames);
                            getPtsCsvCgiDto.setGuid(uuid);
                            logger.info("调用pts_shori的参数{}", getPtsCsvCgiDto);

                            ResourceBundle resource = ResourceBundle.getBundle("pathConfig");
                            String paths = resource.getString("PtsUploadData");
                            String result = null;
                            result = cgiUtil.postCgi(paths, getPtsCsvCgiDto, tokenInfo);
                            logger.info("taskid返回pts文件处理：{}", result);
                            String queryPath = resourceBundle.getString("TaskQuery");
                            // 带着taskid，再次请求cgi获取运行状态/数据
                            Map<String, Object> data = cgiUtil.postCgiLoop(queryPath, result, tokenInfo);
                            logger.info("调用pts_shori的结果{}", data);
                        }
                    }
                }
                return ResultMaps.result(ResultEnum.SUCCESS);

            } else {
                return ResultMaps.result(ResultEnum.FAILURE);
            }
        } catch (IOException e) {
            logger.info("报错,上传文件报错：{}", e.getMessage());
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
        logger.info("a{}", multipartFile.getSize());
        // 不存在会创建路径
        judgeExistsPath(productDownPath);
        String files = productDownPath + "productFile.csv";
        String excelFiles = productDownPath + "productExcel.xlsx";
        //存放文件
        try {
            excelPutPath(multipartFile, files);
            //读取csv写入excel
            try (DataInputStream in = new DataInputStream(new FileInputStream(files));) {
                InputStreamReader inputStreamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
                try (BufferedReader reader = new BufferedReader(inputStreamReader);) {
                    String line = null;
                    try (XSSFWorkbook workbook = new XSSFWorkbook();) {
                        XSSFSheet sheet = workbook.createSheet("商品力点数表");
                        XSSFRow row = null;
                        XSSFCell cell = null;
                        for (int i = 0; (line = reader.readLine()) != null; i++) {
                            row = sheet.createRow(i);
                            if (i == 0) {
                                line = line.replace("\"", "");
                                line = line.substring(1, line.length());
                            }
                            String[] item = line.split(",");
                            for (int j = 0; j < item.length; j++) {
                                cell = row.createCell(j);
                                cell.setCellValue(item[j]);
                            }
                        }
                        FileOutputStream fos = new FileOutputStream(excelFiles);
                        workbook.write(fos);
                        try {
                            response.setContentType("application/Octet-stream");
                            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode("商品力点数表", "UTF-8") + ".xlsx");
                        } catch (UnsupportedEncodingException e) {
                            logger.info("csv转excel报错:{}", e.getMessage());
                        }
                        File fileExcel = new File(excelFiles);
                        OutputStream outputStream = response.getOutputStream();
                        try (InputStream is = new FileInputStream(fileExcel);) {
                            // 构造一个长度为1024的字节数组
                            byte[] buffer = new byte[1024];
                            int len = 0;
                            while ((len = is.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, len);
                                outputStream.flush();
                            }
                            outputStream.close();
                            fos.close();
                            inputStreamReader.close();
                            File fileCsv = new File(files);
                            if (!fileCsv.delete()) {
                                logger.info("文件删除失败");
                            }
                            if (!fileExcel.delete()) {
                                logger.info("文件删除失败");
                            }
                        }

                    }

                }
            }
        } catch (IOException e) {
            logger.info("csv转excel报错2:{}", e.getMessage());
        }
        return Collections.emptyMap();
    }

    /**
     * 文件保存到服务器
     *
     * @param path
     * @param companyCd
     * @param fileName
     * @throws IOException
     */
    private void fileSaveRemote(String path, String companyCd, String fileName, String remotePaths, String projectIds,
                                String bucketNames) throws IOException {
        String remotePath = remotePaths;
        remotePath = remotePath.replace("COMPANYCD", companyCd);
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
        BlobId blobId = BlobId.of(bucketName, remotePath + objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));
        File delFile = new File(path + fileName);
        if (!delFile.delete()) {
            logger.info("删除文件失败");
        }
    }

    /**
     * 判断文件路径是否存在
     */
    public boolean judgeExistsPath(String csvPath) {
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

    /**
     * 将csv文件转成字符串数组集合
     *
     * @param multipartFile
     * @return
     */
    @Override
    public List<String[]> uploadCsvToList(MultipartFile multipartFile) {
        if (multipartFile == null) {
            return Collections.emptyList();
        }
        List<String[]> result = new ArrayList<>();
        //起手转成字符流
        InputStream is = null;
        InputStreamReader isReader = null;
        BufferedReader br = null;
        try {
            is = multipartFile.getInputStream();
            isReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            br = new BufferedReader(isReader);
            //循环逐行读取
            while (br.ready()) {
                result.add(br.readLine().split(","));
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
            if (isReader != null) {
                try {
                    isReader.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return result;
    }
}

package com.trechina.planocycle.service.impl;


import com.trechina.planocycle.entity.dto.GetPtsCsvCgiDto;
import com.trechina.planocycle.entity.dto.PtsPatternRelationDto;
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
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.charset.StandardCharsets;
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
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * ???????????????????????????????????????
     *
     *
     * @return
     */
    //@Override
    //public Map<String, Object> csvUpload(MultipartFile multipartFile, String path, String companyCd, String filename,
    //                                     String projectIds, String bucketNames) {
    //    try {
    //        String fileName = filename;
    //        if (!multipartFile.isEmpty()) {
    //            String names = "";
    //            if (multipartFile.getOriginalFilename() != null) {
    //                names = multipartFile.getOriginalFilename();
    //            }
    //            if (names != null && names.indexOf(".csv") > -1) {
    //                // ??????????????????????????????
    //                judgeExistsPath(path);
    //                //?????????????????????
    //                excelPutPath(multipartFile, path + fileName);
    //                ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
    //                fileSaveRemote(path, companyCd, fileName, resourceBundle.getString("ProductCsvPath"),
    //                        projectIds, bucketNames);
    //                return ResultMaps.result(ResultEnum.SUCCESS);
    //            } else {
    //                return ResultMaps.result(ResultEnum.FAILURE);
    //            }
    //
    //        } else {
    //            return ResultMaps.result(ResultEnum.FAILURE);
    //        }
    //    } catch (IOException | NullPointerException e) {
    //        return ResultMaps.result(ResultEnum.FAILURE);
    //    }
    //}

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
            // Jan?????????????????????????????????null
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
        return str == null ? null : Integer.valueOf(str.trim());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> csvUploadMulti(MultipartFile[] multipartFileList, String path, String companyCd, String projectIds,
                                              String bucketNames) {
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try {
            if (multipartFileList != null && multipartFileList.length > 0) {
                // ??????????????????????????????
                judgeExistsPath(path);
                String authorCd = session.getAttribute("aud").toString();
                Date now = Calendar.getInstance().getTime();
                //????????????????????? // ??????????????????????????????
                String[] arr1;
                String[] arr2;
                String[] arr3;
                PtsPatternRelationDto resultMap = new PtsPatternRelationDto();
                for (MultipartFile file : multipartFileList) {
                    String filenames = "";
                    if (file != null && file.getOriginalFilename() != null) {
                        filenames = ("" + file.getOriginalFilename());
                        if (filenames.contains(".csv")) {
                            excelPutPath(file, path + filenames);
                            File file1 = new File(path + filenames);
                            logger.info("??????????????????");
                            if (!file1.setReadable(true)) {
                                logger.info("?????????????????????");
                            }

                            inputStreamReader = new InputStreamReader(new FileInputStream(file1), "Shift_Jis");
                            //
                            ShelfPtsDataVersion ptsDataVersion = new ShelfPtsDataVersion();
                            ptsDataVersion.setCompanyCd(companyCd);
                            List<String[]> arrList1 = new ArrayList<>();
                            List<String[]> arrList2 = new ArrayList<>();
                            List<String[]> arrList3 = new ArrayList<>();
                            bufferedReader = new BufferedReader(inputStreamReader);
                            String line = "";
                            int lineNum = 1;
                            int titleNum = 0;
                            while ((line = bufferedReader.readLine()) != null) {
                                if(line.startsWith(",") || line.replaceAll(",*", "").equals("")){
                                    logger.info("????????????,{}", line);
                                    continue;
                                }
                                if (lineNum == 1) {
                                    logger.info("???????????????{}", line);
                                    String[] arr = line.split(",");
                                    if (!arr[1].equals("V3.0") && !arr[1].equals("V2.0")) {
                                        logger.info("?????????3.0???2.0");
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
                                // ????????????????????????
                                if (line.contains("?????????")) {
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
                                logger.info("?????????????????????");
                                return ResultMaps.result(ResultEnum.FILECONTENTFAILURE);
                            }

                            logger.info("check????????????????????????????????????");

                            ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");

                            // pts???????????????
                            ShelfPtsDto shelfPtsDto = new ShelfPtsDto();
                            shelfPtsDto.setCompanyCd(companyCd);
                            shelfPtsDto.setFileName(filenames);

                            Map<String, Object> res = shelfPtsService.setShelfPtsInfo(shelfPtsDto, 0);
                             resultMap = (PtsPatternRelationDto) res.get("data");
                            Integer ptsId = Integer.valueOf(resultMap.getFileCd());
                            ptsDataVersion.setPtsCd(ptsId);
                            FilesOperationService filesOperationService = applicationContext.getBean(FilesOperationService.class);
                            filesOperationService.savePtsData(ptsDataVersion, arrList1, arrList2, arrList3, ptsId, companyCd, authorCd, now);

                            // id?????????
                            String uuid = UUID.randomUUID().toString();
                            String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
                            GetPtsCsvCgiDto getPtsCsvCgiDto = new GetPtsCsvCgiDto();
                            getPtsCsvCgiDto.setCompany(companyCd);
                            getPtsCsvCgiDto.setMode("pts_shori");
                            getPtsCsvCgiDto.setPtsIdCsvNm(ptsId + "@" + filenames);
                            getPtsCsvCgiDto.setGuid(uuid);
                            logger.info("??????pts_shori?????????{}", getPtsCsvCgiDto);

                            ResourceBundle resource = ResourceBundle.getBundle("pathConfig");
                            String paths = resource.getString("PtsUploadData");
                            String result = null;
                            result = cgiUtil.postCgi(paths, getPtsCsvCgiDto, tokenInfo);
                            logger.info("taskid??????pts???????????????{}", result);
                            String queryPath = resourceBundle.getString("TaskQuery");
                            // taskid?????????????????????cgi???????????????/?????????????????????????????????
                            Map<String, Object> data = cgiUtil.postCgiLoop(queryPath, result, tokenInfo);
                            logger.info("??????pts_shori?????????{}", data);
                        }
                    }
                }
                return ResultMaps.result(ResultEnum.SUCCESS,resultMap);

            } else {
                return ResultMaps.result(ResultEnum.FAILURE);
            }
        } catch (Exception e) {
            logger.error("error,???????????????{}", e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            if(e instanceof DataIntegrityViolationException){
                return ResultMaps.result(ResultEnum.FILECONTENTFAILURE);
            }
            return ResultMaps.result(ResultEnum.FAILURE);
        } finally {
            try{
                if(Objects.nonNull(bufferedReader)){
                    bufferedReader.close();
                }
                if(Objects.nonNull(inputStreamReader)){
                    inputStreamReader.close();
                }
            }catch (Exception e){
                logger.error("io???????????????", e);
            }

        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void savePtsData(ShelfPtsDataVersion ptsDataVersion, List<String[]> arrList1,
                            List<String[]> arrList2, List<String[]> arrList3, Integer ptsId, String companyCd, String authorCd, Date now){
        shelfPtsDataVersionMapper.insert(ptsDataVersion);
        List<ShelfPtsDataTaimst> dataTaimstList = convertArrayToTaimstList(arrList1, ptsId, companyCd, authorCd, now);

        List<ShelfPtsDataTanamst> dataTanamstList = convertArrayToTanamstList(arrList2, ptsId, companyCd, authorCd, now);

        List<ShelfPtsDataJandata> dataJandataList = convertArrayToJanList(arrList3, ptsId, companyCd, authorCd, now);

        shelfPtsDataTaimstMapper.insertAll(dataTaimstList);

        shelfPtsDataTanamstMapper.insertAll(dataTanamstList);

        shelfPtsDataJandataMapper.insertAll(dataJandataList);
    }


    /**
     * ?????????????????????????????????
     *
     * @param path
     * @param companyCd
     * @param fileName
     * @throws IOException
     */
    //private void fileSaveRemote(String path, String companyCd, String fileName, String remotePaths, String projectIds,
    //                            String bucketNames) throws IOException {
    //    String remotePath = remotePaths;
    //    remotePath = remotePath.replace("COMPANYCD", companyCd);
    //    // ?????????????????????--???????????????????????????
    //    // The ID of your GCP project
    //    String projectId = projectIds;
    //    // The ID of your GCS bucket
    //    String bucketName = bucketNames;
    //    // The ID of your GCS object
    //    String objectName = fileName;
    //    // The path to your file to upload
    //    String filePath = path + fileName;
    //    File jsonKey = new File("/secrets/.gcp-credientials.json");
    //    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).setCredentials(GoogleCredentials.fromStream(new FileInputStream(jsonKey))).build().getService();
    //    BlobId blobId = BlobId.of(bucketName, remotePath + objectName);
    //    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    //    storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));
    //    File delFile = new File(path + fileName);
    //    if (!delFile.delete()) {
    //        logger.info("??????????????????");
    //    }
    //}

    /**
     * ??????????????????????????????????????????????????????
     */
    public boolean judgeExistsPath(String csvPath) {
        boolean res = true;
        if (!new File(csvPath).exists()) {
            res = new File(csvPath).mkdirs();
        }
        return res;
    }

    /**
     * ?????????????????????
     */
    public static void excelPutPath(MultipartFile file, String csvPath) throws IOException {
        File resultFile = new File(csvPath);
        if (resultFile.exists()) {
            resultFile.mkdirs();
        }
        FileUtils.copyInputStreamToFile(file.getInputStream(), resultFile);
    }

    /**
     * csv??????????????????????????????????????????????????????
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
        //????????????????????????????????????????????????
        try(InputStream is = multipartFile.getInputStream();
            InputStreamReader isReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isReader)) {
            //???????????????????????????
            while (br.ready()) {
                result.add(br.readLine().split(","));
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return result;
    }
}

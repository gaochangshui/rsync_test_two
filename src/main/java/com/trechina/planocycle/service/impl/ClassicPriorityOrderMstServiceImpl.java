package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trechina.planocycle.entity.dto.GoodsRankDto;
import com.trechina.planocycle.entity.dto.PriorityOrderDataForCgiDto;
import com.trechina.planocycle.entity.dto.PriorityOrderMstDto;
import com.trechina.planocycle.entity.dto.PriorityOrderPtsDownDto;
import com.trechina.planocycle.entity.po.PriorityOrderMst;
import com.trechina.planocycle.entity.po.PriorityOrderPattern;
import com.trechina.planocycle.entity.vo.PriorityOrderPrimaryKeyVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.*;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.cgiUtils;
import com.trechina.planocycle.utils.sshFtpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClassicPriorityOrderMstServiceImpl implements ClassicPriorityOrderMstService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private ClassicPriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private ClassicPriorityOrderPatternMapper priorityOrderPatternMapper;
    @Autowired
    private CommodityScoreMasterService commodityScoreMasterService;
    @Autowired
    private ShelfPatternService shelfPatternService;
    @Autowired
    private ClassicPriorityOrderMstAttrSortService priorityOrderMstAttrSortService;
    @Autowired
    private ClassicPriorityOrderJanReplaceService priorityOrderJanReplaceService;
    @Autowired
    private ClassicPriorityOrderJanNewService priorityOrderJanNewService;
    @Autowired
    private ClassicPriorityOrderJanCardService priorityOrderJanCardService;
    @Autowired
    private ClassicPriorityOrderCatePakService priorityOrderCatePakService;
    @Autowired
    private ClassicPriorityOrderJanProposalService priorityOrderJanProposalService;
    @Autowired
    private ClassicPriorityOrderBranchNumService priorityOrderBranchNumService;
    @Autowired
    private ClassicPriorityOrderJanAttributeMapper priorityOrderJanAttributeMapper;
    @Autowired
    private ClassicPriorityOrderJanNewMapper priorityOrderJanNewMapper;
    @Autowired
    private ClassicPriorityOrderJanCardMapper priorityOrderJanCardMapper;
    @Autowired
    private ClassicPriorityOrderJanProposalMapper priorityOrderJanProposalMapper;
    @Autowired
    private PriorityOrderCatepakMapper priorityOrderCatepakMapper;
    @Autowired
    private ClassicPriorityOrderCatepakAttributeMapper priorityOrderCatepakAttributeMapper;
    @Autowired
    private ClassicPriorityOrderCommodityMustMapper priorityOrderCommodityMustMapper;
    @Autowired
    private ClassicPriorityOrderCommodityNotMapper priorityOrderCommodityNotMapper;
    @Autowired
    private ClassicPriorityOrderDataMapper priorityOrderDataMapper;
    @Autowired
    private ClassicPriorityOrderResultDataMapper priorityOrderResultDataMapper;
    @Autowired
    private ClassicPriorityOrderMstService priorityOrderMstService;
    @Autowired
    private ClaasicPriorityOrderAttributeClassifyMapper priorityOrderAttributeClassifyMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    @Autowired
    private cgiUtils cgiUtil;
    /**
     * 获取优先顺位表list
     *
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderList(String companyCd) {
        logger.info("获取优先顺位表参数："+companyCd);
        List<PriorityOrderMst> priorityOrderMstList = priorityOrderMstMapper.selectByPrimaryKey(companyCd);
        logger.info("获取优先顺位表返回值："+priorityOrderMstList);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderMstList);
    }

    /**
     * 保存优先顺位表参数
     *
     * @param priorityOrderMstDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderMst(PriorityOrderMstDto priorityOrderMstDto) {
        logger.info("保存优先顺位表参数"+priorityOrderMstDto);
        String authorCd = session.getAttribute("aud").toString();
        // check优先顺位表名称
        Integer count = priorityOrderPatternMapper.selectByPriorityOrderName(priorityOrderMstDto.getCompanyCd(),
                                                            priorityOrderMstDto.getPriorityOrderName(),
                                                            priorityOrderMstDto.getPriorityOrderCd(),authorCd);
        if (count >0) {
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
         //把参数处理成两个表的的数据，insert
        priorityOrderMstService.setWorkPriorityOrderMst(priorityOrderMstDto);
        //try {
            logger.info("保存优先顺位表参数："+priorityOrderMstDto);
            PriorityOrderMst priorityOrderMst = new PriorityOrderMst();
            priorityOrderMst.setCompanyCd(priorityOrderMstDto.getCompanyCd());
            priorityOrderMst.setPriorityOrderCd(priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderMst.setPriorityOrderName(priorityOrderMstDto.getPriorityOrderName());
            priorityOrderMst.setProductPowerCd(priorityOrderMstDto.getProductPowerCd());
            priorityOrderMst.setAttrOption(priorityOrderMstDto.getAttrOption());
            logger.info("保存优先顺位表mst表要保存的数据："+priorityOrderMst);
            priorityOrderMstMapper.deleteforid(priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderMstMapper.insert(priorityOrderMst,authorCd);
            //jannew最終テーブルデータの保存
            priorityOrderJanNewMapper.deleteFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderJanNewMapper.setFinalForWork(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            //janAttribute最終テーブルデータの保存
            priorityOrderJanAttributeMapper.deleteFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderJanAttributeMapper.setFinalForWork(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            //jancut 最終テーブルデータの保存
            priorityOrderJanCardMapper.deleteFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderJanCardMapper.setFinalForWork(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            //janProposal 最終テーブルデータの保存
            priorityOrderJanProposalMapper.deleteFinalByPrimaryKey(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderJanProposalMapper.insertFinalData(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            //CatepakAttribute 最終テーブルデータの保存
            priorityOrderCatepakAttributeMapper.deleteFinalByPrimaryKey(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderCatepakAttributeMapper.insertFinalData(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            //Catepak 最終テーブルデータの保存
            priorityOrderCatepakMapper.deleteFinalByPrimaryKey(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderCatepakMapper.insertFinalData(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());

            //must 最終テーブルデータの保存
            priorityOrderCommodityMustMapper.deleteFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderCommodityMustMapper.setFinalForWork(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            //not 最終テーブルデータの保存
            priorityOrderCommodityNotMapper.deleteFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderCommodityNotMapper.setFinalForWork(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            //classify 最終テーブルデータの保存
            priorityOrderAttributeClassifyMapper.deleteFinal(priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderAttributeClassifyMapper.insertFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());

            //resultData保存
            priorityOrderResultDataMapper.deleteFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderResultDataMapper.setFinalForWork(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd(),authorCd);
            //屬性attr保存
            priorityOrderMstAttrSortMapper.deleteAttrFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderMstAttrSortMapper.insertAttrFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            //sort保存
            priorityOrderMstAttrSortMapper.deleteAttrSortFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderMstAttrSortMapper.insertAttrSortFinal(priorityOrderMstDto.getCompanyCd(),priorityOrderMstDto.getPriorityOrderCd());
            List<PriorityOrderPattern> priorityOrderPatternList = new ArrayList<>();
            String[] shelfPatternList = priorityOrderMstDto.getShelfPatternCd().split(",");
            for (int i = 0; i < shelfPatternList.length; i++) {
                PriorityOrderPattern priorityOrderPattern = new PriorityOrderPattern();
                priorityOrderPattern.setCompanyCd(priorityOrderMstDto.getCompanyCd());
                priorityOrderPattern.setPriorityOrderCd(priorityOrderMstDto.getPriorityOrderCd());
                priorityOrderPattern.setShelfPatternCd(Integer.valueOf(shelfPatternList[i]));
                priorityOrderPatternList.add(priorityOrderPattern);
            }
            logger.info("保存优先顺位表pattert表要保存的数据："+priorityOrderPatternList.toString());
            priorityOrderPatternMapper.deleteforid(priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderPatternMapper.insert(priorityOrderPatternList);
            return ResultMaps.result(ResultEnum.SUCCESS);
        //} catch (Exception e) {
        //    logger.info("报错:"+e);
        //    logger.error("保存优先顺位表报错："+e);
        //    return ResultMaps.result(ResultEnum.FAILURE);
        //}
    }
    // 调用cgi保存数据
    private void cgiSave(PriorityOrderMstDto priorityOrderMstDto) {
        JSONArray jsonArray = (JSONArray) JSONArray.parse(String.valueOf(priorityOrderMstDto.getPriorityData()).replaceAll(" ",""));
        String[] res = new String[jsonArray.size()];

        for (int i = 0; i < jsonArray.size(); i++) {
            String rowStr = "";
            Map<String,Object> rowMaps = new HashMap<>();
            rowMaps = (Map<String, Object>) jsonArray.get(i);
            rowStr+=((Map) jsonArray.get(i)).get("jan_old").toString().trim().isEmpty()?"_":((Map) jsonArray.get(i)).get("jan_old")+" ";
            rowStr+=((Map) jsonArray.get(i)).get("jan_new").toString().trim().isEmpty()?"_":((Map) jsonArray.get(i)).get("jan_new")+" ";

            final String[] keys = {""};
            Object[] listKey = rowMaps.keySet().stream().sorted().toArray();
            for (int z = 0; z < listKey.length; z++) {
                if (listKey[z].toString().indexOf("attr")>-1){
                    rowStr+=((Map) jsonArray.get(i)).get(listKey[z])+" ";
                }
            }
            rowStr+=((Map) jsonArray.get(i)).get("pos_amount").toString().trim().isEmpty()?"_":((Map) jsonArray.get(i)).get("pos_amount")+" ";
            rowStr+=((Map) jsonArray.get(i)).get("pos_before_rate").toString().trim().isEmpty()?"_":((Map) jsonArray.get(i)).get("pos_before_rate")+" ";
            rowStr+=((Map) jsonArray.get(i)).get("branch_amount").toString().trim().isEmpty()?"_":((Map) jsonArray.get(i)).get("branch_amount")+" ";
            rowStr+=((Map) jsonArray.get(i)).get("unit_price").toString().trim().isEmpty()?"_":((Map) jsonArray.get(i)).get("unit_price")+" ";
            rowStr+=((Map) jsonArray.get(i)).get("unit_before_diff").toString().trim().isEmpty()?"_":((Map) jsonArray.get(i)).get("unit_before_diff")+" ";
            rowStr+=((Map) jsonArray.get(i)).get("rank").toString().trim().isEmpty()?"_":((Map) jsonArray.get(i)).get("rank")+" ";
            rowStr+=((Map) jsonArray.get(i)).get("branch_num").toString().trim().isEmpty()?"_":((Map) jsonArray.get(i)).get("branch_num")+" ";
            rowStr+=((Map) jsonArray.get(i)).get("rank_prop").toString().trim().isEmpty()?"_":((Map) jsonArray.get(i)).get("rank_prop")+" ";
            rowStr+=((Map) jsonArray.get(i)).get("rank_upd").toString().trim().isEmpty()?"_":((Map) jsonArray.get(i)).get("rank_upd")+" ";
            rowStr+=((Map) jsonArray.get(i)).get("branch_num_upd").toString().trim().isEmpty()?"_":((Map) jsonArray.get(i)).get("branch_num_upd")+" ";
            rowStr+=((Map) jsonArray.get(i)).get("pos_amount_upd").toString().trim().isEmpty()?"_":((Map) jsonArray.get(i)).get("pos_amount_upd")+" ";
            rowStr+=((Map) jsonArray.get(i)).get("difference").toString().trim().isEmpty()?"_":((Map) jsonArray.get(i)).get("difference")+" ";
            rowStr+=((Map) jsonArray.get(i)).get("sale_forecast").toString().trim().isEmpty()?"_":((Map) jsonArray.get(i)).get("sale_forecast");
            res[i] = rowStr;
        }

        String wirteReadFlag = "write";
        Map<String,Object> results= priorityDataWRFlag(priorityOrderMstDto, res, wirteReadFlag);
    }

    // 处理属性保存
    private void attrSave(PriorityOrderMstDto priorityOrderMstDto,List<Map<String, Object>> array) {

//        logger.info("获取rankAttributeCdList"+array);
//        List<PriorityOrderMstAttrSort> priorityOrderMstAttrSortList = new ArrayList<>();
//        for (int i = 0; i < array.size(); i++) {
//            PriorityOrderMstAttrSort priorityOrderMstAttrSort = new PriorityOrderMstAttrSort();
//          priorityOrderMstAttrSort.setCompanyCd(priorityOrderMstDto.getCompanyCd());
//          priorityOrderMstAttrSort.setPriorityOrderCd(priorityOrderMstDto.getPriorityOrderCd());
//          if (array.get(i).get("value").toString().equals("mulit_attr")){
//              priorityOrderMstAttrSort.setValue(array.size());
//              priorityOrderMstAttrSort.setCd(13);
//          } else {
//              priorityOrderMstAttrSort.setValue(Integer.valueOf(array.get(i).get("value").toString()));
//              priorityOrderMstAttrSort.setCd(Integer.valueOf(array.get(i).get("cd").toString()));
//          }
//          if (array.get(i).get("sort").toString().equals("")){
//              priorityOrderMstAttrSort.setSort(0);
//          } else {
//            priorityOrderMstAttrSort.setSort(Integer.valueOf(array.get(i).get("sort").toString()));
//          }
//          priorityOrderMstAttrSortList.add(priorityOrderMstAttrSort);
//        }
//        priorityOrderMstAttrSortService.setPriorityAttrSort(priorityOrderMstAttrSortList);
    }

    /**
     * 读写priorityorderData
     * @param priorityOrderMstDto
     * @param res
     * @param wirteReadFlag
     * @return
     */
    @Override
    public Map<String, Object> priorityDataWRFlag(PriorityOrderMstDto priorityOrderMstDto, String[] res, String wirteReadFlag) {
        PriorityOrderDataForCgiDto priorityOrderDataForCgiDto = new PriorityOrderDataForCgiDto();
        priorityOrderDataForCgiDto.setCompany(priorityOrderMstDto.getCompanyCd());
        String uuid = UUID.randomUUID().toString();
        priorityOrderDataForCgiDto.setGuid(uuid);
        priorityOrderDataForCgiDto.setMode("priority_data");
        priorityOrderDataForCgiDto.setPriorityNO(priorityOrderMstDto.getPriorityOrderCd());
        priorityOrderDataForCgiDto.setWriteReadFlag(wirteReadFlag);
        priorityOrderDataForCgiDto.setAttributeCd(priorityOrderMstDto.getAttributeCd());
        if (wirteReadFlag.equals("write")){
            priorityOrderDataForCgiDto.setDataArray(res);
        }
        logger.info("保存优先顺位表给cgi的参数"+priorityOrderDataForCgiDto);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("PriorityOrderData");
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        Map<String,Object> resultCgi = new HashMap<>();
        //递归调用cgi，首先去taskid

        String result = cgiUtil.postCgi(path,priorityOrderDataForCgiDto,tokenInfo);
        logger.info("taskId返回："+result);
        String queryPath = resourceBundle.getString("TaskQuery");
        //带着taskId，再次请求cgi获取运行状态/数据
        resultCgi =cgiUtil.postCgiLoop(queryPath,result,tokenInfo);
        logger.info("保存优先顺位表结果："+resultCgi);
        return resultCgi;

    }


    /**
     * 获取登录这所在企业是否有优先顺位表
     *
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderExistsFlg() {
        List<String> companyCd  = Arrays.asList(session.getAttribute("inCharge").toString().split(","));
        int result = priorityOrderMstMapper.selectPriorityOrderCount(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS,result);
    }

    /**
     * 优先顺位表获取rank属性的动态列
     *
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    @Override
    public Map<String, Object> getRankAttr(String companyCd, Integer productPowerCd) {
        logger.info("优先顺位表获取rank属性的动态列："+companyCd+","+productPowerCd);
        Map<String,Object> result = new HashMap<>();
        commodityScoreMasterService.productPowerParamAttrName(companyCd, productPowerCd, result);
        return ResultMaps.result(ResultEnum.SUCCESS,result);
    }

    /**
     * 获取pts文件下载
     *
     * @param priorityOrderPtsDownDto
     * @param response
     * @return
     */
    @Override
    public Map<String, Object> getPtsFileDownLoad(PriorityOrderPtsDownDto priorityOrderPtsDownDto, HttpServletResponse response,String ptsDownPath) {
        logger.info("获取pts出力参数:"+priorityOrderPtsDownDto);
        // 从cgi获取数据
        String uuid = UUID.randomUUID().toString();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("PriorityOrderData");
        priorityOrderPtsDownDto.setGuid(uuid);
        // rankAttributeCd
        List<Map<String, Object>> array = (List<Map<String, Object>>) JSONArray.parse(priorityOrderPtsDownDto.getRankAttributeList());
        logger.info("获取rankAttributeCdList"+array);
        String rankInfo = "";
        String attrInfo = "";
        String rankInfo_mulit = "";
        String attrInfo_mulit = "";
        String sortStr = "";
        String sort = "";
        for (int i = 1; i <= array.size(); i++) {
            for (int j = 0; j < array.size(); j++) {
                sortStr = String.valueOf(array.get(j).get("sort"));
                sort = "";
                if(sortStr.equals("")){
                    sort="0";
                } else {
                    sort=sortStr;
                }
                if (String.valueOf(array.get(j).get("value")).equals("mulit_attr") && i ==array.size()){
                    rankInfo_mulit += array.get(j).get("cd") + "_" + i + "_" + sort + ",";
                    attrInfo_mulit += "13,";
                }else {
                    if (!String.valueOf(array.get(j).get("value")).equals("mulit_attr") && i == Integer.valueOf(String.valueOf(array.get(j).get("value")))){
                        rankInfo += array.get(j).get("cd") + "_" + i + "_" + sort + ",";
                        attrInfo += array.get(j).get("cd")+",";
                    }
                }
            }
        }
        rankInfo = rankInfo+rankInfo_mulit;
        attrInfo = attrInfo+attrInfo_mulit;
        String rankFinalInfo = rankInfo.substring(0,rankInfo.length()-1);
        String attrFinalInfo = attrInfo.substring(0,attrInfo.length()-1);
        logger.info("处理完的rankAttributeCd"+rankFinalInfo);
        priorityOrderPtsDownDto.setAttributeCd(attrFinalInfo);
        priorityOrderPtsDownDto.setRankAttributeCd(rankFinalInfo);
        // shelfPatternNoNm
        String resultShelf=shelfPatternService.getShePatternNoNm(priorityOrderPtsDownDto.getShelfPatternNo());
        logger.info("抽出完的shelfPatternNoNm"+resultShelf);
        priorityOrderPtsDownDto.setShelfPatternNoNm(resultShelf.replaceAll(" ","*"));
        logger.info("获取处理完的pts出力参数:"+priorityOrderPtsDownDto);
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        Map<String,Object> ptsPath = new HashMap<>();
        //递归调用cgi，首先去taskid

        String result = cgiUtil.postCgi(path,priorityOrderPtsDownDto,tokenInfo);
        logger.info("taskId返回："+result);
        String queryPath = resourceBundle.getString("TaskQuery");
        //带着taskId，再次请求cgi获取运行状态/数据
        ptsPath =cgiUtil.postCgiLoop(queryPath,result,tokenInfo);
        logger.info("pts路径返回数据："+ptsPath);

        String filePath = ptsPath.get("data").toString();
        if (filePath.length()>1) {
            String[] fileName = filePath.split("/");

            sshFtpUtils sshFtp = new sshFtpUtils();
            try {
                logger.info("pts全路径输出："+ptsDownPath+ptsPath.get("data").toString());
                byte[] files = sshFtp.downLoafCgi(ptsDownPath+ptsPath.get("data").toString(),tokenInfo);
                logger.info("files byte:"+files);
                response.setContentType("application/Octet-stream");
                logger.info("finename:"+fileName[fileName.length-1]);
                response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(fileName[fileName.length-1], "UTF-8"));
                OutputStream outputStream = response.getOutputStream();
                outputStream.write(files);
                outputStream.close();
            } catch (IOException e) {
//                e.printStackTrace();
                logger.info("获取pts文件下载报错"+e);
            }

//            String pathResult = ptsPath.get("data").toString();
//            if (pathResult.equals("-1") || pathResult.equals("2")){
//                return ResultMaps.result(ResultEnum.FAILURE);
//            }
//            String[] ptsPathArr = pathResult.split("/");
//            String fileName = ptsPathArr[ptsPathArr.length - 1];
//            logger.info("pts文件名称"+fileName);
//            logger.info("实例化filesOperationService");
//            FilesOperationServiceImpl filesOperationService = new FilesOperationServiceImpl();
//            logger.info("实例化成功filesOperationService");
//            String usercd = (String) session.getAttribute("aud");
//            String tempPath = resourceBundle.getString("csvPathShelf") + usercd + "/";
//            logger.info("temppath"+tempPath);
//            String csvPath = tempPath + fileName;
//            logger.info("csvPath"+csvPath);
//            filesOperationService.judgeExistsPath(tempPath);
//            // 数据桶版本
//            String projectIds= resourceBundle.getString("projectId");
//            String bucketNames= resourceBundle.getString("bucketName");
//            // The ID of your GCP project
//            String projectId = projectIds;
//            // The ID of your GCS bucket
//            String bucketName = bucketNames;
//            // The ID of your GCS object
//            String objectName = pathResult;
//            logger.info("objectName"+objectName);
//            // The path to your file to upload
//            String destFilePath  = csvPath;
//            logger.info("destFilePath"+destFilePath);
//            File jsonKey = new File("/secrets/.gcp-credientials.json");
//            try {
//                Storage storage = StorageOptions.newBuilder().setProjectId(projectId).setCredentials(GoogleCredentials
//                        .fromStream(new FileInputStream(jsonKey))).build().getService();
//                Blob blob = storage.get(BlobId.of(bucketName, objectName));
//                blob.downloadTo(Paths.get(destFilePath));
//                File files = new File(csvPath);
//                response.setContentType("application/Octet-stream");
////                String chars = new String(fileName.getBytes("iso8859-1"), "utf-8");
//                response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
//                try(FileInputStream ips = new FileInputStream(files);){
//                    OutputStream outputStream = response.getOutputStream();
//                    int len =0;
//                    byte[] buffer  = new byte[1024];
//                    while ((len = ips.read(buffer)) != -1){
//                        outputStream.write(buffer,0,len);
//                        outputStream.flush();
//                    }
//                    outputStream.close();
//                    if(!files.delete()){
//                        logger.info("删除文件失败");
//                    }
//                }
//
//            } catch (UnsupportedEncodingException e) {
//                logger.info("下载pts报错"+e);
//            } catch (FileNotFoundException e) {
//                logger.info("报错:"+e);
//            } catch (IOException e) {
//                logger.info("报错:"+e);
//            }
            // 物理机版本
//            try {
//                response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            sshFtpUtils sshFtp = new sshFtpUtils();
//            sshFtp.getFile(ptsPath.get("data").toString(), csvPath, response, fileName);
        }
        logger.info("下载成功");
        return null;
    }

    /**
     * 根据优先顺位表cd获取商品力点数表cd
     *
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getProductPowerCdForPriority(Integer priorityOrderCd) {
        logger.info("根据优先顺位表cd获取商品力点数表cd的参数"+priorityOrderCd);
        Map<String,Object> productPowerCd = priorityOrderMstMapper.selectProductPowerCd(priorityOrderCd);
        logger.info("根据优先顺位表cd获取商品力点数表cd的返回值"+priorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS,productPowerCd);
    }

    /**
     * 删除所有优先顺位表信息
     *
     * @param primaryKeyVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> delPriorityOrderAllInfo(PriorityOrderPrimaryKeyVO primaryKeyVO) {
        String companyCd= primaryKeyVO.getCompanyCd();
        Integer priorityOrderCd = primaryKeyVO.getPriorityOrderCd();
        // 删除主表
        delPriorityOrderMst(primaryKeyVO);
        //删除data
        priorityOrderResultDataMapper.deleteFinal(companyCd,priorityOrderCd);
        // 删除jan变list
        priorityOrderJanReplaceService.delJanReplaceInfo(companyCd,priorityOrderCd);
        // 删除新规商品list
        priorityOrderJanNewService.delriorityOrderJanNewInfo(companyCd,priorityOrderCd);
        // 删除card商品list
        priorityOrderJanCardService.delPriorityOrderJanCardInfo(companyCd,priorityOrderCd);
        // 删除catepak扩缩
        priorityOrderCatePakService.delPriorityOrderCatePakInfo(companyCd,priorityOrderCd);
        priorityOrderCatePakService.delPriorityOrderCatePakAttrInfo(companyCd,priorityOrderCd);
        // 删除jan变提案list
        priorityOrderJanProposalService.delPriorityOrderJanProposalInfo(companyCd,priorityOrderCd);
        // 删除必须和不可和中间表
        priorityOrderBranchNumService.delPriorityOrderCommodityMustInfo(companyCd,priorityOrderCd);
        priorityOrderBranchNumService.delPriorityOrderCommodityNotInfo(companyCd,priorityOrderCd);
        priorityOrderBranchNumService.delPriorityOrderBranchNumInfo(companyCd,priorityOrderCd);
        // 删除数据的排序
        priorityOrderMstAttrSortService.delPriorityAttrSortInfo(companyCd,priorityOrderCd);
        //删除attr
        priorityOrderMstAttrSortMapper.deleteAttrSortFinal(companyCd,priorityOrderCd);
        // 删除jannew的属性列
        priorityOrderJanAttributeMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
        // 删除棚pattern关联信息
        priorityOrderPatternMapper.deleteforid(priorityOrderCd);

        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 根据productpowercd查询关联的优先顺位表cd
     *
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    @Override
    public String selPriorityOrderCdForProdCd(String companyCd, Integer productPowerCd) {
        return priorityOrderMstMapper.selectPriorityOrderCdForProdCd(companyCd,productPowerCd);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setWorkPriorityOrderMst(PriorityOrderMstDto priorityOrderMstDto) {

        String authorCd = session.getAttribute("aud").toString();
        String companyCd = priorityOrderMstDto.getCompanyCd();
        Integer priorityOrderCd = priorityOrderMstDto.getPriorityOrderCd();
        String priorityData = priorityOrderMstDto.getPriorityData();
        List<GoodsRankDto> goodsRank = priorityOrderDataMapper.getGoodsRank(companyCd,priorityOrderCd);
        JSONArray datas = JSON.parseArray(priorityData);
        List<Map<String, String>> keyNameList = new ArrayList<>();
        colNameList(datas, keyNameList);
        List<Map> delJanList = datas.toJavaList(Map.class).stream().filter(item -> item.get("rank_upd").equals(99999999)).collect(Collectors.toList());
        priorityOrderJanCardMapper.deleteByPrimaryKey(companyCd, priorityOrderCd);
        if (!delJanList.isEmpty()) {
            priorityOrderJanCardMapper.setDelJanList(delJanList, companyCd, priorityOrderCd);
        }

        priorityOrderDataMapper.deleteWorkData(companyCd,priorityOrderCd);
        List<LinkedHashMap<String, Object>> linkedHashMaps = new Gson().fromJson(datas.toString(), new TypeToken<List<LinkedHashMap<String, Object>>>() {
        }.getType());

        priorityOrderDataMapper.insertWorkData(companyCd,priorityOrderCd,linkedHashMaps,authorCd);
        if (!goodsRank.isEmpty()) {
            priorityOrderDataMapper.updateGoodsRank(goodsRank, companyCd, priorityOrderCd);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    public void colNameList(JSONArray datas, List<Map<String, String>> keyNameList) {
        boolean isGoodsExist = false;
        for (int i = 0; i < ((JSONObject) datas.get(0)).keySet().toArray().length; i++) {
            Map<String, String> maps = new HashMap<>();
            maps.put("name", (String) ((JSONObject) datas.get(0)).keySet().toArray()[i]);
            String col =(String) ((JSONObject) datas.get(0)).keySet().toArray()[i];
            if (col.equals("goods_rank")){
                isGoodsExist = true;
            }
            keyNameList.add(maps);
        }
        if (!isGoodsExist){
            Map<String, String> maps = new HashMap<>();
            maps.put("name","goods_rank");
            keyNameList.add(maps);
        }
    }


    /**
     * 优先顺位表主表信息删除
     * @param primaryKeyVO
     * @return
     */
    private Integer delPriorityOrderMst(PriorityOrderPrimaryKeyVO primaryKeyVO){
        return priorityOrderMstMapper.deleteByPrimaryKey(primaryKeyVO.getCompanyCd(),primaryKeyVO.getPriorityOrderCd());
    }

}

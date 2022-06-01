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
     * 優先順位テーブルlistの取得
     *
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderList(String companyCd) {
        logger.info("優先順位テーブルパラメータの取得："+companyCd);
        List<PriorityOrderMst> priorityOrderMstList = priorityOrderMstMapper.selectByPrimaryKey(companyCd);
        logger.info("優先順位テーブル戻り値の取得："+priorityOrderMstList);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderMstList);
    }

    /**
     * 優先順位テーブルパラメータの保存
     *
     * @param priorityOrderMstDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderMst(PriorityOrderMstDto priorityOrderMstDto) {
        logger.info("優先順位テーブルパラメータの保存"+priorityOrderMstDto);
        String authorCd = session.getAttribute("aud").toString();
        // チェック優先順位テーブル名
        Integer count = priorityOrderPatternMapper.selectByPriorityOrderName(priorityOrderMstDto.getCompanyCd(),
                                                            priorityOrderMstDto.getPriorityOrderName(),
                                                            priorityOrderMstDto.getPriorityOrderCd(),authorCd);
        if (count >0) {
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
         //パラメータを2つのテーブルのデータに処理するinsert
        priorityOrderMstService.setWorkPriorityOrderMst(priorityOrderMstDto);
        try {
            logger.info("優先順位テーブルパラメータの保存："+priorityOrderMstDto);
            PriorityOrderMst priorityOrderMst = new PriorityOrderMst();
            priorityOrderMst.setCompanyCd(priorityOrderMstDto.getCompanyCd());
            priorityOrderMst.setPriorityOrderCd(priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderMst.setPriorityOrderName(priorityOrderMstDto.getPriorityOrderName());
            priorityOrderMst.setProductPowerCd(priorityOrderMstDto.getProductPowerCd());
            priorityOrderMst.setAttrOption(priorityOrderMstDto.getAttrOption());
            logger.info("優先順位テーブルmstテーブルが保存するデータを保存する："+priorityOrderMst);
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
            logger.info("優先順位テーブルpattertテーブルが保存するデータを保存する："+priorityOrderPatternList.toString());
            priorityOrderPatternMapper.deleteforid(priorityOrderMstDto.getPriorityOrderCd());
            priorityOrderPatternMapper.insert(priorityOrderPatternList);
            return ResultMaps.result(ResultEnum.SUCCESS);
        } catch (Exception e) {
            logger.info("エラーを報告:"+e);
            logger.error("保存優先順位テーブルエラー："+e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }
    // cgiを呼び出してデータを保存する
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

    // 処理属性の保存
    private void attrSave(PriorityOrderMstDto priorityOrderMstDto,List<Map<String, Object>> array) {

//        logger.info("つかむ取rankAttributeCdList"+array);
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
     * リードライトpriorityorderData
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
        logger.info("cgiに優先順位テーブルのパラメータを保存します"+priorityOrderDataForCgiDto);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("PriorityOrderData");
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        Map<String,Object> resultCgi = new HashMap<>();
        String result = cgiUtil.postCgi(path,priorityOrderDataForCgiDto,tokenInfo);
        logger.info("taskId："+result);
        String queryPath = resourceBundle.getString("TaskQuery");
        resultCgi =cgiUtil.postCgiLoop(queryPath,result,tokenInfo);
        logger.info("優先順位テーブル結菓の保存："+resultCgi);
        return resultCgi;

    }


    /**
     * この企業に優先順位テーブルがあるかどうかのログインを取得します。
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
     * 優先順位テーブルrank属性の動的列の取得
     *
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    @Override
    public Map<String, Object> getRankAttr(String companyCd, Integer productPowerCd) {
        logger.info("優先順位テーブルrank属性の動的列の取得："+companyCd+","+productPowerCd);
        Map<String,Object> result = new HashMap<>();
        commodityScoreMasterService.productPowerParamAttrName(companyCd, productPowerCd, result);
        return ResultMaps.result(ResultEnum.SUCCESS,result);
    }

    /**
     * ptsファイルのダウンロードを取得する
     *
     * @param priorityOrderPtsDownDto
     * @param response
     * @return
     */
    @Override
    public Map<String, Object> getPtsFileDownLoad(PriorityOrderPtsDownDto priorityOrderPtsDownDto, HttpServletResponse response,String ptsDownPath) {
        logger.info("pts出力パラメータを取得する:"+priorityOrderPtsDownDto);
        // 从cgiつかむ取数据
        String uuid = UUID.randomUUID().toString();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        String path = resourceBundle.getString("PriorityOrderData");
        priorityOrderPtsDownDto.setGuid(uuid);
        // rankAttributeCd
        List<Map<String, Object>> array = (List<Map<String, Object>>) JSONArray.parse(priorityOrderPtsDownDto.getRankAttributeList());
        logger.info("rankAttributeCdList"+array);
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
        logger.info("處理完的rankAttributeCd"+rankFinalInfo);
        priorityOrderPtsDownDto.setAttributeCd(attrFinalInfo);
        priorityOrderPtsDownDto.setRankAttributeCd(rankFinalInfo);
        // shelfPatternNoNm
        String resultShelf=shelfPatternService.getShePatternNoNm(priorityOrderPtsDownDto.getShelfPatternNo());
        logger.info("抽出完的shelfPatternNoNm"+resultShelf);
        priorityOrderPtsDownDto.setShelfPatternNoNm(resultShelf.replaceAll(" ","*"));
        logger.info("つかむ取處理完的pts出力参数:"+priorityOrderPtsDownDto);
        String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
        Map<String,Object> ptsPath = new HashMap<>();
        //cgiを再帰的に呼び出し、まずtaskidに行きます。

        String result = cgiUtil.postCgi(path,priorityOrderPtsDownDto,tokenInfo);
        logger.info("taskIdリターン："+result);
        String queryPath = resourceBundle.getString("TaskQuery");
        //taskIdを持っています，cgiに実行状態の取得を再度要求する
        ptsPath =cgiUtil.postCgiLoop(queryPath,result,tokenInfo);
        logger.info("ptsパスがデータを返す："+ptsPath);

        String filePath = ptsPath.get("data").toString();
        if (filePath.length()>1) {
            String[] fileName = filePath.split("/");

            sshFtpUtils sshFtp = new sshFtpUtils();
            try {
                logger.info("ptsフルパス出力："+ptsDownPath+ptsPath.get("data").toString());
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
                logger.info("ptsファイルダウンロードエラーの取得"+e);
            }
        }
        logger.info("ダウンロード成功");
        return null;
    }

    /**
     * 優先順位テーブルcdに基づく商品力点数テーブルcdの取得
     *
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getProductPowerCdForPriority(Integer priorityOrderCd) {
        logger.info("優先順位テーブルcdに基づいて商品力点数テーブルcdのパラメータを取得する"+priorityOrderCd);
        Map<String,Object> productPowerCd = priorityOrderMstMapper.selectProductPowerCd(priorityOrderCd);
        logger.info("優先順位テーブルcdから商品力点数テーブルcdの戻り値を取得する"+priorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS,productPowerCd);
    }

    /**
     * すべての優先順位テーブル情報を削除
     *
     * @param primaryKeyVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> delPriorityOrderAllInfo(PriorityOrderPrimaryKeyVO primaryKeyVO) {
        String companyCd= primaryKeyVO.getCompanyCd();
        Integer priorityOrderCd = primaryKeyVO.getPriorityOrderCd();
        // マスターテーブルの削除
        delPriorityOrderMst(primaryKeyVO);
        priorityOrderResultDataMapper.deleteFinal(companyCd,priorityOrderCd);
        priorityOrderJanReplaceService.delJanReplaceInfo(companyCd,priorityOrderCd);
        priorityOrderJanNewService.delriorityOrderJanNewInfo(companyCd,priorityOrderCd);
        priorityOrderJanCardService.delPriorityOrderJanCardInfo(companyCd,priorityOrderCd);
        // 削除catepakかくだい
        priorityOrderCatePakService.delPriorityOrderCatePakInfo(companyCd,priorityOrderCd);
        priorityOrderCatePakService.delPriorityOrderCatePakAttrInfo(companyCd,priorityOrderCd);
        // jan変提案listを削除する
        priorityOrderJanProposalService.delPriorityOrderJanProposalInfo(companyCd,priorityOrderCd);
        // 必須テーブルと不可テーブルの削除
        priorityOrderBranchNumService.delPriorityOrderCommodityMustInfo(companyCd,priorityOrderCd);
        priorityOrderBranchNumService.delPriorityOrderCommodityNotInfo(companyCd,priorityOrderCd);
        priorityOrderBranchNumService.delPriorityOrderBranchNumInfo(companyCd,priorityOrderCd);
        // 削除データのソート
        priorityOrderMstAttrSortService.delPriorityAttrSortInfo(companyCd,priorityOrderCd);
        //attrを削除
        priorityOrderMstAttrSortMapper.deleteAttrSortFinal(companyCd,priorityOrderCd);
        // jannewの属性列を削除
        priorityOrderJanAttributeMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
        // 棚pattern関連情報の削除
        priorityOrderPatternMapper.deleteforid(priorityOrderCd);

        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * productpowercdによる関連優先順位テーブルcdのクエリ
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
     * 優先順位テーブルマスタテーブル情報削除
     * @param primaryKeyVO
     * @return
     */
    private Integer delPriorityOrderMst(PriorityOrderPrimaryKeyVO primaryKeyVO){
        return priorityOrderMstMapper.deleteByPrimaryKey(primaryKeyVO.getCompanyCd(),primaryKeyVO.getPriorityOrderCd());
    }

}

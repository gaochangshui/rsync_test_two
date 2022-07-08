package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.PriorityOrderDataForCgiDto;
import com.trechina.planocycle.entity.po.PriorityOrderJanProposal;
import com.trechina.planocycle.entity.po.ShelfPtsData;
import com.trechina.planocycle.entity.vo.PriorityOrderJanProposalVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.ClassicPriorityOrderJanProposalService;
import com.trechina.planocycle.service.CommonMstService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.cgiUtils;
import com.trechina.planocycle.utils.dataConverUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClassicPriorityOrderJanProposalServiceImpl implements ClassicPriorityOrderJanProposalService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private ClassicPriorityOrderJanProposalMapper priorityOrderJanProposalMapper;
    @Autowired
    private ClassicPriorityOrderDataMapper priorityOrderDataMapper;
    @Autowired
    private JanClassifyMapper janClassifyMapper;
    @Autowired
    private ClassicPriorityOrderJanProposalService priorityOrderJanProposalService;
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    private CommonMstService commonMstService;
    @Autowired
    private ProductPowerDataMapper productPowerDataMapper;
    @Autowired
    private cgiUtils cgiUtil;

    @Autowired
    private SysConfigMapper sysConfigMapper;


    /**
     * jan変提案listの取得
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderJanProposal(String companyCd, Integer priorityOrderCd,Integer productPowerNo,String shelfPatternNo) {
        logger.info("jan変提案listのパラメータを取得する:{},{}",companyCd,priorityOrderCd);
        List<PriorityOrderJanProposalVO> priorityOrderJanProposals = priorityOrderJanProposalMapper.selectByPrimaryKey(companyCd,
                priorityOrderCd);
        logger.info("jan変提案listの戻り値を取得する：{}",priorityOrderJanProposals);
        if(priorityOrderJanProposals.isEmpty()){

//            try {
//                janProposalData(companyCd, productPowerNo,shelfPatternNo,priorityOrderCd);
            String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
                janProposalDataFromDB(companyCd, productPowerNo,shelfPatternNo,priorityOrderCd, coreCompany);
//                priorityOrderJanProposals = priorityOrderJanProposalMapper.selectByPrimaryKey(companyCd,priorityOrderCd);
                String tableName = String.format("\"%s\".prod_0000_jan_info", coreCompany);
                priorityOrderJanProposals = priorityOrderJanProposalMapper.selectJanInfoByPrimaryKey(companyCd,priorityOrderCd,
                        tableName,"1", "2");

//            } catch (IOException e) {
//                logger.info("報錯:"+e);
//            }
        }
        logger.info("jan変提案listの戻り値を取得する：{}",priorityOrderJanProposals);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanProposals);
    }

    /**
     * jan変情報の保存エラーcgiからjan変提案listデータをpostgreに書く
     * @throws IOException
     */
    public void janProposalData(String companyCd,Integer productPowerNo,String shelfPatternNo,Integer priorityOrderCd)  {
            PriorityOrderDataForCgiDto priorityOrderDataForCgiDto = new PriorityOrderDataForCgiDto();
            // 調用cgi拿jan變提案list的数据
            String uuids = UUID.randomUUID().toString();
            priorityOrderDataForCgiDto.setMode("priority_jan_motion");
            priorityOrderDataForCgiDto.setGuid(uuids);
            priorityOrderDataForCgiDto.setCompany(companyCd);
            priorityOrderDataForCgiDto.setProductPowerNo(productPowerNo);
            priorityOrderDataForCgiDto.setShelfPatternNo(shelfPatternNo);
            logger.info("从cgi拿jan變提案list数据参数"+priorityOrderDataForCgiDto);
            String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
            ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
            String path = resourceBundle.getString("PriorityOrderData");
            String queryPath = resourceBundle.getString("TaskQuery");

            String resultJan = cgiUtil.postCgi(path, priorityOrderDataForCgiDto, tokenInfo);
            logger.info("taskId返回：" + resultJan);

            Map<String, Object> dataJan = cgiUtil.postCgiLoop(queryPath, resultJan, tokenInfo);
            logger.info("jan變提案list cgi返回数据：" + dataJan);
            if (!dataJan.get("data").equals("[ ]")) {
                JSONArray datasJan = (JSONArray) JSON.parse(dataJan.get("data").toString());

                priorityOrderJanProposalService.savePriorityOrderJanProposal(datasJan,companyCd,priorityOrderCd);
            }
    }

    /**
     * from db
     * @param companyCd
     * @param productPowerNo
     * @param shelfPatternNo
     * @param priorityOrderCd
     */
    public void janProposalDataFromDB(String companyCd,Integer productPowerNo,String shelfPatternNo,Integer priorityOrderCd, String coreCompany) {
        List<ShelfPtsData> shelfPtsData = shelfPtsDataMapper.getPtsCdByPatternCd(companyCd, shelfPatternNo);
        //only 品名2
        String tableName = String.format("\"%s\".prod_%s_jan_attr_header_sys", coreCompany, MagicString.FIRST_CLASS_CD);
        List<Map<String, Object>> classify = janClassifyMapper.selectJanClassify(tableName);
        Optional<Map<String, Object>> janCdOpt = classify.stream().filter(c -> c.get("attr").equals(MagicString.JAN_HEADER_JAN_CD_COL)).findFirst();
        String janCdCol = MagicString.JAN_HEADER_JAN_CD_DEFAULT;
        if(janCdOpt.isPresent()){
            janCdCol = janCdOpt.get().get("sort").toString();
        }
        Optional<Map<String, Object>> janNameOpt = classify.stream().filter(c -> c.get("attr").equals(MagicString.JAN_HEADER_JAN_NAME_COL)).findFirst();
        String janNameCol = MagicString.JAN_HEADER_JAN_NAME_DEFAULT;
        if(janNameOpt.isPresent()){
            janNameCol = janNameOpt.get().get("sort").toString();
        }
        tableName = String.format("\"%s\".prod_%s_jan_info", coreCompany, MagicString.FIRST_CLASS_CD);
        List<PriorityOrderJanProposal> list = productPowerDataMapper.selectSameNameJan(productPowerNo,
                shelfPtsData.stream().map(pts->pts.getId()+"").collect(Collectors.joining(",")), tableName, janCdCol, janNameCol);
        JSONArray datasJan = JSON.parseArray(JSON.toJSONString(list));
        priorityOrderJanProposalService.savePriorityOrderJanProposal(datasJan,companyCd,priorityOrderCd);
    }


    /**
     * jan変提案listのflagを修正する
     *
     * @param priorityOrderJanProposal
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderJanProposal(List<PriorityOrderJanProposal> priorityOrderJanProposal) {
        logger.info("jan変提案listのパラメータを修正する："+priorityOrderJanProposal);
        try {
            dataConverUtils dataConverUtil = new dataConverUtils();
            String companyCd = priorityOrderJanProposal.get(0).getCompanyCd();
            Integer priorityOrderCd = priorityOrderJanProposal.get(0).getPriorityOrderCd();

            List<PriorityOrderJanProposal> proposals = dataConverUtil.priorityOrderCommonMstInsertMethod(PriorityOrderJanProposal.class,
                    priorityOrderJanProposal,companyCd,priorityOrderCd);
            logger.info("jan変提案listの処理完了後のパラメータを修正する："+proposals);
            priorityOrderJanProposalMapper.updateByPrimaryKey(proposals);

            // 変更後にメインテーブルに反映
            //priorityOrderDataMapper.updatePriorityOrderDataForProp(companyCd,priorityOrderCd,
            //        "public.priorityorder"+session.getAttribute("aud").toString());
            return ResultMaps.result(ResultEnum.SUCCESS);
        } catch (Exception e) {
            logger.info("jan変更提案listエラーの修正："+e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }

    /**
     * cgiが返すjan提案リストを保存する
     *
     * @param jsonArray
     * @return
     */
    @Override
    public Map<String, Object> savePriorityOrderJanProposal(JSONArray jsonArray,String companyCd,Integer priorityOrderCd) {
        int result = priorityOrderJanProposalMapper.insert(jsonArray,companyCd,priorityOrderCd);
        return null;
    }

    /**
     * jan変提案listを削除する
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Integer delPriorityOrderJanProposalInfo(String companyCd, Integer priorityOrderCd) {
        return priorityOrderJanProposalMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
    }
}

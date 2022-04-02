package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.entity.dto.PriorityOrderDataForCgiDto;
import com.trechina.planocycle.entity.po.PriorityOrderJanProposal;
import com.trechina.planocycle.entity.vo.PriorityOrderJanProposalVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.exception.BussinessException;
import com.trechina.planocycle.mapper.PriorityOrderDataMapper;
import com.trechina.planocycle.mapper.PriorityOrderJanProposalMapper;
import com.trechina.planocycle.service.PriorityOrderJanProposalService;
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
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

@Service
public class PriorityOrderJanProposalServiceImpl implements PriorityOrderJanProposalService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private PriorityOrderJanProposalMapper priorityOrderJanProposalMapper;
    @Autowired
    private PriorityOrderDataMapper priorityOrderDataMapper;
    @Autowired
    private PriorityOrderJanProposalService priorityOrderJanProposalService;
    @Autowired
    private cgiUtils cgiUtil;

    /**
     * jan変提案リストの取得
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderJanProposal(String companyCd, Integer priorityOrderCd,Integer productPowerNo,String shelfPatternNo) {
        logger.info("获取jan变提案list的参数:"+companyCd+","+priorityOrderCd);
        List<PriorityOrderJanProposalVO> priorityOrderJanProposals = priorityOrderJanProposalMapper.selectByPrimaryKey(companyCd,
                priorityOrderCd);
        logger.info("获取jan变提案list的返回值："+priorityOrderJanProposals);
        if(priorityOrderJanProposals.size()==0){
            // 如果没数据获取cgi数据
            try {
                janProposalData(companyCd, productPowerNo,shelfPatternNo,priorityOrderCd);
                priorityOrderJanProposals = priorityOrderJanProposalMapper.selectByPrimaryKey(companyCd,
                        priorityOrderCd);
            } catch (IOException e) {
                logger.info("报错:"+e);
            }
        }
        logger.info("获取jan变提案list的返回值："+priorityOrderJanProposals);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanProposals);
    }

    /**
     * cgiからjan変提案リストデータをpostgreに書く
     * @throws IOException
     */
    public void janProposalData(String companyCd,Integer productPowerNo,String shelfPatternNo,Integer priorityOrderCd) throws IOException {
            PriorityOrderDataForCgiDto priorityOrderDataForCgiDto = new PriorityOrderDataForCgiDto();
            // cgiがjan変提案リストのデータを呼び出す
            String uuids = UUID.randomUUID().toString();
            priorityOrderDataForCgiDto.setMode("priority_jan_motion");
            priorityOrderDataForCgiDto.setGuid(uuids);
            priorityOrderDataForCgiDto.setCompany(companyCd);
            priorityOrderDataForCgiDto.setProductPowerNo(productPowerNo);
            priorityOrderDataForCgiDto.setShelfPatternNo(shelfPatternNo);
            logger.info("从cgi拿jan变提案list数据参数"+priorityOrderDataForCgiDto);
            String tokenInfo = (String) session.getAttribute("MSPACEDGOURDLP");
            ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
            String path = resourceBundle.getString("PriorityOrderData");
            String queryPath = resourceBundle.getString("TaskQuery");
            //再帰的にcgiを呼び出して、まずtaskidに行きます
            String resultJan = cgiUtil.postCgi(path, priorityOrderDataForCgiDto, tokenInfo);
            logger.info("taskId返回：" + resultJan);
            //taskIdを持って、再度cgiに運転状態/データの取得を要求する
            Map<String, Object> DataJan = cgiUtil.postCgiLoop(queryPath, resultJan, tokenInfo);
            logger.info("jan变提案list cgi返回数据：" + DataJan);
            if (!DataJan.get("data").equals("[ ]")) {
                JSONArray datasJan = (JSONArray) JSON.parse(DataJan.get("data").toString());
                //jan変提案リストデータの保存
                priorityOrderJanProposalService.savePriorityOrderJanProposal(datasJan,companyCd,priorityOrderCd);
            }
    }


    /**
     * jan変提案リストのflagを修正
     *
     * @param priorityOrderJanProposal
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderJanProposal(List<PriorityOrderJanProposal> priorityOrderJanProposal) {
        logger.info("修改jan变提案list的参数：{}",priorityOrderJanProposal);
        try {
            dataConverUtils dataConverUtil = new dataConverUtils();
            String companyCd = priorityOrderJanProposal.get(0).getCompanyCd();
            Integer priorityOrderCd = priorityOrderJanProposal.get(0).getPriorityOrderCd();
            //処理パラメータ
            List<PriorityOrderJanProposal> proposals = dataConverUtil.priorityOrderCommonMstInsertMethod(PriorityOrderJanProposal.class,
                    priorityOrderJanProposal,companyCd,priorityOrderCd);
            logger.info("修改jan变提案list的处理完后的参数：{}",proposals);
            priorityOrderJanProposalMapper.updateByPrimaryKey(proposals);

            // 変更後にプライマリ・テーブルに反映
            priorityOrderDataMapper.updatePriorityOrderDataForProp(companyCd,priorityOrderCd,
                    "public.priorityorder"+session.getAttribute("aud").toString());
            return ResultMaps.result(ResultEnum.SUCCESS);
        } catch (Exception e) {
            logger.info("修改jan变提案list报错：",e);
            throw new BussinessException("修改jan变提案list报错");
        }
    }

    /**
     * cgiが返すjan提案リストを保存
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
     * jan変提案リストの削除
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

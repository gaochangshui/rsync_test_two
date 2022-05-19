package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.entity.dto.PriorityOrderDataForCgiDto;
import com.trechina.planocycle.entity.po.PriorityOrderJanProposal;
import com.trechina.planocycle.entity.po.ShelfPtsData;
import com.trechina.planocycle.entity.vo.PriorityOrderJanProposalVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.ClassicPriorityOrderJanProposalService;
import com.trechina.planocycle.service.ShelfPtsService;
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
    private ShelfPtsDataJandataMapper ptsDataJandataMapper;
    @Autowired
    private ProductPowerDataMapper productPowerDataMapper;
    @Autowired
    private cgiUtils cgiUtil;

    /**
     * 获取jan变提案list
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
//                priorityOrderJanProposals = priorityOrderJanProposalMapper.selectByPrimaryKey(companyCd,priorityOrderCd);
                String tableName = "\"1000\".prod_0000_jan_info";
                priorityOrderJanProposals = priorityOrderJanProposalMapper.selectJanInfoByPrimaryKey(companyCd,priorityOrderCd,
                        tableName,"1", "2");

            } catch (IOException e) {
                logger.info("报错:"+e);
            }
        }
        logger.info("获取jan变提案list的返回值："+priorityOrderJanProposals);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanProposals);
    }

    /**
     * 从cgi拿jan变提案list数据 写到postgre
     * @throws IOException
     */
    public void janProposalData(String companyCd,Integer productPowerNo,String shelfPatternNo,Integer priorityOrderCd) throws IOException {
            PriorityOrderDataForCgiDto priorityOrderDataForCgiDto = new PriorityOrderDataForCgiDto();
            // 调用cgi拿jan变提案list的数据
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
            //递归调用cgi，首先去taskid

            String resultJan = cgiUtil.postCgi(path, priorityOrderDataForCgiDto, tokenInfo);
            logger.info("taskId返回：" + resultJan);
            //带着taskId，再次请求cgi获取运行状态/数据
            Map<String, Object> DataJan = cgiUtil.postCgiLoop(queryPath, resultJan, tokenInfo);
            logger.info("jan变提案list cgi返回数据：" + DataJan);
            if (!DataJan.get("data").equals("[ ]")) {
                JSONArray datasJan = (JSONArray) JSON.parse(DataJan.get("data").toString());
                //保存jan变提案list数据
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
    public void janProposalDataFromDB(String companyCd,Integer productPowerNo,String shelfPatternNo,Integer priorityOrderCd) {
        ShelfPtsData shelfPtsData = shelfPtsDataMapper.selectPtsCdByPatternCd(companyCd, Long.parseLong(shelfPatternNo));
        //只是用品名2
        String tableName = "\"1000\".prod_0000_jan_info";
        List<Map<String, Object>> classify = janClassifyMapper.selectJanClassify(tableName);
        Optional<Map<String, Object>> janCdOpt = classify.stream().filter(c -> c.get("attr").equals("jan_cd")).findFirst();
        String janCdCol = janCdOpt.get().get("attr").toString();
        Optional<Map<String, Object>> janNameOpt = classify.stream().filter(c -> c.get("attr").equals("jan_name")).findFirst();
        String janNameCol = janNameOpt.get().get("attr").toString();
        List<PriorityOrderJanProposal> list = productPowerDataMapper.selectSameNameJan(productPowerNo, shelfPtsData.getId(), tableName, janCdCol, janNameCol);
        JSONArray datasJan = JSON.parseArray(JSON.toJSONString(list));
        priorityOrderJanProposalService.savePriorityOrderJanProposal(datasJan,companyCd,priorityOrderCd);
    }


    /**
     * 修改jan变提案list的flag
     *
     * @param priorityOrderJanProposal
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderJanProposal(List<PriorityOrderJanProposal> priorityOrderJanProposal) {
        logger.info("修改jan变提案list的参数："+priorityOrderJanProposal);
        try {
            dataConverUtils dataConverUtil = new dataConverUtils();
            String companyCd = priorityOrderJanProposal.get(0).getCompanyCd();
            Integer priorityOrderCd = priorityOrderJanProposal.get(0).getPriorityOrderCd();
            //处理参数
            List<PriorityOrderJanProposal> proposals = dataConverUtil.priorityOrderCommonMstInsertMethod(PriorityOrderJanProposal.class,
                    priorityOrderJanProposal,companyCd,priorityOrderCd);
            logger.info("修改jan变提案list的处理完后的参数："+proposals);
            priorityOrderJanProposalMapper.updateByPrimaryKey(proposals);

            // 修改后反映到主表
            //priorityOrderDataMapper.updatePriorityOrderDataForProp(companyCd,priorityOrderCd,
            //        "public.priorityorder"+session.getAttribute("aud").toString());
            return ResultMaps.result(ResultEnum.SUCCESS);
        } catch (Exception e) {
            logger.info("修改jan变提案list报错："+e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }

    /**
     * 保存cgi返回的jan提案list
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
     * 删除jan变提案list
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

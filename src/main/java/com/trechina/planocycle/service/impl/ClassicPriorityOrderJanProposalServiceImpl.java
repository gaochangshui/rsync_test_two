package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderMstDto;
import com.trechina.planocycle.entity.po.PriorityOrderJanProposal;
import com.trechina.planocycle.entity.po.ProductPowerParamVo;
import com.trechina.planocycle.entity.po.ShelfPtsData;
import com.trechina.planocycle.entity.vo.PriorityOrderJanProposalVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.BasicPatternMstService;
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
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    @Autowired
    private ClassicPriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private BasicPatternMstService basicPatternMstService;
    @Autowired
    private MstBranchMapper mstBranchMapper;


    /**
     * jan?????????list?????????
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderJanProposal(String companyCd, Integer priorityOrderCd,Integer productPowerNo,String shelfPatternNo) {
        logger.info("jan?????????list?????????????????????????????????:{},{}",companyCd,priorityOrderCd);
        String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
        int existTableName = mstBranchMapper.checkTableExist("prod_0000_jan_info", coreCompany);
        if (existTableName == 0){
            coreCompany = companyCd;
        }
        String tableName = String.format("\"%s\".prod_0000_jan_info", coreCompany);
        PriorityOrderMstDto priorityOrderMst = priorityOrderMstMapper.getPriorityOrderMst(companyCd, priorityOrderCd);
        ProductPowerParamVo param = productPowerDataMapper.getParam(companyCd, priorityOrderMst.getProductPowerCd());
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(param.getCommonPartsData(), companyCd);
        List<PriorityOrderJanProposalVO> priorityOrderJanProposals = priorityOrderJanProposalMapper.selectByPrimaryKey(companyCd,
                priorityOrderCd,tableName);
        logger.info("jan?????????list??????????????????????????????{}",priorityOrderJanProposals);
        if(priorityOrderJanProposals.isEmpty()){
                janProposalDataFromDB(companyCd, productPowerNo,shelfPatternNo,priorityOrderCd, commonTableName);

                priorityOrderJanProposals = priorityOrderJanProposalMapper.selectJanInfoByPrimaryKey(companyCd,priorityOrderCd,
                        tableName,"1", "2");
        }
        logger.info("jan?????????list??????????????????????????????{}",priorityOrderJanProposals);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanProposals);
    }


    /**
     * from db
     * @param companyCd
     * @param productPowerNo
     * @param shelfPatternNo
     * @param priorityOrderCd
     */
    public void janProposalDataFromDB(String companyCd,Integer productPowerNo,String shelfPatternNo,Integer priorityOrderCd, GetCommonPartsDataDto commonTableName) {
        List<ShelfPtsData> shelfPtsData = shelfPtsDataMapper.getPtsCdByPatternCd(companyCd, shelfPatternNo);
        //only ??????2
        List<Map<String, Object>> classify = janClassifyMapper.selectJanClassify(commonTableName.getProAttrTable());
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
        List<PriorityOrderJanProposal> list = productPowerDataMapper.selectSameNameJan(productPowerNo,
                shelfPtsData.stream().map(pts->pts.getId()+"").collect(Collectors.joining(",")),
                commonTableName.getProInfoTable(), janCdCol, janNameCol);
        JSONArray datasJan = JSON.parseArray(JSON.toJSONString(list));
        priorityOrderJanProposalService.savePriorityOrderJanProposal(datasJan,companyCd,priorityOrderCd);
    }


    /**
     * jan?????????list???flag???????????????
     *
     * @param priorityOrderJanProposal
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderJanProposal(List<PriorityOrderJanProposal> priorityOrderJanProposal) {
        logger.info("jan?????????list????????????????????????????????????{}",priorityOrderJanProposal);
        try {
            dataConverUtils dataConverUtil = new dataConverUtils();
            String companyCd = priorityOrderJanProposal.get(0).getCompanyCd();
            Integer priorityOrderCd = priorityOrderJanProposal.get(0).getPriorityOrderCd();

            List<PriorityOrderJanProposal> proposals = dataConverUtil.priorityOrderCommonMstInsertMethod(PriorityOrderJanProposal.class,
                    priorityOrderJanProposal,companyCd,priorityOrderCd);
            logger.info("jan?????????list??????????????????????????????????????????????????????{}",proposals);
            priorityOrderJanProposalMapper.updateByPrimaryKey(proposals);
            return ResultMaps.result(ResultEnum.SUCCESS);
        } catch (Exception e) {
            logger.info("jan????????????list?????????????????????",e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }

    /**
     * cgi?????????jan??????????????????????????????
     *
     * @param jsonArray
     * @return
     */
    @Override
    public Map<String, Object> savePriorityOrderJanProposal(JSONArray jsonArray,String companyCd,Integer priorityOrderCd) {
        if(!jsonArray.isEmpty()){
            priorityOrderJanProposalMapper.insert(jsonArray,companyCd,priorityOrderCd);
        }
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * jan?????????list???????????????
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

package com.trechina.planocycle.service.impl;

import com.google.api.client.util.Strings;
import com.google.common.base.Joiner;
import com.trechina.planocycle.aspect.LogAspect;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderMstDto;
import com.trechina.planocycle.entity.po.ClassicPriorityOrderJanCard;
import com.trechina.planocycle.entity.po.ProductPowerParamVo;
import com.trechina.planocycle.entity.vo.ClassicPriorityOrderJanCardVO;
import com.trechina.planocycle.entity.vo.ClassicPriorityOrderJanNewVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.ClassicPriorityOrderJanCardService;
import com.trechina.planocycle.service.PriorityOrderJanReplaceService;
import com.trechina.planocycle.utils.ListDisparityUtils;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.dataConverUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpSession;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClassicPriorityOrderJanCardServiceImpl implements ClassicPriorityOrderJanCardService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private ClassicPriorityOrderJanCardMapper priorityOrderJanCardMapper;
    @Autowired
    private PriorityOrderJanReplaceService priorityOrderJanReplaceService;

    @Autowired
    private ClassicPriorityOrderJanNewMapper priorityOrderJanNewMapper;
    @Autowired
    private ClassicPriorityOrderJanReplaceMapper classicPriorityOrderJanReplaceMapper;
    @Autowired
    private ClassicPriorityOrderMstMapper classicPriorityOrderMstMapper;
    @Autowired
    private BasicPatternMstServiceImpl basicPatternMstService;
    @Autowired
    private ProductPowerDataMapper productPowerDataMapper;
    @Autowired
    private JanClassifyMapper janClassifyMapper;
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private MstBranchMapper mstBranchMapper;
    @Autowired
    private LogAspect logAspect;
    /**
     * card商品リストの取得
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderJanCard(String companyCd, Integer priorityOrderCd) {
        List<ClassicPriorityOrderJanNewVO> janNewList = priorityOrderJanNewMapper.getExistOtherMst(companyCd, priorityOrderCd);
        logger.info("card商品リストパラメータを取得する:{},{}",companyCd,priorityOrderCd);
        String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
        int existTableName = mstBranchMapper.checkTableExist("prod_0000_jan_info", coreCompany);

        if (existTableName == 0){
            coreCompany = companyCd;
        }
        String tableName = MessageFormat.format("\"{0}\".prod_0000_jan_info", coreCompany);
        List<Map<String, Object>> janHeader = janClassifyMapper.selectJanClassify(tableName);
        String janCdCol = janHeader.stream().filter(map -> map.get("attr").equals(MagicString.JAN_HEADER_JAN_CD_COL))
                .map(map -> map.get("sort")).findFirst().orElse(MagicString.JAN_HEADER_JAN_CD_DEFAULT).toString();
        String janNameCol = janHeader.stream().filter(map -> map.get("attr").equals(MagicString.JAN_HEADER_JAN_NAME_COL))
                .map(map -> map.get("sort")).findFirst().orElse(MagicString.JAN_HEADER_JAN_NAME_DEFAULT).toString();

        List<ClassicPriorityOrderJanCardVO> priorityOrderJanCardVOS = priorityOrderJanCardMapper.selectJanCard(companyCd,priorityOrderCd, tableName, janCdCol, janNameCol);
        for (ClassicPriorityOrderJanNewVO priorityOrderJanNewVO : janNewList) {
            for (ClassicPriorityOrderJanCardVO priorityOrderJanCardVO : priorityOrderJanCardVOS) {
                if (priorityOrderJanNewVO.getJanNew().equals(priorityOrderJanCardVO.getJanOld())){
                    priorityOrderJanCardVO.setErrMsg("すでに新規商品として入力済みです。");
                }
            }
        }
        logger.info("card商品list戻り値の取得：{}",priorityOrderJanCardVOS);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanCardVOS);
    }

    /**
     * card商品listの保存
     *
     * @param priorityOrderJanCard
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderJanCard(List<ClassicPriorityOrderJanCard> priorityOrderJanCard) {
        logger.info("保存card商品list的参数：{}",priorityOrderJanCard);
        try {
            dataConverUtils dataConverUtil = new dataConverUtils();
            String companyCd = priorityOrderJanCard.get(0).getCompanyCd();
            Integer priorityOrderCd = priorityOrderJanCard.get(0).getPriorityOrderCd();
            // 処理パラメータ
            List<ClassicPriorityOrderJanCard> cards = dataConverUtil.priorityOrderCommonMstInsertMethod(ClassicPriorityOrderJanCard.class,
                    priorityOrderJanCard,companyCd,priorityOrderCd);
            cards = cards.stream().filter(map -> map.getJanOld() != null && !"".equals(map.getJanOld())).collect(Collectors.toList());
            logger.info("保存card商品list的處理完的参数：{}",cards);
            //全削除
            priorityOrderJanCardMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
            // jancheck
            if (cards.isEmpty()){
                return ResultMaps.result(ResultEnum.SUCCESS);
            }
            PriorityOrderMstDto priorityOrderMst = classicPriorityOrderMstMapper.getPriorityOrderMst(companyCd, priorityOrderCd);
            ProductPowerParamVo param = productPowerDataMapper.getParam(companyCd, priorityOrderMst.getProductPowerCd());
            GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(param.getCommonPartsData(), companyCd);
            String proInfoTable = commonTableName.getProInfoTable();
            List<String> janList = cards.stream().map(ClassicPriorityOrderJanCard::getJanOld).collect(Collectors.toList());
            List<String> existJan = classicPriorityOrderJanReplaceMapper.selectJanDistinctByJan(proInfoTable, janList,priorityOrderCd);

            List<String> notExistJan = ListDisparityUtils.getListDisparitStr(janList,existJan);

            if (!existJan.isEmpty()) {
                //全挿入
                priorityOrderJanCardMapper.insertWork(existJan,companyCd,priorityOrderCd);
            }
            if (!notExistJan.isEmpty()){
                return ResultMaps.result(ResultEnum.JANNOTESISTS, Joiner.on(",").join(notExistJan));

            }else {
                return ResultMaps.result(ResultEnum.SUCCESS);
            }
        } catch (Exception e) {
            logger.info("保存card商品list報錯：",e);
            logAspect.setTryErrorLog(e,new Object[]{priorityOrderJanCard});
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultMaps.result(ResultEnum.FAILURE);
        }

    }

    /**
     * card商品listの削除
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Integer delPriorityOrderJanCardInfo(String companyCd, Integer priorityOrderCd) {
        return priorityOrderJanCardMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
    }

    @Override
    public Map<String, String> checkIsJanCut(List<ClassicPriorityOrderJanCard> priorityOrderJanCard) {

        Map<String, String> errorJan = new HashMap<>();
        if(priorityOrderJanCard.isEmpty()){
            return errorJan;
        }
        List<String> janReplaceList = priorityOrderJanCardMapper.checkJanReplace(priorityOrderJanCard);
        List<String> janReplaceOldList = priorityOrderJanCardMapper.checkJanReplaceOld(priorityOrderJanCard);
        if (!janReplaceList.isEmpty() || !janReplaceOldList.isEmpty()){
            janReplaceList.addAll(janReplaceOldList);
            janReplaceList.forEach(jan->{
                if(Strings.isNullOrEmpty(errorJan.getOrDefault(jan, ""))) {
                errorJan.put(jan, "すでにJAN変商品として入力済みです。");
            }});
        }

        List<String> janProposalList = priorityOrderJanCardMapper.checkJanProposal(priorityOrderJanCard);
        List<String> janProposalOldList = priorityOrderJanCardMapper.checkJanProposalOld(priorityOrderJanCard);
        if (!janProposalList.isEmpty() || !janProposalOldList.isEmpty()){
            janProposalList.addAll(janProposalOldList);
            janProposalList = janProposalList.stream().distinct().collect(Collectors.toList());

            janProposalList.forEach(jan->{
                if(Strings.isNullOrEmpty(errorJan.getOrDefault(jan, ""))){
                    errorJan.put(jan, "すでにJAN変商品として入力済みです。");
                }
            });
        }

        List<String> janNewList = priorityOrderJanCardMapper.checkJanNew(priorityOrderJanCard);
        if (!janNewList.isEmpty()){
            janNewList.forEach(jan->{
                if(Strings.isNullOrEmpty(errorJan.getOrDefault(jan, ""))){
                    errorJan.put(jan, "すでに新規商品として入力済みです。");
                }
            });
        }

        return errorJan;
    }


}

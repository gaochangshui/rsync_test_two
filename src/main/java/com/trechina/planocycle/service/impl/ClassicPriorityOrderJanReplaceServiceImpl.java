package com.trechina.planocycle.service.impl;

import com.google.common.collect.Lists;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderMstDto;
import com.trechina.planocycle.entity.po.PriorityOrderJanReplace;
import com.trechina.planocycle.entity.po.ProductPowerParamVo;
import com.trechina.planocycle.entity.vo.ClassicPriorityOrderJanReplaceVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.ClassicPriorityOrderJanReplaceService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.dataConverUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ClassicPriorityOrderJanReplaceServiceImpl implements ClassicPriorityOrderJanReplaceService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private ClassicPriorityOrderJanReplaceMapper priorityOrderJanReplaceMapper;
    @Autowired
    private ClassicPriorityOrderDataMapper priorityOrderDataMapper;
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private ClassicPriorityOrderJanCardMapper priorityOrderJanCardMapper;
    @Autowired
    private ClassicPriorityOrderCommodityNotMapper priorityOrderCommodityNotMapper;
    @Autowired
    private ClassicPriorityOrderMstMapper classicPriorityOrderMstMapper;
    @Autowired
    private ProductPowerDataMapper productPowerDataMapper;
    @Autowired
    private BasicPatternMstServiceImpl basicPatternMstService;
    @Autowired
    private ClassicPriorityOrderJanNewMapper classicPriorityOrderJanNewMapper;
    @Autowired
    private MstBranchMapper mstBranchMapper;
    /**
     * jan変更リストの情報を取得する
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderJanInfo(String companyCd, Integer priorityOrderCd) {
        logger.info("jan変の情報パラメータを取得する：{},{}",companyCd,priorityOrderCd);
        String coreCompany = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
        int existTableName = mstBranchMapper.checkTableExist("prod_0000_jan_info", coreCompany);
        if (existTableName == 0){
            coreCompany = companyCd;
        }
        String tableName = String.format("\"%s\".prod_%s_jan_info", coreCompany, MagicString.FIRST_CLASS_CD);
        List<ClassicPriorityOrderJanReplaceVO> priorityOrderJanReplaceVOList = priorityOrderJanReplaceMapper
                .selectJanInfo(companyCd,priorityOrderCd, tableName, MagicString.JAN_HEADER_JAN_CD_DEFAULT, MagicString.JAN_HEADER_JAN_NAME_DEFAULT);
        logger.info("jan変の情報戻り値を取得する：{}",priorityOrderJanReplaceVOList);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanReplaceVOList);
    }

    /**
     * jan変listの情報を保存する（全削除全挿入）
     *
     * @param priorityOrderJanReplace
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderJanInfo(List<PriorityOrderJanReplace> priorityOrderJanReplace) {
        logger.info("jan変の情報パラメータを保存する：{}",priorityOrderJanReplace);
        // 取得したパラメータは1行目に企業と順位テーブルcdがあるだけで、パラメータを巡回し、すべての行に値を割り当てる必要があります。
        try{
            String companyCd = priorityOrderJanReplace.get(0).getCompanyCd();
            Integer priorityOrderCd = priorityOrderJanReplace.get(0).getPriorityOrderCd();
            dataConverUtils dataConverUtil = new dataConverUtils();
            // 共通メソッドを呼び出し、データを処理する
            List<PriorityOrderJanReplace> jan =dataConverUtil.priorityOrderCommonMstInsertMethod(PriorityOrderJanReplace.class,priorityOrderJanReplace,
                    companyCd ,priorityOrderCd);

            logger.info("jan変の情報を保存して処理した後のパラメータ：{}",jan);
            // check

            PriorityOrderMstDto priorityOrderMst = classicPriorityOrderMstMapper.getPriorityOrderMst(companyCd, priorityOrderCd);
            ProductPowerParamVo param = productPowerDataMapper.getParam(companyCd, priorityOrderMst.getProductPowerCd());
            GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(param.getCommonPartsData(), companyCd);
            String proInfoTable = commonTableName.getProInfoTable();


            //削除

            priorityOrderJanReplaceMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
            List<PriorityOrderJanReplace> exists = new ArrayList<>();
            String notExists = "";
            for (int i = 0; i < jan.size(); i++) {
                if(jan.get(i).getJanNew()==null && jan.get(i).getJanOld()==null){
                    continue;
                }
                boolean existJanInfo = isExistJanInfo(proInfoTable, jan.get(i).getJanOld(),priorityOrderCd);
                if (!existJanInfo){
                    notExists += jan.get(i).getJanOld()+",";
                }else {
                    exists.add(jan.get(i));
                }
            }
            logger.info("存在するjan情報{}",exists);
            logger.info("存在しないjan情報{}",notExists);
            //データベースへの書き込み
            if (!exists.isEmpty()) {
                priorityOrderJanReplaceMapper.insert(exists);

            }
            if (notExists.length()>0) {
                return ResultMaps.result(ResultEnum.JANNOTESISTS,notExists.substring(0,notExists.length()-1));
            } else {
                return ResultMaps.result(ResultEnum.SUCCESS);
            }
        } catch (Exception e) {
            logger.error("jan変情報の保存エラー：",e);
            // トランザクションの手動ロールバック
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }


    @Override
    public String getJanInfo(String proInfoTable) {
        return priorityOrderJanReplaceMapper.selectJanDistinct(proInfoTable);
    }

    @Override
    public boolean isExistJanInfo(String proInfoTable, String jan,Integer priorityOrderCd) {

        return !priorityOrderJanReplaceMapper.selectJanDistinctByJan(proInfoTable, Lists.newArrayList(jan),priorityOrderCd).isEmpty();
    }

    /**
     * janを削除してリストを変更する
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Integer delJanReplaceInfo(String companyCd, Integer priorityOrderCd) {
        return priorityOrderJanReplaceMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
    }
}

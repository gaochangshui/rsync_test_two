package com.trechina.planocycle.service.impl;

import cn.hutool.extra.mail.MailAccount;
import com.google.api.client.util.Strings;
import com.trechina.planocycle.config.MailConfig;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.po.BranchList;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.ClassicPriorityOrderCommodityMustMapper;
import com.trechina.planocycle.mapper.MstBranchMapper;
import com.trechina.planocycle.mapper.SysConfigMapper;
import com.trechina.planocycle.service.MstBranchService;
import com.trechina.planocycle.service.MstCommodityService;
import com.trechina.planocycle.service.MstJanService;
import com.trechina.planocycle.utils.MailUtils;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MstBranchServiceImpl implements MstBranchService {
    @Autowired
    private MstBranchMapper mstBranchMapper;
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private ClassicPriorityOrderCommodityMustMapper classicPriorityOrderCommodityMustMapper;
    @Autowired
    private MstCommodityService mstCommodityService;
    @Value("${projectIds}")
    private String projectIds;
    @Autowired
    private MstJanService mstJanService;

    private Logger logger = LoggerFactory.getLogger(MstBranchServiceImpl.class);

    @Override
    public Map<String, Object> getBranchInfo(BranchList branchList) {
        String companyCd = "1000";

        if ("0".equals(branchList.getCommonPartsData().getStoreIsCore())) {
            companyCd = branchList.getCompanyCd();
        }
        String branchInfoTableName = MessageFormat.format("\"{0}\".ten_{1}_ten_info", companyCd,
                branchList.getCommonPartsData().getStoreMstClass());
        List<String> groupCompany = classicPriorityOrderCommodityMustMapper.getGroupCompany(branchList.getCompanyCd());
        groupCompany.add(branchList.getCompanyCd());
        List<Map<String, Object>> companyBranchInfo;
        if (!companyCd.equals("1000")){
             companyBranchInfo = mstBranchMapper.getCompanyBranchInfo(branchInfoTableName, companyCd, groupCompany);
        }else {
             companyBranchInfo = mstBranchMapper.getCommuneBranchInfo(branchInfoTableName, branchList.getCompanyCd(),groupCompany);
        }
        return ResultMaps.result(ResultEnum.SUCCESS,companyBranchInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> setBranchInfo(List<BranchList> branchList) {
        String companyCd = "1000";
        String groupCd = "";

        if ("0".equals(branchList.get(0).getCommonPartsData().getStoreIsCore())) {
            companyCd = branchList.get(0).getCompanyCd();
        }else {
               branchList.forEach(map -> map.setBranchCd(map.getCompanyCd() + "_" + map.getBranchCd()));
            groupCd = branchList.get(0).getGroupCd();

        }
        branchList = branchList.stream().filter(map-> !Strings.isNullOrEmpty(map.getBranchCd())).collect(Collectors.toList());
        String branchInfoTableName = MessageFormat.format("\"{0}\".ten_{1}_ten_info", companyCd,
                branchList.get(0).getCommonPartsData().getStoreMstClass());
        List<String> groupCompany = classicPriorityOrderCommodityMustMapper.getGroupCompany(groupCd);
        if (groupCompany.isEmpty()) {
            groupCompany.add(groupCd);
        }
        if (companyCd.equals("1000")){
            mstBranchMapper.deleteBranch(branchInfoTableName,groupCompany);
        }else {
            mstBranchMapper.deleteBranch1(branchInfoTableName);
        }

        mstBranchMapper.setBranchInfo(branchList,branchInfoTableName);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public Map<String, Object> syncTenData(String env) {
        List<Map<String, Object>> syncResults = new ArrayList<>();
        try {
            String syncCompanyList = sysConfigMapper.selectSycConfig("sync_company_list");
            String[] companyList = syncCompanyList.split(",");
            String masterTenTb;
            String masterTenTbPkey;
            String masterTenTbWk;

            for (String companyCd : companyList) {
                masterTenTb = MessageFormat.format(MagicString.MASTER_TEN, companyCd);
                masterTenTbWk = MessageFormat.format(MagicString.WK_MASTER_TEN, companyCd);
                masterTenTbPkey = MessageFormat.format(MagicString.MASTER_TEN_PKEY, companyCd);
                String masterTenExist = mstBranchMapper.selectMasterTenExist(companyCd);
                if (Strings.isNullOrEmpty(masterTenExist)) {
                    mstBranchMapper.creatMasterTen(masterTenTb, masterTenTbWk, masterTenTbPkey);
                }
                List<String> tenClass = mstBranchMapper.getMasterTenClass(masterTenTb);
                for (String classCd : tenClass) {
                    Map<String, Object> resultMap = perSyncTenData(companyCd, classCd, masterTenTb);
                    syncResults.add(resultMap);
                }
            }

            if(syncResults.stream().anyMatch(map->map.containsKey("error"))){
                List<Map<String, Object>> error = syncResults.stream().filter(map -> map.containsKey("error")).collect(Collectors.toList());
                Exception errorMsg = (Exception) error.get(0).get("error");

                MailAccount account = MailConfig.getMailAccount(!projectIds.equals("nothing"));
                String title = MessageFormat.format("「{0}」同期发生异常:不明なエラーが発生しました", env);
                String content = String.format(MailConfig.MAIL_EXCEPTION_TEMPLATE, "syncTenData", errorMsg.getMessage());
                MailUtils.sendEmail(account, MagicString.TO_MAIL, title, content);
            }

            return ResultMaps.result(ResultEnum.SUCCESS, syncResults);
        }catch (Exception e){
            MailAccount account = MailConfig.getMailAccount(!projectIds.equals("nothing"));
            String title = MessageFormat.format("「{0}」同期发生异常:不明なエラーが発生しました", env);
            String content = String.format(MailConfig.MAIL_EXCEPTION_TEMPLATE, "syncTenData", e.getMessage());
            MailUtils.sendEmail(account, MagicString.TO_MAIL, title, content);
        }

        return ResultMaps.result(ResultEnum.FAILURE.getCode(), "Ten同期失败しました");
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> perSyncTenData(String companyCd, String classCd, String masterTenTb){
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("companyCd", companyCd);
        resultMap.put("classCd", classCd);

        try {
            String tableNameInfo;
            String tableNameInfoPkey;
            String tableNameInfoWK;
            String tableNameHeaderInfo;

            String tableNameExist = mstBranchMapper.selectTableNameExist(companyCd, classCd);
            tableNameInfo = MessageFormat.format(MagicString.PROD_TEN_INFO, companyCd, classCd);
            tableNameInfoPkey = MessageFormat.format(MagicString.PROD_TEN_INFO_PKEY, classCd);
            tableNameInfoWK = MessageFormat.format(MagicString.WK_PROD_TEN_INFO, companyCd, classCd);
            tableNameHeaderInfo = MessageFormat.format(MagicString.WK_PROD_TEN_INFO_HEADER, companyCd, classCd);

            int i = mstBranchMapper.checkTableExist(tableNameInfoWK.split("\\.")[1], companyCd);
            if (i < 1) {
                //if not exist, delete from table
                mstBranchMapper.deleteNotExistMst(classCd, masterTenTb);
                logger.info("{} not exist", tableNameInfoWK);
                return resultMap;
            }

            List<LinkedHashMap<String, Object>> tenList = mstBranchMapper.getTenHeader(tableNameHeaderInfo);
            String column = tenList.stream().map(e -> e.get("3").toString()).collect(Collectors.joining(","));
            if (Strings.isNullOrEmpty(tableNameExist)) {
                mstBranchMapper.creatTenData(tableNameInfo, tableNameInfoWK, tableNameInfoPkey);
            }else{
                mstJanService.addPrimaryKey(tableNameInfo.split("\\.")[1], "1", companyCd, tableNameInfoPkey);
            }
            int syncCount = mstBranchMapper.syncTenData(tableNameInfo, tableNameInfoWK, column);
            resultMap.put("result", "true");
            resultMap.put("count", syncCount);
        }catch (Exception e){
            resultMap.put("result", "false");
            resultMap.put("count", "0");
            resultMap.put("error", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return resultMap;
    }
}

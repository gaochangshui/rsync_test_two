package com.trechina.planocycle.service.impl;

import com.google.api.client.util.Strings;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.po.BranchList;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.ClassicPriorityOrderCommodityMustMapper;
import com.trechina.planocycle.mapper.MstBranchMapper;
import com.trechina.planocycle.mapper.SysConfigMapper;
import com.trechina.planocycle.service.MstBranchService;
import com.trechina.planocycle.service.MstCommodityService;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    public Map<String, Object> setBranchInfo(List<BranchList> branchList) {
        String companyCd = "1000";
        String groupCd = "";
        branchList = branchList.stream().filter(map-> !Strings.isNullOrEmpty(map.getBranchCd())).collect(Collectors.toList());
        if ("0".equals(branchList.get(0).getCommonPartsData().getStoreIsCore())) {
            companyCd = branchList.get(0).getCompanyCd();
        }else {
               branchList.forEach(map -> map.setBranchCd(map.getCompanyCd() + "_" + map.getBranchCd()));
            groupCd = branchList.get(0).getGroupCd();

        }
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

    @Transactional
    @Override
    public Map<String, Object> syncTenData() {
        String syncCompanyList = sysConfigMapper.selectSycConfig("sync_company_list");
        String[] companyList = syncCompanyList.split(",");
        String tableNameInfo;
        String masterTenTb;
        String tableNameInfoWK;
        String tableNameHeaderInfo;

        List<LinkedHashMap<String,Object>> tenList;
        String column;
        for (String companyCd : companyList) {
            masterTenTb = MessageFormat.format(MagicString.WK_MASTER_TEN, companyCd);
            List<String> tenClass = mstBranchMapper.getMasterTenClass(masterTenTb);

            for (String classCd : tenClass) {
                tableNameInfo = MessageFormat.format(MagicString.PROD_TEN_INFO, companyCd, classCd);
                tableNameInfoWK = MessageFormat.format(MagicString.WK_PROD_TEN_INFO, companyCd, classCd);
                tableNameHeaderInfo = MessageFormat.format(MagicString.WK_PROD_TEN_INFO_HEADER, companyCd, classCd);

                int i = mstBranchMapper.checkTableExist(tableNameInfoWK.split("\\.")[1], companyCd);
                if(i < 1){
                    logger.info("{} not exist", tableNameInfoWK);
                    continue;
                }

                tenList = mstBranchMapper.getTenHeader(tableNameHeaderInfo);
                column = tenList.stream().map(e->e.get("3").toString()).collect(Collectors.joining(","));

                mstBranchMapper.syncTenData(tableNameInfo, tableNameInfoWK, column);
            }
        }
        return ResultMaps.result(ResultEnum.SUCCESS.getCode(),"同期完了しました");
    }
}

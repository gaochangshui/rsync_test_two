package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.po.BranchList;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.ClassicPriorityOrderCommodityMustMapper;
import com.trechina.planocycle.mapper.MstBranchMapper;
import com.trechina.planocycle.service.MstBranchService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@Service
public class MstBranchServiceImpl implements MstBranchService {
    @Autowired
    private MstBranchMapper mstBranchMapper;
    @Autowired
    private ClassicPriorityOrderCommodityMustMapper classicPriorityOrderCommodityMustMapper;
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

        if ("0".equals(branchList.get(0).getCommonPartsData().getStoreIsCore())) {
            companyCd = branchList.get(0).getCompanyCd();
        }
        String branchInfoTableName = MessageFormat.format("\"{0}\".ten_{1}_ten_info", companyCd,
                branchList.get(0).getCommonPartsData().getStoreMstClass());
        mstBranchMapper.deleteBranch(branchInfoTableName);
        Integer branchSize = mstBranchMapper.getBranchSize(branchInfoTableName, companyCd);
        int diff = branchSize - branchList.get(0).getBranchCd().length();
        for (BranchList list : branchList) {
            StringBuilder branchStr = new StringBuilder();
            if (companyCd.equals("1000")){
                branchStr.append(branchList.get(0).getCompanyCd()+"_");
            }
            for (int l = 0; l < diff; l++) {
                branchStr.append("0");

            }
            branchStr.append(list.getBranchCd());
            list.setBranchCd(branchStr.toString());
        }
        mstBranchMapper.setBranchInfo(branchList,branchInfoTableName);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }


}

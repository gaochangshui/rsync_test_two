package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.BranchList;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
public interface MstBranchService {
    Map<String,Object> getBranchInfo(BranchList branchList);

    @Transactional
    Map<String, Object> syncTenData(String env);
    Map<String, Object> setBranchInfo(List<BranchList> branchList);
}

package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.po.BranchList;

import java.util.List;
import java.util.Map;
public interface MstBranchService {
    Map<String,Object> getBranchInfo(BranchList branchList);

    Map<String, Object> setBranchInfo(List<BranchList> branchList);
}

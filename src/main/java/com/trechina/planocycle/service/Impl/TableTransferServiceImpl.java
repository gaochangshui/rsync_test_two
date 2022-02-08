package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.po.Jans;
import com.trechina.planocycle.mapper.AreasMapper;
import com.trechina.planocycle.mapper.AttributeMapper;
import com.trechina.planocycle.mapper.BranchsMapper;
import com.trechina.planocycle.mapper.JansMapper;
import com.trechina.planocycle.service.TableTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TableTransferServiceImpl implements TableTransferService {
    @Autowired
    private AreasMapper areasMapper;
    @Autowired
    private AttributeMapper attributeMapper;
    @Autowired
    private BranchsMapper branchsMapper;
    @Autowired
    private JansMapper jansMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int getAreasTransfer() {
        try {
            areasMapper.delete();
            int updateResult = areasMapper.updateTransfer();
            return updateResult;
        } catch (Exception e){
            return -1;
        }
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int getBranchsTransfer() {
        try {
            branchsMapper.deleteByPrimaryKey();
            int updateResult = branchsMapper.updateTransfer();
            return updateResult;
        } catch (Exception e) {
            return -1;
        }
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int getJansTransfer() {
        try {
            jansMapper.deleteByPrimaryKey();
            int updateResult = jansMapper.updateTransfer();
            return updateResult;
        } catch (Exception e) {
            return -1;
        }
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int getAttrTransfer() {
        try{
            attributeMapper.deleteByPrimaryKey();
            int updateResule = attributeMapper.updateTransfer();
            return updateResule;
        } catch (Exception e) {
            return -1;
        }
    }
}

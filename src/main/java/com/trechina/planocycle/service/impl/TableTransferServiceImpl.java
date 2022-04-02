package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.exception.BusinessException;
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
            return areasMapper.updateTransfer();
        } catch (Exception e){
            throw new BusinessException("更新失敗");
        }
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int getBranchsTransfer() {
        try {
            branchsMapper.deleteByPrimaryKey();
            return branchsMapper.updateTransfer();
        } catch (Exception e) {
            throw new BusinessException("更新失敗");
        }
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int getJansTransfer() {
        try {
            jansMapper.deleteByPrimaryKey();
            return jansMapper.updateTransfer();
        } catch (Exception e) {
            throw new BusinessException("更新失敗");
        }
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int getAttrTransfer() {
        try{
            attributeMapper.deleteByPrimaryKey();
            return attributeMapper.updateTransfer();
        } catch (Exception e) {
            throw new BusinessException("更新失敗");
        }
    }
}

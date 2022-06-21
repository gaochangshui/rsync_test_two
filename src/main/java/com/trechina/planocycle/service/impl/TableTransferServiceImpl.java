package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.exception.BusinessException;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.TableTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private JanInfoMapper janInfoMapper;
    @Autowired
    private ZokuseiMstMapper zokuseiMstMapper;

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
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int getJanInfoTransfer() {
        List<String> workTableList = janInfoMapper.getSchemaOrTableName();
        for (String table : workTableList) {
            try {
                String[] wks = table.split("_wk");

            System.out.println(wks[0]);
                janInfoMapper.dropFinal(wks[0]+"\"");
                janInfoMapper.setFinalForWork(table,wks[0]+"\"");
            } catch (Exception e) {
                throw new BusinessException(table+"更新失敗");
            }
        }
        return 0;
    }


    public void setZokuseiData(String company,String classCd,Integer zokuseiId,Integer col){
        Integer integer = zokuseiMstMapper.selectExist(company, classCd, col);
        if (integer >0) {
            List<String> list = new ArrayList();
            int a = 9;
            while (a != 1) {
                list.add(a + "");
                a = a - 2;

            }

            list = list.stream().sorted().collect(Collectors.toList());
            zokuseiMstMapper.insertZokuseiData(company, classCd, zokuseiId, col, list);
        }else {
            zokuseiMstMapper.insertZokuseiData1(company, classCd, zokuseiId, col);
        }
    }
}

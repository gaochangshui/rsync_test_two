package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.po.CommoditySyncSet;
import com.trechina.planocycle.exception.BusinessException;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.TableTransferService;
import com.trechina.planocycle.service.ZokuseiMstDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

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
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private MstCommodityMapper mstCommodityMapper;

    @Autowired
    private ZokuseiMstDataService zokuseiMstDataService;

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

                janInfoMapper.dropFinal(wks[0]+"\"");
                janInfoMapper.setFinalForWork(table,wks[0]+"\"");
            } catch (Exception e) {
                throw new BusinessException(table+"更新失敗");
            }
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncZokuseiMst() {
        String syncCompanyList = sysConfigMapper.selectSycConfig("sync_company_list");
        String[] companyList = syncCompanyList.split(",");
        for (String company : companyList) {
            String tableName = MessageFormat.format(MagicString.MASTER_SYOHIN, company);
            List<CommoditySyncSet> commodityList = mstCommodityMapper.getCommodityList(tableName);
            for (CommoditySyncSet commoditySyncSet : commodityList) {
                zokuseiMstDataService.syncZokuseiMstData(company, commoditySyncSet.getProdMstClass());
            }
        }
    }

}

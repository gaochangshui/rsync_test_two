package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.po.CommoditySyncSet;
import com.trechina.planocycle.entity.vo.MstCommodityVO;
import com.trechina.planocycle.mapper.MstCommodityMapper;
import com.trechina.planocycle.mapper.SysConfigMapper;
import com.trechina.planocycle.service.MstCommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
public class MstCommodityServiceImpl implements MstCommodityService {

    @Autowired
    MstCommodityMapper mstCommodityMapper;
    @Autowired
    private SysConfigMapper sysConfigMapper;

    /**
     * 商品マスタ
     * @return
     */
    @Override
    public List<MstCommodityVO> getCommodityList() {
        String tableNameCommodity = MessageFormat.format(MagicString.MASTER_SYOHIN, MagicString.DEFAULT_COMPANY_CD);
        return mstCommodityMapper.getCommodityList(tableNameCommodity);
    }

    /**
     * 同期設定を検索
     * @param companyCd
     * @return
     */
    @Override
    public List<CommoditySyncSet> getSyncSet(String companyCd) {
        String coreCompanycd = sysConfigMapper.selectSycConfig(MagicString.CORE_COMPANY);
        String tableName = MessageFormat.format(MagicString.MASTER_SYOHIN, coreCompanycd);
        String tableNameCompany = MessageFormat.format(MagicString.MASTER_SYOHIN, companyCd);
        return mstCommodityMapper.getSyncSet(tableName,tableNameCompany);
    }
}

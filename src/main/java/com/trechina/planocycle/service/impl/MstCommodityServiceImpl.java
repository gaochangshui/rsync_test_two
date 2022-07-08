package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.vo.MstCommodityVO;
import com.trechina.planocycle.mapper.MstCommodityMapper;
import com.trechina.planocycle.service.MstCommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
public class MstCommodityServiceImpl implements MstCommodityService {

    @Autowired
    MstCommodityMapper mstCommodityMapper;

    /**
     * 商品マスタ
     * @return
     */
    @Override
    public List<MstCommodityVO> getCommodityList() {
        String tableNameCommodity = MessageFormat.format("\"{0}\".master_syohin", MagicString.DEFAULT_COMPANY_CD);
        return mstCommodityMapper.getCommodityList(tableNameCommodity);
    }
}

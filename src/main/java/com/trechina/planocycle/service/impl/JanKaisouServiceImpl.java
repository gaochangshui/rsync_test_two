package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.ProdKaisouHeaderMapper;
import com.trechina.planocycle.mapper.SysConfigMapper;
import com.trechina.planocycle.service.JanKaisouService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JanKaisouServiceImpl implements JanKaisouService {
    @Autowired
    private ProdKaisouHeaderMapper prodKaisouHeaderMapper;
    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Override
    public Map<String, Object> saveProductItem(Map<String,Object> map){


        //String limitCount = sysConfigMapper.selectSycConfig(MagicString.ZOKUSEI_COUNT);
        //int maxCol = prodKaisouHeaderMapper.selectMaxByCol(MagicString.COLUMN_INDEX_JANINFO_COLUMN);
        //if(maxCol>Integer.parseInt(limitCount)){
        //    return ResultMaps.result(ResultEnum.KAISOU_COUNT_LIMIT);
        //}
        //
        //int kaisouMaxCol = prodKaisouHeaderMapper.selectMaxByCol(MagicString.COLUMN_INDEX_KAISOU_COLUMN);
        //int kaisouCount = prodKaisouHeaderMapper.selectItemName(productItemVO.getItemName());
        //if(kaisouCount>0){
        //    return ResultMaps.result(ResultEnum.KAISOU_COUNT_LIMIT);
        //}
        //productItemVO.setItemCd("c"+maxCol);
        //int insert = prodKaisouHeaderMapper.insert(productItemVO, maxCol + "", kaisouMaxCol + "");
        //if (insert<1) {
        //    return this.saveProductItem(productItemVO);
        //}
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
}

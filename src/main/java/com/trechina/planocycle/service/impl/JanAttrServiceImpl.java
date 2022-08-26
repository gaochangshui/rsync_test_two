package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.vo.ProductItemVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.MstJanMapper;
import com.trechina.planocycle.mapper.ProdKaisouHeaderMapper;
import com.trechina.planocycle.mapper.ZokuseiMstMapper;
import com.trechina.planocycle.service.JanAttrService;
import com.trechina.planocycle.utils.ResultMaps;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JanAttrServiceImpl implements JanAttrService {
    @Autowired
    private ProdKaisouHeaderMapper prodKaisouHeaderMapper;
    @Autowired
    private MstJanMapper mstJanMapper;
    @Autowired
    private ZokuseiMstMapper zokuseiMstMapper;

    @Override
    public Map<String, Object> saveProductItem(ProductItemVO productItemVO){

        String tableNameAttr = MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", "9999", "0000");
        String nameExist = mstJanMapper.getNameExist(productItemVO.getName(), tableNameAttr);
        if (nameExist!= null){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        List<Map<String, Object>> planItem = prodKaisouHeaderMapper.getPlanItem(tableNameAttr);
        List<String> colNameList = planItem.stream().map(map->map.get(MagicString.COLUMN_INDEX_JANINFO_COLUMN).toString()).collect(Collectors.toList());
        String itemSort = planItem.stream().mapToInt(value -> MapUtils.getInteger(value,MagicString.COLUMN_INDEX_KAISOU_COLUMN)).max().orElse(200)+1 +"";

        String itemColName = "";
        for (Integer i = MagicString.PLAN_START; i <= MagicString.PLAN_END; i++) {
            if (!colNameList.contains(i+"")){
                itemColName = i+"";
                break;
            }
        }
        LinkedHashMap<String,Object> map = new LinkedHashMap<>();
        map.put("1","plano"+itemColName);
        map.put("2",productItemVO.getName());
        map.put(MagicString.COLUMN_INDEX_JANINFO_COLUMN,itemColName);
        map.put(MagicString.COLUMN_INDEX_KAISOU_COLUMN,itemSort);
        map.put("11","6");
        if ("number".equals(productItemVO.getType())){
            map.put("13","0");
        }else if ("string".equals(productItemVO.getType())){
            map.put("13","1");
        }
        prodKaisouHeaderMapper.setItem(map,tableNameAttr);
        zokuseiMstMapper.setItem(itemColName,Integer.valueOf(itemColName),productItemVO.getName());
        return ResultMaps.result(ResultEnum.SUCCESS,map.get("1"));
    }

    @Override
    public Map<String, Object> delProductItem(ProductItemVO productItemVO) {
        String tableNameAttr = MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", "9999", "0000");
        String janInfoTableName = MessageFormat.format("\"{0}\".prod_{1}_jan_info", "9999", "0000");
        String colName = prodKaisouHeaderMapper.getItem(productItemVO.getValue(), tableNameAttr);
        prodKaisouHeaderMapper.delItem(productItemVO.getValue(),tableNameAttr);
        if (colName != null) {
            mstJanMapper.clearCol(colName, janInfoTableName);
        }

        zokuseiMstMapper.delZokuseiMstForId(Integer.valueOf(colName));
        zokuseiMstMapper.delZokuseiMstDataForId(Integer.valueOf(colName));
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public Map<String, Object> updateAttrInfo(ProductItemVO productItemVO) {
        String tableNameAttr = MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", "9999", "0000");
        String nameExist = mstJanMapper.getNameExist(productItemVO.getName(), tableNameAttr);
        if (nameExist != null && !nameExist.equals(productItemVO.getValue())){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        String type = "";
        if ("number".equals(productItemVO.getType())){
            type ="0";
        }else if ("string".equals(productItemVO.getType())){
            type ="1";
        }
        prodKaisouHeaderMapper.updateName(productItemVO,tableNameAttr,type);
        String zokuseiCol = prodKaisouHeaderMapper.getItem(productItemVO.getValue(), tableNameAttr);
        zokuseiMstMapper.updateZokuseiName(productItemVO,zokuseiCol);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }


}

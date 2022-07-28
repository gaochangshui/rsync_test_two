package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.vo.ProductItemVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.MstJanMapper;
import com.trechina.planocycle.mapper.ProdKaisouHeaderMapper;
import com.trechina.planocycle.mapper.SysConfigMapper;
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
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private MstJanMapper mstJanMapper;

    @Override
    public Map<String, Object> saveProductItem(ProductItemVO productItemVO){
        String companyCd = "1000";
        String classCd = productItemVO.getCommonPartsData().getProdMstClass();
        if ("0".equals(productItemVO.getCommonPartsData().getProdIsCore())) {
            companyCd = productItemVO.getCompanyCd();
        }
        String tableNameAttr = MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", companyCd, classCd);
        String tableNameKaisou = MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou_header_sys", companyCd, classCd);
        String janInfoTableName = MessageFormat.format("\"{0}\".prod_{1}_jan_info", companyCd,classCd);
        List<Map<String, Object>> planItem = prodKaisouHeaderMapper.getPlanItem(tableNameAttr);
        List<String> colNameList = planItem.stream().map(map->map.get(MagicString.COLUMN_INDEX_JANINFO_COLUMN).toString()).collect(Collectors.toList());
        String itemSort = planItem.stream().mapToInt(value -> MapUtils.getInteger(value,MagicString.COLUMN_INDEX_KAISOU_COLUMN)).max().orElse(111) +"";

        String itemColName = "";
        for (Integer i = MagicString.PLAN_START; i <= MagicString.PLAN_END; i++) {
            if (!colNameList.contains(i+"")){
                itemColName = i+"";
                break;
            }
        }
        LinkedHashMap<String,Object> map = new LinkedHashMap<>();
        map.put("1","");
        map.put("2",productItemVO.getName());
        map.put(MagicString.COLUMN_INDEX_JANINFO_COLUMN,itemColName);
        map.put(MagicString.COLUMN_INDEX_KAISOU_COLUMN,itemColName);
        map.put("8","6");
        map.put("9","");
        if ("number".equals(productItemVO.getType())){
            map.put("10","0");
        }else if ("string".equals(productItemVO.getType())){
            map.put("10","1");
        }
        prodKaisouHeaderMapper.setItem(map,tableNameKaisou);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public Map<String, Object> delProductItem(ProductItemVO productItemVO) {
        String companyCd = "1000";
        String classCd = productItemVO.getCommonPartsData().getProdMstClass();
        if ("0".equals(productItemVO.getCommonPartsData().getProdIsCore())) {
            companyCd = productItemVO.getCompanyCd();
        }
        String tableNameAttr = MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", companyCd, classCd);
        String janInfoTableName = MessageFormat.format("\"{0}\".prod_{1}_jan_info", companyCd,classCd);
        prodKaisouHeaderMapper.delItem(productItemVO.getValue(),tableNameAttr);

        return null;
    }


}

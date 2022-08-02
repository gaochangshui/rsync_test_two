package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.vo.ProductItemVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.MstJanMapper;
import com.trechina.planocycle.mapper.ProdKaisouHeaderMapper;
import com.trechina.planocycle.mapper.SysConfigMapper;
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
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private MstJanMapper mstJanMapper;
    @Autowired
    private ZokuseiMstMapper zokuseiMstMapper;

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
        String nameExist = mstJanMapper.getNameExist(productItemVO.getName(), tableNameAttr);
        if (nameExist!= null){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        List<Map<String, Object>> planItem = prodKaisouHeaderMapper.getPlanItem(tableNameAttr);
        List<String> colNameList = planItem.stream().map(map->map.get(MagicString.COLUMN_INDEX_JANINFO_COLUMN).toString()).collect(Collectors.toList());
        String itemSort = planItem.stream().mapToInt(value -> MapUtils.getInteger(value,MagicString.COLUMN_INDEX_KAISOU_COLUMN)).max().orElse(201) +"";

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
        map.put("12","");
        if ("number".equals(productItemVO.getType())){
            map.put("13","0");
        }else if ("string".equals(productItemVO.getType())){
            map.put("13","1");
        }
        prodKaisouHeaderMapper.setItem(map,tableNameAttr);
        Integer maxZokuseiId = zokuseiMstMapper.getMaxZokuseiId(companyCd, classCd);
        zokuseiMstMapper.setItem(itemColName,maxZokuseiId+1,companyCd,classCd,productItemVO.getName());
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public Map<String, Object> delProductItem(ProductItemVO productItemVO) {
        String companyCd = MagicString.SELF_SERVICE;
        String classCd = productItemVO.getCommonPartsData().getProdMstClass();
        if ("0".equals(productItemVO.getCommonPartsData().getProdIsCore())) {
            companyCd = productItemVO.getCompanyCd();
        }
        String tableNameAttr = MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", companyCd, classCd);
        String janInfoTableName = MessageFormat.format("\"{0}\".prod_{1}_jan_info", companyCd,classCd);
        String colName = prodKaisouHeaderMapper.getItem(productItemVO.getValue(), tableNameAttr);
        prodKaisouHeaderMapper.delItem(productItemVO.getValue(),tableNameAttr);
        if (colName != null) {
            mstJanMapper.clearCol(colName, janInfoTableName);
        }

        Integer zokuseiIdForCol = zokuseiMstMapper.getZokuseiIdForCol(colName, companyCd, classCd);
        zokuseiMstMapper.delZokuseiMstForId(classCd, companyCd,zokuseiIdForCol);
        zokuseiMstMapper.delZokuseiMstDataForId(classCd, companyCd,zokuseiIdForCol);
        List<Map<String, Object>> zokuseiId = prodKaisouHeaderMapper.getZokuseiId(companyCd,classCd);
        zokuseiMstMapper.updateZokuseiMstData(zokuseiId,companyCd,classCd);
        zokuseiMstMapper.updateZokuseiMst(zokuseiId,companyCd,classCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public Map<String, Object> updateAttrInfo(ProductItemVO productItemVO) {
        String companyCd = MagicString.SELF_SERVICE;
        String classCd = productItemVO.getCommonPartsData().getProdMstClass();
        if ("0".equals(productItemVO.getCommonPartsData().getProdIsCore())) {
            companyCd = productItemVO.getCompanyCd();
        }
        String tableNameAttr = MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", companyCd, classCd);
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
        zokuseiMstMapper.updateZokuseiName(productItemVO,companyCd,classCd,zokuseiCol);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }


}

package com.trechina.planocycle.service.impl;

import com.google.common.base.Strings;
import com.trechina.planocycle.entity.po.Zokusei;
import com.trechina.planocycle.mapper.ZokuseiMstMapper;
import com.trechina.planocycle.service.ZokuseiMstDataService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ZokuseiMstDataServiceImpl implements ZokuseiMstDataService {
    @Autowired
    private ZokuseiMstMapper zokuseiMstMapper;
    @Autowired
    private ZokuseiMstDataService zokuseiMstDataService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void syncZokuseiMstData(String companyCd, String classCd) {
        List<String> kaisouTableNameList = zokuseiMstMapper.selectAllKaisouTable(companyCd);

        int colIndex = 1;
        for (String kaisouTbName : kaisouTableNameList) {
            colIndex = 1;
            String syncClassCd = Strings.isNullOrEmpty(classCd)?kaisouTbName.split("_")[1]:classCd;
            kaisouTbName = "\""+companyCd+"\"."+kaisouTbName;

            zokuseiMstMapper.deleteData(companyCd, syncClassCd);
            List<Map<String, Object>> headerMap = zokuseiMstMapper.selectHeader(kaisouTbName);
            List<Zokusei> zokuseiList = new ArrayList<>();
            for (Map<String, Object> header : headerMap) {
                String col = MapUtils.getString(header, "col");
                if(col.endsWith("_cd")){
                    continue;
                }
                List<Map<String, Object>> collect = headerMap.stream()
                        .filter(map -> MapUtils.getString(map, "col").equals(col.split("_")[0]+"_cd"))
                        .collect(Collectors.toList());
                if(!collect.isEmpty()){
                    Map<String, Object> cdMap = collect.get(0);
                    Integer colI = MapUtils.getInteger(cdMap, "sort");

                    Zokusei zokusei = new Zokusei();
                    zokusei.setZokuseiId(colIndex);
                    zokusei.setZokuseiNm(MapUtils.getString(header, "name"));
                    zokusei.setCompanyCd(companyCd);
                    zokusei.setClassCd(syncClassCd);
                    zokusei.setType(MapUtils.getInteger(header, "type"));
                    zokusei.setZokuseiSort(MapUtils.getInteger(header, "sort"));
                    zokusei.setZokuseiCol(colI);

                    zokuseiList.add(zokusei);
                    zokuseiMstDataService.setZokuseiData(companyCd, syncClassCd, zokusei.getZokuseiId(), colI, headerMap);
                }

                colIndex++;
            }
            zokuseiMstMapper.delete(companyCd, syncClassCd);
            zokuseiMstMapper.insertBatch(companyCd, syncClassCd, zokuseiList);

            String attrTbName = "\""+companyCd+"\".prod_"+syncClassCd+"_jan_attr_header_sys";
            List<Map<String, Object>> attrHeaderMap = zokuseiMstMapper.selectHeader(attrTbName);

            List<Zokusei> attrZokuseiList = new ArrayList<>();
            for (Map<String, Object> header : attrHeaderMap) {
                Zokusei zokusei = new Zokusei();
                Integer colI = MapUtils.getInteger(header, "sort");
                zokusei.setZokuseiId(colIndex);
                zokusei.setZokuseiNm(MapUtils.getString(header, "name"));
                zokusei.setCompanyCd(companyCd);
                zokusei.setClassCd(syncClassCd);
                zokusei.setType(MapUtils.getInteger(header, "type"));
                zokusei.setZokuseiSort(MapUtils.getInteger(header, "sort"));
                zokusei.setZokuseiCol(colI);

                attrZokuseiList.add(zokusei);
                zokuseiMstDataService.setZokuseiData(companyCd, syncClassCd, zokusei.getZokuseiCol(), colI, attrHeaderMap);
                colIndex++;
            }
            zokuseiMstMapper.insertBatch(companyCd, syncClassCd, attrZokuseiList);
        }
    }

    @Override
    public void setZokuseiData(String company, String classCd, Integer zokuseiId, Integer col, List<Map<String, Object>> headerMap){
        List<Integer> cdList = headerMap.stream().filter(map -> MapUtils.getString(map, "col").endsWith("_cd"))
                .map(map->MapUtils.getInteger(map, "sort")).collect(Collectors.toList());


        if(cdList.isEmpty()){
            zokuseiMstMapper.insertZokuseiData1(company, classCd, col, col);
        }else {
            zokuseiMstMapper.insertZokuseiData(company, classCd, col, col, cdList);
        }
    }
}

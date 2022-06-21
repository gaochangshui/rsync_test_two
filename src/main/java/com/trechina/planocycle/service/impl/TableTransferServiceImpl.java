package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.po.Zokusei;
import com.trechina.planocycle.exception.BusinessException;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.TableTransferService;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private ZokuseiMstMapper zokuseiMstMapper;
    private Logger logger = LoggerFactory.getLogger(TableTransferServiceImpl.class);

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


    public void setZokuseiData(String company,String classCd,Integer zokuseiId,Integer col, List<Map<String, Object>> headerMap){
        List<Integer> cdList = headerMap.stream().filter(map -> MapUtils.getString(map, "col").endsWith("_cd"))
                .map(map->MapUtils.getInteger(map, "sort")).collect(Collectors.toList());
        logger.info("{}",cdList);

        if(cdList.isEmpty()){
            zokuseiMstMapper.insertZokuseiData1(company, classCd, zokuseiId, col);
        }else {
            zokuseiMstMapper.insertZokuseiData(company, classCd, zokuseiId, col, cdList);
        }
    }

    @Override
    @PostConstruct
    @Transactional(rollbackFor = Exception.class)
    public void syncZokuseiMst() {
        String syncCompanyList = sysConfigMapper.selectSycConfig("sync_company_list");
        String[] companyList = syncCompanyList.split(",");
        for (String company : companyList) {
            List<String> kaisouTableNameList = zokuseiMstMapper.selectAllKaisouTable(company);

            int colIndex = 1;
            for (String kaisouTbName : kaisouTableNameList) {
                colIndex = 1;
                String classCd = kaisouTbName.split("_")[1];
                kaisouTbName = "\""+company+"\"."+kaisouTbName;

                zokuseiMstMapper.deleteData(company, classCd);
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
                        zokusei.setCompanyCd(company);
                        zokusei.setClassCd(classCd);
                        zokusei.setType(MapUtils.getInteger(header, "type"));
                        zokusei.setZokuseiSort(MapUtils.getInteger(header, "sort"));
                        zokusei.setZokuseiCol(colI);

                        zokuseiList.add(zokusei);
                        this.setZokuseiData(company, classCd, zokusei.getZokuseiId(), colI, headerMap);
                    }

                    colIndex++;
                }
                zokuseiMstMapper.delete(company, classCd);
                zokuseiMstMapper.insertBatch(company, classCd, zokuseiList);

                String attrTbName = "\""+company+"\".prod_"+classCd+"_jan_attr_header_sys";
                List<Map<String, Object>> attrHeaderMap = zokuseiMstMapper.selectHeader(attrTbName);

                List<Zokusei> attrZokuseiList = new ArrayList<>();
                for (Map<String, Object> header : attrHeaderMap) {
                    Zokusei zokusei = new Zokusei();
                    Integer colI = MapUtils.getInteger(header, "sort");
                    zokusei.setZokuseiId(colIndex);
                    zokusei.setZokuseiNm(MapUtils.getString(header, "name"));
                    zokusei.setCompanyCd(company);
                    zokusei.setClassCd(classCd);
                    zokusei.setType(MapUtils.getInteger(header, "type"));
                    zokusei.setZokuseiSort(MapUtils.getInteger(header, "sort"));
                    zokusei.setZokuseiCol(colI);

                    attrZokuseiList.add(zokusei);
                    this.setZokuseiData(company, classCd, zokusei.getZokuseiId(), colI, attrHeaderMap);
                    colIndex++;
                }
                zokuseiMstMapper.insertBatch(company, classCd, attrZokuseiList);
            }
        }
    }
}

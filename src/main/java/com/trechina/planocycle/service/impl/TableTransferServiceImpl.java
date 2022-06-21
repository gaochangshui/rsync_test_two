package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.po.Zokusei;
import com.trechina.planocycle.exception.BusinessException;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.TableTransferService;
import org.apache.commons.collections4.MapUtils;
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
                        zokuseiMstMapper.delete(company, classCd);
                    }

                    colIndex++;
                }
                zokuseiMstMapper.insertBatch(company, classCd, zokuseiList);

                String attrTbName = "\""+company+"\".prod_"+classCd+"_jan_attr_header_sys";
                List<Map<String, Object>> attrHeaderMap = zokuseiMstMapper.selectHeader(attrTbName);

                List<Zokusei> attrZokuseiList = new ArrayList<>();
                for (Map<String, Object> header : attrHeaderMap) {
                    Zokusei zokusei = new Zokusei();
                    zokusei.setZokuseiId(colIndex);
                    zokusei.setZokuseiNm(MapUtils.getString(header, "name"));
                    zokusei.setCompanyCd(company);
                    zokusei.setClassCd(classCd);
                    zokusei.setType(MapUtils.getInteger(header, "type"));
                    zokusei.setZokuseiSort(MapUtils.getInteger(header, "sort"));
                    zokusei.setZokuseiCol(MapUtils.getInteger(header, "sort"));

                    attrZokuseiList.add(zokusei);
                    colIndex++;
                }
                zokuseiMstMapper.insertBatch(company, classCd, attrZokuseiList);
            }
        }
    }
}

package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.po.BasicPatternRestrictResult;
import com.trechina.planocycle.entity.po.ShelfPtsDataTanamst;
import com.trechina.planocycle.entity.po.ZokuseiMst;
import com.trechina.planocycle.entity.vo.BasicPatternAutoDetectVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.BasicPatternMstService;
import com.trechina.planocycle.utils.ResultMaps;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BasicPatternMstServiceImpl implements BasicPatternMstService {
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private ZokuseiMapper zokuseiMapper;
    @Autowired
    private JanInfoMapper janInfoMapper;
    @Autowired
    private HttpSession session;
    @Autowired
    private ShelfPtsDataTanamstMapper shelfPtsDataTanamst;
    @Autowired
    private BasicPatternRestrictResultMapper restrictResultMapper;
    @Autowired
    private BasicPatternRestrictRelationMapper restrictRelationMapper;
    @Autowired
    private BasicPatternAttrMapper basicMapperMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> autoDetect(BasicPatternAutoDetectVO autoDetectVO) {
        Integer shelfPatternCd = autoDetectVO.getShelfPatternCd();
        Integer priorityOrderCd = autoDetectVO.getPriorityOrderCd();
        String companyCd = autoDetectVO.getCompanyCd();
        String commonPartsData = autoDetectVO.getCommonPartsData();
        GetCommonPartsDataDto commonTableName = getCommonTableName(commonPartsData, companyCd);
        String classCd = commonTableName.getProdMstClass();
        String zokuseiIds = autoDetectVO.getAttrList();
        List<Integer> list = Arrays.asList(zokuseiIds.split(",")).stream().map(Integer::parseInt).collect(Collectors.toList());

        List<Integer> cdList = zokuseiMapper.selectCdHeader(commonTableName.getProKaisouTable(), commonTableName.getProAttrTable());
        List<ZokuseiMst> zokuseiMsts = zokuseiMapper.selectZokusei(companyCd,
                classCd, zokuseiIds);
        priorityOrderMstAttrSortMapper.deleteAttrList(companyCd,priorityOrderCd);
        priorityOrderMstAttrSortMapper.setAttrList(companyCd,priorityOrderCd,list);
        List<ShelfPtsDataTanamst> tanamsts = shelfPtsDataTanamst.selectByPatternCd((long) shelfPatternCd);
        List<Map<String, Object>> classifyList = janInfoMapper.selectJanClassify(commonTableName.getProInfoTable(), shelfPatternCd, zokuseiMsts, cdList);
        String aud = session.getAttribute("aud").toString();

        basicMapperMapper.delete(priorityOrderCd, companyCd);

        Map<String, BasicPatternRestrictResult> classify = getJanInfoClassify(classifyList, companyCd,
                zokuseiIds, aud, (long) autoDetectVO.getPriorityOrderCd());
        restrictResultMapper.deleteByPriorityCd(autoDetectVO.getPriorityOrderCd(), companyCd);
        long restrictCd = 1;
        for (Map.Entry<String, BasicPatternRestrictResult> entry : classify.entrySet()) {
            BasicPatternRestrictResult value = entry.getValue();
            value.setRestrictCd(restrictCd);
            classify.put(entry.getKey(), value);
            restrictCd++;
        }
        List<BasicPatternRestrictResult> basicPatternRestrictResults = new ArrayList<>(classify.values());
        restrictResultMapper.insertBatch(basicPatternRestrictResults);
        basicMapperMapper.insertBatch(basicPatternRestrictResults);

        ArrayList<String> zokuseiList = Lists.newArrayList(zokuseiIds.split(","));

        restrictRelationMapper.deleteByPrimaryKey(priorityOrderCd, companyCd);
        for (ShelfPtsDataTanamst tanamst : tanamsts) {
            Integer taiCd = tanamst.getTaiCd();
            Integer tanaCd = tanamst.getTanaCd();
            Integer tanaWidth = tanamst.getTanaWidth();

            List<Map<String, Object>> jans = classifyList.stream().filter(map -> MapUtils.getInteger(map, MagicString.TAI_CD).equals(taiCd) &&
                    MapUtils.getInteger(map, MagicString.TANA_CD).equals(tanaCd)).collect(Collectors.toList());

            for (int i = 0; i < jans.size(); i++) {
                Map<String, Object> janMap = jans.get(i);
                double width = MapUtils.getDouble(janMap, "width");
                int percent = BigDecimal.valueOf(width).divide(BigDecimal.valueOf(tanaWidth), BigDecimal.ROUND_UP)
                        .multiply(BigDecimal.valueOf(100)).setScale(0, BigDecimal.ROUND_UP).intValue();
                janMap.put("area", percent);
                janMap.put("priorityOrderCd", priorityOrderCd);
                janMap.put("companyCd", companyCd);
                janMap.put("authorCd", aud);

                StringBuilder key = new StringBuilder();
                for (String zokusei : zokuseiList) {
                    if(key.length()>0){
                        key.append(",");
                    }
                    key.append(MapUtils.getString(janMap, zokusei));
                }

                BasicPatternRestrictResult basicPatternRestrictResult = classify.get(key.toString());
                janMap.put("restrictCd", basicPatternRestrictResult.getRestrictCd());

                jans.set(i, janMap);
            }

            final int[] index = {1};
            Comparator<Map<String, Object>> area = Comparator.comparing(map->MapUtils.getInteger(map, "area"));
            jans.stream().sorted(area.reversed()).forEach(map->{
                map.put("tanaPosition", index[0]);
                index[0]++;
            });
            restrictRelationMapper.insertBatch(jans);
        }

        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    private Map<String, BasicPatternRestrictResult> getJanInfoClassify(List<Map<String, Object>> classifyList,
            String companyCd, String zokuseiIds, String authorCd, Long priorityOrderCd){
        Map<String, BasicPatternRestrictResult> classify = new HashMap<>();

        ArrayList<String> zokuseiList = Lists.newArrayList(zokuseiIds.split(","));
        classifyList.stream().forEach(map->{
            StringBuilder classifyId = new StringBuilder();
            for (String id : zokuseiList) {
                if(classifyId.length()>0){
                    classifyId.append(",");
                }
                classifyId.append(MapUtils.getString(map, id));
            }

            if (!classify.containsKey(classifyId.toString())) {
                BasicPatternRestrictResult result = new BasicPatternRestrictResult();
                for (String zokusei : zokuseiList) {
                    Method method = null;
                    try {
                        method = result.getClass().getMethod("setZokusei" + zokusei, String.class);
                        method.invoke(result, MapUtils.getString(map, zokusei));
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                result.setCompanyCd(companyCd);
                result.setAuthorCd(authorCd);
                result.setPriorityOrderCd(priorityOrderCd);

                classify.put(classifyId.toString(), result);
            }
        });

        return classify;
    }

    public GetCommonPartsDataDto getCommonTableName(String commonPartsData, String companyCd ) {
        //{"dateIsCore":"1","storeLevel":"3","storeIsCore":"1","storeMstClass":"0000","prodIsCore":"1","prodMstClass":"0000"}

        JSONObject jsonObject = JSONObject.parseObject(commonPartsData);
        String prodMstClass = jsonObject.get("prodMstClass").toString();
        String prodIsCore = jsonObject.get("prodIsCore").toString();

        GetCommonPartsDataDto getCommonPartsDataDto = new GetCommonPartsDataDto();
        getCommonPartsDataDto.setProdMstClass(prodMstClass);

        String coreCompany = sysConfigMapper.selectSycConfig("core_company");
        String isCompanyCd = null;
        if ("1".equals(prodIsCore)) {
            isCompanyCd = coreCompany;
        } else {
            isCompanyCd = companyCd;
        }
        getCommonPartsDataDto.setProdIsCore(isCompanyCd);
        //if (jsonObject.get("storeIsCore").toString() !=null) {
        //    String storeIsCore = jsonObject.get("storeIsCore").toString();
        //    String storeMstClass = jsonObject.get("storeMstClass").toString();
        //    String storeIsCompanyCd = null;
        //    if ("1".equals(storeIsCore)) {
        //        storeIsCompanyCd = coreCompany;
        //    } else {
        //        storeIsCompanyCd = companyCd;
        //    }
        //    getCommonPartsDataDto.setStoreInfoTable(MessageFormat.format("\"{0}\".ten_{1}_ten_info", storeIsCompanyCd, storeMstClass));
        //    getCommonPartsDataDto.setStoreKaisouTable(MessageFormat.format("\"{0}\".ten_{1}_ten_kaisou_header_sys", storeIsCompanyCd, storeMstClass));
        //}

        getCommonPartsDataDto.setProKaisouTable(MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou_header_sys", isCompanyCd, prodMstClass));
        getCommonPartsDataDto.setProAttrTable(MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", isCompanyCd, prodMstClass));
        getCommonPartsDataDto.setProInfoTable(MessageFormat.format("\"{0}\".prod_{1}_jan_info", isCompanyCd, prodMstClass));
        getCommonPartsDataDto.setProZokuseiDataTable(MessageFormat.format("\"{0}\".zokusei_{1}_mst_data", isCompanyCd, prodMstClass));
        getCommonPartsDataDto.setProZokuseiMstTable(MessageFormat.format("\"{0}\".zokusei_{1}_mst", isCompanyCd, prodMstClass));

        return getCommonPartsDataDto;
    }
}

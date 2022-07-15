package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.po.JanHeaderAttr;
import com.trechina.planocycle.entity.vo.JanInfoVO;
import com.trechina.planocycle.entity.vo.JanParamVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.entity.po.JanInfoList;
import com.trechina.planocycle.mapper.MstJanMapper;
import com.trechina.planocycle.mapper.SysConfigMapper;
import com.trechina.planocycle.service.MstJanService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.dataConverUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MstJanServiceImpl implements MstJanService {

    @Autowired
    MstJanMapper mstJanMapper;
    @Autowired
    private SysConfigMapper sysConfigMapper;

    /**
     * janデータの取得
     * @param janParamVO 検索条件
     * commonPartsData 商品轴信息
     *     prodIsCore    0企业1自社
     *     prodMstClass  商品轴ID
     *     shelfMstClass 棚割専用轴ID，有值代表使用 棚割専用 9999 数据，无值使用 大本マスタ各个企业自己 数据
     *     janContain    包含的商品
     *     janKato       排除的商品
     *     fuzzyQuery    模糊查询，查询商品名
     * @return
     */
    @Override
    public JanInfoVO getJanList(JanParamVO janParamVO) {
        JanInfoVO janInfoVO = new JanInfoVO();
        //３類のパラメーター
        if (StringUtils.hasLength(janParamVO.getCommonPartsData().getShelfMstClass())) {
            janParamVO.setCompanyCd(MagicString.DEFAULT_COMPANY_CD);
            janParamVO.getCommonPartsData().setProdMstClass(janParamVO.getCommonPartsData().getShelfMstClass());
        }else if("1".equals(janParamVO.getCommonPartsData().getProdIsCore())){
            janParamVO.setCompanyCd(sysConfigMapper.selectSycConfig("core_company"));
        }
        String tableNameAttr = MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", janParamVO.getCompanyCd(),
                janParamVO.getCommonPartsData().getProdMstClass());
        String janInfoTableName = MessageFormat.format("\"{0}\".prod_{1}_jan_info", janParamVO.getCompanyCd(),
                janParamVO.getCommonPartsData().getProdMstClass());
        List<JanHeaderAttr> janHeader = mstJanMapper.getJanHeader(tableNameAttr);
        //SQL文の列： "\"1\" \"jan_cd\",\"2\" \"jan_name\",\"21\" \"kikaku\",\"22\" \"maker\",\"23\"
        String column = janHeader.stream().map(map -> "COALESCE(\"" + map.getSort() + "\",'') AS \"" + dataConverUtils.camelize(map.getAttr()) + "\"")
                .collect(Collectors.joining(","));
        janInfoVO.setJanHeader(janHeader.stream().map(map -> String.valueOf(map.getAttrVal()))
                .collect(Collectors.joining(",")));
        janInfoVO.setJanColumn(janHeader.stream().map(map -> String.valueOf(dataConverUtils.camelize(map.getAttr())))
                .collect(Collectors.joining(",")));
        janInfoVO.setTotal(mstJanMapper.getJanCount(janParamVO, janInfoTableName, "count(\"1\")"));
        Integer pageCount = (janParamVO.getPage() - 1) * janParamVO.getPageSize();
        janInfoVO.setJanDataList(mstJanMapper.getJanList(janParamVO, janInfoTableName,
                column, janParamVO.getPageSize(),pageCount));
        return janInfoVO;
    }

    @Override
    public Map<String, Object> getJanListInfo(JanInfoList janInfoList) {
        String companyCd = MagicString.DEFAULT_COMPANY_CD;

        String tableNameAttr ="";
        String janInfoTableName ="";
        String tableNameKaisou ="";
        if (StringUtils.hasLength(janInfoList.getCommonPartsData().getShelfMstClass())) {

             tableNameAttr = MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", companyCd,
                    janInfoList.getCommonPartsData().getShelfMstClass());
            tableNameKaisou = MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou_header_sys", companyCd,
                    janInfoList.getCommonPartsData().getShelfMstClass());
             janInfoTableName = MessageFormat.format("\"{0}\".prod_{1}_jan_info", companyCd,
                    janInfoList.getCommonPartsData().getShelfMstClass());
        }else {
            companyCd = janInfoList.getCompanyCd();
             tableNameAttr = MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", companyCd,
                    janInfoList.getCommonPartsData().getProdMstClass());
            tableNameKaisou = MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou_header_sys", companyCd,
                    janInfoList.getCommonPartsData().getProdMstClass());
             janInfoTableName = MessageFormat.format("\"{0}\".prod_{1}_jan_info", companyCd,
                    janInfoList.getCommonPartsData().getProdMstClass());
        }
        LinkedHashMap<String, Object> janInfoList1 = mstJanMapper.getJanInfoList(janInfoTableName, janInfoList.getJan());
        List<LinkedHashMap<String,Object>> janAttrList = mstJanMapper.getJanAttrList(tableNameAttr);

        List<LinkedHashMap<String,Object>> janKaisouList = mstJanMapper.getJanKaisouList(tableNameKaisou);
        List<LinkedHashMap<String,Object>> janAttrGroup1 = janAttrList.stream().filter(map->map.get("9").equals("0")).collect(Collectors.toList());
        List<LinkedHashMap<String,Object>> janAttrGroup2 = janAttrList.stream().filter(map->map.get("9").equals("1")).collect(Collectors.toList());
        Map<String,Object> janInfoMap = new HashMap<>();

        janInfoMap.put(MagicString.JAN,janInfoList1.get("1"));
       janInfoMap.put(MagicString.JAN_NAME,janInfoList1.get("2"));

        Map<String,Object> janInfo = new HashMap<>();

       List janClass = new ArrayList();

        for (LinkedHashMap<String, Object> stringObjectLinkedHashMap : janKaisouList) {
            Map<String,Object> janKaisouInfo = new HashMap<>();
            janKaisouInfo.put("name",stringObjectLinkedHashMap.get("2"));
            janKaisouInfo.put("id",janInfoList1.get((Integer.valueOf(stringObjectLinkedHashMap.get("3").toString())-1)+""));
            janKaisouInfo.put("title",janInfoList1.get(stringObjectLinkedHashMap.get("3")));
            janKaisouInfo.put("pid","zokusei"+stringObjectLinkedHashMap.get("3"));
            janKaisouInfo.put("isRequired",stringObjectLinkedHashMap.get("7"));

            janClass.add(janKaisouInfo);

        }
        janInfo.put("janClass",janClass);
        List janAttr = new ArrayList();
        List attrGroup1 = new ArrayList();

        for (LinkedHashMap<String, Object> stringObjectLinkedHashMap : janAttrGroup1) {
            Map<String,Object> janAttrInfo = new HashMap<>();
            janAttrInfo.put("name",stringObjectLinkedHashMap.get("2"));
            janAttrInfo.put("title",janInfoList1.get(stringObjectLinkedHashMap.get("3")));
            janAttrInfo.put("id",janInfoList1.get(stringObjectLinkedHashMap.get("3")));
            janAttrInfo.put("pid","zokusei"+stringObjectLinkedHashMap.get("3"));
            janAttrInfo.put("isRequired",stringObjectLinkedHashMap.get("7"));
            janAttrInfo.put("type",stringObjectLinkedHashMap.get("8"));
            attrGroup1.add(janAttrInfo);

        }
        janAttr.add(attrGroup1);

        for (LinkedHashMap<String, Object> stringObjectLinkedHashMap : janAttrGroup2) {
            Map<String,Object> janAttrInfo = new HashMap<>();
            janAttrInfo.put("name",stringObjectLinkedHashMap.get("2"));
            janAttrInfo.put("title",janInfoList1.get(stringObjectLinkedHashMap.get("3")));
            janAttrInfo.put("id",janInfoList1.get(stringObjectLinkedHashMap.get("3")));
            janAttrInfo.put("pid","zokusei"+stringObjectLinkedHashMap.get("3"));
            janAttrInfo.put("isRequired",stringObjectLinkedHashMap.get("7"));
            janAttrInfo.put("type",stringObjectLinkedHashMap.get("8"));
            janAttr.add(janAttrInfo);

        }
        janInfo.put("janAttr",janAttr);
        janInfoMap.put("janInfo",janInfo);
        return ResultMaps.result(ResultEnum.SUCCESS,janInfoMap);
    }
}

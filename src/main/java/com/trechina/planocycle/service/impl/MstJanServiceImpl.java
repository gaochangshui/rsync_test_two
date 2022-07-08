package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.po.JanHeaderAttr;
import com.trechina.planocycle.entity.vo.JanInfoVO;
import com.trechina.planocycle.entity.vo.JanParamVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.JanInfoList;
import com.trechina.planocycle.mapper.MstJanMapper;
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
        String companyCd = MagicString.DEFAULT_COMPANY_CD;
        if (StringUtils.hasLength(janParamVO.getCommonPartsData().getShelfMstClass())) {
            companyCd = janParamVO.getCommonPartsData().getShelfMstClass();
        }
        String tableNameAttr = MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", companyCd,
                janParamVO.getCommonPartsData().getProdMstClass());
        String janInfoTableName = MessageFormat.format("\"{0}\".prod_{1}_jan_info", companyCd,
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
            companyCd = janInfoList.getCompanyCd();
             tableNameAttr = MessageFormat.format("\"{0}\".prod_{1}_jan_attr_header_sys", companyCd,
                    janInfoList.getCommonPartsData().getProdMstClass());
            tableNameKaisou = MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou_header_sys", companyCd,
                    janInfoList.getCommonPartsData().getProdMstClass());
             janInfoTableName = MessageFormat.format("\"{0}\".prod_{1}_jan_info", companyCd,
                    janInfoList.getCommonPartsData().getShelfMstClass());
        }else {
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

        List janInfo = new ArrayList();
        Map<String,Object> janInfoMap = new HashMap<>();

        janInfoMap.put("title","JAN基本情報");
        Map<String,Object> janData = new HashMap<>();
        Map<String,Object> jan = new HashMap<>();
        jan.put(MagicString.JAN,"JAN");
        jan.put(MagicString.VALUE,janInfoList1.get("1"));
        jan.put(MagicString.INPUT_SHOW,false);
        Map<String,Object> janName = new HashMap<>();
        janName.put(MagicString.NAME,"商品名");
        janName.put(MagicString.VALUE,janInfoList1.get("2"));
        janName.put(MagicString.INPUT_SHOW,false);
        janData.put("jan",jan);
        janData.put(MagicString.JAN_NAME,janName);
        janInfoMap.put("data",janData);
        janInfo.add(janInfoMap);

        Map<String,Object> janAttrMap = new HashMap<>();
        janAttrMap.put("title","商品属性情報");
        Map<String,Object> janAttrData = new HashMap<>();
        for (LinkedHashMap<String, Object> stringObjectLinkedHashMap : janAttrList) {
            Map<String,Object> janAttrInfo = new HashMap<>();
            janAttrInfo.put("name",stringObjectLinkedHashMap.get("2"));
            janAttrInfo.put("value",janInfoList1.get(stringObjectLinkedHashMap.get("3")));
            janAttrInfo.put("inputShow",false);
            janAttrData.put("zokusai"+stringObjectLinkedHashMap.get("3"),janAttrInfo);

        }
        janAttrMap.put("data",janAttrData);
        janInfo.add(janAttrMap);

        Map<String,Object> janKaisouMap = new HashMap<>();
        janKaisouMap.put("title","商品分類情報");
        Map<String,Object> janKaisouData = new HashMap<>();
        for (LinkedHashMap<String, Object> stringObjectLinkedHashMap : janKaisouList) {
            Map<String,Object> janKaisouInfo = new HashMap<>();
            janKaisouInfo.put("name",stringObjectLinkedHashMap.get("2"));
            janKaisouInfo.put("value",janInfoList1.get(stringObjectLinkedHashMap.get("3")));
            janKaisouInfo.put("inputShow",false);
            janKaisouData.put("zokusai"+stringObjectLinkedHashMap.get("3"),janKaisouInfo);

        }
        janKaisouMap.put("data",janAttrData);
        janInfo.add(janKaisouMap);

        return ResultMaps.result(ResultEnum.SUCCESS,janInfo);
    }
}

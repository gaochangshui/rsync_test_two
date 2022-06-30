package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderAttrDto;
import com.trechina.planocycle.entity.po.PriorityOrderJanNew;
import com.trechina.planocycle.entity.po.WorkPriorityOrderMst;
import com.trechina.planocycle.entity.vo.JanMstPlanocycleVo;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.BasicPatternMstService;
import com.trechina.planocycle.service.PriorityOrderJanNewService;
import com.trechina.planocycle.service.PriorityOrderJanReplaceService;
import com.trechina.planocycle.service.PriorityOrderMstAttrSortService;
import com.trechina.planocycle.utils.ListDisparityUtils;
import com.trechina.planocycle.utils.ResultMaps;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PriorityOrderJanNewServiceImpl implements PriorityOrderJanNewService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private PriorityOrderJanNewMapper priorityOrderJanNewMapper;
    @Autowired
    private PriorityOrderJanAttributeMapper priorityOrderJanAttributeMapper;
    @Autowired
    private PriorityOrderDataMapper priorityOrderDataMapper;
    @Autowired
    private PriorityOrderJanReplaceService priorityOrderJanReplaceService;
    @Autowired
    private PriorityOrderRestrictSetMapper priorityOrderRestrictSetMapper;
    @Autowired
    private ProductPowerMstMapper productPowerMstMapper;
    @Autowired
    private BasicPatternMstService basicPatternMstService;
    @Autowired
    private PriorityOrderMstAttrSortService priorityOrderMstAttrSortService;
    @Autowired
    private PriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private JanClassifyMapper janClassifyMapper;
    @Autowired
    private ZokuseiMstMapper zokuseiMstMapper;
    @Autowired
    private WorkPriorityOrderJanNewAttrMapper workPriorityOrderJanNewAttrMapper;
    @Autowired
    private PriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    @Autowired
    private WorkPriorityOrderMstMapper workPriorityOrderMstMapper;
    /**
     * 新規janListの取得
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderJanNew(String companyCd, Integer priorityOrderCd,Integer productPowerNo) {

            logger.info("つかむ取新規商品list参数：{}{}{}",companyCd,",",priorityOrderCd);
        PriorityOrderAttrDto attrDto = priorityOrderMstMapper.selectCommonPartsData(companyCd, priorityOrderCd);
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(attrDto.getCommonPartsData(),companyCd);
        List<String> attrList = priorityOrderMstAttrSortMapper.getAttrList(companyCd, priorityOrderCd);
        List<Integer> attrs = attrList.stream().map(Integer::parseInt).collect(Collectors.toList());
        List<Map<String,Object>> zokuseiCol = zokuseiMstMapper.getZokuseiCol(attrs, commonTableName.getProdIsCore(), commonTableName.getProdMstClass());
        List<Map<String,Object>> priorityOrderJanNewVOS = priorityOrderJanNewMapper.selectJanNew(companyCd,priorityOrderCd,commonTableName,zokuseiCol);
            logger.info("つかむ取新規商品list返回結果集b：{}",priorityOrderJanNewVOS);
           return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanNewVOS);
    }
    /**
     * 新しいjanの名前分類を取得
     * @param
     * @return
     *
     */
    @Override
    public Map<String, Object> getPriorityOrderJanNewInfo(String[] janCd,String companyCd, Integer priorityOrderCd) {
        PriorityOrderAttrDto attrDto = priorityOrderMstMapper.selectCommonPartsData(companyCd, priorityOrderCd);
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(attrDto.getCommonPartsData(),companyCd);
        List<String> attrList = priorityOrderMstAttrSortMapper.getAttrList(companyCd, priorityOrderCd);
        List<Integer> attrs = attrList.stream().map(Integer::parseInt).collect(Collectors.toList());
        List<Map<String,Object>> zokuseiCol = zokuseiMstMapper.getZokuseiCol(attrs, commonTableName.getProdIsCore(), commonTableName.getProdMstClass());
        List<Map<String,Object>> priorityOrderJanNewVOList = priorityOrderJanNewMapper.getDynamicJanNameClassify(commonTableName.getProInfoTable(), zokuseiCol, janCd);
        List<String> listNew = new ArrayList();
        for (Map<String,Object> priorityOrderJanNewVO : priorityOrderJanNewVOList) {
            listNew.add( priorityOrderJanNewVO.get("janCd").toString());
        }
        List<String> list = Arrays.asList(janCd);
        List<String> listDisparitStr = ListDisparityUtils.getListDisparitStr(list, listNew);
        String [] array = new String[listDisparitStr.size()];
        listDisparitStr.toArray(array);

        Map<String,Object> map = new HashMap<>();
            map.put("array",array);
        map.put("priorityOrderJanNewVOList",priorityOrderJanNewVOList);
        return ResultMaps.result(ResultEnum.SUCCESS,map);
    }

    /**
     * ワークシート保存新規商品リスト
     *
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderJanNew(List<PriorityOrderJanNew> priorityOrderJanNew) {
        String authorCd = session.getAttribute("aud").toString();
        String companyCd = null;
        Integer priorityOrderCd = null;
        for (PriorityOrderJanNew orderJanNew : priorityOrderJanNew) {
            companyCd = orderJanNew.getCompanyCd();
            priorityOrderCd = orderJanNew.getPriorityOrderCd();
        }
        priorityOrderJanNewMapper.workDelete(companyCd, authorCd, priorityOrderCd);
        if(priorityOrderJanNew.get(0).getJanNew() != null) {
            priorityOrderJanNewMapper.insert(priorityOrderJanNew, authorCd);
        }
            return ResultMaps.result(ResultEnum.SUCCESS);
    }


    /**
     * 分類によって商品の力点数表を除いて同類の商品を抽出する
     * @param
     * @return
     */
    @Override
    public Map<String, Object> getSimilarity(Map<String,Object> map) {
        String companyCd = map.get("companyCd").toString();
        Integer priorityOrderCd = Integer.valueOf(map.get("priorityOrderCd").toString());
        List<Map<String,Object>> data = (List<Map<String,Object>>)map.get("data");
        String aud = session.getAttribute("aud").toString();
        List<String> errorMsgJan = priorityOrderJanNewMapper.getErrorMsgJan(companyCd, priorityOrderCd);
        PriorityOrderAttrDto attrDto = priorityOrderMstMapper.selectCommonPartsData(companyCd, priorityOrderCd);
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(attrDto.getCommonPartsData(),companyCd);
        WorkPriorityOrderMst priorityOrderMst = workPriorityOrderMstMapper.selectByAuthorCd(companyCd, aud,priorityOrderCd);
        Integer productPowerCd = priorityOrderMst.getProductPowerCd();
        Long shelfPatternCd = priorityOrderMst.getShelfPatternCd();

        Map<String, Object> mapAttr =new HashMap<>();
        mapAttr.putAll(data.get(0));
        mapAttr.remove("janCd");
        mapAttr.remove("janName");
        mapAttr.remove("rank");
        mapAttr.remove("errMsg");
        List<Map<String,Object>> list = new ArrayList<>();
        List<Integer> attrList = new ArrayList();
        for (Map.Entry<String, Object> stringObjectEntry : mapAttr.entrySet()) {
            Map<String,Object> newMap = new HashMap<>();
           newMap.put("zokuseiId",stringObjectEntry.getKey().split("zokuseiName")[1]);
           newMap.put("zokuseiValue",stringObjectEntry.getValue());
           list.add(newMap);
           attrList.add(Integer.valueOf(stringObjectEntry.getKey().split("zokuseiName")[1]));
        }
        List<Map<String,Object>> zokuseiCol = zokuseiMstMapper.getZokuseiCol(attrList, commonTableName.getProdIsCore(), commonTableName.getProdMstClass());
        for (Map<String, Object> objectMap : list) {
            for (Map<String, Object> stringObjectMap : zokuseiCol) {
                if (objectMap.get("zokuseiId").equals(stringObjectMap.get("zokusei_id")+"")){
                    objectMap.put("zokuseiCol",stringObjectMap.get("zokusei_col"));
                }
            }
        }
        List<Map<String,Object>> productPowerData = priorityOrderJanNewMapper.getProductPowerData(productPowerCd, list,aud
                ,commonTableName.getProInfoTable(),priorityOrderCd,shelfPatternCd,data);
        for (Map<String, Object> datum : data) {
            if (errorMsgJan.contains(datum.get("janCd").toString())){
                datum.put("errMsg","現状棚に並んでいる可能性がありますので削除してください。");
            }else {
                datum.put("errMsg","");
            }
        }
        if (!productPowerData.isEmpty()) {
            productPowerData = this.janSort(productPowerData, data);
        }
        List list1 = new ArrayList();
        list1.add(data);
        list1.add(productPowerData);

        return ResultMaps.result(ResultEnum.SUCCESS,list1);
    }
    /**
     * 商品詳細は新規では存在しません
     * @param janMstPlanocycleVo
     * @return
     */

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setJanNewInfo(List<JanMstPlanocycleVo> janMstPlanocycleVo) {
        String companyCd = janMstPlanocycleVo.get(0).getCompanyCd();


        priorityOrderJanNewMapper.deleteJanNewInfo(companyCd);
        priorityOrderJanNewMapper.setJanNewInfo(janMstPlanocycleVo,companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * 商品詳細は表示されません
     * @param
     * @return
     */
    @Override
    public Map<String, Object> getJanNewInfo(String companyCd) {
        List<JanMstPlanocycleVo> janNewInfo = priorityOrderJanNewMapper.getJanNewInfo(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS,janNewInfo);
    }

    @Override
    public List<Map<String, Object>> janSort(List<Map<String, Object>> ptsJanList, List<Map<String, Object>> JanNewList) {
        ptsJanList = ptsJanList.stream().sorted(Comparator.comparing(map -> MapUtils.getInteger(map, "rank"))).collect(Collectors.toList());
        int i = 1;
        for (Map<String, Object> objectMap : ptsJanList) {
            objectMap.put("rank",i++);
        }
        for (Map<String, Object> objectMap : JanNewList) {
            ptsJanList.add(Integer.valueOf(objectMap.get("rank").toString())-1,objectMap);
        }
        i = 1;
        for (Map<String, Object> objectMap : ptsJanList) {
            objectMap.put("rank",i++);
        }
        return ptsJanList;
    }

}

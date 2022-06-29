package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderAttrDto;
import com.trechina.planocycle.entity.po.PriorityOrderJanNew;
import com.trechina.planocycle.entity.vo.JanMstPlanocycleVo;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.BasicPatternMstService;
import com.trechina.planocycle.service.PriorityOrderJanNewService;
import com.trechina.planocycle.service.PriorityOrderJanReplaceService;
import com.trechina.planocycle.service.PriorityOrderMstAttrSortService;
import com.trechina.planocycle.utils.ListDisparityUtils;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
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
    /**
     * 新規janListの取得
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderJanNew(String companyCd, Integer priorityOrderCd,Integer productPowerNo,String attrList) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

            logger.info("つかむ取新規商品list参数：{}{}{}",companyCd,",",priorityOrderCd);
        PriorityOrderAttrDto attrDto = priorityOrderMstMapper.selectCommonPartsData(companyCd, priorityOrderCd);
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(attrDto.getCommonPartsData(),companyCd);
        List<Integer> attrs = Arrays.stream(attrList.split(",")).map(Integer::parseInt).collect(Collectors.toList());
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
    public Map<String, Object> getPriorityOrderJanNewInfo(String[] janCd,String companyCd, Integer priorityOrderCd,String attrList) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String authorCd = session.getAttribute("aud").toString();
        PriorityOrderAttrDto attrDto = priorityOrderMstMapper.selectCommonPartsData(companyCd, priorityOrderCd);
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(attrDto.getCommonPartsData(),companyCd);
        List<Integer> attrs = Arrays.stream(attrList.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        workPriorityOrderJanNewAttrMapper.deleteWork(companyCd,priorityOrderCd);
        workPriorityOrderJanNewAttrMapper.insert(attrs,companyCd,priorityOrderCd,authorCd);
        List<Map<String,Object>> zokuseiCol = zokuseiMstMapper.getZokuseiCol(attrs, commonTableName.getProdIsCore(), commonTableName.getProdMstClass());
        List<Map<String,Object>> priorityOrderJanNewVOList = priorityOrderJanNewMapper.getDynamicJanNameClassify(commonTableName.getProInfoTable(), zokuseiCol, janCd);
        List<String> listNew = new ArrayList();
        for (Map<String,Object> priorityOrderJanNewVO : priorityOrderJanNewVOList) {
            listNew.add( priorityOrderJanNewVO.get("janNew").toString());
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
    public Map<String, Object> getSimilarity(Map<String,Object> map) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String companyCd = map.get("companyCd").toString();
        Integer priorityOrderCd = Integer.valueOf(map.get("priorityOrderCd").toString());
        String aud = session.getAttribute("aud").toString();
        PriorityOrderAttrDto attrDto = priorityOrderMstMapper.selectCommonPartsData(companyCd, priorityOrderCd);
        GetCommonPartsDataDto commonTableName = basicPatternMstService.getCommonTableName(attrDto.getCommonPartsData(),companyCd);

        Integer productPowerCd = productPowerMstMapper.getProductPowerCd(companyCd, aud,priorityOrderCd);
        map.remove("companyCd");
        map.remove("priorityOrderCd");


        List<Map<String,Object>> list = new ArrayList<>();
        List<Integer> attrList = new ArrayList();
        for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
            Map<String,Object> newMap = new HashMap<>();
           newMap.put("zokuseiId",stringObjectEntry.getKey().split("zokusei")[1]);
           newMap.put("zokuseiValue",stringObjectEntry.getValue());
           list.add(newMap);
           attrList.add(Integer.valueOf(stringObjectEntry.getKey().split("zokusei")[1]));
        }
        List<Map<String,Object>> zokuseiCol = zokuseiMstMapper.getZokuseiCol(attrList, commonTableName.getProdIsCore(), commonTableName.getProdMstClass());
        for (Map<String, Object> objectMap : list) {
            for (Map<String, Object> stringObjectMap : zokuseiCol) {
                if (objectMap.get("zokuseiId").equals(stringObjectMap.get("zokusei_id")+"")){
                    objectMap.put("zokuseiCol",stringObjectMap.get("zokusei_col"));
                }
            }
        }
        List<Map<String,Object>> productPowerData = priorityOrderJanNewMapper.getProductPowerData(productPowerCd, list,aud,commonTableName.getProInfoTable(),priorityOrderCd);

        //for (PriorityOrderJanNewDto productPowerDatum : productPowerData) {
        //    if (productPowerDatum.getJanName() == null){
        //        PriorityOrderJanNewDto productForWork = priorityOrderJanNewMapper.getProductForWork(productPowerDatum, priorityOrderJanNewVO.getCompanyCd());
        //        productPowerDatum.setJanName(productForWork.getJanName());
        //        productPowerDatum.setZokusei1(productForWork.getZokusei1());
        //        productPowerDatum.setZokusei2(productForWork.getZokusei2());
        //        productPowerDatum.setZokusei3(productForWork.getZokusei3());
        //        productPowerDatum.setZokusei4(productForWork.getZokusei4());
        //    }
        //}
        //List<PriorityOrderAttrValueDto> attrValues = priorityOrderRestrictSetMapper.getAttrValues1();
        //Class clazz = PriorityOrderJanNewDto.class;
        //for (int i = 1; i <= 4; i++) {
        //    Method getMethod = clazz.getMethod("get"+"Zokusei"+i);
        //
        //    Method setMethod = clazz.getMethod("set"+"Zokusei"+i, String.class);
        //    for (PriorityOrderJanNewDto priorityOrderJanNewDto : productPowerData) {
        //        for (PriorityOrderAttrValueDto attrValue : attrValues) {
        //            if (getMethod.invoke(priorityOrderJanNewDto)!=null&&getMethod.invoke(priorityOrderJanNewDto).equals(attrValue.getVal()) && attrValue.getZokuseiId()==i){
        //                setMethod.invoke(priorityOrderJanNewDto,attrValue.getNm());
        //            }
        //
        //        }
        //    }
        //}

        return ResultMaps.result(ResultEnum.SUCCESS,productPowerData);
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

}

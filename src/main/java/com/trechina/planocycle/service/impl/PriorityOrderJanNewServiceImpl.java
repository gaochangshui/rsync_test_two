package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.dto.GetCommonPartsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderAttrDto;
import com.trechina.planocycle.entity.dto.PriorityOrderAttrValueDto;
import com.trechina.planocycle.entity.dto.PriorityOrderJanNewDto;
import com.trechina.planocycle.entity.po.PriorityOrderJanNew;
import com.trechina.planocycle.entity.vo.JanMstPlanocycleVo;
import com.trechina.planocycle.entity.vo.PriorityOrderJanNewVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    private PriorityOrderMstMapper priorityOrderMstMapper;
    @Autowired
    private PriorityOrderJanReplaceService priorityOrderJanReplaceService;
    @Autowired
    private PriorityOrderMstAttrSortService priorityOrderMstAttrSortService;
    @Autowired
    private JanClassifyMapper janClassifyMapper;
    @Autowired
    private PriorityOrderRestrictSetMapper priorityOrderRestrictSetMapper;
    @Autowired
    private ProductPowerMstMapper productPowerMstMapper;
    /**
     * 新規janListの取得
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderJanNew(String companyCd, Integer priorityOrderCd,Integer productPowerNo) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

            logger.info("つかむ取新規商品list参数：{}{}{}",companyCd,",",priorityOrderCd);
            List<PriorityOrderJanNewDto> priorityOrderJanNewVOS = priorityOrderJanNewMapper.selectJanNew(companyCd,priorityOrderCd);
            logger.info("つかむ取新規商品list返回結果集b：{}",priorityOrderJanNewVOS);
            List<PriorityOrderAttrValueDto> attrValues = priorityOrderRestrictSetMapper.getAttrValues1();
            Class clazz = PriorityOrderJanNewDto.class;
        for (int i = 1; i <= 4; i++) {
            Method getMethod = clazz.getMethod("get"+"Zokusei"+i);
            Method setMethod = clazz.getMethod("set"+"Zokusei"+i, String.class);
            for (PriorityOrderAttrValueDto attrValue : attrValues) {
                for (PriorityOrderJanNewDto priorityOrderJanNewVO : priorityOrderJanNewVOS) {
                    if (getMethod.invoke(priorityOrderJanNewVO)!=null && getMethod.invoke(priorityOrderJanNewVO).equals(attrValue.getVal()) && attrValue.getZokuseiId()==i){
                        setMethod.invoke(priorityOrderJanNewVO,attrValue.getNm());
                    }


                }
            }
        }
           return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanNewVOS);
    }
    /**
     * 新しいjanの名前分類を取得
     * @param
     * @return
     *
     */
    @Override
    public Map<String, Object> getPriorityOrderJanNewInfo(String[] janCd,String companyCd, Integer priorityOrderCd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        PriorityOrderAttrDto attrDto = priorityOrderMstMapper.selectCommonPartsData(companyCd, priorityOrderCd);
        GetCommonPartsDataDto commonTableName = priorityOrderMstAttrSortService.getCommonTableName(attrDto);

        List<Map<String, Object>> janClassify = janClassifyMapper.getColCdClassify(commonTableName.getProKaisouTable());
        List<String> col = janClassify.stream().filter(map -> !MapUtils.getString(map, "attr").equals("jan_cd") &&
                MapUtils.getString(map, "attr").endsWith("_cd")).map(map->MapUtils.getString(map, "sort")).collect(Collectors.toList());
        List<PriorityOrderJanNewVO> priorityOrderJanNewVOList = priorityOrderJanNewMapper.getDynamicJanNameClassify(commonTableName.getProInfoTable(), col, janCd);
        if (priorityOrderJanNewVOList == null){

            return ResultMaps.result(ResultEnum.JANCDINEXISTENCE);
        }
        List<String> listNew = new ArrayList();
        for (PriorityOrderJanNewVO priorityOrderJanNewVO : priorityOrderJanNewVOList) {
           listNew.add( priorityOrderJanNewVO.getJanNew());
        }
        List<String> list = Arrays.asList(janCd);
        List<String> listDisparitStr = ListDisparityUtils.getListDisparitStr(list, listNew);
        String [] array = new String[listDisparitStr.size()];
        listDisparitStr.toArray(array);
        Class clazz = PriorityOrderJanNewVO.class;
        List<PriorityOrderAttrValueDto> attrValues = priorityOrderRestrictSetMapper.getAttrValues1();
        String [] result=null;
        if(array.length>0) {
            List<PriorityOrderJanNewVO> janNameClass = priorityOrderJanNewMapper.getJanNameClass(array, companyCd);
            List listNew1 = new ArrayList();
            for (PriorityOrderJanNewVO nameClass : janNameClass) {
                priorityOrderJanNewVOList.add(nameClass);
                listNew1.add(nameClass.getJanNew());
            }
            List<String> strings = Arrays.asList(array);
            List<String> lists = ListDisparityUtils.getListDisparitStr(strings, listNew1);
            result = new String[lists.size()];
            lists.toArray(result);
        }

        for (int i = 1; i <= 4; i++) {
            Method getMethod = clazz.getMethod("get"+"Scat"+i+"cdVal");
            Method setMethod = clazz.getMethod("set"+"Scat"+i+"cdVal", String.class);
            for (PriorityOrderAttrValueDto attrValue : attrValues) {
                for (PriorityOrderJanNewVO priorityOrderJanNewVO : priorityOrderJanNewVOList) {
                    if (getMethod.invoke(priorityOrderJanNewVO)!=null && getMethod.invoke(priorityOrderJanNewVO).equals(attrValue.getVal()) && attrValue.getZokuseiId()==i){
                        setMethod.invoke(priorityOrderJanNewVO,attrValue.getNm());
                    }
                }

                }
            }
        List<PriorityOrderJanNewDto> priorityOrderJanNewDtos = new ArrayList<>();
        PriorityOrderJanNewDto priorityOrderJanNewDto = null;
        for (PriorityOrderJanNewVO priorityOrderJanNewVO : priorityOrderJanNewVOList) {
            priorityOrderJanNewDto = new PriorityOrderJanNewDto();
            priorityOrderJanNewDto.setJanName(priorityOrderJanNewVO.getJanName());
            priorityOrderJanNewDto.setJanCd(priorityOrderJanNewVO.getJanNew());
            priorityOrderJanNewDto.setZokusei1(priorityOrderJanNewVO.getScat1cdVal());
            priorityOrderJanNewDto.setZokusei2(priorityOrderJanNewVO.getScat2cdVal());
            priorityOrderJanNewDto.setZokusei3(priorityOrderJanNewVO.getScat3cdVal());
            priorityOrderJanNewDto.setZokusei4(priorityOrderJanNewVO.getScat4cdVal());
            priorityOrderJanNewDtos.add(priorityOrderJanNewDto);
        }
        Map<String,Object> map = new HashMap<>();
        if (array.length==0){
            map.put("array",array);
        }else {
            map.put("array",result);
        }

        map.put("priorityOrderJanNewVOList",priorityOrderJanNewDtos);
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
     * @param priorityOrderJanNewVO
     * @return
     */
    @Override
    public Map<String, Object> getSimilarity(PriorityOrderJanNewDto priorityOrderJanNewVO) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String aud = session.getAttribute("aud").toString();
        Integer productPowerCd = productPowerMstMapper.getProductPowerCd(priorityOrderJanNewVO.getCompanyCd(), aud,priorityOrderJanNewVO.getPriorityOrderCd());
        List<PriorityOrderJanNewDto> productPowerData = priorityOrderJanNewMapper.getProductPowerData(productPowerCd, priorityOrderJanNewVO,aud);
        for (PriorityOrderJanNewDto productPowerDatum : productPowerData) {
            if (productPowerDatum.getJanName() == null){
                PriorityOrderJanNewDto productForWork = priorityOrderJanNewMapper.getProductForWork(productPowerDatum, priorityOrderJanNewVO.getCompanyCd());
                productPowerDatum.setJanName(productForWork.getJanName());
                productPowerDatum.setZokusei1(productForWork.getZokusei1());
                productPowerDatum.setZokusei2(productForWork.getZokusei2());
                productPowerDatum.setZokusei3(productForWork.getZokusei3());
                productPowerDatum.setZokusei4(productForWork.getZokusei4());
            }
        }
        List<PriorityOrderAttrValueDto> attrValues = priorityOrderRestrictSetMapper.getAttrValues1();
        Class clazz = PriorityOrderJanNewDto.class;
        for (int i = 1; i <= 4; i++) {
            Method getMethod = clazz.getMethod("get"+"Zokusei"+i);

            Method setMethod = clazz.getMethod("set"+"Zokusei"+i, String.class);
            for (PriorityOrderJanNewDto priorityOrderJanNewDto : productPowerData) {
                for (PriorityOrderAttrValueDto attrValue : attrValues) {
                    if (getMethod.invoke(priorityOrderJanNewDto)!=null&&getMethod.invoke(priorityOrderJanNewDto).equals(attrValue.getVal()) && attrValue.getZokuseiId()==i){
                        setMethod.invoke(priorityOrderJanNewDto,attrValue.getNm());
                    }

                }
            }
        }

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

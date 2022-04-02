package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.PriorityOrderAttrValueDto;
import com.trechina.planocycle.entity.dto.PriorityOrderJanNewDto;
import com.trechina.planocycle.entity.po.PriorityOrderJanAttribute;
import com.trechina.planocycle.entity.po.PriorityOrderJanNew;
import com.trechina.planocycle.entity.vo.JanMstPlanocycleVo;
import com.trechina.planocycle.entity.vo.PriorityOrderJanNewVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.PriorityOrderJanNewService;
import com.trechina.planocycle.service.PriorityOrderJanReplaceService;
import com.trechina.planocycle.utils.ListDisparityUtils;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

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
    private PriorityOrderCatepakAttributeMapper priorityOrderCatepakAttributeMapper;
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

            logger.info("获取新规商品list参数："+companyCd+","+priorityOrderCd);
            List<PriorityOrderJanNewDto> priorityOrderJanNewVOS = priorityOrderJanNewMapper.selectJanNew(companyCd,priorityOrderCd);
            logger.info("获取新规商品list返回结果集b："+priorityOrderJanNewVOS);
            List<PriorityOrderAttrValueDto> attrValues = priorityOrderRestrictSetMapper.getAttrValues1();
            Class clazz = PriorityOrderJanNewDto.class;
        for (int i = 1; i <= 4; i++) {
            Method getMethod = clazz.getMethod("get"+"Zokusei"+i);
            Method setMethod = clazz.getMethod("set"+"Zokusei"+i, String.class);
            for (PriorityOrderAttrValueDto attrValue : attrValues) {
                for (PriorityOrderJanNewDto priorityOrderJanNewVO : priorityOrderJanNewVOS) {
                    if (getMethod.invoke(priorityOrderJanNewVO)!=null && getMethod.invoke(priorityOrderJanNewVO).equals(attrValue.getVal()) && attrValue.getZokuseiId()==i){
                        setMethod.invoke(priorityOrderJanNewVO,attrValue.getNm());
                    }else{

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
    public Map<String, Object> getPriorityOrderJanNewInfo(String[] janCd,String companyCd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<PriorityOrderJanNewVO> priorityOrderJanNewVOList = priorityOrderJanNewMapper.getJanNameClassify(janCd);
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
                    }else{

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
     * 新規商品リストの削除
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Integer delriorityOrderJanNewInfo(String companyCd, Integer priorityOrderCd) {
        return priorityOrderJanNewMapper.delete(companyCd,priorityOrderCd);
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

    private String dataSave(JSONArray jsonArray, List<PriorityOrderJanNew> janNewList,
                          List<PriorityOrderJanAttribute> janAttributeList, String companyCd,
                          Integer priorityOrderCd,String notExistsJan) {
        String janInfo = priorityOrderJanReplaceService.getJanInfo();
        List<String> list= Arrays.asList(janInfo.split(","));
        for (int i = 0; i < jsonArray.size(); i++) {
            if(list.indexOf(((HashMap) jsonArray.get(i)).get("janNew"))==-1){
                notExistsJan += ((HashMap) jsonArray.get(i)).get("janNew")+",";
            } else {
                // jannewプライマリテーブルを構築するパラメータ
                janNew(janNewList, companyCd, priorityOrderCd, (HashMap) jsonArray.get(i));
                // janダイナミックプロパティ列を構築するパラメータ
                janAttr(janAttributeList, companyCd, priorityOrderCd, (HashMap) jsonArray.get(i));
            }
        }
        logger.info("保存新规商品list主表处理完后的参数："+ janNewList.toString());
        logger.info("保存新规商品list动态属性列处理完后的参数："+ janAttributeList.toString());
        //フル挿入
        if (janNewList.size()>0){
            priorityOrderJanAttributeMapper.insert(janAttributeList);
            //すべてのjannew修正配荷店舗数を検索
            List<Map<String,Object>> maps = priorityOrderJanNewMapper.selectJanNewOrAttr(companyCd,priorityOrderCd);
            maps.forEach(item -> {
                String[] attrName = item.get("attr").toString().split(",");
                String sel = "";
                for (int i = 1; i <= attrName.length; i++) {
                    if (i==attrName.length){
                        sel += "mulit_attr='"+attrName[i-1]+"',";
                    }else{
                        sel += "attr"+i+"='"+attrName[i-1]+"',";
                    }
                }
                sel+= "rank_upd='"+item.get("rank")+"'";
                List<String> colValueList = Arrays.asList(sel.split(","));
                String branchNum = priorityOrderCatepakAttributeMapper.selectForTempTable(colValueList,"public.priorityorder"+session.getAttribute("aud").toString());
                logger.info("查询定番店铺数"+branchNum);
                if (branchNum!=null){
                    priorityOrderJanNewMapper.updateBranchNum(Integer.valueOf(item.get("priority_order_cd").toString()),
                            item.get("jan_new").toString(),Integer.valueOf(branchNum));
                } else {
                    priorityOrderJanNewMapper.updateBranchNum(Integer.valueOf(item.get("priority_order_cd").toString()),
                            item.get("jan_new").toString(),0);
                }
            });

        }
        return notExistsJan;
    }

    private void notExistsData(String companyCd, Integer priorityOrderCd) {
        List<Map<String,Object>> priorityOrderData =  priorityOrderJanNewMapper.selectJanNewNotExistsMst(companyCd, priorityOrderCd,
                "public.priorityorder"+session.getAttribute("aud").toString());
        logger.info("不存在的数据"+priorityOrderData.toString());
        //結果セットを巡回して動的列を分割
        if (priorityOrderData.size()>0) {
            priorityOrderData.forEach(item->{
                String[] attrList = item.get("attr").toString().split(",");
                String[] valList;
                for (int i = 0; i < attrList.length-1; i++) {
                    valList = attrList[i].split(":");
                   item.put("attr"+valList[0],valList[1]);
                }
                // 10番目の属性をマルチ属性の列名に変更
                item.put("mulit_attr",attrList[attrList.length-1].split(":")[1]);
                item.remove("attr");
                // skuはjanでjananmeをクエリーします
                String janName = priorityOrderDataMapper.selectJanName(item.get("jan_new").toString());
                item.remove("sku");
                item.put("sku", janName);
            });
        //存在しないデータをプライマリテーブルに挿入
        PriorityOrderDataServiceImpl priorityOrderDataService= new PriorityOrderDataServiceImpl();
        List<Map<String,String>> keyNameList = new ArrayList<>();
        //ヘッダーの生成
        JSONArray priJsonArray = (JSONArray) JSONObject.toJSON(priorityOrderData);
        priorityOrderDataService.colNameList(priJsonArray,keyNameList);
        //データの挿入

        priorityOrderDataMapper.insert(priJsonArray,keyNameList,"public.priorityorder"+session.getAttribute("aud").toString());

        }
    }

    /**
     * jannewプライマリテーブルを構築するパラメータ
     * @param janAttributeList
     * @param companyCd
     * @param priorityOrderCd
     * @param item
     */
    private void janAttr(List<PriorityOrderJanAttribute> janAttributeList, String companyCd, Integer priorityOrderCd, HashMap item) {
        for (Object key : item.keySet()){
            // ダイナミックプロパティリスト
            PriorityOrderJanAttribute priorityOrderJanAttribute = new PriorityOrderJanAttribute();
            priorityOrderJanAttribute.setCompanyCd(companyCd);
            priorityOrderJanAttribute.setPriorityOrderCd(priorityOrderCd);
            priorityOrderJanAttribute.setJanNew(String.valueOf(item.get("janNew")));
            if (key.toString().indexOf("attr")>-1) {
                priorityOrderJanAttribute.setAttrCd(Integer.valueOf(key.toString().replace("attr","")));
                priorityOrderJanAttribute.setAttrValue(String.valueOf(item.get(key)));
                janAttributeList.add(priorityOrderJanAttribute);

            }
        }
    }

    /**
     * janダイナミックプロパティ列を構築するパラメータ
     * @param janNewList
     * @param companyCd
     * @param priorityOrderCd
     * @param item
     */
    private void janNew(List<PriorityOrderJanNew> janNewList, String companyCd, Integer priorityOrderCd, HashMap item) {
        // 新規商品リスト
        PriorityOrderJanNew priorityOrderJanNew = new PriorityOrderJanNew();
        priorityOrderJanNew.setCompanyCd(companyCd);
        priorityOrderJanNew.setPriorityOrderCd(priorityOrderCd);
        priorityOrderJanNew.setJanNew(String.valueOf(item.get("janNew")));
        priorityOrderJanNew.setRank(Integer.valueOf(String.valueOf(item.get("rank"))) );
        priorityOrderJanNew.setBranchAccount(BigDecimal.valueOf(Double.valueOf(String.valueOf(item.get("branchAccount")))));
        janNewList.add(priorityOrderJanNew);
    }
}

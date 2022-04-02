package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.dto.*;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityOrderRestrictSetMapper;
import com.trechina.planocycle.mapper.PriorityOrderShelfDataMapper;
import com.trechina.planocycle.mapper.WorkPriorityOrderResultDataMapper;
import com.trechina.planocycle.service.PriorityOrderShelfDataService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service

public class PriorityOrderShelfDataServiceImpl implements PriorityOrderShelfDataService {

    @Autowired
    private PriorityOrderShelfDataMapper priorityOrderShelfDataMapper;
    @Autowired
    private PriorityOrderRestrictSetMapper priorityOrderRestrictSetMapper;
    @Autowired
    private WorkPriorityOrderResultDataMapper workPriorityOrderResultDataMapper;
    @Autowired
    private HttpSession session;

    /**
     * 新規では基本的なパター制約に関する情報を入手
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getRestrictData(String companyCd,Integer priorityOrderCd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {


        String authorCd = session.getAttribute("aud").toString();
        List<PriorityOrderRestrictDto> restrictData = priorityOrderShelfDataMapper.getRestrictData(companyCd, authorCd,priorityOrderCd);
        List<PriorityOrderAttrValueDto> attrValues = priorityOrderRestrictSetMapper.getAttrValues();
        Class clazz = PriorityOrderRestrictDto.class;
        for (int i = 1; i <= 10; i++) {
            Method getMethod = clazz.getMethod("get"+"Zokusei"+i);
            Method getMethodName = clazz.getMethod("get"+"ZokuseiName"+i);
            Method setMethod = clazz.getMethod("set"+"ZokuseiName"+i, String.class);
            for (PriorityOrderRestrictDto priorityOrderRestrictDto : restrictData) {
                for (PriorityOrderAttrValueDto attrValue : attrValues) {
                    if (getMethod.invoke(priorityOrderRestrictDto)!=null&&getMethod.invoke(priorityOrderRestrictDto).equals(attrValue.getVal()) && attrValue.getZokuseiId()==i){
                        setMethod.invoke(priorityOrderRestrictDto,attrValue.getNm());
                    }
                    if (getMethodName.invoke(priorityOrderRestrictDto)==null ){
                        setMethod.invoke(priorityOrderRestrictDto,"");
                    }

                }
            }
        }



        List<PriorityOrderRestDto> list = new ArrayList<>();
        PriorityOrderRestDto priorityOrderRestDto =null;
        Class c = PriorityOrderRestrictDto.class;
        for (PriorityOrderRestrictDto restrictDatum : restrictData) {
            priorityOrderRestDto = new PriorityOrderRestDto();
            priorityOrderRestDto.setRestrictCd(restrictDatum.getRestrictCd());
            String s = "";
            for (int i = 1; i <= 10; i++){
                Method getMethod = c.getMethod("get"+"ZokuseiName"+i);
                if (getMethod.invoke(restrictDatum)!=null && !getMethod.invoke(restrictDatum).equals("")){
                         s+= "->"+getMethod.invoke(restrictDatum);
                }
            }
            if (!s.equals("")) {
                s = s.substring(2);
            }else {
                s="_";
            }
            priorityOrderRestDto.setRestrictName(s);
            priorityOrderRestDto.setFaceNum(restrictDatum.getFaceNum());
            priorityOrderRestDto.setSkuNum(restrictDatum.getSkuNum());
            list.add(priorityOrderRestDto);
        }

        return ResultMaps.result(ResultEnum.SUCCESS,list);
    }
    /**
     * 新規では基本パター制約別janの詳細情報を取得
     * @param
     * @return
     */
    @Override
    public Map<String, Object> getRestrictJans(PriorityOrderRestDto priorityOrderRestDto) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String aud = session.getAttribute("aud").toString();


        List<PriorityOrderRestrictJanDto> restrictJans = priorityOrderShelfDataMapper.getRestrictJans(priorityOrderRestDto,aud);
        List<PriorityOrderAttrValueDto> attrValues = priorityOrderRestrictSetMapper.getAttrValues1();
        Class clazz = PriorityOrderRestrictJanDto.class;
        for (int i = 1; i <= 10; i++) {
            Method getMethod = clazz.getMethod("get"+"Zokusei"+i);
            Method setMethod = clazz.getMethod("set"+"ZokuseiName"+i, String.class);
            for (PriorityOrderRestrictJanDto priorityOrderRestrictJanDto : restrictJans) {
                for (PriorityOrderAttrValueDto attrValue : attrValues) {
                    if (getMethod.invoke(priorityOrderRestrictJanDto)!=null&&getMethod.invoke(priorityOrderRestrictJanDto).equals(attrValue.getVal()) && attrValue.getZokuseiId()==i){
                        setMethod.invoke(priorityOrderRestrictJanDto,attrValue.getNm());
                    }

                }
            }
        }
        return ResultMaps.result(ResultEnum.SUCCESS,restrictJans);
    }

    /**
     * 新規では基本的なパター棚別の情報を取得
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getPlatformShedData(String companyCd,Integer priorityOrderCd) {
        String authorCd = session.getAttribute("aud").toString();
        List<PriorityOrderPlatformShedDto> platformShedData = priorityOrderShelfDataMapper.getPlatformShedData(companyCd, authorCd,priorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS,platformShedData);
    }
    /**
     * 新規では基本的なパタ台棚別jansの詳細情報を入手
     * @param priorityOrderPlatformShedDto
     * @return
     */
    @Override
    public Map<String, Object> getPlatformShedJans(PriorityOrderPlatformShedDto priorityOrderPlatformShedDto) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        String aud = session.getAttribute("aud").toString();
        List<PriorityOrderRestrictJanDto> platformShedJans = priorityOrderShelfDataMapper.getPlatformShedJans(priorityOrderPlatformShedDto,aud);
        List<PriorityOrderAttrValueDto> attrValues = priorityOrderRestrictSetMapper.getAttrValues1();
        Class clazz = PriorityOrderRestrictJanDto.class;
        for (int i = 1; i <= 10; i++) {
            Method getMethod = clazz.getMethod("get"+"Zokusei"+i);
            Method setMethod = clazz.getMethod("set"+"ZokuseiName"+i, String.class);
            for (PriorityOrderRestrictJanDto priorityOrderRestrictJanDto : platformShedJans) {
                for (PriorityOrderAttrValueDto attrValue : attrValues) {
                    if (getMethod.invoke(priorityOrderRestrictJanDto)!=null&&getMethod.invoke(priorityOrderRestrictJanDto).equals(attrValue.getVal()) && attrValue.getZokuseiId()==i){
                        setMethod.invoke(priorityOrderRestrictJanDto,attrValue.getNm());
                    }

                }
            }
        }
        return ResultMaps.result(ResultEnum.SUCCESS,platformShedJans);
    }
    /**
     * faceNumの保存
     * @param priorityOrderRestrictJanDto
     * @return
     */
    @Override
    public Map<String, Object> setFaceNumForData(List<PriorityOrderRestrictJanDto> priorityOrderRestrictJanDto) {
        String authorCd = session.getAttribute("aud").toString();
        workPriorityOrderResultDataMapper.updateFaceNum(priorityOrderRestrictJanDto,authorCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

}

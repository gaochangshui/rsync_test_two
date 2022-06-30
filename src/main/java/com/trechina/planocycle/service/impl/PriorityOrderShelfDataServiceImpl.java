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
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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


        return ResultMaps.result(ResultEnum.SUCCESS);
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
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setFaceNumForData(List<PriorityOrderRestrictJanDto> priorityOrderRestrictJanDto) {
        String authorCd = session.getAttribute("aud").toString();
        workPriorityOrderResultDataMapper.updateFaceNum(priorityOrderRestrictJanDto,authorCd);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    @Override
    public Map<String, Object> getNewPlatformShedData(String companyCd, Integer priorityOrderCd) {
        String authorCd = session.getAttribute("aud").toString();
        List<PriorityOrderPlatformShedDto> platformShedData = priorityOrderShelfDataMapper.getNewPlatformShedData(companyCd, authorCd,priorityOrderCd);
        return ResultMaps.result(ResultEnum.SUCCESS,platformShedData);
    }

}

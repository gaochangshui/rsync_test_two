package com.trechina.planocycle.service.impl;

import com.trechina.planocycle.entity.dto.DownloadSortDto;
import com.trechina.planocycle.entity.dto.PriorityOrderMstAttrSortDto;
import com.trechina.planocycle.entity.po.PriorityOrderAttributeClassify;
import com.trechina.planocycle.entity.po.PriorityOrderMstAttrSort;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.ClaasicPriorityOrderAttributeClassifyMapper;
import com.trechina.planocycle.mapper.ClassicPriorityOrderMstAttrSortMapper;
import com.trechina.planocycle.mapper.PriorityOrderMstAttrSortMapper;
import com.trechina.planocycle.service.ClassicPriorityOrderAttributeClassifyService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClassicPriorityOrderAttributeClassifyServiceImpl implements ClassicPriorityOrderAttributeClassifyService {
    @Autowired
    private HttpSession session;
    @Autowired
    private ClaasicPriorityOrderAttributeClassifyMapper priorityOrderAttributeClassifyMapper;
    @Autowired
    private ClassicPriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;

    @Override
    public Map<String, Object> getClassifyList(DownloadSortDto downloadSortDto) {
        Integer attrNum = priorityOrderAttributeClassifyMapper.getAttrNum(downloadSortDto.getCompanyCd(), downloadSortDto.getPriorityOrderCd());
        List<PriorityOrderMstAttrSortDto> mstAttrSorts = priorityOrderMstAttrSortMapper.selectWKAttr(downloadSortDto.getCompanyCd(), downloadSortDto.getPriorityOrderCd());
        Map<String, String> mstAttrSortMap = mstAttrSorts.stream().collect(Collectors.toMap(PriorityOrderMstAttrSortDto::getValue, PriorityOrderMstAttrSortDto::getSort));
        List<PriorityOrderAttributeClassify> classifyList = null;
            if (attrNum > 0){
                classifyList = priorityOrderAttributeClassifyMapper.getClassifyList(downloadSortDto.getCompanyCd(), downloadSortDto.getPriorityOrderCd());
            }else {
                classifyList = priorityOrderAttributeClassifyMapper.classifyList(mstAttrSortMap.get(downloadSortDto.getTaiCd()),
                        mstAttrSortMap.get(downloadSortDto.getTanaCd()), downloadSortDto.getPriorityOrderCd());
                classifyList.forEach(item -> {
                    item.setCompanyCd(downloadSortDto.getCompanyCd());
                    item.setPriorityOrderCd(downloadSortDto.getPriorityOrderCd());
                });
                priorityOrderAttributeClassifyMapper.delete(downloadSortDto.getPriorityOrderCd());
                priorityOrderAttributeClassifyMapper.insert(classifyList);
            }


        return ResultMaps.result(ResultEnum.SUCCESS,classifyList);
    }

    @Override
    public Map<String, Object> setClassifyList(List<PriorityOrderAttributeClassify> classifyList) {
        String companyCd = classifyList.get(0).getCompanyCd();
        Integer priorityOrderCd = classifyList.get(0).getPriorityOrderCd();
        priorityOrderAttributeClassifyMapper.delete(priorityOrderCd);
        priorityOrderAttributeClassifyMapper.insert(classifyList);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }

}

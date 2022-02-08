package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.po.PriorityOrderCommodityNot;
import com.trechina.planocycle.entity.po.PriorityOrderJanReplace;
import com.trechina.planocycle.entity.vo.PriorityOrderJanReplaceVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityOrderDataMapper;
import com.trechina.planocycle.mapper.PriorityOrderJanReplaceMapper;
import com.trechina.planocycle.service.PriorityOrderJanReplaceService;
import com.trechina.planocycle.utils.ResultMaps;
import com.trechina.planocycle.utils.dataConverUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
@Service
public class PriorityOrderJanReplaceServiceImpl implements PriorityOrderJanReplaceService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private PriorityOrderJanReplaceMapper priorityOrderJanReplaceMapper;
    @Autowired
    private PriorityOrderDataMapper priorityOrderDataMapper;
    /**
     * 获取jan变的信息
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Map<String, Object> getPriorityOrderJanInfo(String companyCd, Integer priorityOrderCd) {
        logger.info("获取jan变的信息参数："+companyCd+","+priorityOrderCd);
        List<PriorityOrderJanReplaceVO> priorityOrderJanReplaceVOList = priorityOrderJanReplaceMapper
                .selectJanInfo(companyCd,priorityOrderCd);
        logger.info("获取jan变的信息返回值："+priorityOrderJanReplaceVOList);
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderJanReplaceVOList);
    }

    /**
     * 保存jan变的信息
     *
     * @param priorityOrderJanReplace
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setPriorityOrderJanInfo(List<PriorityOrderJanReplace> priorityOrderJanReplace) {
        logger.info("保存jan变的信息参数："+priorityOrderJanReplace);
        // 拿到的参数只有第一行有企业和顺位表cd，需要遍历参数，给所有行都赋值
        try{
            String companyCd = priorityOrderJanReplace.get(0).getCompanyCd();
            Integer priorityOrderCd = priorityOrderJanReplace.get(0).getPriorityOrderCd();
            dataConverUtils dataConverUtil = new dataConverUtils();
            // 调用共同方法，处理数据
            List<PriorityOrderJanReplace> jan =dataConverUtil.priorityOrderCommonMstInsertMethod(PriorityOrderJanReplace.class,priorityOrderJanReplace,
                    companyCd ,priorityOrderCd);

            logger.info("保存jan变的信息处理完后的参数："+jan);
            // check
            String resuleJanDistinct =getJanInfo();
            logger.info("checkjan的返回值"+resuleJanDistinct);
            //删除

            priorityOrderJanReplaceMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
            List<PriorityOrderJanReplace> exists = new ArrayList<>();
            String notExists = "";
            List<String> list= Arrays.asList(resuleJanDistinct.split(","));
            for (int i = 0; i < jan.size(); i++) {
                if (list.indexOf(jan.get(i).getJanNew())==-1){
                    notExists += jan.get(i).getJanNew()+",";
                } else {
                    exists.add(jan.get(i));
                }
            }
            logger.info("存在的jan信息"+exists.toString());
            logger.info("不存在的jan信息"+notExists);
            //写入数据库
            if (exists.size()>0) {
                priorityOrderJanReplaceMapper.insert(exists);
                //修改主表
                priorityOrderDataMapper.updatePriorityOrderDataForJanNew(companyCd,priorityOrderCd,
                        "public.priorityorder"+session.getAttribute("aud").toString());
            }
            if (notExists.length()>0) {
                return ResultMaps.result(ResultEnum.JANNOTESISTS,notExists.substring(0,notExists.length()-1));
            } else {
                return ResultMaps.result(ResultEnum.SUCCESS);
            }
        } catch (Exception e) {
            logger.error("保存jan变信息出错："+e);
            return ResultMaps.result(ResultEnum.FAILURE);
        }
    }

    @Override
    public String getJanInfo() {
        return priorityOrderJanReplaceMapper.selectJanDistinct();
    }

    /**
     * 删除jan变list
     *
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    @Override
    public Integer delJanReplaceInfo(String companyCd, Integer priorityOrderCd) {
        return priorityOrderJanReplaceMapper.deleteByPrimaryKey(companyCd,priorityOrderCd);
    }
}

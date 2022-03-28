package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.po.PriorityOrderNumGenerator;
import com.trechina.planocycle.entity.po.ProductPowerNumGenerator;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityOrderNumGeneratorMapper;
import com.trechina.planocycle.mapper.ProductPowerNumGeneratorMapper;
import com.trechina.planocycle.service.IDGeneratorService;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;
@Service
public class IDGeneratorServiceImpl implements IDGeneratorService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    HttpSession session;
    @Autowired
    private PriorityOrderNumGeneratorMapper priorityOrderNumGeneratorMapper;

    @Autowired
    private ProductPowerNumGeneratorMapper productPowerNumGeneratorMapper;
    /**
     * 自动采号，并返回
     *
     * @return
     */
    @Override
    public Map<String, Object> priorityOrderNumGenerator() {
        PriorityOrderNumGenerator priorityOrderNumGenerator = new PriorityOrderNumGenerator();
        priorityOrderNumGenerator.setUsercd(session.getAttribute("aud").toString());
        Integer id = priorityOrderNumGeneratorMapper.insert(priorityOrderNumGenerator);
        logger.info("优先顺位表自动取号："+priorityOrderNumGenerator.getId());
        return ResultMaps.result(ResultEnum.SUCCESS,priorityOrderNumGenerator.getId());
    }

    /**
     * 商品力点数表自动采号，并返回
     */
    @Override
    public Map<String, Object> productPowerNumGenerator() {
        ProductPowerNumGenerator productPowerNumGenerator = new ProductPowerNumGenerator();
        productPowerNumGenerator.setUsercd(session.getAttribute("aud").toString());
        Integer id =productPowerNumGeneratorMapper.insert(productPowerNumGenerator);
        logger.info("商品力点数表取号："+productPowerNumGenerator.getId());
        return ResultMaps.result(ResultEnum.SUCCESS,productPowerNumGenerator);
    }

    @Override
    public Map<String, Object> priorityAllID() {
        return null;
    }
}

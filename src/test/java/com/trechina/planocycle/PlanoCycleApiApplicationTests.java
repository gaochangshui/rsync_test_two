package com.trechina.planocycle;

import com.trechina.planocycle.entity.dto.PriorityAllResultDataDto;
import com.trechina.planocycle.entity.po.ProductPowerMstData;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.PriorityOrderMstService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class PlanoCycleApiApplicationTests {
/*    @Autowired
    private ProductPowerDataMapper productPowerDataMapper;*/
    @Autowired
    ProductPowerMstMapper productPowerMstMapper;
    @Autowired
    ShelfPatternMstMapper shelfPatternMstMapper;
    @Autowired
    ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    PriorityOrderRestrictSetMapper priorityOrderRestrictSetMapper;
    @Autowired
    PriorityOrderMstAttrSortMapper priorityOrderMstAttrSortMapper;
    @Autowired
    PriorityOrderShelfDataMapper priorityOrderShelfDataMapper;
    @Autowired
    PriorityOrderJanReplaceMapper priorityOrderJanReplaceMapper;
    @Autowired
    WorkPriorityOrderRestrictResultMapper workPriorityOrderRestrictResultMapper;
    @Autowired
    WorkPriorityOrderResultDataMapper workPriorityOrderResultDataMapper;
    @Autowired
    PriorityOrderMstService priorityOrderMstService;
    @Autowired
    PriorityOrderJanCardMapper priorityOrderJanCardMapper;
    @Autowired
    WorkPriorityOrderRestrictSetMapper workPriorityOrderRestrictSetMapper;
    @Autowired
    PriorityOrderJanNewMapper priorityOrderJanNewMapper;
    @Autowired
    WorkPriorityAllResultDataMapper workPriorityAllResultDataMapper;
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public static Object arrayToObject(Object[] obj, Class<?> classType) {
        Object stu1 = null;
        try {
            stu1 = classType.newInstance();
            for (int i = 0; i < classType.getDeclaredFields().length; i++) {
                String setMethodName = "set" + classType.getDeclaredFields()[i].getName().substring(0, 1).toUpperCase() + classType.getDeclaredFields()[i].getName().substring(1);
                Method setMethod = classType.getDeclaredMethod(setMethodName, new Class[]{classType.getDeclaredFields()[i].getType()});
                setMethod.invoke(stu1, obj[i]);
            }
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
        }
        return stu1;
    }


    @Test
    public void test() throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<ProductPowerMstData> dataList = new ArrayList<>();
        ProductPowerMstData data = null;
        for (int i = 0; i < 10; i++) {
            data = new ProductPowerMstData();
            data.setCompanyCd("001");
            data.setProductPowerCd((long) 12);
            data.setJan("JAN" + i);
            data.setSkuName("sku");
            data.setPdPosAmount(BigDecimal.valueOf(Math.random() * 10));
            data.setPdPosNum(BigDecimal.valueOf(Math.random() * 10));
            data.setPdBranchAmount(BigDecimal.valueOf(Math.random() * 10));

            data.setGdPosAmount(BigDecimal.valueOf(Math.random() * 10));
            data.setGdPosNum(BigDecimal.valueOf(Math.random() * 10));
            data.setGdBranchAmount(BigDecimal.valueOf(Math.random() * 10));
            dataList.add(data);
        }
        String[] columns = {"pPosAmount", "pPosNum", "pBranchAmount", "gPosAmount", "gPosNum", "gBranchAmount"};
        String column = "pPosAmount";


        Class clazz = ProductPowerMstData.class;
        Method getMethod = clazz.getMethod("get" + column);
        Method setMethod = clazz.getMethod("set" + column + "Rank");


        Collections.sort(dataList, new Comparator<ProductPowerMstData>() {
            @Override
            public int compare(ProductPowerMstData o1, ProductPowerMstData o2) {
                try {
                    return new BigDecimal(String.valueOf(getMethod.invoke(o1))).compareTo(new BigDecimal(String.valueOf(getMethod.invoke(o2))));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.error("", e);
                    return 0;
                }
            }
        });


        int i = 1;
        for (ProductPowerMstData productPowerMstData : dataList) {
            setMethod.invoke(productPowerMstData, i + 1);
        }
        System.out.println();
    }


    @Test
    void test6() {
        List<PriorityAllResultDataDto> resultDatas = workPriorityAllResultDataMapper.getResultDatas("0001", "10215814", 0, 80);
        resultDatas.stream().forEach(System.out::println);
    }

    public  void test7(){}


}

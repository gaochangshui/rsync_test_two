package com.trechina.planocycle;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.PriorityAllResultDataDto;
import com.trechina.planocycle.entity.po.ProductPowerMstData;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.PriorityOrderMstService;
import com.trechina.planocycle.service.TableTransferService;
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    ProductPowerDataMapper productPowerDataMapper;
    @Autowired
    JanClassifyMapper janClassifyMapper;
    @Autowired
    TableTransferService tableTransferService;
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
    public void test10(){
        String company = "0001";
        String classCd = "0000";
        Integer col = 5;
        Integer zokuseiId = 2;

          tableTransferService.setZokuseiData(company,classCd,zokuseiId,col);
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


        dataList.sort((o1, o2) -> {
            try {
                return new BigDecimal(String.valueOf(getMethod.invoke(o1))).compareTo(new BigDecimal(String.valueOf(getMethod.invoke(o2))));
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("", e);
                return 0;
            }
        });


        int i = 1;
        for (ProductPowerMstData productPowerMstData : dataList) {
            setMethod.invoke(productPowerMstData, i + 1);
        }
    }


    @Test
    void test6() {
        List<PriorityAllResultDataDto> resultDatas = workPriorityAllResultDataMapper.getResultDatas("0001", "10215814", 0, 80);
        resultDatas.stream().forEach(System.out::println);
    }
    @Test
    public  void test7(){
        String commonPartsData = "{\"dateIsCore\":\"1\",\"storeLevel\":\"3\",\"storeIsCore\":\"1\",\"storeMstClass\":\"0001\",\"prodIsCore\":\"2\",\"prodMstClass\":\"0001\"}";
        String companyCd = "87c6f4";
        String authorCd = "10215814";
        JSONObject jsonObject = JSONObject.parseObject(commonPartsData);
        String prodMstClass = jsonObject.get("prodMstClass").toString();
        String prodIsCore = jsonObject.get("prodIsCore").toString();
        String isCompanyCd =null;
        if ("1".equals(prodIsCore)){
            isCompanyCd = "1000";
        }else {
            isCompanyCd = companyCd;
        }

        String tableName = MessageFormat.format("\"{0}\".prod_{1}_jan_kaisou_header_sys", isCompanyCd,prodMstClass);
        String janInfoTableName = MessageFormat.format("\"{0}\".prod_{1}_jan_info", isCompanyCd, prodMstClass);
        List<Map<String, Object>> janClassifyList = janClassifyMapper.selectJanClassify(tableName);
        Map<String, String> attrMap = janClassifyList.stream().collect(Collectors.toMap(map -> map.get("attr").toString(), map -> map.get("attr_val").toString()));
        Map<String, String> attrColumnMap = janClassifyList.stream().collect(Collectors.toMap(map -> map.get("attr").toString(), map -> map.get("sort").toString()));
        List<Map<String, Object>> allData = productPowerDataMapper.getSyokikaAllData(companyCd,
                janInfoTableName, "\""+attrColumnMap.get("jan_cd")+"\"", janClassifyList,authorCd,1);
    }


}

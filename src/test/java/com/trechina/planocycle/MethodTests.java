package com.trechina.planocycle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trechina.planocycle.entity.dto.ShelfPtsDataTanaCount;
import com.trechina.planocycle.entity.po.ProductPowerMstData;
import com.trechina.planocycle.entity.po.ShelfPtsDataTanamst;
import com.trechina.planocycle.entity.po.WorkPriorityOrderRestrictSet;
import com.trechina.planocycle.entity.vo.PriorityOrderAttrVO;
import com.trechina.planocycle.service.PriorityOrderMstAttrSortService;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
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

@SpringBootTest
@RunWith(SpringRunner.class)
public class MethodTests {
    @Autowired
    private PriorityOrderMstAttrSortService priorityOrderMstAttrSortService;

    @Test
    @Disabled
    public void test() throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<ProductPowerMstData> dataList = new ArrayList<>();
        ProductPowerMstData data = null;
        for (int i = 0; i < 10; i++) {
            data = new ProductPowerMstData();
            data.setCompanyCd("001");
            data.setProductPowerCd((long) 12);
            data.setJan("JAN" + i);
            data.setSkuName("sku");
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
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return 0;
                } catch (InvocationTargetException e) {
                    return 0;
                }
            }
        });


        int i = 1;
        for (ProductPowerMstData productPowerMstData : dataList) {
            setMethod.invoke(productPowerMstData, i + 1);
        }
    }

    @Test
    @Disabled
    public void setRestrict() throws JsonProcessingException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Integer[] pattens = {12, 4, 4};
        Integer[] tais = {5, 5, 4, 6};

        List<PriorityOrderAttrVO> dataList = new ArrayList<>();
        PriorityOrderAttrVO vo = null;
        for (int i = 0; i < pattens.length; i++) {
            vo = new PriorityOrderAttrVO();
            vo.setTanaPattan(pattens[i]);
            vo.setAttrACd(i + "a");
            dataList.add(vo);
        }
        List<ShelfPtsDataTanaCount> tanaCountList = new ArrayList<>();
        ShelfPtsDataTanaCount tanaCount = null;
        for (int i = 0; i < tais.length; i++) {
            tanaCount = new ShelfPtsDataTanaCount();
            tanaCount.setTaiCd(i + 1);
            tanaCount.setTanaCount(tais[i]);
            tanaCount.setTanaUsedCount(0);
            tanaCountList.add(tanaCount);
        }
        List<ShelfPtsDataTanamst> ptsDataTanamstList = Collections.emptyList();
        List<WorkPriorityOrderRestrictSet> setList = priorityOrderMstAttrSortService.setRestrict(dataList, ptsDataTanamstList, tanaCountList, (short) 2, (short) 2, "0001", "10047515", 1);
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(setList));
    }

    @Test
    public void setString() {
        String a = null;
        String b = "Hello world";
        System.out.println(a + b);

        WorkPriorityOrderRestrictSet set = new WorkPriorityOrderRestrictSet();
        set.setTanaType((short) 1);
        WorkPriorityOrderRestrictSet set1 = new WorkPriorityOrderRestrictSet();
        WorkPriorityOrderRestrictSet set2 = new WorkPriorityOrderRestrictSet();
        BeanUtils.copyProperties(set, set1);
        BeanUtils.copyProperties(set, set2);
        set1.setTaiCd(1);
        System.out.println(set1.getTaiCd());
        System.out.println(set2.getTaiCd());
    }
}

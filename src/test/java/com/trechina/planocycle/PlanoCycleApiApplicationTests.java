package com.trechina.planocycle;

import com.trechina.planocycle.entity.po.ProductPowerSyokika;
import com.trechina.planocycle.mapper.ProductPowerDataMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class PlanoCycleApiApplicationTests {
@Autowired
private ProductPowerDataMapper productPowerDataMapper;



    @Test
    public void test1() {
     //productPowerDataMapper.endYobiiiternDataForWk("0001", 123, "10215814");
        List<ProductPowerSyokika> productPowerSyokikas = productPowerDataMapper.selectSyokika("0001", 1);
        productPowerSyokikas.forEach(System.out::println);
        //productPowerDataMapper.endYobiiiternForWk("0001", 123, "10215814");
    // productPowerDataMapper.endSyokikaForWK("0001", 123, "10215814");
        //productPowerDataMapper.endGroupForWK("0001", 123, "10215814");
    }

    public static Object arrayToObject(Object[] obj,Class<?> classType){
        Object stu1 = null;
        try {
            stu1=classType.newInstance();
            for (int i = 0;i < classType.getDeclaredFields().length;i++){
                String setMethodName = "set"+classType.getDeclaredFields()[i].getName().substring(0,1).toUpperCase()+classType.getDeclaredFields()[i].getName().substring(1);
                Method setMethod = classType.getDeclaredMethod(setMethodName, new Class[]{classType.getDeclaredFields()[i].getType()});
                setMethod.invoke(stu1,obj[i]);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return stu1;
    }

}

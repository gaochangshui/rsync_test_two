package com.trechina.planocycle;

import com.trechina.planocycle.entity.po.ProductPowerMstData;
import com.trechina.planocycle.mapper.ProductPowerDataMapper;
import com.trechina.planocycle.mapper.ProductPowerMstMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
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
@Autowired
private ProductPowerDataMapper productPowerDataMapper;
@Autowired
    ProductPowerMstMapper productPowerMstMapper;


    @Test
    public void test1() {
     //   productPowerDataMapper.endYobiiiternDataForWk("0001", 123, "10215814");
       // productPowerDataMapper.setWkSyokikaForFinally("0001",1,"10215814");
       // productPowerDataMapper.setWkGroupForFinally("0001",1,"10215814");
       // productPowerDataMapper.setWkYobilitemForFinally("0001",1,"10215814");
       // productPowerDataMapper.setWkDataForFinally("0001",1,"10215814");
        //productPowerDataMapper.setWKData("0001",1,"10215814");
        //productPowerDataMapper.setWkYobilitemDataForFinally("0001",1,"10215814");

       // List<ProductPowerMstData> productPowerMstDataList = productPowerDataMapper.selectWKKokyaku("10215814","0001");
        //productPowerMstDataList.stream().forEach(System.out::println);
      //  productPowerMstMapper.delete("0001",735 ,"10215814","10215814");
        //productPowerDataMapper.endSyokikaForWK("0001",735 ,"10215814");
      //  productPowerDataMapper.endGroupForWK("0001",735 ,"10215814");
     /*   productPowerDataMapper.phyDeleteYobiiitern("0001",735 ,"10215814");
        productPowerDataMapper.phyDeleteYobiiiternData("0001",735 ,"10215814");
        productPowerDataMapper.endYobiiiternForWk("0001",735 ,"10215814");
        productPowerDataMapper.endYobiiiternDataForWk("0001",735 ,"10215814");

//*///productPowerDataMapper.deleteWKYobiiiternData("10215814","0001");
//        productPowerDataMapper.setWkYobilitemDataForFinally("0001",735,"10215814");
    /*    productPowerDataMapper.phyDeleteYobiiitern("0001",735 ,"10215814");
        productPowerDataMapper.phyDeleteYobiiiternData("0001",735 ,"10215814");
        productPowerDataMapper.endYobiiiternForWk("0001",735 ,"10215814");
        productPowerDataMapper.endYobiiiternDataForWk("0001",735 ,"10215814");*/
      /*  productPowerDataMapper.phyDeleteGroup("0001",735 ,"10215814");
        productPowerDataMapper.endGroupForWK("0001",735 ,"10215814");*/
      /*  productPowerDataMapper.phyDeleteYobiiitern("0001",801 ,"10212159");
        productPowerDataMapper.phyDeleteYobiiiternData("0001",801 ,"10212159");
        productPowerDataMapper.endYobiiiternForWk("0001",801 ,"10212159");
        productPowerDataMapper.endYobiiiternDataForWk("0001",801 ,"10212159");*/
       /* List<ReserveMstVo> reserve = productPowerDataMapper.getReserve(801, "0001");
        reserve.forEach(System.out::println);
        System.out.println(reserve);
*/
        //List<ProductPowerMstData> productPowerMstDataList = productPowerDataMapper.selectWKKokyaku("10212159", "0001");
        //List<WKYobiiiternData> wkYobiiiternDataList = productPowerDataMapper.selectWKYobiiiternData( "10212159", "0001");
       // wkYobiiiternDataList.forEach(System.out::println);
/*        Integer integer = Integer.valueOf("3100" + 1);
        Integer integer1 = Integer.valueOf("3100" + 1);
        boolean b = integer.equals(integer1);
        System.out.println(b);*/


       /* productPowerDataMapper.deleteData("0001",735 ,"10215814");
        productPowerDataMapper.setData(735 ,"10215814","0001");*/
     //   productPowerDataMapper.endSyokikaForWK("0001",2,"10215814");
       // productPowerDataMapper.endGroupForWK("0001",1,"10215814");       // StringBuilder strs = new StringBuilder();
       // productPowerDataMapper.endYobiiiternForWk("0001",1,"10215814");
        //productPowerDataMapper.endYobiiiternDataForWk("0001",1,"10215814");
        //productPowerDataMapper.deleteData("0001",1,"10212159");
       // productPowerDataMapper.setData(1,"0001","10212159");
        //productPowerDataMapper.getAllData("0001",1).stream().forEach(System.out::println);
       /* for (int i=0;i<=1000;i++){
            String str="0001 "+(i)+" 1 1 1 1 1 1 1 1 1@";
            s
            System.out.println(str);
        }*/
        //List<ProductPowerMstData> productPowerMstData = productPowerDataMapper.selectWKKokyaku("0001", "10215814");
        //List<ProductPowerSyokika> productPowerSyokikas = productPowerDataMapper.selectSyokika("0001", 1);
        // productPowerMstData.forEach(System.out::println);
        //productPowerDataMapper.endYobiiiternForWk("0001", 123, "10215814");
        // productPowerDataMapper.endSyokikaForWK("0001", 123, "10215814");
        //productPowerDataMapper.endGroupForWK("0001", 123, "10215814");
        //  productPowerDataMapper.deleteYobiiitern("0001", 1,"10215814");
     /*   List<WKYobiiiternData> wkYobiiiternData = productPowerDataMapper.selectWKYobiiiternData("0001", "10215814");
        //wkYobiiiternData.forEach(System.out::println);
        for (WKYobiiiternData wkYobiiiternDatum : wkYobiiiternData) {
            if (wkYobiiiternDatum.getDataSort()==3){
                System.out.println(wkYobiiiternDatum);
                System.out.println("刘新宇");
            }
        }
    }*/
        /*List<ProductPowerMstData> productPowerMstDataList = productPowerDataMapper.rankCalculates();
        Collections.sort(productPowerMstDataList, new Comparator<ProductPowerMstData>() {
            @Override
            public int compare(ProductPowerMstData o1, ProductPowerMstData o2) {
            return (o2.getgCompareAmount()).compareTo(o1.getgCompareAmount());
            }
        });*/

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
@Test
    public void test4(){
        List<Student> list = new ArrayList();
        Student student = new Student();
        student.setAge(10);
    Student s1 = new Student();
        s1.setAge(140);
    Student s2 = new Student();
        s2.setAge(1);
    Student s3 = new Student();
        s3.setAge(10355);
    Student s4 = new Student();
        s4.setAge(1001);
        list.add(s2);
        list.add(s1);
        list.add(student);
        list.add(s3);
        list.add(s4);




    }
/*
    @Test
    public void  test6() throws NoSuchFieldException, IllegalAccessException {
        RankCalculateVo vo = new RankCalculateVo();
        vo.setgBranchAmount(1);
        Class c = vo.getClass();
        Field field = c.getDeclaredField("pPosAmount");

        field.setAccessible(true);
        field.set(vo,1);

        System.out.println(field.get(vo));
        System.out.println(vo.getpPosAmount());
    }


    public void getpCompareAmount(List<ProductPowerMstData> productPowerMstDataList, RankCalculateVo rankCalculateVo,String fieldName,String filedName1){
        if (rankCalculateVo.getpCompareAmount()!=0) {
            Collections.sort(productPowerMstDataList, new Comparator<ProductPowerMstData>() {
                @Override
                public int compare(ProductPowerMstData o1, ProductPowerMstData o2) {

                    return o2.getpCompareAmount().compareTo(o1.getpCompareAmount());
                }
            });


            int i = 1;
            for (ProductPowerMstData productPowerMstData : productPowerMstDataList) {
                productPowerMstData.setpCompareAmountRank(i++);
            }
        }
    }*/

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
            data.setPdPosAmount(new BigDecimal(Math.random() * 10));
            data.setPdPosNum(new BigDecimal(Math.random() * 10));
            data.setPdBranchAmount(new BigDecimal(Math.random() * 10));

            data.setGdPosAmount(new BigDecimal(Math.random() * 10));
            data.setGdPosNum(new BigDecimal(Math.random() * 10));
            data.setGdBranchAmount(new BigDecimal(Math.random() * 10));
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
        System.out.println();
    }

}

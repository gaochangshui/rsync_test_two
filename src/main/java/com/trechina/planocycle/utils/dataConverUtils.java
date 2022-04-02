package com.trechina.planocycle.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class dataConverUtils {
    /**
     * 用来处理优先順位表各种mst信息插入的数据转换
     * 因はい插入的代码完全一致，只有模板クラス参数不一致，統一接收不同模板クラス，处理完数据后返回。
     * @param cla
     * @param companyCd
     * @param priorityOrderCd
     * @param <U>
     * @return
     */
    public  <U> List<U> priorityOrderCommonMstInsertMethod(Class<U> clazz,List<U> cla, String companyCd, Integer priorityOrderCd) {
        List <U> uList = new ArrayList<>();
        cla.forEach(item ->{
            U u = clazz.cast(item);
            for (Field field : u.getClass().getDeclaredFields()){
                field.setAccessible(true);
                try {
                    if (field.getName().equals("companyCd")){
                        field.set(u,companyCd);
                    }
                    if (field.getName().equals("priorityOrderCd")){
                        field.set(u,priorityOrderCd);
                    }
                } catch (IllegalAccessException e) {

                }
            }
            uList.add(u);
            System.out.println(uList);
        });
        return uList;
    }
}

package com.trechina.planocycle.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class dataConverUtils {
    /**
     * 用来処理優先順位表各種mst信息插入的数据convert
     * 因为插入的code完全一致，只有模板クラス参数不一致，統一接收不同模板クラス，処理完数据后返回。
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

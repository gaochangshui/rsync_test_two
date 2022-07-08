package com.trechina.planocycle.utils;

import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class dataConverUtils {
    /**
     * 用来処理優先順位表各種mst信息插入的数据convert
     * なぜなら插入的code完全一致，只有模板クラス参数不一致，統一接收不同模板クラス，処理完数据后返回。
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

    /**
     *　スネークケース-->キャメルケースに変更
     * @param str　スネークケースの文字
     * @return キャメルケースの文字
     */
    public static String camelize(String str){
        if(!StringUtils.hasLength(str)){
            return str;
        }
        Pattern pattern = Pattern.compile("(.*)_(\\w)(.*)");
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()){
            return camelize(matcher.group(1) + matcher.group(2).toUpperCase() + matcher.group(3));
        }else{
            return str;
        }
    }

}

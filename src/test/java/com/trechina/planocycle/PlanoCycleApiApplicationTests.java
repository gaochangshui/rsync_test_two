/*
package com.trechina.planocycle;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RunWith(SpringRunner.class)
class PlanoCycleApiApplicationTests {

    @Test
    void contextLoads() {

    String[] str = new String[3];
    str[0]="zhangsan";
    str[1]="age";
    str[2]="addr";
        System.out.println(str);
        Student student = new Student();
        for (int i = 0; i < str.length; i++) {
            student.setName(str[0]);
            student.setAge(str[1]);
            student.setAddr(str[2]);
        }
        System.out.println(student);

    }




    public static void main(String[] args) {
        String[] str = new String[3];
        str[0]="zhangsan";
        str[1]="20";
        str[2]="qd";
        Student student =(Student) arrayToObject(str, Student.class);
        System.out.println("name"+student.getName());
        System.out.println("age"+student.getAge());
        System.out.println("addr"+student.getAddr());
        System.out.println(student.toString());

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
*/

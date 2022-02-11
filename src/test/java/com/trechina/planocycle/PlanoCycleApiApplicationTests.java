package com.trechina.planocycle;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
class PlanoCycleApiApplicationTests {

    @Test
    void contextLoads() {

        Integer t1[] ={1,2,3,4,5,6};
        Integer t2[] ={4,5,6,7,8};
        ArrayList<Object> t3 = new ArrayList<>();
        for (int i = 0; i < t1.length; i++) {
            boolean isEq=false;
            for (int j =0 ;j < t2.length; j++){
                if (t1[i].equals(t2[j])){
                    isEq=true;
                    break;
                }
            }
            if (!isEq){
                t3.add(t1[i]);
                System.out.println(t1[i]);
            }
        }
        t3.forEach(System.out::print);
    }

    @Test
    public void test1(){
        ArrayList<Integer> t1 = new ArrayList();

        t1.add(3);
        t1.add(4);
        ArrayList<Integer> t2 = new ArrayList();
        t2.add(1);
        t2.add(2);
        t2.add(3);
        t2.add(4);

        t1.removeAll(t2);
        System.out.println(t1);
    }



}

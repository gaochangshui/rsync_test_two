package com.trechina.planocycle.utils;


import java.util.ArrayList;
import java.util.List;

public class ListDisparityUtils {
    public static List<Integer> getListDisparit( List<Integer> t1,List<Integer> t2){

       ArrayList<Integer> t3 = new ArrayList<>();
       t3.addAll(t1);

       /* for (int i = 0; i < t1.size(); i++) {
            boolean isEq=false;
            for (int j =0 ;j < t2.size(); j++){
                if (t1.get(i).equals(t2.get(j))){
                    isEq=true;
                    break;
                }
            }
            if (!isEq){
                t3.add(t1.get(i));
            }
        }
        return t3;*/
        t3.removeAll(t2);
        return t3;
    }
    public static List<String> getListDisparitStr( List<String> t1,List<String> t2){

        ArrayList<String> t3 = new ArrayList<>();
        t3.addAll(t1);

       /* for (int i = 0; i < t1.size(); i++) {
            boolean isEq=false;
            for (int j =0 ;j < t2.size(); j++){
                if (t1.get(i).equals(t2.get(j))){
                    isEq=true;
                    break;
                }
            }
            if (!isEq){
                t3.add(t1.get(i));
            }
        }
        return t3;*/
        t3.removeAll(t2);
        return t3;
    }
}

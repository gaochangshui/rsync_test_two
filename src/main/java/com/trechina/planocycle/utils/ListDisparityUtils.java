package com.trechina.planocycle.utils;


import java.util.ArrayList;
import java.util.List;

public class ListDisparityUtils {
    private ListDisparityUtils() {}
    public static List<Integer> getListDisparit( List<Integer> t1,List<Integer> t2){

       ArrayList<Integer> t3 = new ArrayList<>();
       t3.addAll(t1);
        t3.removeAll(t2);
        return t3;
    }
    public static List<String> getListDisparitStr( List<String> t1,List<String> t2){

        ArrayList<String> t3 = new ArrayList<>();
        t3.addAll(t1);
        t3.removeAll(t2);
        return t3;
    }
}

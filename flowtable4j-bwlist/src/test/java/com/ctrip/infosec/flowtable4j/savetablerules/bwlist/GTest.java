package com.ctrip.infosec.flowtable4j.savetablerules.bwlist;

import org.junit.Test;

import java.util.*;

/**
 * Created by thyang on 2015/3/24 0024.
 */
public class GTest {

    public void printStrings(String... args) {
        for (String s : args) {
            System.out.println(s);
        }
    }

    @Test
    public void main() {
//        long Ip = 1033410193;
//        long a =  (Ip & 0xFF000000)>>24;
//        long b =  (Ip & 0x00FF0000)>>16;
//        long c =  (Ip & 0x0000FF00)>> 8;
//        long d =   Ip & 0x000000FF;
//
//        System.out.println(a + "." + b + "." + c + "." + d);
//
//        String ipS ="192.168.11.22";
//        String[] ss= ipS.split("[.]|[:]");
//        for(String s:ss)
//        {
//            System.out.println(s);
//        }
        String ip="61.152.150.145";
        long n_Ip = 0;
        if (ip != null && ip.length()>7) {
            String[] arr = ip.split("[.]|[:]");
            if (arr.length >= 4) {
                long a = Long.parseLong(arr[0].toString());
                long b = Long.parseLong(arr[1].toString());
                long c = Long.parseLong(arr[2].toString());
                long d = Long.parseLong(arr[3].toString());
                n_Ip = (((((a << 8) | b) << 8) |c)<<8) | d;
            }
        }
        System.out.println(n_Ip);

    }

    public void match(String source, String pattern) {
        int offset = 0;
        int num = 0;
        int value = 0;
        int[] arr = buildPMT(pattern);
        while (offset + pattern.length() <= source.length()) {
            for (int i = 0; i < pattern.length(); i++) {
                if (pattern.charAt(i) == source.charAt(i + offset)) {
                    num++;
                    if(i==pattern.length()-1){
                        System.out.println("match!");
                    }
                } else {
                    if(i>0){
                        value=arr[i-1];
                    }
                    break;
                }
            }
            offset+=num==0?1:(num-value);
            num=0;
            value=0;
        }
    }

    //partial match table 部分匹配表
    public int[] buildPMT(String str) {
        int[] arr = new int[str.length()];

        for (int i = 0; i < str.length(); i++) {
            arr[i] = getValue(str.substring(0, i + 1));
        }

        return arr;
    }

    public int getValue(String str) {
        int count=0;
        int length=str.length();
        HashMap<Integer,Integer> map0 = new HashMap<Integer, Integer>();
        HashMap<Integer,Integer> map1 = new HashMap<Integer, Integer>();
        for(int i=0;i<length-1&&length>1;i++){

            String str0=str.substring(0,i+1);
            String str1=str.substring(length-1-i,length);

            if(str0.equals(str1)){
                count=str0.length();
            }
        }
        return count;
    }
}

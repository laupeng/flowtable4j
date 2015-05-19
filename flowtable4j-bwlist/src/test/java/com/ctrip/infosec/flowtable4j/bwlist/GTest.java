package com.ctrip.infosec.flowtable4j.bwlist;

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
//        String src = "select UserIPValue from CTRIP_FLT_UserIPValue_Uid(nolock)  Where Uid = @Uid and CreateDate>=@StartTimeLimit and CreateDate<=@TimeLimit";
//        printStrings(src.replaceAll("@", ":"));
//        printStrings(null, "BB");
//        printStrings();
         Map<String, Integer> ss= new HashMap<String, Integer>();
         ss.put("AA",11);
         ss.put("AA",22);
         System.out.println(ss.get("AA"));
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

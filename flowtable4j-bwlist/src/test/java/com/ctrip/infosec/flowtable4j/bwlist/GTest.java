package com.ctrip.infosec.flowtable4j.bwlist;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thyang on 2015/3/24 0024.
 */
public class GTest {

    public void printStrings(String...args){
       for (String s:args){
           System.out.println(s);
       }
    }

    @Test
    public void main()
    {
        String src="select UserIPValue from CTRIP_FLT_UserIPValue_Uid(nolock)  Where Uid = @Uid and CreateDate>=@StartTimeLimit and CreateDate<=@TimeLimit";
        printStrings(src.replaceAll("@",":"));
        printStrings(null, "BB");
        printStrings();
    }
}

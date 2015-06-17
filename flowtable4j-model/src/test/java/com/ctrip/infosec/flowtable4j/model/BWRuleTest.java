package com.ctrip.infosec.flowtable4j.model;

import java.text.SimpleDateFormat;
import java.util.*;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by thyang on 2015/3/18 0018.
 */

public class BWRuleTest {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
     @Test
     public void testFactExtent()
    {
        BWFact fact= new BWFact();
        fact.setContent(new HashMap<String, Object>());
        fact.getContent().put("NAME", "BRUCE YANG");
        fact.getContent().put("AGE", "40");
        Map<String,Object> address = new HashMap<String, Object>();
        address.put("NATION", "CHINA");
        address.put("POSTCODE", "200135");
        fact.getContent().put("ADDRESS", address);
        List<String> hobby=new ArrayList<String>();
        hobby.add("GAME");
        hobby.add("CHESS");
        hobby.add("TOUR");
        fact.getContent().put("HOBBY",hobby);
        List<Map<String,Object>> college= new ArrayList<Map<String, Object>>();
        Map<String,Object> c1= new HashMap<String, Object>();
        c1.put("NAME", "XUHONG");
        c1.put("LEVEL", 17);
        college.add(c1);
        Map<String,Object> c2= new HashMap<String, Object>();
        c2.put("NAME", "XUYAN");
        c2.put("LEVEL", 15);
        college.add(c2);
        fact.getContent().put("COLLEGE", college);

        //Test getString
        Assert.assertEquals(fact.getString("NAME"), "BRUCE YANG");
        Assert.assertEquals(fact.getString("AGE".split("[.]")), "40");
        Assert.assertEquals(fact.getString(new String[]{"NAME"}), "BRUCE YANG");
        Assert.assertEquals(fact.getString("BBB"),null);
        Assert.assertEquals(fact.getString("ADDRESS.NATION".split("[.]")),"CHINA");
        Assert.assertEquals(fact.getString("ADDRESS.CITY".split("[.]")),null);
        Assert.assertEquals(fact.getString("BEIJING.CITY".split("[.]")), null);

        //Test Array
        Assert.assertEquals(fact.getList("HOBBY").size(),3);
        Assert.assertEquals(fact.getList("HOBBY").get(1),"CHESS");
        Assert.assertEquals(fact.getList("COLLEGE").size(), 2);
        Map<String,Object> c3=(Map<String,Object>) fact.getList("COLLEGE").get(0);
        Assert.assertEquals(c3.get("NAME"),"XUHONG");

        //Test getMap
        Assert.assertEquals(fact.getMap("BBB"),null);
        Assert.assertEquals(fact.getMap("ADDRESS").get("POSTCODE"),"200135");

    }
}

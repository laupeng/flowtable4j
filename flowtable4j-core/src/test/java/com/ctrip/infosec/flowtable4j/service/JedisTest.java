package com.ctrip.infosec.flowtable4j.service;

import com.ctrip.flowtable4j.core.checkPaymentAdapt.AccountCheckItem;
import com.ctrip.flowtable4j.core.checkPaymentAdapt.PaymentViaAccount;
import org.apache.commons.lang.time.FastDateFormat;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangsx on 2015/3/18.
 */
public class JedisTest {
    public void testJedisOp(){
        String host = "";
        int port = 0;
        int timeout = 0;
        Jedis jedis = new Jedis(host,port,timeout);
    }

    @Ignore
    @Test
    public void testCRedis(){
//        RAppSetting.setAppID("100000807");
//        RAppSetting.setLoggingServerIP("collector.logging.uat.qa.nt.ctripcorp.com");//"collector.logging.uat.qa.nt.ctripcorp.com"
//        RAppSetting.setLoggingServerPort("63100");
//        RAppSetting.setLogging(false);
//        RAppSetting.setCRedisServiceUrl("http://ws.config.framework.fws.qa.nt.ctripcorp.com/configws/");//"http://ws.config.framework.fws.qa.nt.ctripcorp.com/configws/"
//
//        CacheProvider cache = CacheFactory.GetProvider("AccountRiskControl");
//        List<String> list = cache.lrange("CHECKTYPE:1|SCENETYPE:35|CHECKVALUE:15012341235", 0, -1);


//        System.out.println(">>>");
//        System.out.println(list.size());
////        System.out.println(cache.get("CHECKTYPE:1|SCENETYPE:35|CHECKVALUE:15012341235"));
//        System.out.println("<<<");
        PaymentViaAccount paymentViaAccount = new PaymentViaAccount();
        List<AccountCheckItem> list = new ArrayList<AccountCheckItem>();
        AccountCheckItem item = new AccountCheckItem("IP","LOGIN-SITE\"","192.168.22.08");
        list.add(item);
        Map<String, Integer> map = paymentViaAccount.CheckBWGRule(list);
        System.out.println(map.size());
    }

    @Ignore
    @Test
    public void testDateFormat(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
        try {
            System.out.println(">>>");
            System.out.println(sdf.parse("1990-12-11 06:11:22.000"));
            System.out.println(">>>");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

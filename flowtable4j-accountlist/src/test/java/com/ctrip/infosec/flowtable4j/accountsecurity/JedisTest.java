package com.ctrip.infosec.flowtable4j.accountsecurity;

import com.ctrip.infosec.flowtable4j.model.AccountFact;
import com.ctrip.infosec.flowtable4j.model.AccountItem;
import com.ctrip.infosec.flowtable4j.model.RuleContent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * Created by zhangsx on 2015/3/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/flowtable4j*.xml"})
public class JedisTest {
    @Autowired
    private AccountBWGManager accountBWGManager;
    @Test
    public void testMerge(){
        List<RuleContent> list = new ArrayList<RuleContent>();
        RuleContent content0 = new RuleContent();
        content0.setSceneType("PAY");
        content0.setCheckType("UID");
        content0.setCheckValue("test1");
        content0.setExpiryDate("2099-10-10T00:00:00");
        content0.setResultLevel(10);

        RuleContent content1 = new RuleContent();
        content1.setSceneType("PAY");
        content1.setCheckType("UID");
        content1.setCheckValue("test2");
        content1.setExpiryDate("2099-10-10T00:00:00");
        content1.setResultLevel(20);

        RuleContent content2 = new RuleContent();
        content2.setSceneType("PAY1");
        content2.setCheckType("UID");
        content2.setCheckValue("test1");
        content2.setExpiryDate("2099-10-10T00:00:00");
        content2.setResultLevel(40);

        RuleContent content3 = new RuleContent();
        content3.setSceneType("PAY");
        content3.setCheckType("UID");
        content3.setCheckValue("test1");
        content3.setExpiryDate("2099-10-10T00:00:00");
        content3.setResultLevel(120);

        RuleContent content4 = new RuleContent();
        content4.setSceneType("PAY");
        content4.setCheckType("UID");
        content4.setCheckValue("test1");
        content4.setExpiryDate("2099-10-10T00:00:00");
        content4.setResultLevel(140);
//        list.add(content0);
//        list.add(content1);
        list.add(content2);
        list.add(content3);
        list.add(content4);

//        paymentViaAccount.removeBWGRule(list);
//        paymentViaAccount.setBWGRule(list);

        AccountFact fact = new AccountFact();
        AccountItem item = new AccountItem("UID","CREDIT-EXCHANGE","zsx123");
        AccountItem item1 = new AccountItem("UID","CREDIT-EXCHANGE","t2");
        AccountItem item2 = new AccountItem("UID","CREDIT-EXCHANGE","t1");
//        AccountItem item2 = new AccountItem("UID","PAYMENT-CONF-WEIXIN","D00026295");
        List<AccountItem> items = new ArrayList<AccountItem>();
        items.add(item);
//        items.add(item1);
//        items.add(item2);
//        items.add(item1);
        fact.setCheckItems(items);
        Map<String,Integer> map = new HashMap<String, Integer>();
        accountBWGManager.checkBWGRule(fact, map);

        final int i=0;

        for(Iterator<String> it=map.keySet().iterator();it.hasNext();){
            String key = it.next();
            System.out.println(key+":"+map.get(key));
        }
    }
}

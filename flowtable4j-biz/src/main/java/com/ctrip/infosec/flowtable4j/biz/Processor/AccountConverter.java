package com.ctrip.infosec.flowtable4j.biz.processor;

import com.ctrip.infosec.flowtable4j.model.AccountFact;
import com.ctrip.infosec.flowtable4j.model.AccountItem;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.google.common.base.Strings;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Convert PO Entity to for Account BWG Check
 * Created by thyang on 2015-06-10.
 */
@Component
public class AccountConverter extends ConverterBase {
    private static List<String> sceneTypes = new ArrayList<String>();
    static {
        sceneTypes.add("PAYMENT-CONF-LIPIN");
        sceneTypes.add("PAYMENT-CONF-CC");
        sceneTypes.add("PAYMENT-CONF-CCC");
        sceneTypes.add("PAYMENT-CONF-CTRIPAY");
        sceneTypes.add("CREDIT-EXCHANGE");
        sceneTypes.add("CTRIPAY-CASHOUT");
        sceneTypes.add("CASH-EXCHANGE");
        sceneTypes.add("PAYMENT-CONF-DCARD");
        sceneTypes.add("PAYMENT-CONF-ALIPAY");
        sceneTypes.add("PAYMENT-CONF-CASH");
        sceneTypes.add("PAYMENT-CONF-WEIXIN");
        sceneTypes.add("PAYMENT-CONF-EBANK");
        sceneTypes.add("CREDIT-GUARANTEE");
    }

    public AccountFact convert(PO po){
        AccountFact fact=new AccountFact();
        List<AccountItem> items=new ArrayList<AccountItem>();
        String uid = getString(po.getProductinfo(),new String[]{"userinfo","uid"});
        String did =getString(po.getProductinfo(),new String[]{"deviceid","did"});;
        String ip  =getString(po.getProductinfo(),new String[]{"ipinfo","useripadd"});;
        if(!Strings.isNullOrEmpty(uid)){
            for(String s:sceneTypes){
                items.add(new AccountItem("UID",s,uid));
            }
        }
        if(!Strings.isNullOrEmpty(did)){
            for(String s:sceneTypes){
                items.add(new AccountItem("DID",s,did));
            }
        }
        if(!Strings.isNullOrEmpty(ip)){
            for(String s:sceneTypes){
                items.add(new AccountItem("IP",s,ip));
            }
        }
        fact.setCheckItems(items);
        return fact;
    }
}

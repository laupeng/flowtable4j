package com.ctrip.infosec.flowtable4j.biz.converter;

import com.ctrip.infosec.flowtable4j.biz.baseconverter.ConverterBase;
import com.ctrip.infosec.flowtable4j.model.AccountFact;
import com.ctrip.infosec.flowtable4j.model.AccountItem;
import com.ctrip.infosec.flowtable4j.model.RequestBody;
import com.google.common.base.Strings;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /**
     * 暂时只考虑 DID、UID、UserIP
     * @param requestBody
     * @return
     */
    public AccountFact convert(RequestBody requestBody){
        AccountFact fact=new AccountFact();
        List<AccountItem> items=new ArrayList<AccountItem>();
        Map<String,Object> eventBody= requestBody.getEventBody();
        String uid =getString(eventBody,"uid");
        String did =getString(eventBody,"did");;
        String ip  =getString(eventBody,"useripadd");;
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

package com.ctrip.infosec.flowtable4j.v2m.converter;

import com.ctrip.infosec.flowtable4j.model.BWFact;
import com.ctrip.infosec.flowtable4j.model.MapX;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;

/**
 * 转换PO为黑白名单，支付风控黑白名单
 * Created by thyang on 2015-06-10.
 */
public class BlackWhiteConverter extends ConverterBase {
    public BWFact convert(PO po){

        BWFact fact=new BWFact();
        Map<String,Object> content = new HashMap<String, Object>();
        fact.setContent(content);

        fillProductInfoToMap(po,"IPInfo",content,ImmutableMap.of("IPCity","IPCity","IPCountry","IPCountry","UserIPAdd","UserIP"));

        fillProductInfoToMap(po,"OtherInfo",content,ImmutableMap.of("OrderToSignUpDate","OrderToSignUpDate"));



        return fact;
    }

    protected void fillProductInfoToMap(PO po,String keyPath,Map<String,Object> target,Map<String,String> fieldMap){
        Map<String,Object> src =(Map<String,Object>) MapX.getMap(po.getProductInfo(),keyPath);
        if(src!=null) {
            for (String key : fieldMap.keySet()) {
                 setValue(target,fieldMap.get(key),getString(src,key));
            }
        }
    }

    protected void fillPaymentInfoToMap(PO po,String keyPath,Map<String,Object> target){

    }

}

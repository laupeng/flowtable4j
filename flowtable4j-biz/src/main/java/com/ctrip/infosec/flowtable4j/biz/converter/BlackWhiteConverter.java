package com.ctrip.infosec.flowtable4j.biz.converter;

import com.ctrip.infosec.flowtable4j.biz.baseconverter.ConverterBase;
import com.ctrip.infosec.flowtable4j.model.BWFact;
import com.ctrip.infosec.flowtable4j.model.RequestBody;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 转换PO为黑白名单，支付风控黑白名单
 * Created by thyang on 2015-06-10.
 */
@Component
public class BlackWhiteConverter extends ConverterBase {

    public BWFact convert(RequestBody requestBody) {
        BWFact fact = new BWFact();
        Map<String, Object> content = new HashMap<String, Object>();
        Map<String, Object> eventBody= requestBody.getEventBody();
        fact.setContent(content);
        fact.setOrderType(Integer.parseInt(getString(eventBody,"ordertype","0")));
        fact.setOrderTypes(new ArrayList<Integer>());
        fact.getOrderTypes().add(0);
        fact.getOrderTypes().add(fact.getOrderType());
        Map<String,Object> bwlist=getMap(eventBody,"blacklist");
        if(bwlist!=null && bwlist.size()>0) {
            content.putAll(bwlist);
        }
        return fact;
    }

}

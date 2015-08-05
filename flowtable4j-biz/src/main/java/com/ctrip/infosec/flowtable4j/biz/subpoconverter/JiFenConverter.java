package com.ctrip.infosec.flowtable4j.biz.subpoconverter;

import com.ctrip.infosec.flowtable4j.biz.baseconverter.ConverterBase;
import com.ctrip.infosec.flowtable4j.model.CtripOrderType;
import com.ctrip.infosec.flowtable4j.model.CtripSubOrderType;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015-08-04.
 */
@Component
public class JiFenConverter extends ConverterBase {

    public void fillDistribution(Map<String, Object> productInfo, Map<String, Object> eventBody, int orderType,int subOrderType) {
        Map<String, Object> distribut=null;
        if(subOrderType== CtripSubOrderType.FNCMall.getCode()|| subOrderType== CtripSubOrderType.NEWFNCMall.getCode()){
            distribut=copyMap(eventBody, ImmutableMap.of("distributiondistrict","district","distributioncity","city","distributionprovince","province","receivertelephone","telcall"));
            copyMap(eventBody, distribut, new String[]{"distributionrecipient","telno"});
            setValue(distribut,"distributionadress","");
        }  else if(  orderType==CtripOrderType.TravelMoney.getCode() ){
            distribut=copyMap(eventBody, ImmutableMap.of("distributiondistrict","district","distributioncity","city","distributionprovince","province"));
            copyMap(eventBody, distribut, new String[]{"distributionadress","shippingfee","orderdeliveryid","distributionzipcode","distributionrecipient"});
        }

        if (orderType == CtripOrderType.CRH.getCode()) {
            distribut=copyMap(getMap(eventBody, "distributioninfo"), "infosecurity_distributioninfo");
        }

        if(orderType== CtripOrderType.Insurer.getCode()){
            distribut=copyMap(eventBody,"infosecurity_distributioninfo");
        }
        if (orderType == CtripOrderType.JiFen.getCode()) {
            distribut = createMap();
            Map<String, Object> contactMap = getMap(eventBody, "deliveryinfoview");
            if (contactMap != null && contactMap.size() > 0) {
                copyValue(contactMap, "city", distribut, "city");
                copyValue(contactMap, "address", distribut, "distributionadress");
                copyValue(contactMap, "recipient", distribut, "distributionrecipient");
                copyValue(contactMap, "zipcode", distribut, "distributionzipcode");
                copyValue(contactMap, "district", distribut, "district");
                copyValue(contactMap, "province", distribut, "province");
                copyValue(contactMap, "telno", distribut, "telno");
                copyValue(contactMap, "ishistoryaddress", distribut, "ishistoryaddress");
            }
        }
        setValue(productInfo, "distribution", distribut);
    }

    public void fillJifenOrderItemList(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        List<Map<String, Object>> jifenList = createList();
        List<Map<String, Object>> jifenListMap = getList(eventBody, "suborderlist");
        if (jifenListMap != null && jifenListMap.size() > 0) {
            for (Map<String, Object> jifenMap : jifenListMap) {
                Map<String, Object> jifen = createMap();
                //SubOrder
                Map<String, Object> order = createMap();
                copyValue(jifenMap, "quantity", order, "quantity");
                setValue(jifen, "order", order);
                //Greeting Card
                Map<String, Object> greetMap = getMap(jifenMap, "greetingcardinfoview");
                if (greetMap != null && greetMap.size() > 0) {
                    Map<String, Object> greet = createMap();
                    copyMap(greetMap, greet, "infosecurity_greetingcardinfoviewbyjifen");
                    setValue(jifen, "greetingcard", greet);
                }
                //Prize Detail
                Map<String, Object> prizeMap = getMap(jifenMap, "prizedetail");
                if (prizeMap != null && prizeMap.size() > 0) {
                    Map<String, Object> prize = createMap();
                    copyMap(prizeMap, prize, "infosecurity_prizedetailitembyjifen");
                    copyValue(prizeMap,"categoryid",prize,"prizecategoryid");
                    copyValue(prizeMap,"categoryname",prize,"prizecategoryname");
                    copyValue(prizeMap,"name",prize,"prizename");
                    copyValue(prizeMap,"permission",prize,"prizepermission");
                    copyValue(prizeMap,"status",prize,"prizestatus");
                    copyValue(prizeMap,"storage",prize,"prizestorage");
                    copyValue(prizeMap,"storagemanagetype",prize,"prizestoragemanagetype");
                    copyValue(prizeMap,"subcategoryid",prize,"prizesubcategoryid");
                    copyValue(prizeMap,"subtypecode",prize,"prizesubtypecode");
                    copyValue(prizeMap,"subtypevalue",prize,"prizesubtypevalue");
                    copyValue(prizeMap,"zonecode",prize,"prizezonecode");
                    copyValue(prizeMap,"zoneid",prize,"prizezoneid");
                    setValue(jifen, "prizedetail", prize);
                }
                setValue(jifen,"paymentitem",copyMap(getMap(jifenMap,"paymentwayview"),"infosecurity_paymentitemviewbyjifen"));
                jifenList.add(jifen);

            }
        }
        setValue(productInfo, "jifenorderitemlist", jifenList);
    }
}

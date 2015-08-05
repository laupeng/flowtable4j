package com.ctrip.infosec.flowtable4j.biz.subpoconverter;

import com.ctrip.infosec.flowtable4j.biz.baseconverter.ConverterBase;
import com.ctrip.infosec.flowtable4j.model.CtripOrderType;
import com.ctrip.infosec.flowtable4j.model.CtripSubOrderType;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015-08-04.
 */
@Component
public class TraveMoneyConverter extends ConverterBase {

    public void fillFNCMallList(Map<String, Object> productInfo, Map<String, Object> eventBody){
        List<Map<String,Object>> fncList = createList();
        Map<String,Object> fnc = createMap();
        setValue(fnc,"travelmoneyfncmall",copyMap(eventBody,new String[]{"mallorderremark","operatetype","sourcecode","totalexperience","totalprice"}));
        setValue(fnc,"suborderitemlist",copyList(getList(eventBody,"fncmallsuborderitems"),"infosecurity_fncmallsuborderitem"));
        fncList.add(fnc);
        setValue(productInfo,"fncmalllist",fncList);
    }

    public void fillInvoiceInfoList(Map<String, Object> productInfo, Map<String, Object> eventBody,int orderType,int subOrderType) {
        if(orderType== CtripOrderType.TravelMoney.getCode()) {
            setValue(productInfo,"invoiceinfolist",copyList(getList(eventBody,"invoiceinfolist"),"infosecurity_invoicelistinfo"));
        }
        else if(orderType== CtripOrderType.Car.getCode()) {
            List<Map<String,Object>> invoiceinfolist=createList();
            List<Map<String,Object>> invoiceinfolistMap=getList(eventBody, "invoicelistinfos");
            if (invoiceinfolistMap != null && invoiceinfolistMap.size() > 0) {
                for (Map<String, Object> invoiceMap : invoiceinfolistMap) {
                    Map<String, Object> invoice = createMap();
                    copyValue(invoiceMap, "detail", invoice, "invoicecontent");
                    copyValue(invoiceMap, "title", invoice, "invoicehead");
                    copyValue(invoiceMap, "receivertel", invoice, "invoicephone");
                    copyValue(invoiceMap, "receivername", invoice, "fullname");
                    copyValue(invoiceMap, "zipcode", invoice, "zipcode");
                    copyValue(invoiceMap, "province", invoice, "deliveryprovince");
                    copyValue(invoiceMap, "city", invoice, "deliverycity");
                    copyValue(invoiceMap, "addressdetail", invoice, "deliverydistrict");
                    copyValue(invoiceMap, "costtype", invoice, "costtype");
                    copyValue(invoiceMap, "costvalue", invoice, "costvalue");
                    copyValue(invoiceMap, "area", invoice, "deliveryarea");
                    invoiceinfolist.add(invoice);
                }
            }
            setValue(productInfo,"invoiceinfolist",invoiceinfolist);
        }
        else if(orderType == CtripOrderType.Vacation.getCode()){
            setValue(productInfo,"invoiceinfolist",copyList(getList(eventBody,"invoicelistinfos"),"infosecurity_invoicelistinfo"));
        }
    }

    /**
     * 填充VactionInfoList
     *
     * @param productInfo
     * @param eventBody
     */
    public void fillVacationInfoList(Map<String, Object> productInfo, Map<String, Object> eventBody, int orderType) {
        List<Map<String, Object>> vacationinfolist = createList();
        Map<String, Object> order;
        //从EventBody取数的，指定字段
        if(orderType== CtripOrderType.TravelMoney.getCode()){
            order = copyMap(eventBody,new String[]{"dcity","acity","departuredate","productname"});
        } else {
            order = copyMap(eventBody,"infosecurity_vacationinfo");
        }

        List<Map<String, Object>> userlist = createList();
        List<Map<String, Object>> userInfolistMap = getList(eventBody, "userinfos");
        if (userInfolistMap != null && userInfolistMap.size() > 0) {
            for (Map<String, Object> userMap : userInfolistMap) {
                Map<String, Object> vu;
                if(CtripOrderType.Car.getCode()==orderType){
                    vu = copyMap(userMap,new String[]{"visitorcontactinfo","visitorname","visitornationality","cityid","cityname","corpuserid"});
                    copyMap(userMap,vu,ImmutableMap.of("visitorcardno","visitoridcode","idtype","visitoridcardtype","idtypename","visitoridcardtypename","passengertype","visitortype"));
                 } else {
                    vu = copyMap(userMap, new String[]{"visitorname", "visitorcontactinfo", "visitornationality", "visitoridcardtype"});

                    if (CtripOrderType.HotelGroup.getCode() == orderType) {
                        copyValue(userMap, "idtype", vu, "visitoridcardtype");
                    }
                    if (CtripOrderType.TTD.getCode() == orderType
                            || CtripOrderType.CRH.getCode() == orderType
                            || CtripOrderType.HotelGroup.getCode() == orderType
                            || CtripOrderType.Cruise.getCode()==orderType
                            || CtripOrderType.CruiseByTianHai.getCode()==orderType
                            || CtripOrderType.HHTravel.getCode()==orderType
                            || CtripOrderType.Mice.getCode() == orderType
                            || CtripOrderType.TravelMoney.getCode()==orderType) {
                        copyValue(userMap, "visitorcardno", vu, "visitoridcode");
                    }
                }
                userlist.add(vu);
            }
        }

        List<Map<String, Object>> optlist = copyList(getList(eventBody, "optionitems"),"infosecurity_vacationoptioninfo");

        vacationinfolist.add(ImmutableMap.of("order", order, "userlist", userlist, "optionlist", optlist));

        setValue(productInfo, "vacationinfolist", vacationinfolist);
    }

    public void fillGoodsList(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        List<Map<String,Object>> goodslist=createList();
        Map<String,Object> goods = createMap();
        Map<String,Object> goodsMap= getMap(eventBody,"gooslistinfo");
        setValue(goods,"goods",copyMap(goodsMap,"infosecurity_goodslistinfo"));
        setValue(goods,"goodsitemlist",copyList(getList(goodsMap, "goodsiteminfos"), "infosecurity_goodsiteminfo"));
        goodslist.add(goods);
        setValue(productInfo,"goodslist",goodslist);
    }
}

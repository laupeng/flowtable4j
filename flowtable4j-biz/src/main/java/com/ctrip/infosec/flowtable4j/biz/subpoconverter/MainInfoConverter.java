package com.ctrip.infosec.flowtable4j.biz.subpoconverter;

import com.ctrip.infosec.flowtable4j.biz.baseconverter.ConverterBase;
import com.ctrip.infosec.flowtable4j.model.CtripOrderType;
import com.ctrip.infosec.flowtable4j.model.CtripSubOrderType;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by thyang on 2015-08-04.
 */
@Component
public class MainInfoConverter extends ConverterBase {

    /**
     * 业务选择器
     *
     * @param orderType
     * @return
     */
    public List<String> getModules(int orderType,int subOrderType,int checkType) {
        if (orderType == CtripOrderType.HotelGroup.getCode()) {
            return Arrays.asList(new String[]{"hotelgroupinfolist", "vacationinfolist"});
        }
        if (orderType == CtripOrderType.Flights.getCode()) {
            //CheckType 0,1,2
            return Arrays.asList(new String[]{"flightinfolist", "corporation", "appinfo", "orderccard", "fillprofit", "flightspecial"});
        }
        if (orderType == CtripOrderType.CRH.getCode()) {
            return Arrays.asList(new String[]{"railinfolist", "corporation", "distribution", "vacationinfolist"});
        }
        if (orderType == CtripOrderType.TTD.getCode()) {
            return Arrays.asList(new String[]{"vacationinfolist", "tddspecial"});
        }
        if (orderType == CtripOrderType.Hotel.getCode()) {
            return Arrays.asList(new String[]{"hotelinfolist", "giftitemlist", "corporation", "hotelspecial"});
        }
        if (orderType == CtripOrderType.JiFen.getCode()) {
            return Arrays.asList(new String[]{"devoteinfoviewbyjifen", "jifenorderitemlist", "distribution",});
        }
        if (orderType == CtripOrderType.DIY.getCode()) {
            return Arrays.asList(new String[]{"diy"});
        }
        if(orderType == CtripOrderType.BindingCard.getCode()){
            return Arrays.asList(new String[]{"walletwithdrawal","appinfo"});
        }
        if(orderType == CtripOrderType.BusByCRH.getCode()){
            return Arrays.asList(new String[]{"railinfolist","corporation"});
        }
        if(orderType == CtripOrderType.Car.getCode()){
            return Arrays.asList(new String[]{"vacationinfolist","corporation","chproduct","invoiceinfolist","appinfo"});
        }
        if(orderType == CtripOrderType.Coupons.getCode()){
            return Arrays.asList(new String[]{"coupons","customer","smsverify","appinfo"});
        }
        if(orderType == CtripOrderType.Cruise.getCode()){
            return Arrays.asList(new String[]{"vacationinfolist"});
        }
        if(orderType == CtripOrderType.CruiseByTianHai.getCode()){
            return Arrays.asList(new String[]{"vacationinfolist","tianhai"});
        }
        if(orderType== CtripOrderType.CurrencyExchange.getCode()){
            return Arrays.asList(new String[]{"currencyexchange"});
        }
        if(orderType== CtripOrderType.Fun.getCode()){
            return Arrays.asList(new String[]{"vacationinfolist"});
        }
        if(orderType== CtripOrderType.HHTravel.getCode()){
            return Arrays.asList(new String[]{"vacationinfolist"});
        }
        if(orderType== CtripOrderType.HotelEBK.getCode()){
            return Arrays.asList(new String[]{"hotelinfolist","hotelebkspecial"});
        }
        if(orderType== CtripOrderType.Insurer.getCode()){
            return Arrays.asList(new String[]{"proposer","distribution","insureinfolist"});
        }
        if(orderType== CtripOrderType.Marketing.getCode()){
            return Arrays.asList(new String[]{"marketing","marketdata"});
        }
        if(orderType== CtripOrderType.Mice.getCode()){
            return Arrays.asList(new String[]{"miceinfo","vacationinfolist"});
        }
        if(orderType== CtripOrderType.MiceByPKG.getCode()){
            return Arrays.asList(new String[]{"miceinfo","vacationinfolist"});
        }
        if(orderType== CtripOrderType.TieYou.getCode()){
            return Arrays.asList(new String[]{"corporation","railinfolist"});
        }
        if(orderType==CtripOrderType.TravelMoney.getCode()){
            if(subOrderType==CtripSubOrderType.TravelMoneyRetailer.getCode() ||
                    subOrderType==CtripSubOrderType.TravelMoneyTTD.getCode()){
                return Arrays.asList(new String[]{"travemoneyretailer","travelmoneyproductlist","invoiceinfolist","rechargesuborderlist","vacationinfolist"});
            }
            if(subOrderType==CtripSubOrderType.DistributionFlight.getCode()){
                return Arrays.asList(new String[]{"flightinfolist", "corporation", "orderccard"});
            }
            if(subOrderType==CtripSubOrderType.FNCMall.getCode()){
                return Arrays.asList(new String[]{"travemoneyretailer","travelmoneyproductlist","invoiceinfolist","rechargesuborderlist",
                        "vacationinfolist","distributioncompany","fncmalllist"});
            }
            if(subOrderType==CtripSubOrderType.TaoPanChan.getCode()){
                return Arrays.asList(new String[]{"travelmoneyproductlist","invoiceinfolist","travelmoneyproductplus"});
            }
            if(subOrderType==CtripSubOrderType.NEWFNCMall.getCode()){
                return Arrays.asList(new String[]{"travemoneyretailer","travelmoneyproductlist","invoiceinfolist","rechargesuborderlist",
                        "vacationinfolist","distributioncompany","fncmalllist","goodslist","fncmallorder","distribution"});
            }
            if(subOrderType==CtripSubOrderType.FundCertificate.getCode()){
                return Arrays.asList(new String[]{"distributioncompany","invoiceinfo","distribution","passenger","fundcertificate"});
            }
            return Arrays.asList(new String[]{"distribution","travelmoneyproductlist","employee"});
        }
        if(orderType==CtripOrderType.Vacation.getCode()){
            if(subOrderType==CtripSubOrderType.VacationByInternational.getCode()){
                return Arrays.asList(new String[]{"vacationinfolist","invoiceinfolist","vacationproduct","vacationspecial"});
            }
            return Arrays.asList(new String[]{"vacationinfolist","vacationspecial"});
        }
        if(orderType==CtripOrderType.Wallet.getCode()){
            if(subOrderType== CtripSubOrderType.Wallet.getCode() && checkType< 2){
                return Arrays.asList(new String[]{"gps","walletwithdrawal"});
            }
            return Arrays.asList(new String[]{"gps"});
        }
        if(orderType== CtripOrderType.YongChe.getCode()){
            return Arrays.asList(new String[]{"yongche"});
        }
        if(orderType==CtripOrderType.YongAnFlight.getCode()){
            return Arrays.asList(new String[]{"flightinfolist", "corporation", "appinfo", "orderccard", "fillprofit", "flightspecial"});
        }
        if(orderType==CtripOrderType.YongAnHotel.getCode()){
            return Arrays.asList(new String[]{"hotelinfolist", "giftitemlist", "corporation", "hotelspecial"});
        }
        if(orderType==CtripOrderType.TopShop.getCode()){
            if(subOrderType==CtripSubOrderType.SHP.getCode()){
                return Arrays.asList(new String[]{"topshoporderlist","topshopcatalog"});
            }
            return Arrays.asList(new String[]{"topshoporderlist"});
        }
        return new ArrayList<String>();
    }

    private List<Integer> corporationidNotEmpty = Arrays.asList(new Integer[]{CtripOrderType.CRH.getCode(),CtripOrderType.Flights.getCode(),CtripOrderType.Hotel.getCode(),
            CtripOrderType.BusByCRH.getCode(),CtripOrderType.TieYou.getCode()});
    private List<Integer> refNo=Arrays.asList(new Integer[]{CtripOrderType.TTD.getCode(),CtripOrderType.DIY.getCode(),CtripOrderType.Hotel.getCode(),CtripOrderType.Car.getCode(),
            CtripOrderType.Cruise.getCode(),CtripOrderType.CruiseByTianHai.getCode(),CtripOrderType.Mice.getCode(),CtripOrderType.MiceByPKG.getCode(),
            CtripOrderType.TravelMoney.getCode()});

    public void fillMainInfo(Map<String,Object> eventBody,PO po){
        Map<String, Object> mainInfo = createMap();
        copyMap(eventBody, mainInfo, "infosecurity_maininfo");
        setValue(mainInfo, "lastcheck", "T");
        setValue(mainInfo, "ordertype", po.getOrdertype());
        setValue(mainInfo, "createdate", sdf.format(System.currentTimeMillis()));
        setValue(mainInfo, "corporationid", "");

        if (corporationidNotEmpty.contains(po.getOrdertype())) {
            setValue(mainInfo, "corporationid", getObject(eventBody, "corporationid"));
        }

        if (refNo.contains(po.getOrdertype())) {
            String refNo = getString(eventBody, "referenceno");
            if (StringUtils.isNumeric(refNo)) {
                setValue(mainInfo, "refno", refNo);
            }
        }

        if(po.getOrdertype()== CtripOrderType.BindingCard.getCode()){
            setValue(mainInfo,"checktype","1");
        }

        //这三种类型的Amount不是OrderAmount
        if (po.getOrdertype() != CtripOrderType.Flights.getCode()
                && po.getSubordertype()!= CtripSubOrderType.FNCMall.getCode()
                && po.getSubordertype()!=CtripSubOrderType.TaoPanChan.getCode()) {
            copyValue(eventBody, "orderamount", mainInfo, "amount");
        }

        if(po.getOrdertype()==CtripOrderType.Coupons.getCode()){
            copyValue(eventBody,"payouttime",mainInfo,"orderdate");
            copyValue(eventBody,"payoutamount",mainInfo,"amount");
            setValue(mainInfo,"checktype","0");
        }

        if(po.getOrdertype()==CtripOrderType.Mice.getCode()
                || po.getOrdertype()==CtripOrderType.MiceByPKG.getCode()){
            copyValue(eventBody,"productname",mainInfo,"orderproductname");
        }

        setValue(mainInfo,"applyremark","Flowtable4j");
        setValue(po.getProductinfo(), "maininfo", mainInfo);
    }
}

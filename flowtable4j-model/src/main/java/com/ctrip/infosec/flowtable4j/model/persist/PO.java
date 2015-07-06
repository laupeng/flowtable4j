package com.ctrip.infosec.flowtable4j.model.persist;

import com.ctrip.infosec.flowtable4j.model.CtripOrderType;
import com.ctrip.infosec.flowtable4j.model.MapX;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015-06-05.
 */
public class PO extends MapX {

    private Map<String,Object> productinfo;
    private Map<String,Object> paymentinfo;

    //主要支付方式
    private String prepaytype;
    private Long orderid = 0L;
    private Integer ordertype =0 ;
    private Integer subordertype =0;
    private Integer checktype =1;
    private String merchantid;

    private static Map<String, String> prop2table = new HashMap<String, String>();

    public static Map<String, String> getProp2Table() {
        return prop2table;
    }

    static {
        //a
        prop2table.put("appinfo", "infosecurity_appinfo");

        //c
        prop2table.put("contactinfo","infosecurity_contactinfo");
        prop2table.put("corporation","infosecurity_corporationinfo");
        prop2table.put("coupons","infosecurity_couponsinfo");
        prop2table.put("currencyexchange","infosecurity_currencyexchange");
        prop2table.put("customer","infosecurity_customerinfo");

        //d
        prop2table.put("dealinfo", "infosecurity_dealinfo");
        prop2table.put("didinfo", "infosecurity_deviceidinfo");
        prop2table.put("devoteinfoviewbyjifen","infosecurity_devoterinfoviewbyjifen");
        prop2table.put("distribution","infosecurity_distributioninfo");
        prop2table.put("distributioncompany","infosecurity_distributioncompany");
        prop2table.put("diyresourcexlist~","infosecurity_diyresourcexinfo");

        //e
        prop2table.put("employee","infosecurity_employeeinfo");

        //f
        prop2table.put("flightinfolist~.order","infosecurity_flightsorderinfo");          //FlightsOrderID
        prop2table.put("flightinfolist~.passengerlist~","infosecurity_passengerinfo");
        prop2table.put("flightinfolist~.segmentlist~","infosecurity_segmentinfo");
        //flightotherinfolist
        prop2table.put("fncmallorder","infosecurity_fncmallorderinfo");
        prop2table.put("fncmalllist~.travelmoneyfncmall","infosecurity_travelmoneyfncmall"); //FNCMallId
        prop2table.put("fncmalllist~.suborderitemlist~","infosecurity_fncmallsuborderitem");
        prop2table.put("fundcertificate","infosecurity_fundcertificateinfo");

        //g
        prop2table.put("giftitemlist~","infosecurity_giftitem");
        prop2table.put("goodslist~.goods","infosecurity_goodslistinfo");                  //GoodsListInfoID
        prop2table.put("goodslist~.goodsitemlist~","infosecurity_goodsiteminfo");
        prop2table.put("gps", "infosecurity_gpsinfo");

        //h
        prop2table.put("hotelgroupinfo","infosecurity_hotelgroupinfo");
        prop2table.put("hotelinfolist~","infosecurity_hotelinfo");
        //hotelotherinfolist

        //i
        prop2table.put("insureinfolist~","infosecurity_insuredinfo");
        prop2table.put("invoiceinfo","infosecurity_invoiceinfo");
        prop2table.put("invoiceinfolist~","infosecurity_invoicelistinfo");
        //ireotherinfolist
        prop2table.put("ipinfo", "infosecurity_ipinfo");

        //j
        prop2table.put("jifenorderitemlist~.order","infosecurity_suborderitermbyjifen");               //OrderItemId
        prop2table.put("jifenorderitemlist~.greetingcard","infosecurity_greetingcardinfoviewbyjifen");
        prop2table.put("jifenorderitemlist~.prizedetail","infosecurity_prizedetailitembyjifen");     //DetailItemId
        prop2table.put("jifenorderitemlist~.paymentitem","infosecurity_paymentitemviewbyjifen");

        //m
        prop2table.put("maininfo", "infosecurity_maininfo");
        prop2table.put("marketing","infosecurity_marketinginfo");
        prop2table.put("marketdata","infosecurity_marketdatainfo");
        prop2table.put("miceinfo","infosecurity_miceinfo");

        //o
        prop2table.put("otherinfo", "infosecurity_otherinfo");
        prop2table.put("orderccard","ctrip_order_auth_ccard_info");

        //p
        prop2table.put("passenger","infosecurity_passengerinfo");
        prop2table.put("paymentmaininfo", "infosecurity_paymentmaininfo");
        prop2table.put("paymentinfolist~.payment", "infosecurity_paymentinfo");  //PaymentInfoID
        prop2table.put("paymentinfolist~.cardinfolist~", "infosecurity_cardinfo");
        prop2table.put("proposer","infosecurity_proposerinfo");

        //r
        prop2table.put("railinfolist~.rail","infosecurity_exrailinfo");   //ExRailInfoID
        prop2table.put("railinfolist~.user","infosecurity_exrailuserinfo");
        prop2table.put("rechargesuborderlist~","infosecurity_rechargesuborderinfo");

        //s
        prop2table.put("smsverify","infosecurity_smsverifyinfo");

        //t
        prop2table.put("tianhai","infosecurity_vacationbytianhaiinfo");
        prop2table.put("topshopcatalog~.cataloginfo","infosecurity_topshopcataloginfo");     //CatalogInfoId
        prop2table.put("topshopcatalog~.itemlist~","infosecurity_topshopcataloginfoitem");

        prop2table.put("topshoporderlist~.order","infosecurity_topshoporderinfo");    //TopShopOrderId
        prop2table.put("topshoporderlist~.productitemlist~","infosecurity_topshopproductitem");
        prop2table.put("topshoporderlist~.merchantlist~.merchant","infosecurity_topshopmerchantitem"); //MerchantItemId
        prop2table.put("topshoporderlist~.merchantlist~.productionlist~","infosecurity_topshopproductioninfo"); //MerchantItemId

        prop2table.put("travelmoneyretailer","infosecurity_travelmoneyretailerinfo");
        prop2table.put("travelmoneyproductlist~","infosecurity_travelmoneyproductinfo");
        prop2table.put("travelmoneyproductplus","infosecurity_travelmoneyproductinfoplus");

        //u
        prop2table.put("userinfo", "infosecurity_userinfo");

        //v
        prop2table.put("vacationproductlist~","infosecurity_vacationproductinfo");
        //viaotherinfolist
        prop2table.put("vacationinfolist~.order","infosecurity_vacationinfo");     //VacationInfoID
        prop2table.put("vacationinfolist~.userlist~","infosecurity_vacationuserinfo");
        prop2table.put("vacationinfolist~.optionlist~","infosecurity_vacationoptioninfo");
        //vacationotherinfolist

        //w
        prop2table.put("walletwithdrawal", "infosecurity_walletwithdrawal");

        //y
        prop2table.put("yongche","infosecurity_yongcheinfo");
    }

    public Map<String, Object> getProductinfo() {
        return productinfo;
    }

    public void setProductinfo(Map<String, Object> productinfo) {
        this.productinfo = productinfo;
    }

    public Map<String, Object> getPaymentinfo() {
        return paymentinfo;
    }

    public void setPaymentinfo(Map<String, Object> paymentinfo) {
        this.paymentinfo = paymentinfo;
    }

    public String getPrepaytype() {
        return prepaytype;
    }

    public void setPrepaytype(String prepaytype) {
        this.prepaytype = prepaytype;
    }

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public Integer getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(Integer ordertype) {
        this.ordertype = ordertype;
    }

    public Integer getSubordertype() {
        return subordertype;
    }

    public void setSubordertype(Integer subordertype) {
        this.subordertype = subordertype;
    }

    public Integer getChecktype() {
        return checktype;
    }

    public void setChecktype(Integer checktype) {
        this.checktype = checktype;
    }

    public String getMerchantid() {
        return merchantid;
    }

    public void setMerchantid(String merchantid) {
        this.merchantid = merchantid;
    }
}


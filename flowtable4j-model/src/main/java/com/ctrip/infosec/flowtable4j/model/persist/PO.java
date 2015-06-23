package com.ctrip.infosec.flowtable4j.model.persist;

import com.ctrip.infosec.flowtable4j.model.MapX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015-06-05.
 */
public class PO extends MapX {

    private Map<String,Object> productInfo;
    private Map<String,Object> paymentInfo;

    private String prepayType;
    private String orderId;
    private String orderType;
    private String checkType;

    private static Map<String, String> prop2Table = new HashMap<String, String>();

    public static Map<String, String> getProp2Table() {
        return prop2Table;
    }

    /**
     * 非基本类，需要转换
     * @param items
     * @return
     */
    private List<Map<String,Object>> list2Map(List<? extends IMapAble> items){
        List<Map<String,Object>> val = new ArrayList<Map<String, Object>>();
        if(items!=null && items.size()>0){
            for (IMapAble item:items){
                val.add(item.toMap());
            }
        }
        return  val;
    }

    public Map<String,Object> toMap(){
        Map<String,Object> val= new HashMap<String, Object>();

        //A
        if(appInfo!=null) {
            val.put("appInfo", appInfo);
        }

        //C
        if(contactInfo!=null) {
            val.put("contactInfo",contactInfo);
        }
        if(corporation!=null) {
            val.put("corporation",corporation);
        }
        if(coupons!=null) {
            val.put("coupons",coupons);
        }
        if(currencyExchange!=null) {
            val.put("currencyExchange",currencyExchange);
        }
        if(customer!=null) {
            val.put("customer",customer);
        }


        //D
        if(dealInfo!=null) {
            val.put("dealInfo",dealInfo);
        }
        if(deviceId!=null) {
            val.put("deviceId",deviceId);
        }
        if(devoteInfoViewByJiFen!=null) {
            val.put("devoteInfoViewByJiFen",devoteInfoViewByJiFen);
        }
        if(distribution!=null) {
            val.put("distribution",distribution);
        }
        if(distributionCompany!=null) {
            val.put("distributionCompany",distributionCompany);
        }
        if(DIYResourceXList!=null) {
            val.put("DIYResourceXList",DIYResourceXList);
        }


        //E
        if(employee!=null) {
            val.put("employee",employee);
        }


        //F
        if(flightInfoList!=null && flightInfoList.size()>0) {
            val.put("flightInfoList",list2Map(flightInfoList));
        }
        if(flightOtherInfoList!=null && flightOtherInfoList.size()>0) {
            val.put("flightOtherInfoList",list2Map(flightOtherInfoList));
        }
        //flightOtherInfoList
        if(FNCMallOrder!=null) {
            val.put("FNCMallOrder",FNCMallOrder);
        }
        if(FNCMallSubOrderItemList!=null) {
            val.put("FNCMallSubOrderItemList",FNCMallSubOrderItemList);
        }
        if(fundCertificate!=null) {
            val.put("fundCertificate",fundCertificate);
        }


        //G
        if(giftItemList!=null) {
            val.put("giftItemList",giftItemList);
        }
        if(goodsList!=null && goodsList.size()>0) {
            val.put("goodsList",list2Map(goodsList));
        }
        if(GPS!=null) {
            val.put("GPS",GPS);
        }

        //H
        if(hotelGroup!=null) {
            val.put("hotelGroup",hotelGroup);
        }
        if(hotelInfoList!=null) {
            val.put("hotelInfoList",hotelInfoList);
        }
        //hotelOtherInfoList

        //I
        if(insureInfoList!=null) {
            val.put("insureInfoList",insureInfoList);
        }
        if(invoiceInfo!=null) {
            val.put("invoiceInfo",invoiceInfo);
        }
        if(invoiceInfoList!=null) {
            val.put("invoiceInfoList",invoiceInfoList);
        }
        if(corporation!=null) {
            val.put("corporation",corporation);
        }
        if(IREOtherInfoList!=null && IREOtherInfoList.size()>0) {
            val.put("IREOtherInfoList",list2Map(IREOtherInfoList));
        }
        if(IPInfo!=null) {
            val.put("IPInfo",IPInfo);
        }

        //J
        if(jiFenOrderItemList!=null && jiFenOrderItemList.size()>0) {
            val.put("jiFenOrderItemList",list2Map(jiFenOrderItemList));
        }

        //M
        if(mainInfo!=null) {
            val.put("mainInfo",mainInfo);
        }
        if(marketing!=null) {
            val.put("marketing",marketing);
        }
        if(marketData!=null) {
            val.put("marketData",marketData);
        }
        if(miceInfo!=null) {
            val.put("miceInfo",miceInfo);
        }


        //O
        if(otherInfo!=null) {
            val.put("otherInfo",otherInfo);
        }
        if(orderCCard!=null) {
            val.put("orderCCard",orderCCard);
        }

        //P
        if(passenger!=null) {
            val.put("passenger",passenger);
        }
        if(paymentMainInfo!=null) {
            val.put("paymentMainInfo",paymentMainInfo);
        }
        if(paymentInfoList!=null && paymentInfoList.size()>0) {
            val.put("paymentInfoList",list2Map(paymentInfoList));
        }
        if(proposer!=null) {
            val.put("proposer",proposer);
        }

        //R
        if(railInfoList!=null && railInfoList.size()>0) {
            val.put("railInfoList",list2Map(railInfoList));
        }
        if(rechargeSubOrderList!=null) {
            val.put("rechargeSubOrderList",rechargeSubOrderList);
        }

        //S
        if(SMSVerify!=null) {
            val.put("SMSVerify",SMSVerify);
        }

        //T
        if(tianHai!=null) {
            val.put("tianHai",tianHai);
        }
        if(topShopCatalog!=null) {
            val.put("topShopCatalog",topShopCatalog);
        }
        if(topShopCatalogItemList!=null) {
            val.put("topShopCatalogItemList",topShopCatalogItemList);
        }
        if(topShopMerchantList!=null && topShopMerchantList.size()>0) {
            val.put("topShopMerchantList",list2Map(topShopMerchantList));
        }
        if(topShopOrder!=null) {
            val.put("topShopOrder",topShopOrder);
        }
        if(topShopProductItemList!=null) {
            val.put("topShopProductItemList",topShopProductItemList);
        }
        if(travelMoneyProductList!=null) {
            val.put("travelMoneyProductList",travelMoneyProductList);
        }
        if(travelMoneyFNCMall!=null) {
            val.put("travelMoneyFNCMall",travelMoneyFNCMall);
        }
        if(travelMoneyRetailer!=null) {
            val.put("travelMoneyRetailer",travelMoneyRetailer);
        }
        if(travelMoneyProductList!=null) {
            val.put("travelMoneyProductList",travelMoneyProductList);
        }
        if(travelMoneyProductPlus!=null) {
            val.put("travelMoneyProductPlus",travelMoneyProductPlus);
        }

        //U
        if(userInfo!=null) {
            val.put("userInfo",userInfo);
        }

        //V
        if(vacationProductList!=null) {
            val.put("vacationProductList",vacationProductList);
        }
        if(corporation!=null) {
            val.put("corporation",corporation);
        }
        if(VIAOtherInfoList!=null && VIAOtherInfoList.size()>0) {
            val.put("VIAOtherInfoList",list2Map(VIAOtherInfoList));
        }
        if(vacationInfoList!=null && vacationInfoList.size()>0) {
            val.put("vacationInfoList",list2Map(vacationInfoList));
        }
        if(vacationOtherInfoList!=null && vacationOtherInfoList.size()>0) {
            val.put("vacationOtherInfoList",list2Map(vacationOtherInfoList));
        }

        //W
        if(walletWithdrawal!=null) {
            val.put("walletWithdrawal",walletWithdrawal);
        }

        //Y
        if(yongChe!=null) {
            val.put("yongChe",yongChe);
        }

        return val;
    }

    static {
        //A
        prop2Table.put("appInfo", "InfoSecurity_AppInfo");

        //C
        prop2Table.put("contactInfo","InfoSecurity_ContactInfo");
        prop2Table.put("corporation","InfoSecurity_CorporationInfo");
        prop2Table.put("coupons","InfoSecurity_CouponsInfo");
        prop2Table.put("currencyExchange","InfoSecurity_CurrencyExchange");
        prop2Table.put("customer","InfoSecurity_CustomerInfo");

        //D
        prop2Table.put("dealInfo", "InfoSecurity_DealInfo");
        prop2Table.put("deviceId", "InfoSecurity_DeviceIDInfo");
        prop2Table.put("devoteInfoViewByJiFen","InfoSecurity_DevoterInfoViewByJiFen");
        prop2Table.put("distribution","InfoSecurity_DistributionInfo");
        prop2Table.put("distributionCompany","InfoSecurity_DistributionCompany");
        prop2Table.put("DIYResourceXList~","InfoSecurity_DIYResourceXInfo");

        //E
        prop2Table.put("employee","InfoSecurity_EmployeeInfo");

        //F
        prop2Table.put("flightInfoList~.order","InfoSecurity_FlightsOrderInfo");
        prop2Table.put("flightInfoList~.passengerList~","InfoSecurity_PassengerInfo");
        prop2Table.put("flightInfoList~.segmentList~","InfoSecurity_SegmentInfo");
        //flightOtherInfoList
        prop2Table.put("FNCMallOrder","InfoSecurity_FNCMallOrderInfo");
        prop2Table.put("FNCMallSubOrderItemList~","InfoSecurity_FNCMallSubOrderItem");
        prop2Table.put("fundCertificate","InfoSecurity_FundCertificateInfo");

        //G
        prop2Table.put("giftItemList~","InfoSecurity_GiftItem");
        prop2Table.put("goodsList~.goods","InfoSecurity_GoodsListInfo");
        prop2Table.put("goodsList~.goodsItemList~","InfoSecurity_GoodsItemInfo");
        prop2Table.put("GPS", "InfoSecurity_GpsInfo");

        //H
        prop2Table.put("hotelGroup","InfoSecurity_HotelGroupInfo");
        prop2Table.put("hotelInfoList~","InfoSecurity_HotelInfo");
        //hotelOtherInfoList

        //I
        prop2Table.put("insureInfoList~","InfoSecurity_InsuredInfo");
        prop2Table.put("invoiceInfo","InfoSecurity_InvoiceInfo");
        prop2Table.put("invoiceInfoList~","InfoSecurity_InvoiceListInfo");
        //IREOtherInfoList
        prop2Table.put("IPInfo", "InfoSecurity_IPInfo");

        //J
        prop2Table.put("jiFenOrderItemList~.order","InfoSecurity_SubOrderItermByJiFen");
        prop2Table.put("jiFenOrderItemList~.greetingCard","InfoSecurity_GreetingCardInfoViewByJiFen");
        prop2Table.put("jiFenOrderItemList~.paymentItem","InfoSecurity_PaymentItemViewByJiFen");
        prop2Table.put("jiFenOrderItemList~.prizeDetail","InfoSecurity_PrizeDetailItemByJiFen");

        //M
        prop2Table.put("mainInfo", "InfoSecurity_MainInfo");
        prop2Table.put("marketing","InfoSecurity_MarketingInfo");
        prop2Table.put("marketData","InfoSecurity_MarketDataInfo");
        prop2Table.put("miceInfo","InfoSecurity_MiceInfo");

        //O
        prop2Table.put("otherInfo", "InfoSecurity_OtherInfo");
        prop2Table.put("orderCCard","CTRIP_Order_Auth_CCard_Info");

        //P
        prop2Table.put("passenger","InfoSecurity_PassengerInfo");
        prop2Table.put("paymentMainInfo", "InfoSecurity_PaymentMainInfo");
        prop2Table.put("paymentInfoList~.payment", "InfoSecurity_PaymentInfo");
        prop2Table.put("paymentInfoList~.cardInfoList~", "InfoSecurity_CardInfo");
        prop2Table.put("proposer","InfoSecurity_ProposerInfo");

        //R
        prop2Table.put("railInfoList~.rail","InfoSecurity_ExRailInfo");
        prop2Table.put("railInfoList~.user","InfoSecurity_ExRailUserInfo");
        prop2Table.put("rechargeSubOrderList~","InfoSecurity_RechargeSubOrderInfo");

        //S
        prop2Table.put("SMSVerify","InfoSecurity_SMSVerifyInfo");

        //T
        prop2Table.put("tianHai","InfoSecurity_VacationByTianHaiInfo");
        prop2Table.put("topShopCatalog","InfoSecurity_TopShopCatalogInfo");
        prop2Table.put("topShopCatalogItemList~","InfoSecurity_TopShopCatalogInfoItem");
        prop2Table.put("topShopMerchantList~.topShopMerchant","InfoSecurity_TopShopMerchantItem");
        prop2Table.put("topShopMerchantList~.productList~","InfoSecurity_TopShopProductionInfo");
        prop2Table.put("topShopOrder","InfoSecurity_TopShopOrderInfo");
        prop2Table.put("topShopProductItemList~","InfoSecurity_TopShopProductItem");
        prop2Table.put("travelMoneyProductList~","InfoSecurity_TravelMoneyProductInfo");
        prop2Table.put("travelMoneyFNCMall","InfoSecurity_TravelMoneyFNCMall");
        prop2Table.put("travelMoneyRetailer","InfoSecurity_TravelMoneyRetailerInfo");
        prop2Table.put("travelMoneyProductList~","InfoSecurity_TravelMoneyProductInfo");
        prop2Table.put("travelMoneyProductPlus","InfoSecurity_TravelMoneyProductInfoPlus");

        //U
        prop2Table.put("userInfo", "InfoSecurity_UserInfo");

        //V
        prop2Table.put("vacationProductList~","InfoSecurity_VacationProductInfo");
        //VIAOtherInfoList
        prop2Table.put("vacationInfoList~.order","InfoSecurity_VacationInfo");
        prop2Table.put("vacationInfoList~.userList~","InfoSecurity_VacationUserInfo");
        prop2Table.put("vacationInfoList~.optionList~","InfoSecurity_VacationOptionInfo");
        //vacationOtherInfoList

        //W
        prop2Table.put("walletWithdrawal", "InfoSecurity_WalletWithdrawal");

        //Y
        prop2Table.put("yongChe","InfoSecurity_YongCheInfo");
    }

    //A
    public Map<String, Object> appInfo;

    //C
    public Map<String, Object> contactInfo;
    public Map<String, Object> corporation;
    public Map<String, Object> coupons;
    public Integer couponsSubType;
    public Map<String, Object> currencyExchange;
    public Map<String, Object> customer;

    //D
    public Map<String, Object> dealInfo;
    public Map<String, Object> deviceId;
    public Map<String, Object> devoteInfoViewByJiFen;
    public Map<String, Object> distribution;
    public Map<String, Object> distributionCompany;
    public Map<String, Object> DIYResourceXList;

    //E
    public Map<String, Object> employee;

    //F
    public List<FlightInfo> flightInfoList;
    public List<OtherInfo> flightOtherInfoList;
    public Map<String,Object> FNCMallOrder;
    public List<Map<String, Object>> FNCMallSubOrderItemList;
    public Map<String,Object> fundCertificate;

    //G
    public List<Map<String, Object>> giftItemList;
    public List<GoodsInfo> goodsList;
    public Map<String, Object> GPS;

    //H
    public Map<String, Object> hotelGroup;
    public List<Map<String, Object>> hotelInfoList;
    public List<OtherInfo> hotelOtherInfoList;

    //I
    public List<Map<String, Object>> insureInfoList;
    public Map<String, Object> invoiceInfo;
    public List<Map<String, Object>> invoiceInfoList;
    public List<OtherInfo> IREOtherInfoList;
    public Map<String, Object> IPInfo;

    //J
    public List<JiFenOrderItem> jiFenOrderItemList;

    //M
    public Map<String, Object> mainInfo;
    public Map<String, Object> marketData;
    public Map<String, Object> marketing;
    public Map<String, Object> miceInfo;

    //O
    public Map<String, Object> orderCCard;
    public Map<String, Object> otherInfo;

    //P
    public Map<String, Object> passenger;
    public Map<String, Object> paymentMainInfo;
    public List<PaymentInfo> paymentInfoList;
    public Map<String, Object> proposer;

    //R
    public List<RailInfo> railInfoList;
    public List<Map<String, Object>> rechargeSubOrderList;

    //S
    public Map<String, Object> SMSVerify;

    //T
    public Map<String, Object> tianHai;
    public Map<String, Object> topShopCatalog;
    public List<Map<String, Object>> topShopCatalogItemList;
    public List<TopShopMerchantItem> topShopMerchantList;
    public Map<String, Object> topShopOrder;
    public List<Map<String, Object>> topShopProductItemList;
    public Map<String, Object> travelMoneyFNCMall;
    public Map<String, Object> travelMoneyRetailer;
    public Map<String, Object> travelMoneyProductList;
    public Map<String, Object> travelMoneyProductPlus;

    //U
    public Map<String, Object> userInfo;

    //V
    public List<Map<String, Object>> vacationProductList;
    public List<OtherInfo> VIAOtherInfoList;
    public List<VacationInfo> vacationInfoList;
    public List<OtherInfo> vacationOtherInfoList;

    //W
    public Map<String, Object> walletWithdrawal;

    //Y
    public Map<String, Object> yongChe;

    public Map<String, Object> getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(Map<String, Object> productInfo) {
        this.productInfo = productInfo;
    }

    public Map<String, Object> getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(Map<String, Object> paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public String getPrepayType() {
        return prepayType;
    }

    public void setPrepayType(String prepayType) {
        this.prepayType = prepayType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }
}


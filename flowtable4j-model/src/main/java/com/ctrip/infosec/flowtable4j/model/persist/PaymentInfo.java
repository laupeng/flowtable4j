//package com.ctrip.infosec.flowtable4j.model.persist;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by thyang on 2015-06-08.
// */
//public class PaymentInfo  implements IMapAble {
//    private Map<String,Object> payment;
//    private List<Map<String,Object>> cardInfoList;
//
//    public Map<String, Object> getPayment() {
//        return payment;
//    }
//
//    public void setPayment(Map<String, Object> payment) {
//        this.payment = payment;
//    }
//
//    public List<Map<String, Object>> getCardInfoList() {
//        return cardInfoList;
//    }
//
//    public void setCardInfoList(List<Map<String, Object>> cardInfoList) {
//        this.cardInfoList = cardInfoList;
//    }
//
//    public Map<String,Object> toMap(){
//        Map<String,Object> val= new HashMap<String, Object>();
//        if(payment!=null){
//            val.put("rail",payment);
//        }
//        if(cardInfoList!=null){
//            val.put("user",cardInfoList);
//        }
//        return val;
//    }
//}

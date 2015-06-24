//package com.ctrip.infosec.flowtable4j.model.persist;
//
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by thyang on 2015-06-08.
// */
//public class OtherInfo  implements IMapAble {
//    private long orderId;
//    private BigDecimal amount;
//    private String prepayType;
//
//    public long getOrderId() {
//        return orderId;
//    }
//
//    public void setOrderId(long orderId) {
//        this.orderId = orderId;
//    }
//
//    public BigDecimal getAmount() {
//        return amount;
//    }
//
//    public void setAmount(BigDecimal amount) {
//        this.amount = amount;
//    }
//
//    public String getPrepayType() {
//        return prepayType;
//    }
//
//    public void setPrepayType(String prepayType) {
//        this.prepayType = prepayType;
//    }
//
//    public Map<String,Object> toMap(){
//        Map<String,Object> val= new HashMap<String, Object>();
//        val.put("orderId",orderId);
//        val.put("amount",amount);
//        if(prepayType!=null){
//            val.put("prepayType",prepayType);
//        }
//        return val;
//    }
//}

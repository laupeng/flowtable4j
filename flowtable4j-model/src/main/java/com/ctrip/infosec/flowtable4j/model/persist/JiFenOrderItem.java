package com.ctrip.infosec.flowtable4j.model.persist;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thyang on 2015-06-08.
 */
public class JiFenOrderItem  implements IMapAble {
    private Map<String,Object> order;
    private Map<String,Object> greetingCard;
    private Map<String,Object> prizeDetail;
    private Map<String,Object> paymentItem;

    public Map<String, Object> getOrder() {
        return order;
    }

    public void setOrder(Map<String, Object> order) {
        this.order = order;
    }

    public Map<String, Object> getGreetingCard() {
        return greetingCard;
    }

    public void setGreetingCard(Map<String, Object> greetingCard) {
        this.greetingCard = greetingCard;
    }

    public Map<String, Object> getPrizeDetail() {
        return prizeDetail;
    }

    public void setPrizeDetail(Map<String, Object> prizeDetail) {
        this.prizeDetail = prizeDetail;
    }

    public Map<String, Object> getPaymentItem() {
        return paymentItem;
    }

    public void setPaymentItem(Map<String, Object> paymentItem) {
        this.paymentItem = paymentItem;
    }

    public Map<String,Object> toMap(){
        Map<String,Object> val= new HashMap<String, Object>();
        if(order!=null){
            val.put("order",order);
        }
        if(greetingCard!=null){
            val.put("greetingCard",greetingCard);
        }
        if(prizeDetail!=null){
            val.put("prizeDetail",prizeDetail);
        }
        if(paymentItem!=null){
            val.put("paymentItem",paymentItem);
        }
        return val;
    }
}

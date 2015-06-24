package com.ctrip.infosec.flowtable4j.v2m.converter;

import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.MapX;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by thyang on 2015-06-10.
 */
public class FlowConverter extends ConverterBase {
    public FlowFact convert(PO po){
        FlowFact fact = new FlowFact();
        Map<String, Object> target = new HashMap<String, Object>();
        fact.setContent(target);

        copyMap(po,"maininfo",target,new String[]{"clientid","ordertype","subordertype","orderid","amount","checktype","serverfrom","orderdate"});
        processOrderDate(target);

        fillPaymentInfoToMap(po,target,po.getOrderid());

        copyMap(po, "contactinfo", target, new String[]{"mobilephone", "mobilephonecity", "contactemail", "mobilephoneprovince"});

        copyMap(po, "otherinfo", target, new String[]{"ordertosignupdate"});

        copyMap(po, "hotelgroupinfo", target,new String[]{"quantity", "city", "productid", "productname", "producttype", "price"});

        copyMap(po, "userinfo",target,new String[]{"cuscharacter", "bindedmobilephone", "userpassword", "experience", "bindedemail","vipgrade","relatedemail","relatedmobilephone","uid"});

        fillMobileProvince(target,getString(target, "bindedmobilephone"),getString(target,"relatedmobilephone"));

        fillIPCity(po,target);

        copyMap(po,"deviceid",target,new String[]{"did"});

        //字段已经在前面的处理步骤写入
        mergeField(target,"cardbinuid","cardbin","uid");
        mergeField(target,"cardbinmobilephone","cardbin","mobilephone");
        mergeField(target,"cardbinuseripadd","cardbin","useripadd");
        mergeField(target,"contactemailcardbin","contactemail","cardbin");
        mergeMobile7(target, "useripaddmobilenumber", "useripadd");
        mergeMobile7(target, "uidmobilenumber", "uid");

        setUidActive(target);

        getOriginalRiskLevel(po,target);

        return fact;
    }

    protected static SimpleDateFormat mergeOrderDate = new SimpleDateFormat("yyyyMMdd");

    private void setUidActive(Map<String, Object> target) {
        Map<String,Object> map = checkRiskDAO.getLeakedInfo(getString(target,"uid"));
        if(map!=null){
            setValue(target,"uidactive",getString(map,"active"));
        }
    }

    private void mergeMobile7(Map<String, Object> target, String thirdField, String field1) {
        String v1 = getString(target, field1);
        String v2 = getString(target,"mobilephone");
        if(!Strings.isNullOrEmpty(v1) && !Strings.isNullOrEmpty(v2) && v2.length()>6){
            setValue(target,thirdField,v1 + v2.substring(0,6));
        }
    }

    private void mergeField(Map<String, Object> target, String thirdField, String field1, String field2) {
        String v1 = getString(target, field1);
        String v2 = getString(target, field2);
        if(!Strings.isNullOrEmpty(v1) && !Strings.isNullOrEmpty(v2)){
            setValue(target,thirdField,v1+v2);
        }

    }

    private void fillIPCity(PO po, Map<String, Object> target) {
        copyMap(po,"ipinfo",target,new String[]{"useripadd","useripvalue","ipcity","ipcountry"});
        Map<String,Object> map= checkRiskDAO.getCityNameProvince(getString(target,"ipcity"));
        if(map!=null){
            setValue(target,"ipcityname",getString(map,"cityname"));
            setValue(target,"ipprovince",getString(map,"provincename"));
        }
    }

    private void fillMobileProvince(Map<String, Object> target, String bindedMobilePhone, String relatedMobilephone) {
         Map<String,Object> map = checkRiskDAO.getMobileCityAndProv(bindedMobilePhone);
         if(map!=null){
             setValue(target,"bindedmobilephonecity",getString(map,"cityname"));
             setValue(target,"bindedmobilephoneprovince",getString(map,"provincename"));
         }
        if(!bindedMobilePhone.equals(relatedMobilephone)) {
            map = checkRiskDAO.getMobileCityAndProv(relatedMobilephone);
        }
        if(map!=null){
            setValue(target,"relatedmobilephonecity",getString(map,"cityname"));
            setValue(target,"relatedmobilephoneprovince",getString(map,"provincename"));
        }
    }

    private void processOrderDate(Map<String, Object> target) {
        String orderDate = getString(target,"orderdate");
        if(!Strings.isNullOrEmpty(orderDate)){
            try {
                Date date= sdf.parse(orderDate);
                setValue(target,"mergerorderdate",mergeOrderDate.format(date));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                setValue(target,"orderdatehour",calendar.get(Calendar.HOUR_OF_DAY));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    protected void copyMap(PO po, String keyPath, Map<String, Object> target, Map<String, String> fieldMap) {
        Map<String, Object> src = (Map<String, Object>) MapX.getMap(po.getProductinfo(), keyPath);
        if (src != null) {
            for (String key : fieldMap.keySet()) {
                setValue(target, fieldMap.get(key), getString(src, key));
            }
        }
    }

    protected void copyMap(PO po, String keyPath, Map<String, Object> target, String[] fields) {
        Map<String, Object> src = (Map<String, Object>) MapX.getMap(po.getProductinfo(), keyPath);
        if (src != null) {
            for (String key : fields) {
                setValue(target, key,getString(src, key));
            }
        }
    }

    protected void copyMap(Map<String, Object> src, Map<String, Object> target, String[] fields) {
        if (src != null) {
            for (String key : fields) {
                setValue(target, key,getString(src, key));
            }
        }
    }

    protected void fillPaymentInfoToMap(PO po,Map<String, Object> target,Long orderId) {

        List<Map<String, Object>> payInfos = MapX.getList(po.getPaymentinfo(), "paymentinfolist");
        String prepayType;
        StringBuilder sb =new StringBuilder("|");
        if (payInfos != null && payInfos.size() > 0) {
            for (Map<String, Object> payInfo : payInfos) {
                Map<String, Object> payment = (Map<String, Object>) MapX.getMap(payInfo, "payment");
                if (payInfo != null) {
                    prepayType = MapX.getStringEmpty(payment, "prepaytype").toUpperCase();
                    sb.append(prepayType).append("|");
                    if (prepayType.equals("CCARD") || prepayType.equals("DCARD")) {
                        List<Map<String, Object>> cardInfoList = (List<Map<String, Object>>) MapX.getList(payInfo, "cardinfolist");
                        if (cardInfoList != null && cardInfoList.size() > 0) {
                            Map<String, Object> cardInfo0 = cardInfoList.get(0);
                            copyMap(cardInfo0, target, new String[]{"ccardnocode", "cardnorefid", "cvaliditycode", "creditcardtype", "isforigencard",
                                    "cardbinissue", "cardbin", "cardholder"});
                            if(!Strings.isNullOrEmpty(getString(cardInfo0,"cardbin"))) {
                               setValue(target,"cardbinorderid",getString(cardInfo0,"cardbin")+String.valueOf(orderId));
                            }
                        }
                    }
                }
            }
        }
        setValue(target,"mergerorderprepaytype",sb.toString());
    }

    protected void getOriginalRiskLevel(PO po,Map<String, Object> target){
        Map<String,Object> dim = new HashMap<String,Object>();
        setValueIfNotEmpty(dim,"uid",getString(target,"uid"));
        setValueIfNotEmpty(dim,"contactemail",getString(target,"contactemail"));
        setValueIfNotEmpty(dim,"mobilephone",getString(target,"mobilephone"));
        setValueIfNotEmpty(dim,"ccardnocode",getString(target,"ccardnocode"));
        setValueIfNotEmpty(dim,"cardnorefid",getString(target,"cardnorefid"));
        String count =checkRiskDAO.getOriginRiskLevelCount(dim, po.getOrdertype());
        if(!Strings.isNullOrEmpty(count)){
            setValue(target,"originalrisklevelcount",count);
        }

    }
}

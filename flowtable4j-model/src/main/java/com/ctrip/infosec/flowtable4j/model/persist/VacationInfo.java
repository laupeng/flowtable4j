//package com.ctrip.infosec.flowtable4j.model.persist;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by thyang on 2015-06-08.
// */
//public class VacationInfo  implements IMapAble {
//    private Map<String,Object> order;
//    private List<Map<String,Object>> userList;
//    private List<Map<String,Object>> optionList;
//
//    public Map<String,Object> toMap(){
//        Map<String,Object> val= new HashMap<String, Object>();
//        if(order!=null){
//            val.put("order",order);
//        }
//        if(userList!=null){
//            val.put("userList",userList);
//        }
//        if(optionList!=null){
//            val.put("optionList",optionList);
//        }
//        return val;
//    }
//
//    public List<Map<String, Object>> getOptionList() {
//        return optionList;
//    }
//
//    public void setOptionList(List<Map<String, Object>> optionList) {
//        this.optionList = optionList;
//    }
//
//    public List<Map<String, Object>> getUserList() {
//        return userList;
//    }
//
//    public void setUserList(List<Map<String, Object>> userList) {
//        this.userList = userList;
//    }
//
//    public Map<String, Object> getOrder() {
//        return order;
//    }
//
//    public void setOrder(Map<String, Object> order) {
//        this.order = order;
//    }
//}
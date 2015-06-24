//package com.ctrip.infosec.flowtable4j.model.persist;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by thyang on 2015-06-08.
// */
//public class FlightInfo implements IMapAble {
//    private Map<String,Object> order;
//    private List<Map<String,Object>> passengerList;
//    private List<Map<String,Object>> segmentList;
//
//    public Map<String, Object> getOrder() {
//        return order;
//    }
//
//    public void setOrder(Map<String, Object> order) {
//        this.order = order;
//    }
//
//    public List<Map<String, Object>> getPassengerList() {
//        return passengerList;
//    }
//
//    public void setPassengerList(List<Map<String, Object>> passengerList) {
//        this.passengerList = passengerList;
//    }
//
//    public List<Map<String, Object>> getSegmentList() {
//        return segmentList;
//    }
//
//    public void setSegmentList(List<Map<String, Object>> segmentList) {
//        this.segmentList = segmentList;
//    }
//
//    public Map<String,Object> toMap(){
//        Map<String,Object> val= new HashMap<String, Object>();
//        if(order!=null){
//            val.put("order",order);
//        }
//        if(passengerList!=null){
//            val.put("passengerList",passengerList);
//        }
//        if(segmentList!=null){
//            val.put("segmentList",segmentList);
//        }
//        return val;
//    }
//}

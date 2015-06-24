//package com.ctrip.infosec.flowtable4j.model.persist;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by thyang on 2015-06-08.
// */
//public class RailInfo  implements IMapAble {
//    private Map<String,Object> rail;
//    private Map<String,Object> user;
//
//    public Map<String, Object> getUser() {
//        return user;
//    }
//
//    public void setUser(Map<String, Object> user) {
//        this.user = user;
//    }
//
//    public Map<String, Object> getRail() {
//        return rail;
//    }
//
//    public void setRail(Map<String, Object> rail) {
//        this.rail = rail;
//    }
//
//
//    public Map<String,Object> toMap(){
//        Map<String,Object> val= new HashMap<String, Object>();
//        if(rail!=null){
//            val.put("rail",rail);
//        }
//        if(user!=null){
//            val.put("user",user);
//        }
//        return val;
//    }
//
//}

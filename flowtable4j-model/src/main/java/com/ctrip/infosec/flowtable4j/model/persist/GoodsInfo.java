//package com.ctrip.infosec.flowtable4j.model.persist;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by thyang on 2015-06-08.
// */
//public class GoodsInfo  implements IMapAble {
//    private Map<String,Object> goods;
//    private List<Map<String,Object>> goodsItemList;
//
//    public Map<String, Object> getGoods() {
//        return goods;
//    }
//
//    public void setGoods(Map<String, Object> goods) {
//        this.goods = goods;
//    }
//
//    public List<Map<String, Object>> getGoodsItemList() {
//        return goodsItemList;
//    }
//
//    public void setGoodsItemList(List<Map<String, Object>> goodsItemList) {
//        this.goodsItemList = goodsItemList;
//    }
//
//    public Map<String,Object> toMap(){
//        Map<String,Object> val= new HashMap<String, Object>();
//        if(goods!=null){
//            val.put("goods",goods);
//        }
//        if(goodsItemList!=null){
//            val.put("goodsItemList",goodsItemList);
//        }
//        return val;
//    }
//}

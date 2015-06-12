package com.ctrip.infosec.flowtable4j.model.persist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015-06-08.
 */
public class TopShopMerchantItem  implements IMapAble {
    private Map<String,Object> topShopMerchant;
    private List<Map<String,Object>> productList;

    public Map<String,Object> toMap(){
        Map<String,Object> val= new HashMap<String, Object>();
        if(topShopMerchant!=null){
            val.put("topShopMerchant",topShopMerchant);
        }
        if(productList!=null){
            val.put("productList",productList);
        }
        return val;
    }

    public Map<String, Object> getTopShopMerchant() {
        return topShopMerchant;
    }

    public void setTopShopMerchant(Map<String, Object> topShopMerchant) {
        this.topShopMerchant = topShopMerchant;
    }

    public List<Map<String, Object>> getProductList() {
        return productList;
    }

    public void setProductList(List<Map<String, Object>> productList) {
        this.productList = productList;
    }
}

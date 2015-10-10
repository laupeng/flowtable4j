package com.ctrip.infosec.flowtable4j.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015/3/13 0013.
 * 黑白名单校验实体
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BWFact {

    /**
     * 订单类型
     */
    private Integer orderType;

    private List<Integer> orderTypes;
    /**
     * 校验内容，字典类型
     */
    private Map<String, Object> content;

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public void setContent(Map<String, Object> content) {
        this.content = content;
    }

    /**
     * 自定义 OrderTypes，包括 0
     */
    public List<Integer> getOrderTypes() {
        return orderTypes;
    }

    public void setOrderTypes(List<Integer> orderTypes) {
        this.orderTypes = orderTypes;
    }

    public String getString(String key){
        return MapX.getString(content,key);
    }
}

package com.ctrip.infosec.flowtable4j.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015/3/13 0013.
 * 黑白名单校验实体
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowFact extends BaseFact {

    /**
     * 订单类型
     */
    private Integer orderType;

    private List<Integer> orderTypes;

    private List<String>  prepayType;

    public Map<String,String> requestCache = new HashMap<String, String>();

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

    private long reqId;

    public List<String> getPrepayType() {
        return prepayType;
    }

    public void setPrepayType(List<String> prepayType) {
        this.prepayType = prepayType;
    }


    public void setOrderTypes(List<Integer> orderTypes) {
        this.orderTypes = orderTypes;
    }

    public List<Integer> getOrderTypes() {
        return orderTypes;
    }

    public long getReqId() {
        return reqId;
    }

    public void setReqId(long reqId) {
        this.reqId = reqId;
    }

    @Override
    protected Map<String, Object> getRootMap() {
        return content;
    }
}


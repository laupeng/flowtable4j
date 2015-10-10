package com.ctrip.infosec.flowtable4j.model;

import java.util.Map;

/**
 * Created by zhangsx on 2015/5/21.
 */
public class RequestBody {
    /**
     * 接入点
     */
    private String eventPoint;

    /**
     * 开始请求时间Ticks
     */
    private String requestTime;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 接入服务端编号UUID
     */
    private String eventId;

    /**
     * 请求事件内容
     * 支持属性、class、list
     */
    private Map<String,Object> eventBody;

    public String getEventPoint() {
        return eventPoint;
    }

    public void setEventPoint(String eventPoint) {
        this.eventPoint = eventPoint;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Map<String,Object> getEventBody() {
        return eventBody;
    }

    public void setEventBody(Map<String,Object> eventBody) {
        this.eventBody = eventBody;
    }

    protected Map<String, Object> getRootMap() {
        return eventBody;
    }
}

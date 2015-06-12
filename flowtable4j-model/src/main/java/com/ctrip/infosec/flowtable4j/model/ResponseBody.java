package com.ctrip.infosec.flowtable4j.model;

import java.util.Map;

/**
 * Created by zhangsx on 2015/5/21.
 */
public class ResponseBody {
    /**
     * 接入点
     */
    private String  eventPoint ;

    /**
     * 客户端发出请求时间yyyy-MM-dd HH:mm:ss.fff
     */
    private String  requestTime ;

    /**
     * 服务端收到时间yyyy-MM-dd HH:mm:ss.fff
     */
    private String  requestReceive ;

    /**
     * 服务端开始响应时间yyyy-MM-dd HH:mm:ss.fff
     */
    private String  responseTime ;

    /**
     * 客户端收到响应时间yyyy-MM-dd HH:mm:ss.fff
     */
    private String  responseReceive ;

    /**
     * 接入服务端编号UUID
     */
    private String  eventId ;

    /**
     * 状态，OK，TIMEOUT，EXCEPTION
     */
    private String status ="OK" ;

    private boolean Timeout = false;
    private boolean Error = false;

    /**
     * 返回内容,有可能字典嵌套
     * 基本key，riskLevel,riskMessage,channel
     */
    public Map<String, Object> results ;

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

    public String getRequestReceive() {
        return requestReceive;
    }

    public void setRequestReceive(String requestReceive) {
        this.requestReceive = requestReceive;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public String getResponseReceive() {
        return responseReceive;
    }

    public void setResponseReceive(String responseReceive) {
        this.responseReceive = responseReceive;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getResults() {
        return results;
    }

    public void setResults(Map<String, Object> results) {
        this.results = results;
    }

    public boolean isTimeout() {
        return Timeout;
    }

    public void setTimeout(boolean timeout) {
        Timeout = timeout;
        status = "TIMEOUT";
    }

    public boolean isError() {
        return Error;
    }

    public void setError(boolean error) {
        Error = error;
        status = "EXCEPTION";
    }
}

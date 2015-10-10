package com.ctrip.infosec.flowtable4j.t3afs.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thyang on 2015/4/2 0002.
 */
public class RiskResult {
    private String status="OK";
    private List<CheckResultLog> results=new ArrayList<CheckResultLog>();
    private long reqId;

    public void setReqId(long reqId) {
        this.reqId = reqId;
    }

    public void add(CheckResultLog log){
        results.add(log);
    }

    public void merge(RiskResult result){
        if(result!=null && result.results!=null && result.results.size()>0){
            results.addAll(result.getResults());
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CheckResultLog> getResults() {
        return results;
    }

    public long getReqId() {
        return reqId;
    }

    public long getOriginRiskLevel(){
        long level =0;
        if(results!=null && results.size()>0){
            for(CheckResultLog r:results){
                level = Math.max(level,r.getRiskLevel());
            }
        }
        return level;
    }
}
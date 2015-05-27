package com.ctrip.infosec.flowtable4j.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thyang on 2015-05-27.
 */
public class AccountResult {
    private String status;

    private Map<String,Integer> results;

    public Map<String, Integer> getResult() {
        return results;
    }

    public void setResult(Map<String, Integer> result) {
        this.results = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AccountResult(){
        results = new HashMap<String, Integer>();
    }
}

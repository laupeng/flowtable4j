package com.ctrip.infosec.flowtable4j.model.persist;

import java.util.HashMap;

/**
 * Created by zhangsx on 2015/6/17.
 */
public class MMap extends HashMap{
    private String token;
    private boolean isBase;
    private long returnId;
    private String fieldName;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isBase() {
        return isBase;
    }

    public void setIsBase(boolean isBase) {
        this.isBase = isBase;
    }

    public long getReturnId() {
        return returnId;
    }

    public void setReturnId(long returnId) {
        this.returnId = returnId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}

package com.ctrip.infosec.flowtable4j.model.persist;

import java.util.Map;
import java.util.Objects;

/**
 * Created by zhangsx on 2015/6/25.
 */
public class ColumnInfo {
    private String tableName;
    private String name;
    private String data_type;
    private int is_nullable;
    private int is_identity;

    public Object getValue(Map<String, Object> src, Map<String, Long> fks) {
        if (fks != null && fks.containsKey(name)) {
            return fks.get(name);
        } else {
            Object obj = src.get(name);
            if (obj == null && is_nullable == 0) {
                if (data_type.contains("char")) {
                    return "";
                } else if (data_type.equals("datetime")) {
                    return "1900-01-01";
                } else {
                    return "0";
                }
            } else {
                return obj;
            }
        }
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;

    }

    public int getIs_nullable() {
        return is_nullable;
    }

    public void setIs_nullable(int is_nullable) {
        this.is_nullable = is_nullable;
    }

    public int getIs_identity() {
        return is_identity;
    }

    public void setIs_identity(int is_identity) {
        this.is_identity = is_identity;
    }
}

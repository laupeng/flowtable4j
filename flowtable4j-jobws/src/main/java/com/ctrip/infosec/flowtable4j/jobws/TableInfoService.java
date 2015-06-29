package com.ctrip.infosec.flowtable4j.jobws;

import com.ctrip.infosec.flowtable4j.model.persist.ColumnInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangsx on 2015/6/16.
 */
@Component
public class TableInfoService {

    private Map<String, List<ColumnInfo>> tableInfos;

    public void updateTableInfos(Map<String, List<ColumnInfo>> src) {
        this.tableInfos = src;
    }

    public List<ColumnInfo> getTableInfo(String tableName) {
        return tableInfos.get(tableName);
    }
}

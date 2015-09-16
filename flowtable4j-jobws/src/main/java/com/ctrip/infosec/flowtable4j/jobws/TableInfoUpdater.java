package com.ctrip.infosec.flowtable4j.jobws;

import com.ctrip.infosec.flowtable4j.biz.processor.TableInfoService;
import com.ctrip.infosec.flowtable4j.dal.CardRiskDbService;
import com.ctrip.infosec.flowtable4j.model.persist.ColumnInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by zhangsx on 2015/6/16.
 */
@Component
public class TableInfoUpdater {

    @Autowired
    CardRiskDbService cardRiskDbService;

    @Autowired
    TableInfoService tableInfoService;

    @Scheduled(fixedDelay = 300000)
    public void execute() {
        Map<String, List<ColumnInfo>> tables = getTableInfos();
        if(tables!=null){
            tableInfoService.updateTableInfos(tables);
        }
    }

    private Map<String, List<ColumnInfo>> getTableInfos() {
        return cardRiskDbService.jdbcTemplate.query("select obj.name as tableName, " +
                "c.[name],type_name(c.[system_type_id]) data_type,c.is_nullable,c.is_identity  " +
                "from sys.columns c, sys.all_objects obj " +
                "where c.[object_id]= obj.[object_id]  " +
                "and obj.type='U' and obj.name like 'InfoSecurity%'  " +
                "order by obj.name,c.[name] ", new ResultSetExtractor<Map<String, List<ColumnInfo>>>() {
            @Override
            public Map<String, List<ColumnInfo>> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                Map<String, List<ColumnInfo>> tables = new HashMap<String, List<ColumnInfo>>();
                while (resultSet.next()) {
                    String tableName = resultSet.getString("tableName").toLowerCase();
                    List<ColumnInfo> columns = null;
                    if (tables.containsKey(tableName)) {
                        columns = tables.get(tableName);
                    } else {
                        columns = new ArrayList<ColumnInfo>();
                        tables.put(tableName, columns);
                    }
                    ColumnInfo info = new ColumnInfo();
                    info.setTableName(resultSet.getString("tableName").toLowerCase());
                    info.setName(resultSet.getString("name").toLowerCase());
                    info.setIs_nullable(resultSet.getInt("is_nullable"));
                    info.setIs_identity(resultSet.getInt("is_identity"));
                    info.setData_type(resultSet.getString("data_type"));
                    columns.add(info);
                }
                return tables;
            }
        });
    }
}

package com.ctrip.infosec.flowtable4j.jobws;

import com.ctrip.infosec.flowtable4j.dal.CardRiskService;
import com.ctrip.infosec.flowtable4j.model.persist.TableInfo;
import org.apache.bcel.generic.IFNONNULL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangsx on 2015/6/16.
 */
@Component
public class TableInfoService {
    //    private ConcurrentHashMap<String,ConcurrentHashMap<String,String>> tableInfos = new ConcurrentHashMap();
    private Map<String, List<TableInfo>> tableInfos;
    @Autowired
    JdbcTemplate cardRiskDBTemplate;

    @Scheduled(fixedDelay = 300000)
    public void execute() {
        getTableInfos();
    }

    private void getTableInfos() {
//        List<Map<String,Object>> results = cardRiskDBTemplate.queryForList("select obj.name as tableName, \n" +
//                "c.[name],type_name(c.[system_type_id]) data_type,c.is_nullable,c.is_identity \n" +
//                "from sys.columns c, sys.all_objects obj \n" +
//                "where c.[object_id]= obj.[object_id] \n" +
//                "and obj.type='U' and obj.name like 'InfoSecurity%' \n" +
//                "order by obj.name,c.[name]\n");
//        for(Iterator<Map<String,Object>> it = results.iterator();it.hasNext();){
//            Map<String,Object> row = it.next();
//            String tableName = row.get("tableName").toString().toLowerCase();
//            if(!tableInfos.containsKey(tableName)){
//                tableInfos.put(tableName,new ConcurrentHashMap<String,String>());
//            }
//            tableInfos.get(tableName).put(row.get("name").toString().toLowerCase(),row.get("data_type").toString());
//        }

        tableInfos = cardRiskDBTemplate.query("select obj.name as tableName, \n" +
                "c.[name],type_name(c.[system_type_id]) data_type,c.is_nullable,c.is_identity \n" +
                "from sys.columns c, sys.all_objects obj \n" +
                "where c.[object_id]= obj.[object_id] \n" +
                "and obj.type='U' and obj.name like 'InfoSecurity%' \n" +
                "order by obj.name,c.[name]\n", new ResultSetExtractor<Map<String, List<TableInfo>>>() {
            @Override
            public Map<String, List<TableInfo>> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                Map<String, List<TableInfo>> result = new HashMap<String, List<TableInfo>>();
                while(resultSet.next()){
                    TableInfo info = new TableInfo();
                    info.setTableName(resultSet.getString("tableName").toLowerCase());
                    info.setName(resultSet.getString("name").toLowerCase());
                    info.setIs_nullable(resultSet.getInt("is_nullable"));
                    info.setIs_identity(resultSet.getInt("is_identity"));
                    info.setData_type(resultSet.getString("data_type"));

                    if(result.get(info.getTableName())==null)
                        result.put(info.getTableName(),new ArrayList<TableInfo>());
                    result.get(info.getTableName()).add(info);
                }
                return result;
            }
        });
    }

    public List<TableInfo> getTableInfo(String tableName) {
        return tableInfos.get(tableName);
    }
}

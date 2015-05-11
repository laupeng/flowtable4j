package com.ctrip.infosec.flowtable4j.translate.dao.Jndi;

import com.ctrip.datasource.AllInOneConfigParser;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lpxie on 15-4-29.
 * 这个代码是参考dataDispatch来的
 * 这样可以不用context文件来连接数据库
 */
@Repository
public class LocalDataSourceProvider
{
    private static final ConcurrentHashMap<String,DataSource> dataSourcePool = new ConcurrentHashMap<String,DataSource>();

    private static Map<String,String[]> props =null;

    public static synchronized DataSource getDataSource(String name) throws SQLException
    {
        DataSource dataSource = dataSourcePool.get(name);
        if(dataSource == null)
        {
            props = AllInOneConfigParser.newInstance().getDBAllInOneConfig();
            dataSource = createDataSource(name);
            dataSourcePool.put(name, dataSource);
        }
        return dataSource;
    }

    private static DataSource createDataSource(String name) throws SQLException
    {
        String[] prop = props.get(name);
        PoolProperties p = new PoolProperties();
        p.setUrl(prop[0]);
        p.setUsername(prop[1]);
        p.setPassword(prop[2]);
        p.setDriverClassName(prop[3]);
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(100);
        p.setInitialSize(10);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(10);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
                        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource(p);
        ds.createPool();
        return ds;
    }
}

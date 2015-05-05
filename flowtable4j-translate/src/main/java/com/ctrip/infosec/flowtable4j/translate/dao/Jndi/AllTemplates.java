package com.ctrip.infosec.flowtable4j.translate.dao.Jndi;

import com.ctrip.datasource.AllInOneConfigParser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.SQLException;

/**
 * Created by lpxie on 15-4-29.
 */
public class AllTemplates
{
    private JdbcTemplate cardRiskDBTemplate = null;
    private JdbcTemplate riskCtrlPreProcDBTemplate = null;
    private JdbcTemplate cUSRATDBTemplate = null;
    //fixme 这里以后读取配置文件，在每个环境的名称不一样
    private String[] dbNames = {"CardRiskDB_SELECT_1","RiskCtrlPreProcDB_SELECT_1","CUSRATDB_SELECT_1"};//注意这里的name顺序不能改变！ RiskCtrlPreProcDB

    private void init() throws SQLException
    {
        //加载一次AllInOne配置文件
        AllInOneConfigParser.newInstance().reloadAllInOneConfig();
        cardRiskDBTemplate = new JdbcTemplate(LocalDataSourceProvider.getDataSource(dbNames[0]));
        riskCtrlPreProcDBTemplate = new JdbcTemplate(LocalDataSourceProvider.getDataSource(dbNames[1]));
        cUSRATDBTemplate = new JdbcTemplate(LocalDataSourceProvider.getDataSource(dbNames[2]));
    }

    public JdbcTemplate getCardRiskDBTemplate()
    {
        return cardRiskDBTemplate;
    }

    public JdbcTemplate getRiskCtrlPreProcDBTemplate()
    {
        return riskCtrlPreProcDBTemplate;
    }

    public JdbcTemplate getcUSRATDBTemplate()
    {
        return cUSRATDBTemplate;
    }
}

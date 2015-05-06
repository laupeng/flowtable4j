package com.ctrip.infosec.flowtable4j.translate.dao.Jndi;

import com.ctrip.datasource.AllInOneConfigParser;
import com.ctrip.infosec.flowtable4j.translate.common.GlobalConfig;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.SQLException;

/**
 * Created by lpxie on 15-4-29.
 */
public class AllTemplates
{
    private static Logger logger = LoggerFactory.getLogger(AllTemplates.class);

    private JdbcTemplate cardRiskDBTemplate = null;
    private JdbcTemplate riskCtrlPreProcDBTemplate = null;
    private JdbcTemplate cUSRATDBTemplate = null;

    static String cardRiskDBName = GlobalConfig.getString("CardRiskDB");
    static String riskCtrlPreProcDBName = GlobalConfig.getString("RiskCtrlPreProcDB");
    static String CUSRATDBName = GlobalConfig.getString("CUSRATDB");

    static void check() {
        Validate.notEmpty(cardRiskDBName, "在GlobalConfig.properties里没有找到\"CardRiskDB\"配置项.");
        Validate.notEmpty(riskCtrlPreProcDBName, "在GlobalConfig.properties里没有找到\"RiskCtrlPreProcDB\"配置项.");
        Validate.notEmpty(CUSRATDBName, "在GlobalConfig.properties里没有找到\"CUSRATDB\"配置项.");
    }

    private void init() throws SQLException
    {
        check();
        logger.info("开始初始化JNDI模板");
        //加载一次AllInOne配置文件
        AllInOneConfigParser.newInstance().reloadAllInOneConfig();
        cardRiskDBTemplate = new JdbcTemplate(LocalDataSourceProvider.getDataSource(cardRiskDBName));
        riskCtrlPreProcDBTemplate = new JdbcTemplate(LocalDataSourceProvider.getDataSource(riskCtrlPreProcDBName));
        cUSRATDBTemplate = new JdbcTemplate(LocalDataSourceProvider.getDataSource(CUSRATDBName));
        logger.info("初始化JNDI模板结束");
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

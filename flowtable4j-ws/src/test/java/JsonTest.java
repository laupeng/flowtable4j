import com.ctrip.infosec.flowtable4j.model.CheckFact;
import com.ctrip.infosec.sars.monitor.util.Utils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.sql.Types;

/**
 * Created by zhangsx on 2015/4/24.
 */

public class JsonTest {
//    @Test
    public void testJsonIgnore() {
        CheckFact fact = Utils.JSON.parseObject("{\n" +
                "    \"checkTypes\": [\n" +
                "        \"BW\",\n" +
                "        \"FLOWRULE\",\n" +
                "        \"ACCOUNT\"" +
                "    ],\n" +
                "    \"accountFact\": {},\n" +
                "    \"bwFact\": {\n" +
                "        \"cc\": \"1\",\n" +
                "        \"orderType\": 14,\n" +
                "        \"content\": {}\n" +
                "    },\n" +
                "    \"flowFact\": {\n" +
                "        \"cc\": \"11\",\n" +
                "        \"orderType\": 14,\n" +
                "        \"prepayType\": [\n" +
                "            \"CCARD\",\n" +
                "            \"Tmony\"\n" +
                "        ],\n" +
                "        \"content\": {}\n" +
                "    },\n" +
                "    \"reqId\": 7755592\n" +
                "}", CheckFact.class);
        System.out.println(fact);
    }
//    @Test
    public void testSQLType() {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("aa", 1, Types.NVARCHAR);

        System.out.println(((SqlParameterSource) sqlParameterSource).getSqlType("aa"));
    }

}

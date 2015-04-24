import com.ctrip.infosec.flowtable4j.model.CheckFact;
import com.ctrip.infosec.sars.monitor.util.Utils;
import org.junit.Test;

/**
 * Created by zhangsx on 2015/4/24.
 */

public class JsonTest {
    @Test
    public void testJsonIgnore(){
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
                "}",CheckFact.class);
        System.out.println(fact);
    }
}

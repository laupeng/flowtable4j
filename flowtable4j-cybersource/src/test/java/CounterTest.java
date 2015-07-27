
import com.ctrip.infosec.flowtable4j.visa.Card;
import com.ctrip.infosec.flowtable4j.visa.VisaClient;
import com.ctrip.infosec.flowtable4j.visa.VisaRequest;
import com.ctrip.infosec.flowtable4j.visa.VisaResponse;
import com.ctrip.infosec.sars.util.mapper.JsonMapper;
import org.junit.Test;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Map;
//import org.junit.Test;

/**
 * Created by zhangsx on 2015/4/14.
 */
public class CounterTest {
    protected static JsonMapper mapper = new JsonMapper();
    @Test
    public void testReplace() throws Exception {
        VisaRequest request = new VisaRequest();
        request.setMerchantID("cybersource_ctrip");
        request.setMerchantReferenceCode("MRC-123");
        Card card=new Card();
        card.setAccountNumber("5555555555554444");
        card.setCardType("MASTER");
        card.setExpirationMonth(new BigInteger("8"));
        card.setExpirationYear(new BigInteger("2018"));
        request.setCard(card);
        VisaClient client= new VisaClient();
        VisaResponse map =client.requestVisa(request);
        System.out.println(mapper.toJson(map));

    }
}

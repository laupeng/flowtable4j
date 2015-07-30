import com.ctrip.infosec.flowtable4j.dal.ESBClient;
import com.google.common.base.Strings;
//import org.junit.Test;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Map;
//import org.junit.Test;

/**
 * Created by zhangsx on 2015/4/14.
 */
public class CounterTest {
    protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX");

    private boolean f1()
    {
        return true;
    }

    private boolean f2()
    {
        return true;
    }
    @Test
    public void testReplace() throws Exception {


        System.out.println("0001-01-01 00:00:00.000".compareTo("1900-01-01"));
//        String mobile = null;
//        if (!Strings.isNullOrEmpty(mobile)) {
//            while (mobile.startsWith("0")) {
//                mobile = mobile.substring(1);
//            }
//        }
//        System.out.println(mobile);
//        ESBClient client= new ESBClient();
////        System.out.println("======MemberInfo====");
////        Map me =client.getMemberInfo("wwwwww");
////        for(Object key:me.keySet()){
////            System.out.println(key +": "+me.get(key));
////        }
////        System.out.println("======Customer Info====");
////        Map cu = client.getCustomerInfo("bbbb");
////        for(Object key:cu.keySet()){
////            System.out.println(key +": "+cu.get(key));
////        }
//
//        System.out.println("======Card Inf====");
//        Map cc = client.getCardInfo("28996388");
//        for(Object key:cc.keySet()){
//            System.out.println(key +": "+cc.get(key));
//        }
  }
}

import com.google.common.base.Strings;
//import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import org.junit.Test;

/**
 * Created by zhangsx on 2015/4/14.
 */
public class CounterTest {
//    @Test
    public void testReplace(){
        Pattern p = Pattern.compile("(^WWW.CTR.)|(^WWW.FLIGHT)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher("WWW.CTRP");
        System.out.println(m.find());
        System.out.println(m.matches());
        Set set=new HashSet();
        Object a=111;
        set.add("222");
        set.add("111");
        set.add(a);
        System.out.println(set.size());
  }
}

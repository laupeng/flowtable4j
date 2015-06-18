import com.google.common.base.Strings;
//import org.junit.Test;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import org.junit.Test;

/**
 * Created by zhangsx on 2015/4/14.
 */
public class CounterTest {
    private boolean f1()
    {
        return true;
    }

    private boolean f2()
    {
        return true;
    }
    @Test
    public void testReplace(){
        String mobile = null;
        if (!Strings.isNullOrEmpty(mobile)) {
            while (mobile.startsWith("0")) {
                mobile = mobile.substring(1);
            }
        }
        System.out.println(mobile);
  }
}

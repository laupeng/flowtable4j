import com.google.common.base.Strings;
import org.junit.Test;
//import org.junit.Test;
//import org.junit.Test;

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
        Map<String, Object> parentMap = new HashMap<String, Object>();
        parentMap.put("AA",12233);
        Object obj = parentMap.get("AA");
        if(obj != null)
        {
            System.out.println(obj.toString());
        }
        String aa=null;
        System.out.println(f1()|f2());
        System.out.println(String.format("%d GT %d",2,1));
  }
}

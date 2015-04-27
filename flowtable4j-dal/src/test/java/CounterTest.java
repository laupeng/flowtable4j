import com.google.common.base.Strings;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.junit.Test;
//import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import org.junit.Test;

/**
 * Created by zhangsx on 2015/4/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/flowtable4j.datasource.xml"})
public class CounterTest {
//    @Test
    public void testReplace(){
        System.out.println("|A00906791|a00906792|".contains("a00906792"));
        //System.out.println(m.find());
        //System.out.println(m.matches());

  }
}

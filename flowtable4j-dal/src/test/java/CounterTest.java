import com.google.common.base.Strings;
//import org.junit.Test;

/**
 * Created by zhangsx on 2015/4/14.
 */
public class CounterTest {
//  @Test
    public void testReplace(){
      String sql="select uid from";
      sql ="select top 1000 " + sql.substring(7);
      System.out.println(sql);
  }
}

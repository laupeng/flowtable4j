import com.sun.org.apache.xpath.internal.operations.Bool;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * Created by zhangsx on 2015/3/24.
 */
public class CompletionServicetest {
    @Test
    public void testcompletionService() {
        final ExecutorService executorService = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 3; i++) {
            final int finalI = i;
            executorService.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    if (finalI == 2) {
                        executorService.shutdownNow();
                        System.out.println("executorService is shundownnow");
                        return true;
                    }
                    Thread.sleep(1000);
                    System.out.println("threadname:"+Thread.currentThread().getName());
                    return false;
                }
            });
        }

        try {
            System.out.println("awaitTermination is success:"+executorService.awaitTermination(3, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

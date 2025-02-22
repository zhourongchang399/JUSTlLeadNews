import org.checkerframework.checker.units.qual.A;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author ：Zc
 * @description：TODO
 * @date ：2025/2/22 15:05
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ScheduleApplicationTest {

    public void redisTest() {
        RedisTemplate redisTemplate = new RedisTemplate();
    }

}

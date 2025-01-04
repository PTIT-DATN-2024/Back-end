package selling_electronic_devices.back_end.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/test")
    public String testRedisConnection() {
        try {
            redisTemplate.opsForValue().set("testKey3-1-2025", "3/1/2025 chuẩn bị ông công ông táo.");
            return "Ok. Redis Connection Successfully.";
        } catch (Exception e) {
            return "Failed to connect Redis.";
        }
    }
}

package selling_electronic_devices.back_end.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;

@Configuration
public class RedisConfig {

    // connection factory
    @Bean
    public RedisConnectionFactory redisConnectionFactoryChatMessage() {
        return new LettuceConnectionFactory("127.0.0.1", 6379);
    }

    // connect
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        return createRedisTemplate(redisConnectionFactoryChatMessage());
    }

    //
    private RedisTemplate<String, Object> createRedisTemplate(RedisConnectionFactory connectionFactory) { //, ObjectMapper objectMapper) { //dùng khi cấu hình riêng
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Cấu hình ObjectMapper (trực tiếp) để hỗ trợ LocalDateTime
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Yêu cầu Jackson lưu ngày giờ dưới dạng chuỗi ISO-8601 thay vì timestamp số.

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        template.setDefaultSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
        template.setValueSerializer(serializer);
        return template;
    }

    // Cấu hình ObjectMapper riêng
//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        return objectMapper;
//    }
}

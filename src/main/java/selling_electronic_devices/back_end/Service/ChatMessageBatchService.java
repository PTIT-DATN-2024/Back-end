package selling_electronic_devices.back_end.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import selling_electronic_devices.back_end.Entity.ChatMessage;
import selling_electronic_devices.back_end.Repository.ChatMessageRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatMessageBatchService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String REDIS_CHAT_KEY_PREFIX = "chatbox:";
    private static final Long REDIS_BATCH_SIZE = 500L; // ngưỡng lưu batch

    @Scheduled(fixedRate = 60000) // lịch chạy mỗi 30s
    public void batchSaveMessage() {
        // lấy all keys trong Redis có prefix = "chat_box"
        Set<String> keys = redisTemplate.keys(REDIS_CHAT_KEY_PREFIX + "*");

        // lưu toàn bộ nội dung (message) trong mỗi key (chatBox) vào database
        if (keys != null) {
            for (String key : keys) {
                // Thêm batch size condition
                //Long size = redisTemplate.opsForList().size(key);
                //if (size != null && size >= REDIS_BATCH_SIZE) {
                List<Object> messages = redisTemplate.opsForList().range(key, 0, -1); // lấy all mess của key (chat_box)
                // convert obj -> ChatMessage trước khi save
                if (messages != null && !messages.isEmpty()) {
                    List<ChatMessage> chatMessages = messages.stream()
                            .map(msg -> objectMapper.convertValue(msg, ChatMessage.class))
                            .collect(Collectors.toList());

                    // Save batch -> database
                    chatMessageRepository.saveAll(chatMessages);

                    // Xóa khỏi redis sau khi đã lưu thành công
                    redisTemplate.delete(key);
                    //redisTemplate.execute((RedisCallback<Object>) connection -> { // Đảm bảo không delete khi saveAll() vào database thất bại
                    //    connection.del(key.getBytes());
                    //    return null;
                    //});
                }
            }
        }

    }

}

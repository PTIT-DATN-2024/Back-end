package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import selling_electronic_devices.back_end.Entity.IceBox;
import selling_electronic_devices.back_end.Repository.IceBoxRepository;

@Service
public class RedisKeyExpirationListener implements MessageListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private IceBoxRepository iceBoxRepository;

    @Value("${redis.key.prefix.chat_box}")
    private String REDIS_CHAT_KEY_PREFIX;// = "chatbox:";

    private final StringRedisTemplate redisTemplate;

    public RedisKeyExpirationListener(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) { // hàm callback: thực thi mỗi khi Redis gửi msg đến Client (BackendJava)
        String expiredKey = message.toString(); // get key

        // get chatBoxId
        if (expiredKey.startsWith(REDIS_CHAT_KEY_PREFIX)) {
            String chatBoxId = expiredKey.substring(REDIS_CHAT_KEY_PREFIX.length());
            handleChatBoxExpiration(chatBoxId); // xử lý chat box hêt hạn
        }
    }

    public void handleChatBoxExpiration(String chatBoxId) {
        IceBox iceBox = iceBoxRepository.findById(chatBoxId).orElse(null);
        if (iceBox != null) {
            iceBox.setStatus("PROCESSED");
            iceBoxRepository.save(iceBox);

            // thông báo cho staff
            messagingTemplate.convertAndSend("/notification/kill-task/" + chatBoxId, "KILL");
        }
    }
}

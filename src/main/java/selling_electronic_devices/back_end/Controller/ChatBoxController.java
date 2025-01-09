package selling_electronic_devices.back_end.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import selling_electronic_devices.back_end.Entity.ChatMessage;
import selling_electronic_devices.back_end.Entity.IceBox;
import selling_electronic_devices.back_end.Repository.ChatMessageRepository;
import selling_electronic_devices.back_end.Repository.IceBoxRepository;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat")
public class ChatBoxController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private IceBoxRepository iceBoxRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${redis.key.prefix.chat_box}")
    private String REDIS_CHAT_KEY_PREFIX;
    //private static final String REDIS_CHAT_KEY_PREFIX = "chatbox:";

    @MessageMapping("/{to}/{chatBoxId}") //bỏ "/publish" vì Spring tự động thêm prefix "/publish" đã cáu hình vào các destination từ client.
    // {to} để biết SendTo đến ai customer || staff, tránh nhận lại mess của chính mình
    // -> lúc publish thêm {to} "/publish/staff/1234" -> MessageMapping thêm {to}
    @SendTo("/notification/{to}/{chatBoxId}") //giữ prefix "/notification" vì đây là nơi bạn muốn gửi tin nhắn đến, và các client sẽ subscribe vào các topic có prefix này.
    public ChatMessage handleChatMessage(@org.springframework.messaging.handler.annotation.DestinationVariable String chatBoxId,
                                         @org.springframework.messaging.handler.annotation.DestinationVariable String to,
                                         ChatMessage message) {
        try {

            //String sanitizeMessage = HtmlUtils.htmlEscape(message.getMessage()); // Prevent XSS
            String standUtf8 = new String(HtmlUtils.htmlEscape(message.getMessage()).getBytes(StandardCharsets.UTF_8)); // sanitize data + fix font

            Optional<IceBox> optionalIceBox = iceBoxRepository.findById(chatBoxId);
            if (optionalIceBox.isPresent()) {
                IceBox iceBox = optionalIceBox.get();

                // 1. Update status và thông báo khi: customer gửi tin nhắn (chưa được nhận xử lý) HOẶC Staff click chọn task từ Ice Box
                if (to.equals("staff") && iceBox.getStatus().equals("PROCESSED")) { //(customer gửi tin nhắn đầu): nếu chỉ 2 status PROCESSED || PENDING (hoặc xóa || ko xóa) ===> khi staff click cập nhật status "PROCESSED" -> dẫn đến vi trùng điều kiện customer "PROCESSED" và nó lại set lại "PENDING" dù đang xử lý. TH xóa || ko xóa tương tự ---> status 3th = "IN PROGRESS" thỏa mãn: staff update status mà ko trùng status customer
                    iceBox.setStatus("PENDING"); // -> thêm vào "Ice-Box"
                    iceBox.setUpdatedAt(LocalDateTime.now());
                    iceBoxRepository.save(iceBox);

                    // Thông báo cho all staff update "Ice-Box"
                    messagingTemplate.convertAndSend("/notification/update-ice-box", "UPDATE");
                } else if (to.equals("customer") && iceBox.getStatus().equals("PENDING")) { // staff click chọn task từ Ice Box
                    iceBox.setStatus("IN PROGRESS");
                    iceBox.setUpdatedAt(LocalDateTime.now());
                    iceBoxRepository.save(iceBox);

                    // set TTL cho chatBox khi staff (click) bắt đầu xử lý
                    Long currentTtl = redisTemplate.getExpire(REDIS_CHAT_KEY_PREFIX + chatBoxId);
                    if (currentTtl == null || currentTtl == -1) { // nếu key vừa add msg chưa có TTL
                        redisTemplate.expire(REDIS_CHAT_KEY_PREFIX + chatBoxId, 1, TimeUnit.MINUTES);
                    }

                    // Thông báo (vừa remove: PENDING -> IN PROGRESS) để update "Ice-Box"
                    messagingTemplate.convertAndSend("/notification/update-ice-box", "UPDATE");
                }

                // 2. Lưu tin nhắn
//                if (iceBox.getStatus().equals("PENDING")) {
//                    // 2.1. Lưu tin nhắn vào Postgres
//                    chatMessageRepository.save(getChatMessage(chatBoxId, to, message));
//                } else if (iceBox.getStatus().equals("IN PROGRESS")) {
//                    // 2.2. Lưu vào Redis
//                    redisTemplate.opsForList().rightPush(REDIS_CHAT_KEY_PREFIX + chatBoxId, getChatMessage(chatBoxId, to, message));
//                }
                // Lưu toàn bộ tin nhán (bất kể status, to?...) vào Redis + TTL
                // Lưu tin nhắn vào Redis
                redisTemplate.opsForList().rightPush(REDIS_CHAT_KEY_PREFIX + chatBoxId, getChatMessage(chatBoxId, to, message));

                // 3. Kiểm tra thời hạn cuộc trò chuyện
                LocalDateTime latestUpdate = iceBox.getUpdatedAt();
                System.out.println("TIME between: " + Duration.between(latestUpdate, LocalDateTime.now()).toHours());
                if (Duration.between(latestUpdate, LocalDateTime.now()).toHours() >= 1) { //ChronoUnit.HOURS.between(beginUpdate, LocalDateTime.now()) - 1;
                    iceBox.setStatus("PROCESSED");
                    iceBox.setUpdatedAt(LocalDateTime.now());
                    iceBoxRepository.save(iceBox);

                    // Gửi thông báo tới staff để unsubscribe "/notification/staff/{chatBoxId}"
                    messagingTemplate.convertAndSend("/notification/kill-task/" + chatBoxId, "KILL");
                }

            } else {
                throw new IllegalArgumentException("Not found IceBox with ID.");
            }

            // 2.1 Lưu tin nhắn vào Postgres
            //chatMessageRepository.save(getChatMessage(chatBoxId, to, message));

            // 2.2. Lưu vào trong Redis
//            if (optionalIceBox.get().getStatus().equals("IN PROGRESS")) {
//                redisTemplate.opsForList().rightPush(REDIS_CHAT_KEY_PREFIX + chatBoxId, getChatMessage(chatBoxId, to, message));
//            }
//
//            // 3. Kiểm tra thời hạn của cuộc trò chuyện. Nếu thời gian vượt quá 1 giờ, cập nhật trạng thái và thông báo ngắt kết nối
//            IceBox iceBox = optionalIceBox.get();
//            LocalDateTime beginUpdate = iceBox.getUpdatedAt();
//            System.out.println("TIME between: " + Duration.between(beginUpdate, LocalDateTime.now()).toHours());
//            if (Duration.between(beginUpdate, LocalDateTime.now()).toHours() >= 1) { //ChronoUnit.HOURS.between(beginUpdate, LocalDateTime.now()) - 1;
//                iceBox.setStatus("PROCESSED");
//                iceBox.setUpdatedAt(LocalDateTime.now());
//                iceBoxRepository.save(iceBox);
//
//                // Gửi thông báo tới staff về việc kết thúc phiên trò chuyện
//                messagingTemplate.convertAndSend("/notification/kill-task/" + chatBoxId, "KILL-TASK");
//            }

            return message;
        } catch (Exception e) {
            message.setMessage("An error occurred: " + e.getMessage());

            return getChatMessage(chatBoxId, to, message);
        }
    }

    private static ChatMessage getChatMessage(String chatBoxId, String to, ChatMessage message) {
        message.setChatMessageId(UUID.randomUUID().toString());
        message.setChatBoxId(chatBoxId);
        message.setBy(to.equals("customer") ? "STAFF" : "CUSTOMER");
        message.setMessage(message.getMessage()); // Prevent XSS
        message.setTimestamp(LocalDateTime.now());

        return message;
    }

    // Staff call khi vừa click chọn 1 task từ "Ice-Box" -> lấy ra all messages customer nhắn.
    @GetMapping("/{chatBoxId}")
    public ResponseEntity<?> getAllMessageByChatBoxId(@PathVariable String chatBoxId,
                                                      @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                      @RequestParam(value = "limit", defaultValue = "10") int limit) {
        Map<String, Object> response = new HashMap<>();
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("timestamp")));
        response.put("EC", 0);
        response.put("MS", "Get message of box successfully.");

        // lấy tin nhắn từ Redis thay vì Database
        List<Object> messages = redisTemplate.opsForList().range(REDIS_CHAT_KEY_PREFIX + chatBoxId, 0, -1);
        // convert -> ChatMessage
        if (messages != null) {
            List<ChatMessage> chatMessages1 = messages.stream()
                    .map(msg -> objectMapper.convertValue(msg, ChatMessage.class))
                    .collect(Collectors.toList());

            //Collections.reverse(chatMessages1);
            response.put("messages", chatMessages1);
        }

        return ResponseEntity.ok(response);
    }

    // Fetch Ice Box
    @GetMapping("/ice-box/tasks")
    public ResponseEntity<?> getAllTaskFromIceBox(@RequestParam(value = "offset", defaultValue = "0") int offset, @RequestParam(value = "limit", defaultValue = "10") int limit) {

        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Order.asc("updatedAt")));
        Map<String, Object> response = new HashMap<>();
        response.put("EC", 0);
        response.put("MS", "Get all task from Ice-Box successfully.");
        response.put("tasks", iceBoxRepository.findByStatus("PENDING", pageRequest));

        return ResponseEntity.ok(response);
    }
////okkkkkkkkkkk
    // staff gia hạn thêm thời gian cuộc trò chuyện với customer
    @PutMapping("/extend/{chatBoxId}")
    public ResponseEntity<?> extendTime (@PathVariable String chatBoxId, @RequestParam(value = "action") String action) {
        Optional<IceBox> optionalIceBox = iceBoxRepository.findById(chatBoxId);
//        return optionalIceBox
//                .map(
//                        iceBox -> {
//                            iceBox.setUpdatedAt(LocalDateTime.now().minusMinutes(30));// now() - begin ~ 1h -> update begin = now - 30' ==> now() - begin = 30' < 1h
//                            iceBoxRepository.save(iceBox);
//
//                            return ResponseEntity.ok(Map.of("EC", 0, "MS", "Extended expired time."));
//                        }
//                )
//                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("EC", 1, "MS", "Extend expired time failed.")));

        try {
            if (action.equals("extend")) {
                redisTemplate.expire(REDIS_CHAT_KEY_PREFIX + chatBoxId, 3, TimeUnit.MINUTES);
                return ResponseEntity.ok("Extended Time.");
            } else {//if (action.equals("end")) {
                redisTemplate.expire(REDIS_CHAT_KEY_PREFIX + chatBoxId, 1, TimeUnit.MILLISECONDS);
                return ResponseEntity.ok("Closed chat box.");
            }
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error: " + e.getMessage());
        }
    }


}

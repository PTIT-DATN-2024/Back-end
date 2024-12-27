package selling_electronic_devices.back_end.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;
import selling_electronic_devices.back_end.Entity.ChatMessage;
import selling_electronic_devices.back_end.Repository.ChatMessageRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
public class ChatBoxController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/{to}/{chat_box_id}") //bỏ "/publish" vì Spring tự động thêm prefix "/publish" đã cáu hình vào các destination từ client.
    // {to} để biết SendTo đến ai customer || staff,tránh nhận lại mess của chính mình
    // -> lúc publish thêm {to} "/publish/staff/1234" -> MessageMapping thêm {to}
    @SendTo("/notification/{to}/{chat_box_id}") //giữ prefix "/notification" vì đây là nơi bạn muốn gửi tin nhắn đến, và các client sẽ subscribe vào các topic có prefix này.
    public ChatMessage handleChatMessage(@org.springframework.messaging.handler.annotation.DestinationVariable String chat_box_id,
                                         @org.springframework.messaging.handler.annotation.DestinationVariable String to
                                         ,ChatMessage message) {
        // sanitize data and save to the database
        String sanitizeMessage = HtmlUtils.htmlEscape(message.getMessage()); // Prevent XSS
        String standUtf8 = new String(sanitizeMessage.getBytes(StandardCharsets.UTF_8)); // do sau khi dùng HttmlEscape font bị lỗi

        message.setChatMessageId(UUID.randomUUID().toString());
        message.setChatBoxId(chat_box_id);
        message.setBy(to.equals("customer") ? "STAFF" : "CUSTOMER");
        message.setMessage(standUtf8); // Prevent XSS
        message.setTimestamp(LocalDateTime.now());

        chatMessageRepository.save(message);

        return message;
    }

}

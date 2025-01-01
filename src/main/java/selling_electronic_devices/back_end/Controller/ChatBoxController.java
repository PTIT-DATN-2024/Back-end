package selling_electronic_devices.back_end.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/chat")
public class ChatBoxController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private IceBoxRepository iceBoxRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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

                    // Có thể delete all chat_messages sau khi staff fetch (save on local: cookie, session, ...), staff -customer lúc này nhận tin và save trên FE, set TTL = 1h, KHI HẾT 1H ===> Call api update status IceBox = "PROCESSED" để phục vụ cho lần yêu cầu tư ván sau của Cust
                    //chatMessageRepository.deleteAllByChatBoxId(chatBoxId);

                    // Thông bao vừa remove (do 1 staff chọn xử lý) để tất cả staff update "Ice-Box"
                    messagingTemplate.convertAndSend("/notification/update-ice-box", "UPDATE");
                }
            } else {
                throw new IllegalArgumentException("Not found IceBox with ID.");
            }


            chatMessageRepository.save(getChatMessage(chatBoxId, to, message));

            // Nếu thời gian vượt quá 1 giờ, cập nhật trạng thái và thông báo ngắt kết nối
            IceBox iceBox = optionalIceBox.get();
            LocalDateTime beginUpdate = iceBox.getUpdatedAt();
            System.out.println("TIME between: " + Duration.between(beginUpdate, LocalDateTime.now()).toHours());
            if (Duration.between(beginUpdate, LocalDateTime.now()).toHours() >= 1) { //ChronoUnit.HOURS.between(beginUpdate, LocalDateTime.now()) - 1;
                iceBox.setStatus("PROCESSED");
                iceBox.setUpdatedAt(LocalDateTime.now());
                iceBoxRepository.save(iceBox);

                // Gửi thông báo tới staff về việc kết thúc phiên trò chuyện
                messagingTemplate.convertAndSend("/notification/kill-task/" + chatBoxId, "KILL-TASK");
            }

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

        List<ChatMessage> chatMessages = new ArrayList<>(chatMessageRepository.findAllByChatBoxId(chatBoxId, pageRequest).getContent()); // Nếu dùng list thay page -> ko phải immutable list -> reverse đuộc, còn nếu dùng page thì phải array list hóa result (immutable list) trước khi reverse
        //List<ChatMessage> chatMessages1 = chatMessageRepository.findAllByChatBoxId(chatBoxId, pageRequest); // dùng list thay page
        Collections.reverse(chatMessages); // đảo ngược để fe tiện hiển thị.
        response.put("messages", chatMessages);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/status/{chatBoxId}")
    public ResponseEntity<?> updateStatusChatBox(@PathVariable String chatBoxId, @RequestParam(value = "status") String status) {
        Optional<IceBox> optionalIceBox = iceBoxRepository.findById(chatBoxId);
        String statusBeforeUpdate = "";
        if (optionalIceBox.isPresent()) {
            // update status
            IceBox iceBox = optionalIceBox.get();
            statusBeforeUpdate = iceBox.getStatus();
            iceBox.setStatus("PROCESSED");
            iceBoxRepository.save(iceBox);

            return ResponseEntity.ok((Map.of("EC", 0, "MS", "Updated status: " + statusBeforeUpdate + " ---> " + iceBox.getStatus())));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("EC", 1, "MS", "Not found Ice Box with chat_box_id."));
        }
    }

    // call khi vừa click 1 task từ "Ice-Box" -> xóa task khỏi "Ice-Box"
    @DeleteMapping("/ice-box/{chatBoxId}")
    public ResponseEntity<?> deleteTaskFromIceBox(@PathVariable String chatBoxId) {
        Optional<IceBox> optionalIceBox = iceBoxRepository.findById(chatBoxId);
        return optionalIceBox
                .map(
                        iceBox -> {
                            iceBoxRepository.delete(iceBox);
                            return ResponseEntity.ok(Map.of("EC", 0, "MS", "Deleted task from ice box."));
                        }
                )
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("EC", 1, "MS", "Not found task in ice box.")));

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

    // staff gia hạn thêm thời gian cuộc trò chuyện với customer
    @PutMapping("/extend/{chatBoxId}")
    public ResponseEntity<?> extendTime (@PathVariable String chatBoxId) {
        Optional<IceBox> optionalIceBox = iceBoxRepository.findById(chatBoxId);
        return optionalIceBox
                .map(
                        iceBox -> {
                            iceBox.setUpdatedAt(LocalDateTime.now().plusHours(1));
                            return ResponseEntity.ok(Map.of("EC", 0, "MS", "Extended expired time."));
                        }
                )
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("EC", 1, "MS", "Extend expired time failed.")));
    }



}

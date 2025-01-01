package selling_electronic_devices.back_end.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_messages",
        indexes = @Index(name = "idx_chat_message_cbi", columnList = "chat_box_id") // đánh index để tăng hiệu xuất get all message theo chat_box_id.
)
public class ChatMessage {

    @Id
    @Column(name = "chat_message_id")
    private String chatMessageId;

    // ref ice_box
    @Column(name = "chat_box_id", nullable = false)
    private String chatBoxId;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "by", nullable = false)
    private String by; //CUSTOMER or STAFF

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public String getChatMessageId() {
        return chatMessageId;
    }

    public void setChatMessageId(String chatMessageId) {
        this.chatMessageId = chatMessageId;
    }

    public String getChatBoxId() {
        return chatBoxId;
    }

    public void setChatBoxId(String chatBoxId) {
        this.chatBoxId = chatBoxId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

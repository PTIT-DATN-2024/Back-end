package selling_electronic_devices.back_end.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import selling_electronic_devices.back_end.Entity.ChatMessage;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {

    Page<ChatMessage> findAllByChatBoxId(String chatBoxId, PageRequest pageRequest);

    void deleteAllByChatBoxId(String chatBoxId);
}

package ie.revalue.authenticatedbackend.repository;

import ie.revalue.authenticatedbackend.models.Conversation;
import ie.revalue.authenticatedbackend.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByConversationIdOrderByTimestampAsc(Integer conversationId);
}

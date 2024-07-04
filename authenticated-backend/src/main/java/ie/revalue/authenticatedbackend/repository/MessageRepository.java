package ie.revalue.authenticatedbackend.repository;

import ie.revalue.authenticatedbackend.models.ConversationTopic;
import ie.revalue.authenticatedbackend.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByConversationTopicOrderByTimestampAsc(ConversationTopic conversationTopic);
}

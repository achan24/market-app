package ie.revalue.authenticatedbackend.service;

import ie.revalue.authenticatedbackend.exceptions.ResourceNotFoundException;
import ie.revalue.authenticatedbackend.models.ApplicationUser;
import ie.revalue.authenticatedbackend.models.ConversationTopic;
import ie.revalue.authenticatedbackend.models.Message;
import ie.revalue.authenticatedbackend.repository.ConversationTopicRepository;
import ie.revalue.authenticatedbackend.repository.MessageRepository;
import org.hibernate.annotations.NaturalId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private ConversationTopicRepository conversationTopicRepository;

    @Autowired
    private MessageRepository messageRepository;

    public List<ConversationTopic> getUserConversations(ApplicationUser user) {
        return conversationTopicRepository.findByBuyerOrSellerOrderByUpdatedAtDesc(user, user);
    }

    public List<Message> getConversationMessages(Integer conversationTopicId) {
        ConversationTopic topic = conversationTopicRepository.findById(conversationTopicId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
        return messageRepository.findByConversationTopicOrderByTimestampAsc(topic);
    }

    public Message sendMessage(Integer conversationTopicId, ApplicationUser sender, String content) {
        ConversationTopic topic = conversationTopicRepository.findById(conversationTopicId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        Message message = new Message();
        message.setConversationTopic(topic);
        message.setSender(sender);
        message.setContent(content);

        return messageRepository.save(message);
    }
}

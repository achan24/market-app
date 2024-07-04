package ie.revalue.authenticatedbackend.repository;

import ie.revalue.authenticatedbackend.models.ApplicationUser;
import ie.revalue.authenticatedbackend.models.ConversationTopic;
import ie.revalue.authenticatedbackend.models.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationTopicRepository extends JpaRepository<ConversationTopic, Integer> {
    List<ConversationTopic> findByBuyerOrSellerOrderByUpdatedAtDesc(ApplicationUser buyer, ApplicationUser seller);
}

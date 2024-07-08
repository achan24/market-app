package ie.revalue.authenticatedbackend.repository;

import ie.revalue.authenticatedbackend.models.ApplicationUser;
import ie.revalue.authenticatedbackend.models.Conversation;
import ie.revalue.authenticatedbackend.models.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {


    List<Conversation> findByBuyerIdOrSellerId(Integer buyerId, Integer sellerId);
    List<Conversation> findByBuyerIdOrSellerIdOrderByUpdatedAtDesc(Integer buyerId, Integer sellerId);
    Optional<Conversation> findByBuyerIdAndSellerId(Integer buyerId, Integer sellerId);
    Optional<Conversation> findBySellerIdAndBuyerId(Integer sellerId, Integer buyerId);


}

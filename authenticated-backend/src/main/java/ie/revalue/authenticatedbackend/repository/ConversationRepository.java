package ie.revalue.authenticatedbackend.repository;

import ie.revalue.authenticatedbackend.models.ApplicationUser;
import ie.revalue.authenticatedbackend.models.Conversation;
import ie.revalue.authenticatedbackend.models.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
    List<Conversation> findByBuyerOrSellerOrderByUpdatedAtDesc(ApplicationUser buyer, ApplicationUser seller);

    Optional<Conversation> findByBuyerAndSeller(ApplicationUser user1, ApplicationUser user2);

    List<Conversation> findAllByBuyerIdOrSellerId(Integer buyerId, Integer sellerId);
    Conversation findByListingAndBuyerAndSeller(Listing listing, ApplicationUser buyer, ApplicationUser seller);
}

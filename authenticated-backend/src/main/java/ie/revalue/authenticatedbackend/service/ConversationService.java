package ie.revalue.authenticatedbackend.service;

import ie.revalue.authenticatedbackend.exceptions.ResourceNotFoundException;
import ie.revalue.authenticatedbackend.models.ApplicationUser;
import ie.revalue.authenticatedbackend.models.Conversation;
import ie.revalue.authenticatedbackend.models.Listing;
import ie.revalue.authenticatedbackend.models.Message;
import ie.revalue.authenticatedbackend.repository.ConversationRepository;
import ie.revalue.authenticatedbackend.repository.ListingRepository;
import ie.revalue.authenticatedbackend.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ListingRepository listingRepository;


    public Conversation getOrCreateConversation(ApplicationUser user1, ApplicationUser user2) {
        Conversation conversation = conversationRepository.findByBuyerAndSeller(user1, user2)
                .orElse(conversationRepository.findByBuyerAndSeller(user2, user1)
                        .orElse(null));

        if (conversation == null) {
            conversation = new Conversation();
            conversation.setBuyer(user1);
            conversation.setSeller(user2);
            conversation = conversationRepository.save(conversation);
        }

        return conversation;
    }

    public List<Conversation> getUserConversations(ApplicationUser user) {
        return conversationRepository.findByBuyerOrSellerOrderByUpdatedAtDesc(user, user);
    }

    public List<Message> getConversationMessages(Integer conversationTopicId) {
        Conversation topic = conversationRepository.findById(conversationTopicId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
        return messageRepository.findByConversationTopicOrderByTimestampAsc(topic);
    }

    public Message sendMessage(Integer conversationTopicId, ApplicationUser sender, String content) {
        Conversation topic = conversationRepository.findById(conversationTopicId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        Message message = new Message();
        message.setConversationTopic(topic);
        message.setSender(sender);
        message.setContent(content);

        message = messageRepository.save(message);

        // Update the conversation's last update time
        topic.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(topic);

        return message;
    }

    public List<Message> getConversationMessages(Integer conversationId, ApplicationUser user) {
        Conversation conversation = getConversation(conversationId, user);
        return messageRepository.findByConversationTopicOrderByTimestampAsc(conversation);
    }

    private Conversation getConversation(Integer conversationId, ApplicationUser user) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        if (!conversation.getBuyer().equals(user) && !conversation.getSeller().equals(user)) {
            throw new AccessDeniedException("You don't have permission to access this conversation");
        }

        return conversation;
    }

    public List<Listing> getUserInvolvedListings(ApplicationUser user) {
        List<Integer> allListingIds = new ArrayList<>();
        allListingIds.addAll(user.getSellerListingIds());
        allListingIds.addAll(user.getBuyerListingIds());

        return listingRepository.findAllById(allListingIds);
    }

    public List<Listing> getInboxListings(ApplicationUser user) {
        List<Listing> sellerListings = listingRepository.findAllById(user.getSellerListingIds());
        List<Listing> buyerListings = listingRepository.findAllById(user.getBuyerListingIds());

        List<Listing> inboxListings = new ArrayList<>();
        inboxListings.addAll(sellerListings.stream()
                .filter(listing -> listing.getConversation() != null)
                .collect(Collectors.toList()));
        inboxListings.addAll(buyerListings);

        return inboxListings;
    }


    public Conversation getListingConversation(Integer listingId, ApplicationUser user) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));

        // Check if the user is either the buyer or seller
        if (!listing.getSeller().equals(user) && !listing.getBuyer().equals(user)) {
            throw new AccessDeniedException("User is not authorized to access this conversation");
        }

        return listing.getConversation();
    }


    @Transactional
    public Conversation createListingConversation(Integer listingId, ApplicationUser user) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));

        // Check if the user is the seller
        if (!listing.getSeller().equals(user)) {
            throw new AccessDeniedException("Only the seller can create a conversation");
        }

        // Check if a conversation already exists
        if (listing.getConversation() != null) {
            throw new IllegalStateException("A conversation already exists for this listing");
        }

        // Create new conversation
        Conversation conversation = new Conversation();
        conversation.setListing(listing);
        conversation.setBuyer(listing.getBuyer());
        conversation.setSeller(listing.getSeller());

        // Save the conversation
        conversation = conversationRepository.save(conversation);

        // Update the listing with the new conversation
        listing.setConversation(conversation);
        listingRepository.save(listing);

        return conversation;
    }

}

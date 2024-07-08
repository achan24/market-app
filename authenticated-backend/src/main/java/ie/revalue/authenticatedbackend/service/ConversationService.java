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
import java.util.Optional;
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
        Integer user1Id = user1.getUserId();
        Integer user2Id = user2.getUserId();

        Optional<Conversation> optionalConversation = conversationRepository.findByBuyerIdAndSellerId(user1Id, user2Id)
                .or(() -> conversationRepository.findBySellerIdAndBuyerId(user1Id, user2Id));

        if (optionalConversation.isPresent()) {
            return optionalConversation.get();
        } else {
            Conversation conversation = new Conversation();
            conversation.setBuyerId(user1Id);
            conversation.setSellerId(user2Id);
            conversation.setCreatedAt(LocalDateTime.now());
            conversation.setUpdatedAt(LocalDateTime.now());
            conversation.setClosed(false);

            return conversationRepository.save(conversation);
        }
    }


    public List<Conversation> getUserConversations(ApplicationUser user) {
        // Assuming you have methods to find by buyerId or sellerId
        return conversationRepository.findByBuyerIdOrSellerId(user.getUserId(), user.getUserId());
    }


    public Message sendMessage(Integer conversationId, ApplicationUser sender, String content) {
        // Fetch the conversation by its ID
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        // Create and populate a new Message object
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setSenderId(sender.getUserId()); // Assuming sender has a getId() method
        message.setContent(content);

        // Save the message to the database
        message = messageRepository.save(message);

        // Update the conversation's messageIds list
        List<Integer> messageIds = conversation.getMessageIds();
        messageIds.add(message.getId());
        conversation.setMessageIds(messageIds);

        // Update the conversation's last update time
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        return message;
    }

    public List<Message> getConversationMessages(Integer conversationId, ApplicationUser user) {
        // Fetch the conversation by its ID
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        // Check if the user is part of the conversation (either buyer or seller)
        if (!conversation.getBuyerId().equals(user.getUserId()) && !conversation.getSellerId().equals(user.getUserId())) {
            throw new AccessDeniedException("Access denied: User is not part of this conversation");
        }

        // Fetch and return the messages ordered by timestamp
        return messageRepository.findByConversationIdOrderByTimestampAsc(conversationId);
    }

    private Conversation getConversation(Integer conversationId, ApplicationUser user) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        if (!conversation.getBuyerId().equals(user.getUserId()) && !conversation.getSellerId().equals(user.getUserId())) {
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
                .filter(listing -> listing.getConversationId() != null)
                .collect(Collectors.toList()));
        inboxListings.addAll(buyerListings.stream()
                .filter(listing -> listing.getConversationId() != null)
                .collect(Collectors.toList()));

        return inboxListings;
    }



    public Conversation getListingConversation(Integer listingId, ApplicationUser user) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));

        // Check if the user is either the buyer or seller
        if (!listing.getSeller().getUserId().equals(user.getUserId()) &&
                !listing.getBuyer().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("User is not authorized to access this conversation");
        }

        Integer conversationId = listing.getConversationId();
        if (conversationId == null) {
            throw new ResourceNotFoundException("Conversation not found for this listing");
        }

        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
    }


    @Transactional
    public Conversation createListingConversation(Integer listingId, ApplicationUser user) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));

        // Check if the user is the seller
        if (!listing.getSeller().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("Only the seller can create a conversation");
        }

        // Check if a conversation already exists
        if (listing.getConversationId() != null) {
            throw new IllegalStateException("A conversation already exists for this listing");
        }

        // Create new conversation
        Conversation conversation = new Conversation();
        conversation.setListingId(listing.getId());
        conversation.setBuyerId(listing.getBuyer() != null ? listing.getBuyer().getUserId() : null);
        conversation.setSellerId(listing.getSeller().getUserId());
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        conversation.setClosed(false);

        // Save the conversation
        conversation = conversationRepository.save(conversation);

        // Update the listing with the new conversation ID
        listing.setConversationId(conversation.getId());
        listingRepository.save(listing);

        return conversation;
    }

}

package ie.revalue.authenticatedbackend.test;

import ie.revalue.authenticatedbackend.models.ApplicationUser;
import ie.revalue.authenticatedbackend.models.Conversation;
import ie.revalue.authenticatedbackend.repository.ConversationRepository;
import ie.revalue.authenticatedbackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
public class ConversationRepositoryTest {
    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository; // Assume you have this repository

    @Test
    public void testFindAllByBuyer_IdOrSeller_Id() {
        ApplicationUser buyer = userRepository.findById(1).orElseThrow();
        ApplicationUser seller = userRepository.findById(2).orElseThrow();

        List<Conversation> conversations = conversationRepository.findByBuyerIdOrSellerId(buyer.getUserId(), seller.getUserId());

        assertThat(conversations).isNotEmpty();
    }
}
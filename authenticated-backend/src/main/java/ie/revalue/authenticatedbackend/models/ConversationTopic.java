package ie.revalue.authenticatedbackend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="conversation_topics")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationTopic {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private ApplicationUser buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private ApplicationUser seller;

    @OneToMany(mappedBy = "conversationTopic", cascade = CascadeType.ALL,
        orphanRemoval = true)
    private List<Message> messages;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private boolean isClosed;


}

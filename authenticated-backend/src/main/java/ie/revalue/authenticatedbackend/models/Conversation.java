package ie.revalue.authenticatedbackend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="conversations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Conversation {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "listing_id", nullable = false)
    private Integer listingId;

    @Column(name = "buyer_id", nullable = false)
    private Integer buyerId;

    @Column(name = "seller_id", nullable = false)
    private Integer sellerId;

    @ElementCollection
    @CollectionTable(name = "conversation_messages", joinColumns = @JoinColumn(name = "conversation_id"))
    @Column(name = "message_id")
    private List<Integer> messageIds;

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


    @Override
    public String toString() {
        return new StringBuilder("Conversation{")
                .append("id=").append(id)
                .append(", listingId=").append(listingId != null ? listingId : "null")
                .append(", buyerId=").append(buyerId != null ? buyerId : "null")
                .append(", sellerId=").append(sellerId != null ? sellerId : "null")
                .append(", createdAt=").append(createdAt)
                .append(", updatedAt=").append(updatedAt)
                .append(", isClosed=").append(isClosed)
                .append('}').toString();
    }

}

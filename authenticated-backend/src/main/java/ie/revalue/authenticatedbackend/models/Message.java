package ie.revalue.authenticatedbackend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "conversation_topic_id", nullable = false)
    private Conversation conversationTopic;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private ApplicationUser sender;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime timestamp;
    private boolean isRead;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
        isRead = false;
    }
}

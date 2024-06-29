package ie.revalue.authenticatedbackend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="comments")
public class Comment {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name="listingId", nullable = false)
    private Listing listing;

    @ManyToOne
    @JoinColumn(name="userId", nullable = false)
    private ApplicationUser user;

    private String comment;
    private LocalDateTime createdAt;
}

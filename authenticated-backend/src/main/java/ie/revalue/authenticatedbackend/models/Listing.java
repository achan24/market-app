package ie.revalue.authenticatedbackend.models;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="listings")
public class Listing {

    @Id
    @GeneratedValue
    private Integer id;
    private String category;
    private String title;
    private String description;
    private Double askingPrice;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = ListingStatus.ACTIVE;
    }

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Image> images;

    private String location;

    @ManyToOne
    @JoinColumn(name = "seller_id", referencedColumnName = "userId")
    private ApplicationUser seller;

    @ManyToOne
    @JoinColumn(name = "buyer_id", referencedColumnName = "userId")
    private ApplicationUser buyer;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    private Double acceptedPrice;

    @Column(name = "conversation_id")
    private Integer conversationId;

    @Enumerated(EnumType.STRING)
    private ListingStatus status;


    @Override
    public String toString() {
        return new StringBuilder("Listing{")
                .append("id=").append(id)
                .append(", category='").append(category).append('\'')
                .append(", title='").append(title).append('\'')
                .append(", description='").append(description).append('\'')
                .append(", askingPrice=").append(askingPrice)
                .append(", createdAt=").append(createdAt)
                .append(", updatedAt=").append(updatedAt)
                .append(", location='").append(location).append('\'')
                .append(", sellerId=").append(seller != null ? seller.getUserId() : "null")
                .append(", buyerId=").append(buyer != null ? buyer.getUserId() : "null")
                .append(", acceptedPrice=").append(acceptedPrice)
                .append(", conversationId=").append(conversationId != null ? conversationId : "null")
                .append(", status='").append(status).append('\'')
                .append('}').toString();
    }

}

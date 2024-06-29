package ie.revalue.authenticatedbackend.models;


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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
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

}

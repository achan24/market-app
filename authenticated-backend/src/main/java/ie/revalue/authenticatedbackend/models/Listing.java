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

    @ElementCollection
    private List<String> images;

    private String location;
    private Integer sellerId;

    @ElementCollection
    private List<String> comments;

    private Double acceptedPrice;
    private Integer buyerId;
}

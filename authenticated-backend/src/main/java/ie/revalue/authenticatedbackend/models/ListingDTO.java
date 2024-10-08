package ie.revalue.authenticatedbackend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListingDTO {

    private Integer id;
    private String category;
    private String title;
    private String description;
    private Double askingPrice;
    private String location;
    private List<ImageDTO> images;
    private String sellerName;
    private String buyerName;
    private Double acceptedPrice;
    private LocalDateTime createdAt;
    private ListingStatus status;
}

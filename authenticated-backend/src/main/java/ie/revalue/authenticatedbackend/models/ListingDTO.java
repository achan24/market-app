package ie.revalue.authenticatedbackend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListingDTO {

    private String category;
    private String title;
    private String description;
    private Double askingPrice;
    private String location;
}

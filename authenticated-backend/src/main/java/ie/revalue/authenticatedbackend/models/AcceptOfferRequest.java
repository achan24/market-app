package ie.revalue.authenticatedbackend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcceptOfferRequest {
    private String buyerUsername;
    private Double acceptedPrice;
}

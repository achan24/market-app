package ie.revalue.authenticatedbackend.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationDTO {
    private String username;
    private String password;
}

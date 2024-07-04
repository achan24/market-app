package ie.revalue.authenticatedbackend.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO {
    private String username;
    private String email;
    private String location;
    private String createdAt;

    public UserDetailsDTO(String username, String email, String location, LocalDateTime createdAt) {
        this.username = username;
        this.email = email;
        this.location = location;
        this.createdAt = createdAt.toString();  // Convert to String here
    }
}

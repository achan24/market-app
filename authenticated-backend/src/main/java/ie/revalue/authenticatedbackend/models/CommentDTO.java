package ie.revalue.authenticatedbackend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Integer id;
    private String comment;
    private LocalDateTime createdAt;
    private String username;
}

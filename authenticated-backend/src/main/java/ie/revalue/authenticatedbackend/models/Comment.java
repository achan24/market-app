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

    @Column(name="listingId")
    private Integer listingId;

    @Column(name="userId")
    private Integer userId;

    private String comment;
    private LocalDateTime createdAt;
}

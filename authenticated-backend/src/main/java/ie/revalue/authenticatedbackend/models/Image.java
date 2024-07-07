package ie.revalue.authenticatedbackend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="listingId", nullable=false)
    @JsonBackReference
    private Listing listing;

    @Lob
    @Column(name="data", columnDefinition = "MEDIUMBLOB")
    private byte[] data;

    private String fileName;
    private String fileType;
}

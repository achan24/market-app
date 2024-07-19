package ie.revalue.authenticatedbackend.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "location")
@Data
public class Location {

    @Id
    private String name;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

}
package ie.revalue.authenticatedbackend.repository;

import ie.revalue.authenticatedbackend.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, String> {
    Location findByName(String name);
}


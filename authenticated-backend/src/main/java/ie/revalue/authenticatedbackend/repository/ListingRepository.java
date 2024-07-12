package ie.revalue.authenticatedbackend.repository;

import ie.revalue.authenticatedbackend.models.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ListingRepository extends JpaRepository<Listing, Integer> {
    Optional<Listing> findByTitle(String title);
    Optional<Listing> findByCategory(String category);
    Optional<Listing> findByLocationContaining(String location);
    Optional<Listing> findByAskingPriceBetween(Double minPrice, Double maxPrice);


    List<Listing> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

    List<Listing> findByCategoryAndLocationAndAskingPriceBetween(
            String category,
            String location,
            double minPrice,
            double maxPrice);

}

package ie.revalue.authenticatedbackend.repository;

import ie.revalue.authenticatedbackend.models.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ListingRepository extends JpaRepository<Listing, Integer> {
    Optional<Listing> findByTitle(String title);
    Optional<Listing> findBySellerId(Integer sellerId);
    Optional<Listing> findByCategory(String category);
    Optional<Listing> findByLocationContaining(String location);
    Optional<Listing> findByAskingPriceBetween(Double minPrice, Double maxPrice);
    @Query("SELECT l FROM Listing l WHERE " +
            "LOWER(l.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(l.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(l.category) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Optional<Listing> findBySearchTerm(@Param("searchTerm") String searchTerm);
}

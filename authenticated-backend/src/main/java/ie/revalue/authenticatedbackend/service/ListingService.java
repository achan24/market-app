package ie.revalue.authenticatedbackend.service;

import ie.revalue.authenticatedbackend.models.Listing;
import ie.revalue.authenticatedbackend.repository.ListingRepository;
import org.hibernate.annotations.NaturalId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ListingService {


    private final ListingRepository listingRepository;

    @Autowired
    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public Listing createListing(Listing listing) {
        listing.setCreatedAt(LocalDateTime.now());
        listing.setUpdatedAt(LocalDateTime.now());
        return (Listing) listingRepository.save(listing);
    }
}

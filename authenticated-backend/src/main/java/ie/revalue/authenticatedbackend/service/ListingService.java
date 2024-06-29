package ie.revalue.authenticatedbackend.service;

import ie.revalue.authenticatedbackend.models.ApplicationUser;
import ie.revalue.authenticatedbackend.models.Listing;
import ie.revalue.authenticatedbackend.models.ListingDTO;
import ie.revalue.authenticatedbackend.repository.ListingRepository;
import org.hibernate.annotations.NaturalId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public Listing createListing(ListingDTO listingDTO) {
        Listing listing = new Listing();
        listing.setCategory(listingDTO.getCategory());
        listing.setTitle(listingDTO.getTitle());
        listing.setDescription(listingDTO.getDescription());
        listing.setAskingPrice(listingDTO.getAskingPrice());
        listing.setLocation(listingDTO.getLocation());
        listing.setCreatedAt(LocalDateTime.now());
        listing.setUpdatedAt(LocalDateTime.now());

        // Get the currently authenticated user
        ApplicationUser currentUser = (ApplicationUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        listing.setSeller(currentUser);

        return listingRepository.save(listing);
    }
}

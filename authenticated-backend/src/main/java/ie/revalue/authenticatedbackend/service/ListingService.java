package ie.revalue.authenticatedbackend.service;

import ie.revalue.authenticatedbackend.models.ApplicationUser;
import ie.revalue.authenticatedbackend.models.Listing;
import ie.revalue.authenticatedbackend.models.ListingDTO;
import ie.revalue.authenticatedbackend.repository.ListingRepository;
import ie.revalue.authenticatedbackend.repository.UserRepository;
import org.hibernate.annotations.NaturalId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ListingService {

    private final ListingRepository listingRepository;

    @Autowired
    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @Autowired
    private UserRepository userRepository;

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
//        ApplicationUser currentUser = (ApplicationUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        listing.setSeller(currentUser);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String username = jwt.getClaimAsString("sub");  // Adjust this if your username is stored in a different claim
            ApplicationUser currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            listing.setSeller(currentUser);
        } else {
            throw new IllegalStateException("Unexpected principal type: " + authentication.getPrincipal().getClass());
        }

        return listingRepository.save(listing);
    }
}

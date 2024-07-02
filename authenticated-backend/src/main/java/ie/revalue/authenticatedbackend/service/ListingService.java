package ie.revalue.authenticatedbackend.service;

import ie.revalue.authenticatedbackend.models.ApplicationUser;
import ie.revalue.authenticatedbackend.models.Image;
import ie.revalue.authenticatedbackend.models.Listing;
import ie.revalue.authenticatedbackend.models.ListingDTO;
import ie.revalue.authenticatedbackend.repository.ListingRepository;
import ie.revalue.authenticatedbackend.repository.UserRepository;
import org.hibernate.annotations.NaturalId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ListingService {

    private final ListingRepository listingRepository;

    @Autowired
    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @Autowired
    private UserRepository userRepository;

//    public Listing createListing(Listing listing) {
//        listing.setCreatedAt(LocalDateTime.now());
//        listing.setUpdatedAt(LocalDateTime.now());
//        return (Listing) listingRepository.save(listing);
//    }

    public Listing createListing(ListingDTO listingDTO, List<MultipartFile> imageFiles) throws IOException {
        Listing listing = new Listing();
        listing.setCategory(listingDTO.getCategory());
        listing.setTitle(listingDTO.getTitle());
        listing.setDescription(listingDTO.getDescription());
        listing.setAskingPrice(listingDTO.getAskingPrice());
        listing.setLocation(listingDTO.getLocation());
        listing.setCreatedAt(LocalDateTime.now());
        listing.setUpdatedAt(LocalDateTime.now());

        List<Image> images = new ArrayList<>();
        for (MultipartFile imageFile : imageFiles) {
            Image image = new Image();
            image.setFileName(imageFile.getOriginalFilename());
            image.setFileType(imageFile.getContentType());
            image.setData(imageFile.getBytes());
            image.setListing(listing);
            images.add(image);
        }
        listing.setImages(images);

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




    //search should not require authentication
    public List<Listing> getAllListings() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated()) {
//            if (authentication.getPrincipal() instanceof Jwt) {
                return listingRepository.findAll();
//            } else {
//                throw new IllegalStateException("Unexpected authentication type");
//            }
//        } else {
//            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
//        }
    }


    public Listing getListingById(Integer id) {
        return listingRepository.findById(id).orElse(null);
    }
}

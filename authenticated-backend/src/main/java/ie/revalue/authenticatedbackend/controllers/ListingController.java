package ie.revalue.authenticatedbackend.controllers;

import ie.revalue.authenticatedbackend.exceptions.ResourceNotFoundException;
import ie.revalue.authenticatedbackend.models.*;
import ie.revalue.authenticatedbackend.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/listings")
@CrossOrigin("*")
public class ListingController {
    private final ListingService listingService;

    @Autowired
    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }





    // Create a new listing
//    @PostMapping
//    public ResponseEntity<Listing> createListing(@RequestBody Listing listing) {
//        Listing createdListing = listingService.createListing(listing);
//        return new ResponseEntity<>(createdListing, HttpStatus.CREATED);
//    }

    @PostMapping(consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ListingDTO> createListing(
            @RequestPart("listing") ListingDTO listingDTO,
            @RequestPart("images") List<MultipartFile> images) {

        try {
            Listing createdListing = listingService.createListing(listingDTO, images);
            ListingDTO createdListingDTO = convertToDTO(createdListing);
            return new ResponseEntity<>(createdListingDTO, HttpStatus.CREATED);
        } catch (IOException e) {
            // Handle the exception, maybe log it
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    private ListingDTO convertToDTO(Listing listing) {
        ListingDTO dto = new ListingDTO(
                listing.getId(),
                listing.getCategory(),
                listing.getTitle(),
                listing.getDescription(),
                listing.getAskingPrice(),
                listing.getLocation(),
                null,
                listing.getSeller() != null ? listing.getSeller().getUsername() : null,
                listing.getBuyer() != null ? listing.getBuyer().getUsername() : null,
                listing.getAcceptedPrice() != null ? listing.getAcceptedPrice() : null,
                listing.getCreatedAt()
        );

        if (listing.getImages() != null) {
            dto.setImages(listing.getImages().stream()
                    .map(this::convertToImageDTO)
                    .collect(Collectors.toList()));
        }

        // Set seller name
        if (listing.getSeller() != null) {
            dto.setSellerName(listing.getSeller().getUsername());
        }

        // Set buyer name
        if (listing.getBuyer() != null) {
            dto.setBuyerName(listing.getBuyer().getUsername());
        }

        return dto;
    }

    private ImageDTO convertToImageDTO(Image image) {
        return new ImageDTO(
                image.getFileName(),
                image.getFileType(),
                image.getData()
                // Note: We're not including the image data here
        );
    }
//    @PutMapping("/{id}")
//    public ResponseEntity<Listing> updateListing(@PathVariable Long id, @RequestBody Listing listing);
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteListing(@PathVariable Long id);
//

    @GetMapping
    public ResponseEntity<List<ListingDTO>> getAllListings() {
        List<Listing> listings = listingService.getAllListings();
        List<ListingDTO> listingDTOs = listings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listingDTOs);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ListingDTO> getListingById(@PathVariable Integer id) {
        ListingDTO listingDTO = listingService.getListingDTOById(id);
        if (listingDTO != null) {
            return ResponseEntity.ok(listingDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable Integer id, @RequestBody CommentDTO commentDTO, @AuthenticationPrincipal Jwt jwt) {

        try {
            listingService.addComment(id, commentDTO.getComment(), jwt);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Listing not found");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid authentication");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }


    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getComments(@PathVariable Integer id) {
        try {
            List<CommentDTO> comments = listingService.getCommentsForListing(id);
            return ResponseEntity.ok().body(comments);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Listing not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }



    @PostMapping("/{id}/acceptOffer")
    public ResponseEntity<ListingDTO> acceptOffer(@PathVariable Integer id, @RequestBody AcceptOfferRequest request) {
        ListingDTO updatedListing = listingService.acceptOffer(id, request);
        return ResponseEntity.ok(updatedListing);
    }













//
//    @GetMapping("/seller/{sellerId}")
//    public ResponseEntity<List<Listing>> getListingsBySeller(@PathVariable Long sellerId);
//
//    @GetMapping("/category/{category}")
//    public ResponseEntity<List<Listing>> getListingsByCategory(@PathVariable String category);
//
//    @GetMapping("/location/{location}")
//    public ResponseEntity<List<Listing>> getListingsByLocation(@PathVariable String location);
//
//    @GetMapping("/price")
//    public ResponseEntity<List<Listing>> getListingsByPriceRange(
//            @RequestParam Double minPrice,
//            @RequestParam Double maxPrice);
//
//    @PostMapping("/{id}/accept-offer")
//    public ResponseEntity<Listing> acceptOffer(
//            @PathVariable Long id,
//            @RequestParam Long buyerId,
//            @RequestParam Double acceptedPrice);
}

package ie.revalue.authenticatedbackend.controllers;

import ie.revalue.authenticatedbackend.models.Image;
import ie.revalue.authenticatedbackend.models.ImageDTO;
import ie.revalue.authenticatedbackend.models.Listing;
import ie.revalue.authenticatedbackend.models.ListingDTO;
import ie.revalue.authenticatedbackend.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
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
                null
        );

        if (listing.getImages() != null) {
            dto.setImages(listing.getImages().stream()
                    .map(this::convertToImageDTO)
                    .collect(Collectors.toList()));
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
    @GetMapping("/{id}")
    public ResponseEntity<ListingDTO> getListingById(@PathVariable Integer id) {
        Listing listing = listingService.getListingById(id);
        if (listing != null) {
            ListingDTO listingDTO = convertToDTO(listing);
            return ResponseEntity.ok(listingDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }








    @GetMapping
    public ResponseEntity<List<ListingDTO>> getAllListings() {
        List<Listing> listings = listingService.getAllListings();
        List<ListingDTO> listingDTOs = listings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listingDTOs);
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

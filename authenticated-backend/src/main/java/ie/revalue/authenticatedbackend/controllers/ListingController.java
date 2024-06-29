package ie.revalue.authenticatedbackend.controllers;

import ie.revalue.authenticatedbackend.models.Listing;
import ie.revalue.authenticatedbackend.models.ListingDTO;
import ie.revalue.authenticatedbackend.service.ListingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping
    public ResponseEntity<Listing> createListing(@RequestBody Listing listing) {
        Listing createdListing = listingService.createListing(listing);
        return new ResponseEntity<>(createdListing, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<ListingDTO> createListing(@RequestBody ListingDTO listingDTO) {
        Listing createdListing = listingService.createListing(listingDTO);
        ListingDTO createdListingDTO = convertToDTO(createdListing);
        return new ResponseEntity<>(createdListingDTO, HttpStatus.CREATED);
    }

    private ListingDTO convertToDTO(Listing listing) {
        return new ListingDTO(
                listing.getCategory(),
                listing.getTitle(),
                listing.getDescription(),
                listing.getAskingPrice(),
                listing.getLocation()
        );
    }
//    @PutMapping("/{id}")
//    public ResponseEntity<Listing> updateListing(@PathVariable Long id, @RequestBody Listing listing);
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteListing(@PathVariable Long id);
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Listing> getListing(@PathVariable Long id);
//
//    @GetMapping
//    public ResponseEntity<List<Listing>> getAllListings();
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

package ie.revalue.authenticatedbackend.service;

import ie.revalue.authenticatedbackend.exceptions.ResourceNotFoundException;
import ie.revalue.authenticatedbackend.models.*;
import ie.revalue.authenticatedbackend.repository.CommentRepository;
import ie.revalue.authenticatedbackend.repository.ConversationRepository;
import ie.revalue.authenticatedbackend.repository.ListingRepository;
import ie.revalue.authenticatedbackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;

@Service
public class ListingService {

    private final ListingRepository listingRepository;

    @Autowired
    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ConversationRepository conversationRepository;

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


    public ListingDTO getListingDTOById(Integer id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found with id " + id));

        String sellerName = listing.getSeller().getUsername(); // Fetch the seller's name

        ListingDTO listingDTO = new ListingDTO();
        listingDTO.setId(listing.getId());
        listingDTO.setCategory(listing.getCategory());
        listingDTO.setTitle(listing.getTitle());
        listingDTO.setDescription(listing.getDescription());
        listingDTO.setAskingPrice(listing.getAskingPrice());
        listingDTO.setLocation(listing.getLocation());
        listingDTO.setImages(listing.getImages().stream()
                .map(this::convertToImageDTO)
                .collect(Collectors.toList()));
        listingDTO.setSellerName(sellerName); // Set the seller's name
        listingDTO.setBuyerName(listing.getBuyer() != null ? listing.getBuyer().getUsername() : null); // Set the buyer's name
        listingDTO.setAcceptedPrice(listing.getAcceptedPrice()); // Set the accepted price
        listingDTO.setCreatedAt(listing.getCreatedAt());
        listingDTO.setStatus(listing.getStatus());
        return listingDTO;
    }

    private ImageDTO convertToImageDTO(Image image) {
        return new ImageDTO(
                image.getFileName(),
                image.getFileType(),
                image.getData() // Include the image data
        );
    }



    public void addComment(Integer listingId, String text, Jwt jwt) {

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));


        String username = jwt.getClaimAsString("sub");  // Adjust if your username is stored differently
        ApplicationUser currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Comment comment = new Comment();
        comment.setListing(listing);
        comment.setUser(currentUser);
        comment.setComment(text);
        comment.setCreatedAt(LocalDateTime.now());

        commentRepository.save(comment);

    }


    public List<CommentDTO> getCommentsForListing(Integer listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));
        return listing.getComments().stream()
                .map(comment -> new CommentDTO(comment.getId(), comment.getComment(), comment.getCreatedAt(), comment.getUser().getUsername()))
                .collect(Collectors.toList());
    }


    public List<Listing> searchListings(String query, String category, String location, String price) {
        // Implement search logic here
        // For simplicity, we'll create a basic example using Java streams.
        // Ideally, you'd use JPA Specifications or QueryDSL for a more efficient implementation.

        List<Listing> allListings = listingRepository.findAll(); // Assume this fetches all listings

        return allListings.stream()
                .filter(listing -> query == null || listing.getTitle().toLowerCase().contains(query.toLowerCase()) || listing.getDescription().toLowerCase().contains(query.toLowerCase()))
                .filter(listing -> category == null || listing.getCategory().equalsIgnoreCase(category))
                .filter(listing -> location == null || listing.getLocation().equalsIgnoreCase(location))
                .filter(listing -> {
                    if (price == null) return true;
                    String[] priceRange = price.split("-");
                    double minPrice = Double.parseDouble(priceRange[0]);
                    double maxPrice = priceRange.length > 1 ? Double.parseDouble(priceRange[1]) : Double.MAX_VALUE;
                    return listing.getAskingPrice() >= minPrice && listing.getAskingPrice() <= maxPrice;
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public ListingDTO acceptOffer(Integer listingId, AcceptOfferRequest request) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        ApplicationUser buyer = userService.getUserByUsername(request.getBuyerUsername());
        ApplicationUser seller = listing.getSeller();

        listing.setAcceptedPrice(request.getAcceptedPrice());
        listing.setBuyer(buyer);

        listingRepository.save(listing);

        // Update seller and buyer profiles
        userService.addSellerListing(seller.getUserId(), listingId);
        userService.addBuyerListing(buyer.getUserId(), listingId);


        // Create a new conversation
        Conversation conversation = new Conversation();
        conversation.setListingId(listing.getId());
        conversation.setBuyerId(buyer.getUserId());
        conversation.setSellerId(seller.getUserId());
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        conversation.setClosed(false);

        conversationRepository.save(conversation);


        return convertToDTO(listing);
    }


    @Transactional
    public void updateListingStatus(Integer listingId, ListingStatus status) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        listing.setStatus(status);
        listingRepository.save(listing);
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
                listing.getCreatedAt(),
                listing.getStatus() != null ? listing.getStatus() : null
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


}

package ie.revalue.authenticatedbackend.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO {
    private String username;
    private String email;
    private String location;
    private String createdAt;
    private byte[] profilePic; // Base64 encoded profile picture

    private List<Integer> sellerListingIds;
    private List<Integer> buyerListingIds;
    private List<Integer> conversationIds;

    public UserDetailsDTO(String username, String email, String location, LocalDateTime createdAt,
                          List<Integer> sellerListingIds, List<Integer> buyerListingIds,
                          List<Conversation> conversations, byte[] profilePic) {
        this.username = username;
        this.email = email;
        this.location = location;
        this.createdAt = (createdAt != null) ? createdAt.toString() : null;
        this.sellerListingIds = sellerListingIds;
        this.buyerListingIds = buyerListingIds;
        this.conversationIds = (conversations != null && !conversations.isEmpty())
                ? conversations.stream().map(Conversation::getId).toList()
                : null;
        this.profilePic = profilePic;
    }

    public String getProfilePicBase64() {
        return (profilePic != null) ? Base64.getEncoder().encodeToString(profilePic) : null;
    }
}

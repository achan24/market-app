package ie.revalue.authenticatedbackend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name="users")
//@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class ApplicationUser implements UserDetails {

    @Id
    @GeneratedValue
    private Integer userId;

    @Column(unique = true)
    private String username;
    private String password;

    @Column(unique = true)
    private String email;
    private String location;

    @Lob
    @Column(columnDefinition="LONGBLOB")
    private byte[] profilePic;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @ElementCollection
    @CollectionTable(name = "user_seller_listings", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "listing_id")
    private List<Integer> sellerListingIds;

    @ElementCollection
    @CollectionTable(name = "user_buyer_listings", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "listing_id")
    private List<Integer> buyerListingIds;

    @ElementCollection
    @CollectionTable(name = "user_buyer_conversations", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "conversation_id")
    private List<Integer> buyerConversationIds;

    @ElementCollection
    @CollectionTable(name = "user_seller_conversations", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "conversation_id")
    private List<Integer> sellerConversationIds;


    //fetch data for authorities as soon as we fetch user information
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="user_role_junction",
            joinColumns = {@JoinColumn(name="user_id")},
            inverseJoinColumns = {@JoinColumn(name="role_id")}
    )
    private Set<Role> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    @Override
    public String toString() {
        return new StringBuilder("ApplicationUser{")
                .append("userId=").append(userId)
                .append(", username='").append(username).append('\'')
                .append(", email='").append(email).append('\'')
                .append(", location='").append(location).append('\'')
                .append(", profilePic=").append(profilePic != null ? "[BLOB]" : "null")
                .append(", createdAt=").append(createdAt)
                .append(", updatedAt=").append(updatedAt)
                .append('}').toString();
    }
}

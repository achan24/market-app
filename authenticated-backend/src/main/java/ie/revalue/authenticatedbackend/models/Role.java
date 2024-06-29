package ie.revalue.authenticatedbackend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name="roles")
@Data
//@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue
    @Column(name="role_id")
    private Integer roleId;

    private String authority;

    @Override
    public String getAuthority() {
        return authority;
    }

    public Role(String authority) {
        this.authority = authority;
    }
}

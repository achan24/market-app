package ie.revalue.authenticatedbackend.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class TokenService {

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;


    public String generateJwt(Authentication auth) {
        Instant now = Instant.now();

        //put all authorities into one string - to put into jwt
        //looping through all authorities
        //gets the authority
        //combines them all into a single string delimited by space
        String scope = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        //jwt claim set
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(auth.getName()) //person who logged in
                .claim("roles", scope) //what information its holding
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        //uses jwtEncoder to encode a new jwt token
        //using parameters from claims - username and role
        //returns the string value
    }
}

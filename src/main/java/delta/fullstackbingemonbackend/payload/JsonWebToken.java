package delta.fullstackbingemonbackend.payload;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JsonWebToken {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt_expiration}")
    private Long expiration;

    public String generateJWT(String username) {
        Date date = new Date();
        Date expirationDate = new Date(date.getTime() + expiration);
        String newJWT = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS256, secret)
                .setIssuedAt(date)
                .setExpiration(expirationDate)
                .compact();
        System.out.println("What does the Json Web Token look like?: " + newJWT);
        return newJWT;
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    public boolean isTokenValid(String token, UserDetailsAuthentication userDetailsAuthentication) {
        String username = getUsernameFromToken(token);
        return (username.equals(userDetailsAuthentication.getUsername()) && !isTokenExpired(token));
    }
}

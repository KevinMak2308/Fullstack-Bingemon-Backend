package delta.fullstackbingemonbackend.security.jwt;

import delta.fullstackbingemonbackend.security.services.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JsonWebToken {
    private final Logger logger = LoggerFactory.getLogger(JsonWebToken.class);
    @Value("${secret}")
    private String secret;

    @Value("${expiration}")
    private Long expiration;

    public String generateJWT(String username) {
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS256, secret)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expiration))
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJWT(String authenticateToken) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(authenticateToken);
            return true;
        } catch (SignatureException error) {
            logger.error("Invalid JWT secret: {}", error.getMessage());
            return false;
        }
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

    public boolean isTokenValid(String token, UserDetailsImpl userDetailsImpl) {
        String username = getUsernameFromToken(token);
        return (username.equals(userDetailsImpl.getUsername()) && !isTokenExpired(token));
    }
}

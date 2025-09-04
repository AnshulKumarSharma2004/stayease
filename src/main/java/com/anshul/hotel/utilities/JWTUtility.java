package com.anshul.hotel.utilities;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtility {
 private static final String SECRET_KEY="anshulkumarsharmaanshulkumarsharma02";

 private SecretKey getSigningKey(){
     return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
 }
 public String extractUsername(String token){
   return extractAllClaims(token).getSubject();
 }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private Claims extractAllClaims(String token){
     return Jwts.parser()
             .verifyWith(getSigningKey())
             .build()
             .parseSignedClaims(token)
             .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public String generateTokenForUser(String email , String role,String id,String userName){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role",role);
        claims.put("name",userName);

        if ("Hotel_Admin".equals(role)) {
            claims.put("hotelId", id);   // hotelId for admin
        } else if ("User".equals(role)) {
            claims.put("userId", id);    // userId for customer
        }
        return createToken(claims, email);
    }
    public String extractRole(String token) {
        return (String) extractAllClaims(token).get("role");
    }
    public Long extractHotelId(String token) {
        Object id = extractAllClaims(token).get("hotelId");
        return id != null ? ((Number) id).longValue() : null;
    }
    public Long extractUserId(String token) {
        Object id = extractAllClaims(token).get("userId");
        return id != null ? ((Number) id).longValue() : null;
    }
    public Object extractClaim(String token, String key) {
        return extractAllClaims(token).get(key);
    }
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .header().empty().add("typ","JWT")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 *60))
                .signWith(getSigningKey())
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }
    public Boolean hasRole(String token, String role) {
        String tokenRole = extractRole(token);
        return role.equals(tokenRole);
    }

}

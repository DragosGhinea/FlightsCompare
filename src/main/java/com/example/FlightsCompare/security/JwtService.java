package com.example.FlightsCompare.security;

import io.jsonwebtoken.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;

@Service
public class JwtService {

    private static final long DAY_IN_MILLISECONDS = 86_400_000L;
    private long lastValidDate;

    private byte[] generateHmacKey() {
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        //        return Base64.getEncoder().encodeToString(encodedKey); // to string
        return secretKey.getEncoded();
    }

    // old approach
//    @Value("${application.security.jwt.secret-key}")
//    private String secretKey;

    private final List<byte[]> secretKeys = new ArrayList<>();

    public JwtService(
            @Value("${application.security.jwt.access-token.expiration-time}")
            long accessExpiration,
            @Value("${application.security.jwt.refresh-token.expiration-time}")
            long refreshExpiration
    ) {
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;

        long keyCount = refreshExpiration / DAY_IN_MILLISECONDS;
        if (refreshExpiration % DAY_IN_MILLISECONDS != 0) {
            keyCount++;
        }

        for (int i = 0; i < keyCount; i++) {
            secretKeys.add(generateHmacKey());
        }

        lastValidDate = System.currentTimeMillis();
    }

    private final long accessExpiration;
    private final long refreshExpiration;

    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (ExpiredJwtException e) {
            return null;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(new HashMap<>(){
            {
                put("authorities", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray());
            }
        }, userDetails);
    }

    public String generateAccessToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return buildToken(extraClaims, userDetails, accessExpiration);
    }

    public String generateRefreshToken(
            UserDetails userDetails
    ) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {

        long issuedAt = System.currentTimeMillis();

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(issuedAt))
                .setExpiration(new Date(issuedAt + expiration))
                .signWith(getSignInKey(issuedAt), SignatureAlgorithm.HS512)
                .compact();
    }

    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        }catch(ExpiredJwtException e) {
            return true;
        }
    }

    private Claims extractAllClaims(String token) {
        String tokenWithoutSignature = token.substring(0, token.lastIndexOf('.') + 1);
        Date issuedAt = Jwts
                .parserBuilder()
                .build()
                .parseClaimsJwt(tokenWithoutSignature)
                .getBody()
                .getIssuedAt();

        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey(issuedAt.toInstant().toEpochMilli()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private void rotateKeys() {
        lastValidDate += DAY_IN_MILLISECONDS;
        secretKeys.remove(0);
        secretKeys.add(generateHmacKey());
    }

    private Key getSignInKey(long issuedAtMilliseconds) {
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        return Keys.hmacShaKeyFor(keyBytes);

        int keyIndex = (int) (((issuedAtMilliseconds) - lastValidDate) / DAY_IN_MILLISECONDS);
        if (keyIndex == secretKeys.size()) {
            rotateKeys();
            keyIndex--;
        }

        return Keys.hmacShaKeyFor(secretKeys.get(keyIndex));
    }
}
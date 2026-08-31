package com.aiinterview.aiinterviewbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    // =========================================================
    // JWT SECRET KEY
    // =========================================================

    @Value("${jwt.secret}")
    private String secretKey;


    // =========================================================
    // JWT EXPIRATION TIME
    // =========================================================

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;


    // =========================================================
    // GENERATE JWT TOKEN
    // DEFAULT ROLE = USER
    // =========================================================

    public String generateToken(String email) {

        return generateToken(
                email,
                "USER"
        );
    }


    // =========================================================
    // GENERATE JWT TOKEN WITH ROLE
    // =========================================================

    public String generateToken(
            String email,
            String role
    ) {

        return Jwts.builder()

                // -------------------------------------------------
                // EMAIL / SUBJECT
                // -------------------------------------------------

                .subject(email)

                // -------------------------------------------------
                // ROLE
                // -------------------------------------------------

                .claim(
                        "role",
                        role
                )

                // -------------------------------------------------
                // ISSUED DATE
                // -------------------------------------------------

                .issuedAt(
                        new Date()
                )

                // -------------------------------------------------
                // EXPIRATION
                // -------------------------------------------------

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + jwtExpiration
                        )
                )

                // -------------------------------------------------
                // SIGN TOKEN
                // -------------------------------------------------

                .signWith(
                        getSigningKey()
                )

                // -------------------------------------------------
                // BUILD
                // -------------------------------------------------

                .compact();
    }


    // =========================================================
    // EXTRACT EMAIL
    // =========================================================

    public String extractEmail(
            String token
    ) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }


    // =========================================================
    // EXTRACT ROLE
    // =========================================================

    public String extractRole(
            String token
    ) {

        return extractClaim(
                token,
                claims ->
                        claims.get(
                                "role",
                                String.class
                        )
        );
    }


    // =========================================================
    // EXTRACT CLAIM
    // =========================================================

    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {

        final Claims claims =
                extractAllClaims(token);

        return claimsResolver.apply(
                claims
        );
    }


    // =========================================================
    // EXTRACT ALL CLAIMS
    // =========================================================

    private Claims extractAllClaims(
            String token
    ) {

        return Jwts.parser()

                .verifyWith(
                        getSigningKey()
                )

                .build()

                .parseSignedClaims(
                        token
                )

                .getPayload();
    }


    // =========================================================
    // CHECK TOKEN VALID
    // =========================================================

    public boolean isTokenValid(
            String token,
            String email
    ) {

        try {

            String extractedEmail =
                    extractEmail(token);

            return extractedEmail != null
                    && extractedEmail.equals(email)
                    && !isTokenExpired(token);

        } catch (Exception e) {

            return false;
        }
    }


    // =========================================================
    // CHECK TOKEN EXPIRATION
    // =========================================================

    private boolean isTokenExpired(
            String token
    ) {

        Date expiration =
                extractClaim(
                        token,
                        Claims::getExpiration
                );

        return expiration.before(
                new Date()
        );
    }


    // =========================================================
    // SIGNING KEY
    // =========================================================

    private SecretKey getSigningKey() {

        byte[] keyBytes =
                Decoders.BASE64.decode(
                        secretKey
                );

        return Keys.hmacShaKeyFor(
                keyBytes
        );
    }
}
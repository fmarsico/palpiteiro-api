package com.caravela21.palpiteiro.api.infrastructure.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration-ms:86400000}")
	private long expirationMs;

	public String generateToken(String subject) {
		Instant now = Instant.now();
		Instant expiration = now.plusMillis(expirationMs);

		return Jwts.builder()
				.id(UUID.randomUUID().toString())
				.subject(subject)
				.issuedAt(Date.from(now))
				.expiration(Date.from(expiration))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public String extractSubject(String token) {
		return extractAllClaims(token).getSubject();
	}

	public boolean isTokenValid(String token) {
		try {
			Claims claims = extractAllClaims(token);
			return claims.getExpiration() != null && claims.getExpiration().after(new Date());
		} catch (Exception ex) {
			return false;
		}
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}
}


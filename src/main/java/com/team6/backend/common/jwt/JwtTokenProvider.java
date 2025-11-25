package com.team6.backend.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

	private final Key key;

	private static final long ACCESS_TOKEN_VALIDITY_MILLIS  = 1000L * 60 * 30; // 30분
	private static final long REFRESH_TOKEN_VALIDITY_MILLIS = 1000L * 60 * 60 * 24 * 14; // 14일

	public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey) {
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	}

	public String createAccessToken(Long userId) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + ACCESS_TOKEN_VALIDITY_MILLIS);

		return Jwts.builder()
			.setSubject(String.valueOf(userId))
			.setIssuedAt(now)
			.setExpiration(expiry)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public String createRefreshToken(Long userId) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + REFRESH_TOKEN_VALIDITY_MILLIS);

		return Jwts.builder()
			.setSubject(String.valueOf(userId))
			.setIssuedAt(now)
			.setExpiration(expiry)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public Long getUserId(String token) {
		return Long.valueOf(
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject()
		);
	}
}

package com.secops.security;

import com.secops.config.SecOpsProperties;
import com.secops.entity.AppUser;
import com.secops.entity.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.crypto.SecretKey;

@Service
public class JwtService {

	private final SecOpsProperties properties;
	private final SecretKey key;

	public JwtService(SecOpsProperties properties) {
		this.properties = properties;
		byte[] raw;
		
		try {
			raw = Base64.getDecoder().decode(properties.jwt().secret());
		} catch (Exception e) {
			raw = properties.jwt().secret().getBytes(StandardCharsets.UTF_8);
		}
		
		if (raw.length < 64) {
			throw new IllegalStateException("JWT_SECRET debe tener al menos 64 bytes");
		}
		
		key = Keys.hmacShaKeyFor(raw);
	}

	public String issueAccess(AppUser user) {
		Instant now = Instant.now();
		
		return Jwts.builder()
				.issuer(properties.jwt().issuer())
				.audience()
				.add(properties.jwt().audience())
				.and()
				.subject(user.getId().toString())
				.id(UUID.randomUUID().toString())
				.issuedAt(Date.from(now))
				.notBefore(Date.from(now.minusSeconds(5)))
				.expiration(Date.from(now.plus(Duration.ofMinutes(properties.jwt().accessMinutes()))))
				.claim("username", user.getUsername())
				.claim("roles", user.getRoles().stream().map(Enum::name).toList())
				.signWith(key)
				.compact();
	}

	@SuppressWarnings("unchecked")
	public SecOpsPrincipal parse(String token) {
		Claims claims = Jwts.parser()
							.verifyWith(key)
							.requireIssuer(properties.jwt().issuer())
							.requireAudience(properties.jwt().audience())
							.build()
							.parseSignedClaims(token)
							.getPayload();
		
		List<String> rs = (List<String>) claims.get("roles", List.class);
		Set<Role> roles = new HashSet<Role>();
		
		rs.forEach(x -> roles.add(com.secops.entity.Role.valueOf(x)));
		
		return new SecOpsPrincipal(UUID.fromString(claims.getSubject()), claims.get("username", String.class), Set.copyOf(roles));
	}

	public long accessSeconds() {
		return Duration.ofMinutes(properties.jwt().accessMinutes()).toSeconds();
	}
	
}

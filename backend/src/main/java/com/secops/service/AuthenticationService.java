package com.secops.service;

import com.secops.config.SecOpsProperties;
import com.secops.dto.AuthDtos.ChangePasswordRequest;
import com.secops.dto.AuthDtos.LoginRequest;
import com.secops.dto.AuthDtos.TokenResponse;
import com.secops.entity.AppUser;
import com.secops.entity.RefreshToken;
import com.secops.exception.PolicyViolationException;
import com.secops.repository.AppUserRepository;
import com.secops.repository.RefreshTokenRepository;
import com.secops.security.JwtService;
import com.secops.security.SecOpsPrincipal;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthenticationService {

	private final AppUserRepository userRepository;
	private final RefreshTokenRepository tokensRepository;
	private final PasswordEncoder encoder;
	private final JwtService jwt;
	private final SecOpsProperties cfg;
	private final HashingService hashingService;
	private final AuditService auditService;
	private final SecureRandom random = new SecureRandom();

	public AuthenticationService(AppUserRepository userRepository, RefreshTokenRepository tokenRepository,
			PasswordEncoder encoder, JwtService jwt, SecOpsProperties cfg, HashingService hashingService,
			AuditService auditService) {
		this.userRepository = userRepository;
		this.tokensRepository = tokenRepository;
		this.encoder = encoder;
		this.jwt = jwt;
		this.cfg = cfg;
		this.hashingService = hashingService;
		this.auditService = auditService;
	}

	@SuppressWarnings("null")
	@Transactional
	public TokenResponse login(LoginRequest request) {
		AppUser user = userRepository.findByUsernameIgnoreCase(request.username())
				.orElseThrow(() -> new PolicyViolationException("INVALID_CREDENTIALS", "Credenciales inválidas."));
		
		if (!user.isEnabled() || (user.getLockedUntil() != null && user.getLockedUntil().isAfter(Instant.now()))
				|| !encoder.matches(request.password(), user.getPasswordHash())) {
			user.setFailedAttempts(user.getFailedAttempts() + 1);
			if (user.getFailedAttempts() >= 5) {
				user.setLockedUntil(Instant.now().plus(Duration.ofMinutes(15)));
			}
			
			userRepository.save(user);
			auditService.record(null, "LOGIN_FAILURE", "USER", null, "DENY", "AUTH", "Credenciales inválidas");
			throw new PolicyViolationException("INVALID_CREDENTIALS", "Credenciales inválidas.");
		}
		
		user.setFailedAttempts(0);
		user.setLockedUntil(null);
		userRepository.save(user);
		SecOpsPrincipal principal = principal(user);
		
		auditService.record(principal, "LOGIN_SUCCESS", "USER", user.getId().toString(), "ALLOW", "AUTH",
				"Inicio de sesión");
		
		return issue(user, UUID.randomUUID());
	}

	@SuppressWarnings("null")
	@Transactional
	public TokenResponse refresh(String raw) {
		RefreshToken old = tokensRepository.findByTokenHash(hashingService.sha256(raw))
				.orElseThrow(() -> new PolicyViolationException("INVALID_REFRESH", "Sesión inválida."));
		
		if (old.getRevokedAt() != null || old.getExpiresAt().isBefore(Instant.now())) { 
			throw new PolicyViolationException("INVALID_REFRESH", "Sesión inválida.");
		}
		
		old.setRevokedAt(Instant.now());
		AppUser user = userRepository.findById(old.getUserId())
				.orElseThrow(() -> new PolicyViolationException("INVALID_REFRESH", "Sesión inválida."));
		
		TokenResponse next = issue(user, old.getFamilyId());
		old.setReplacedBy(tokensRepository.findByTokenHash(hashingService.sha256(next.refreshToken()))
				.map(RefreshToken::getId).orElse(null));
		tokensRepository.save(old);
		
		return next;
	}

	@SuppressWarnings("null")
	@Transactional
	public void changePassword(SecOpsPrincipal principal, ChangePasswordRequest request) {
		AppUser user = userRepository.findById(principal.userId()).orElseThrow();
		
		if (!encoder.matches(request.currentPassword(), user.getPasswordHash())) {
			throw new PolicyViolationException("INVALID_CREDENTIALS", "La contraseña actual no es válida.");
		}
		
		if (encoder.matches(request.newPassword(), user.getPasswordHash())) {
			throw new PolicyViolationException("PASSWORD_REUSE", "La contraseña nueva debe ser diferente.");
		}
		
		user.setPasswordHash(encoder.encode(request.newPassword()));
		user.setMustChangePassword(false);
		userRepository.save(user);
		
		tokensRepository.findAll().stream().filter(x -> x.getUserId().equals(user.getId()) && x.getRevokedAt() == null)
				.forEach(x -> {
					x.setRevokedAt(Instant.now());
					tokensRepository.save(x);
				});
		
		auditService.record(principal, "PASSWORD_CHANGED", "USER", user.getId().toString(), "ALLOW", "AUTH",
				"Contraseña cambiada y sesiones revocadas");
	}

	private TokenResponse issue(AppUser user, UUID family) {
		byte[] b = new byte[48];
		random.nextBytes(b);
		String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(b);
		
		RefreshToken t = new RefreshToken();
		t.setUserId(user.getId());
		t.setFamilyId(family);
		t.setTokenHash(hashingService.sha256(raw));
		t.setExpiresAt(Instant.now().plus(Duration.ofHours(cfg.jwt().refreshHours())));
		tokensRepository.save(t);
		
		return new TokenResponse(jwt.issueAccess(user), raw, jwt.accessSeconds(), user.getUsername(),
				user.getRoles().stream().map(Enum::name).collect(java.util.stream.Collectors.toUnmodifiableSet()),
				user.isMustChangePassword());
	}

	private SecOpsPrincipal principal(AppUser u) {
		return new SecOpsPrincipal(u.getId(), u.getUsername(), Set.copyOf(u.getRoles()));
	}
	
}

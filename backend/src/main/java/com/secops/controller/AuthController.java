package com.secops.controller;

import com.secops.dto.AuthDtos.ChangePasswordRequest;
import com.secops.dto.AuthDtos.LoginRequest;
import com.secops.dto.AuthDtos.RefreshRequest;
import com.secops.dto.AuthDtos.TokenResponse;
import com.secops.security.SecurityContextService;
import com.secops.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
	
	private final AuthenticationService auth;
	private final SecurityContextService context;

	public AuthController(AuthenticationService service, SecurityContextService context) {
		this.auth = service;
		this.context = context;
	}

	@PostMapping("/auth/login")
	TokenResponse login(@Valid @RequestBody LoginRequest request) {
		return auth.login(request);
	}

	@PostMapping("/auth/refresh")
	TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
		return auth.refresh(request.refreshToken());
	}

	@PutMapping("/me/password")
	ResponseEntity<Void> password(@Valid @RequestBody ChangePasswordRequest request) {
		auth.changePassword(context.current(), request);
		return ResponseEntity.noContent().build();
	}
	
}

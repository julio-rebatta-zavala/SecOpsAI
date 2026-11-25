package com.secops.controller;

import com.secops.dto.AdminDtos.CreateUserRequest;
import com.secops.dto.AdminDtos.UpdateUserRequest;
import com.secops.dto.AdminDtos.UserView;
import com.secops.security.SecurityContextService;
import com.secops.service.UserAdminService;
import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
	
	private final UserAdminService svc;
	private final SecurityContextService ctx;

	public AdminUserController(UserAdminService userAdminService, SecurityContextService context) {
		svc = userAdminService;
		ctx = context;
	}

	@GetMapping
	Page<UserView> list(@PageableDefault(size = 50) Pageable pageable) {
		return svc.list(pageable);
	}

	@PostMapping
	UserView create(@Valid @RequestBody CreateUserRequest request) {
		return svc.create(ctx.current(), request);
	}

	@PatchMapping("/{id}")
	UserView update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
		return svc.update(ctx.current(), id, request);
	}

}

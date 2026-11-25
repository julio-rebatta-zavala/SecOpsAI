package com.secops.service;

import com.secops.dto.AdminDtos.CreateUserRequest;
import com.secops.dto.AdminDtos.UpdateUserRequest;
import com.secops.dto.AdminDtos.UserView;
import com.secops.entity.AppUser;
import com.secops.exception.ConflictException;
import com.secops.exception.NotFoundException;
import com.secops.mapper.UserMapper;
import com.secops.repository.AppUserRepository;
import com.secops.security.SecOpsPrincipal;

import java.util.HashSet;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdminService {

	private final AppUserRepository userRepository;
	private final PasswordEncoder encoder;
	private final UserMapper mapper;
	private final AuditService auditService;

	public UserAdminService(AppUserRepository userRepository, PasswordEncoder encoder, UserMapper mapper,
			AuditService auditService) {
		this.userRepository = userRepository;
		this.encoder = encoder;
		this.mapper = mapper;
		this.auditService = auditService;
	}

	@SuppressWarnings("null")
	public Page<UserView> list(Pageable p) {
		return userRepository.findAll(p).map(mapper::toView);
	}

	@SuppressWarnings("null")
	@Transactional
	public UserView create(SecOpsPrincipal actor, CreateUserRequest request) {
		if (userRepository.findByUsernameIgnoreCase(request.username()).isPresent()) {
			throw new ConflictException("El usuario ya existe");
		}
			
		AppUser user = new AppUser();
		user.setUsername(request.username().strip());
		user.setPasswordHash(encoder.encode(request.temporaryPassword()));
		user.setRoles(new HashSet<>(request.roles()));
		user.setEnabled(true);
		user.setMustChangePassword(true);
		
		user = userRepository.save(user);
		auditService.record(actor, "USER_CREATED", "USER", user.getId().toString(), "ALLOW", "ADMIN", "Usuario creado");
		
		return mapper.toView(user);
	}

	@SuppressWarnings("null")
	@Transactional
	public UserView update(SecOpsPrincipal actor, UUID id, UpdateUserRequest request) {
		AppUser user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
		
		if (request.enabled() != null) {
			user.setEnabled(request.enabled());
		}
		
		if (request.roles() != null && !request.roles().isEmpty()) {
			user.setRoles(new HashSet<>(request.roles()));
		}
		
		user = userRepository.save(user);
		auditService.record(actor, "USER_CHANGED", "USER", id.toString(), "ALLOW", "ADMIN",
				"Estado o roles actualizados");
		return mapper.toView(user);
	}
	
}

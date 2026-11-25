package com.secops.security;

import com.secops.entity.Role;
import java.security.Principal;
import java.util.Set;
import java.util.UUID;

public record SecOpsPrincipal(UUID userId, String username, Set<Role> roles) implements Principal {
	
	public String getName() {
		return username;
	}

	public boolean has(Role r) {
		return roles.contains(r);
	}
	
}
package com.secops.mapper;

import com.secops.dto.AdminDtos.UserView;
import com.secops.entity.AppUser;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
	
	public UserView toView(AppUser u) {
		return new UserView(u.getId(), u.getUsername(), u.isEnabled(), u.isMustChangePassword(), u.getRoles(),
				u.getCreatedAt());
	}
	
}
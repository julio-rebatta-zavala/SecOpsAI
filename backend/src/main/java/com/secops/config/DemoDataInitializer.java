package com.secops.config;

import com.secops.entity.AppUser;
import com.secops.entity.Role;
import com.secops.repository.AppUserRepository;

import java.util.HashSet;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DemoDataInitializer implements CommandLineRunner {

    private final SecOpsProperties properties;
    private final AppUserRepository userRepository;
    private final PasswordEncoder encoder;

    public DemoDataInitializer(SecOpsProperties properties, AppUserRepository userRepository, PasswordEncoder encoder) {
        this.properties = properties;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public void run(@SuppressWarnings("null") String... args) {
        if (!properties.seedDemoUsers()) {
            return;
        }
        
        seed("admin", "ChangeMe_Admin_2026!", Role.ADMIN);
        seed("auditor", "ChangeMe_Audit_2026!", Role.AUDIT);
        seed("user", "ChangeMe_User_2026!", Role.USER);
    }

	private void seed(String name, @NonNull String pass, Role role) {
        if (userRepository.findByUsernameIgnoreCase(name).isPresent()) {
            return;
        }
        
        AppUser user = new AppUser();
        user.setUsername(name);
        user.setPasswordHash(encoder.encode(pass));
        user.setRoles(new HashSet<>(Set.of(role)));
        user.setEnabled(true);
        user.setMustChangePassword(true);
        userRepository.save(user);
    }

}

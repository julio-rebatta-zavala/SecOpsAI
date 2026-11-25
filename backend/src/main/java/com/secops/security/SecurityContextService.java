package com.secops.security;

import com.secops.exception.PolicyViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityContextService {
	
    public SecOpsPrincipal current() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof SecOpsPrincipal s) {
            return s;
        }
        
        throw new PolicyViolationException("UNAUTHENTICATED", "Autenticación requerida.");
    }
    
}
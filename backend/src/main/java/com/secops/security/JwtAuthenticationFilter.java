package com.secops.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwt;

	public JwtAuthenticationFilter(JwtService jwt) {
		this.jwt = jwt;
	}

	@SuppressWarnings("null")
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {
		String authorization = req.getHeader(HttpHeaders.AUTHORIZATION);
		
		if (authorization != null && authorization.startsWith("Bearer ")) {
			try {
				SecOpsPrincipal principal = jwt.parse(authorization.substring(7));
				List<SimpleGrantedAuthority> authorities = principal.roles().stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.name())).toList();
				SecurityContextHolder.getContext()
						.setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, authorities));
			} catch (Exception ignored) {
				SecurityContextHolder.clearContext();
			}
		}
		
		chain.doFilter(req, res);
	}
}

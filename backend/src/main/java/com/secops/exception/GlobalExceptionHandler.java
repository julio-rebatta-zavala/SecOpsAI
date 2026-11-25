package com.secops.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(PolicyViolationException.class)
	ResponseEntity<ProblemDetail> policy(PolicyViolationException e, HttpServletRequest r) {
		return problem(HttpStatus.UNPROCESSABLE_CONTENT, e.code(), e.getMessage(), r);
	}

	@ExceptionHandler(NotFoundException.class)
	ResponseEntity<ProblemDetail> notFound(NotFoundException e, HttpServletRequest r) {
		return problem(HttpStatus.NOT_FOUND, "NOT_FOUND", e.getMessage(), r);
	}

	@ExceptionHandler(ConflictException.class)
	ResponseEntity<ProblemDetail> conflict(ConflictException e, HttpServletRequest r) {
		return problem(HttpStatus.CONFLICT, "CONFLICT", e.getMessage(), r);
	}

	@ExceptionHandler(DependencyUnavailableException.class)
	ResponseEntity<ProblemDetail> dependency(DependencyUnavailableException e, HttpServletRequest r) {
		return problem(HttpStatus.SERVICE_UNAVAILABLE, "DEPENDENCY_UNAVAILABLE",
				"Un servicio de seguridad o IA no está disponible; la operación se detuvo de forma segura.", r);
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class, ConstraintViolationException.class,
			MaxUploadSizeExceededException.class })
	ResponseEntity<ProblemDetail> bad(Exception e, HttpServletRequest r) {
		return problem(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
				"La solicitud no cumple el formato o tamaño permitido.", r);
	}

	@ExceptionHandler(AccessDeniedException.class)
	ResponseEntity<ProblemDetail> denied(AccessDeniedException e, HttpServletRequest r) {
		return problem(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "No tiene permisos para esta operación.", r);
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<ProblemDetail> unknown(Exception e, HttpServletRequest r) {
		return problem(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "No se pudo completar la operación.", r);
	}

	@SuppressWarnings("null")
	private ResponseEntity<ProblemDetail> problem(HttpStatus status, String code, String detail,
			HttpServletRequest req) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
		problem.setTitle(status.getReasonPhrase());
		problem.setType(URI.create("https://secops.local/problems/" + code.toLowerCase()));
		problem.setInstance(URI.create(req.getRequestURI()));
		problem.setProperty("errorCode", code);
		problem.setProperty("correlationId", MDC.get("correlationId"));
		problem.setProperty("timestamp", Instant.now());
		return ResponseEntity.status(status).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(problem);
	}

}

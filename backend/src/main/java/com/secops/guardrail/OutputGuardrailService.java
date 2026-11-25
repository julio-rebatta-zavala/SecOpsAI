package com.secops.guardrail;

import com.secops.exception.PolicyViolationException;
import org.springframework.stereotype.Service;
import java.util.Locale;

@Service
public class OutputGuardrailService {
	
	public String validate(String out) {
		if (out == null || out.isBlank()) {
			throw new PolicyViolationException("EMPTY_MODEL_OUTPUT", "No se obtuvo una respuesta segura.");
		}
		
		String l = out.toLowerCase(Locale.ROOT);
		if (l.contains("secops-system-canary") || l.contains("jwt_secret") || l.contains("data_encryption_key")
				|| l.contains("BEGIN PRIVATE KEY".toLowerCase())) {
			throw new PolicyViolationException("OUTPUT_SECRET_DETECTED",
					"La respuesta fue bloqueada por el control de protección de datos.");
		}
		
		return out;
	}
	
}
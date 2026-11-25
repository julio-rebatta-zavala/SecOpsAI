package com.secops.guardrail;

import com.secops.config.SecOpsProperties;
import com.secops.exception.PolicyViolationException;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class PolicyGuardrailService {
	
	private final SecOpsProperties properties;

	public PolicyGuardrailService(SecOpsProperties properties) {
		this.properties = properties;
	}

	public String validate(String raw) {
		if (raw == null || raw.isBlank()) {
			throw new PolicyViolationException("EMPTY_INPUT", "Escriba una consulta.");
		}
		
		String normalized = Normalizer.normalize(raw, Normalizer.Form.NFKC).replace("\0", "").trim();
		if (normalized.length() > properties.policy().maxInputChars()) {
			throw new PolicyViolationException("INPUT_TOO_LONG", "La consulta excede el tamaño permitido.");
		}
		
		String lower = normalized.toLowerCase(Locale.ROOT);
		for (String term : properties.policy().protectedTermList()) {
			if (containsTerm(lower, term.toLowerCase(Locale.ROOT))) {
				throw new PolicyViolationException("POLICY_PROTECTED_ATTRIBUTE",
						"No puedo procesar solicitudes que usen atributos personales protegidos para clasificar, priorizar o excluir personas. Reformule la consulta sin edad, sexo/género, nacionalidad, ubicación, religión u otros atributos protegidos.");
			}
		}
		
		for (String injection : properties.policy().injectionPatternList()) {
			if (lower.contains(injection.toLowerCase(Locale.ROOT))) {
				throw new PolicyViolationException("PROMPT_INJECTION",
						"No puedo seguir instrucciones para cambiar mi función, revelar instrucciones internas o eludir controles de seguridad.");
			}
		}
		
		return normalized;
	}

	private boolean containsTerm(String input, String term) {
		return Pattern.compile("(?iu)(^|[^\\p{L}])" + Pattern.quote(term) + "([^\\p{L}]|$)").matcher(input).find();
	}
}

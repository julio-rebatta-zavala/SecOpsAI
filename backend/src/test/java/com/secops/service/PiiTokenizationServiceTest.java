package com.secops.service;

import com.secops.client.PresidioClient;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PiiTokenizationServiceTest {

	@SuppressWarnings("null")
	@Test
	void tokenizesAndRestoresWithoutLeaking() {
		var p = mock(PresidioClient.class);
		String raw = "Contacta a ana@example.com por CVE-2021-44228";
		when(p.analyze(raw)).thenReturn(List.of(new PresidioClient.Detection("EMAIL_ADDRESS", 11, 26, .99)));
		var svc = new PiiTokenizationService(p);
		var t = svc.tokenize(raw);
		assertFalse(t.text().contains("ana@example.com"));
		assertTrue(t.text().contains("<PII_EMAIL_ADDRESS_1>"));
		assertEquals(raw, svc.restore(t.text(), t.vault()));
	}
	
}

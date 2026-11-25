package com.secops.service;

import com.secops.config.SecOpsProperties;
import org.junit.jupiter.api.Test;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;

class EncryptionServiceTest {

	@SuppressWarnings("null")
	@Test
	void aesGcmRoundTripAndRandomIv() {
		var p = new SecOpsProperties(null, Base64.getEncoder().encodeToString(new byte[32]), null, null, null, null,
				false, null);
		var s = new EncryptionService(p);
		String a = s.encrypt("secreto"), b = s.encrypt("secreto");
		assertNotEquals(a, b);
		assertEquals("secreto", s.decrypt(a));
	}

}

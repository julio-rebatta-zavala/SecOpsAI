package com.secops.service;

import com.secops.config.SecOpsProperties;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Service
public class EncryptionService {

	private final SecretKey key;
	private final SecureRandom random = new SecureRandom();

	public EncryptionService(SecOpsProperties p) {
		byte[] k;
		
		try {
			k = Base64.getDecoder().decode(p.encryptionKey());
		} catch (Exception e) {
			throw new IllegalStateException("DATA_ENCRYPTION_KEY inválida");
		}
		
		if (k.length != 32) {
			throw new IllegalStateException("DATA_ENCRYPTION_KEY debe decodificar 32 bytes");
		}
		
		key = new SecretKeySpec(k, "AES");
	}

	public String encrypt(String value) {
		try {
			byte[] iv = new byte[12];
			random.nextBytes(iv);
			
			Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
			c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
			
			byte[] enc = c.doFinal(value.getBytes(StandardCharsets.UTF_8));
			byte[] all = new byte[iv.length + enc.length];
			
			System.arraycopy(iv, 0, all, 0, iv.length);
			System.arraycopy(enc, 0, all, iv.length, enc.length);
			return "v1:" + Base64.getEncoder().encodeToString(all);
		} catch (Exception e) {
			throw new IllegalStateException("No se pudo cifrar", e);
		}
	}

	public String decrypt(String value) {
		try {
			byte[] all = Base64.getDecoder().decode(value.substring(3));
			byte[] iv = Arrays.copyOfRange(all, 0, 12), enc = Arrays.copyOfRange(all, 12, all.length);
			
			Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
			c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
			
			return new String(c.doFinal(enc), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new IllegalStateException("No se pudo descifrar", e);
		}
	}
	
}

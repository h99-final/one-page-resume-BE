package com.f5.onepageresumebe.util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class AES256 {

    public final static String alg = "AES/CBC/PKCS5Padding";
    private final String key = "gUkXp2r5u8x/A?D(G+KbPeShVmYq3t6v";
    private final String iv = key.substring(0,16);

    public String encrypt(String token) throws Exception{
        Cipher cipher = Cipher.getInstance(alg);

        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE,keySpec,ivParameterSpec);

        byte[] encrypted = cipher.doFinal(token.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String cipherToken) throws Exception{
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE,keySpec, ivParameterSpec);

        byte[] decodedBytes = Base64.getDecoder().decode(cipherToken);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted,StandardCharsets.UTF_8);
    }
}

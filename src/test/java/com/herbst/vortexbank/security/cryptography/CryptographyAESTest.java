package com.herbst.vortexbank.security.cryptography;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class CryptographyAESTest {
    @Autowired
    private CryptographyAES crypto;

    @Test
    public void encryptAndDecrypt_Success(){
        String expectedName = "Test";
        String encryptedName = crypto.encrypt(expectedName);
        String result = crypto.decrypt(encryptedName);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedName, result);
    }

    @Test
    public void encryptAndDecryptPassingTheKey_Success() throws Exception {
        String key = SecretKeyGeneratorAES.generateKey();
        String expectedName = "Test";
        String encryptedName = crypto.encrypt(expectedName, key);
        String result = crypto.decrypt(encryptedName, key);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedName, result);
    }
}
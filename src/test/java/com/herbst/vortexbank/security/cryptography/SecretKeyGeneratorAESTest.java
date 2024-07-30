package com.herbst.vortexbank.security.cryptography;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class SecretKeyGeneratorAESTest {

    @Test
    public void keyGenerator_Success() throws Exception {
        String key = SecretKeyGeneratorAES.generateKey();
        System.out.println(key);
        Assertions.assertNotNull(key);
    }

}
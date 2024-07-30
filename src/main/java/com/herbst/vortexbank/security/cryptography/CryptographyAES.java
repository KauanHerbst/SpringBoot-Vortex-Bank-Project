package com.herbst.vortexbank.security.cryptography;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class CryptographyAES {
    private static final String ALGORITHM = "AES";

    @Value("${security.crypto.symmetric.key}")
    private String symmetricKey;

    private SecretKey secretKey;

    private Cipher cipher;

    @PostConstruct
    protected void init(){
        try{
            cipher = Cipher.getInstance(ALGORITHM);
            byte[] decodedKey = Base64.getDecoder().decode(symmetricKey);
            secretKey = new SecretKeySpec(decodedKey, ALGORITHM);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String encrypt(String data){
        try{
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String encrypt(String data, String key){
        try{
            secretKey = getSecretKey(key);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public String decrypt(String encryptedData){
        try{
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedData);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String encryptedData, String key){
        try{
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            secretKey = getSecretKey(key);
            byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedData);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public SecretKey getSecretKey(String key){
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decodedKey, ALGORITHM);
    }
}

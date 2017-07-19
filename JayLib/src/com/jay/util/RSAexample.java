package com.jay.util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSAexample {

 public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
	 Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

     byte[] input = "abcdefg hijklmn".getBytes();
     Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
     SecureRandom random = new SecureRandom();
     KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");

     generator.initialize(128, random); // 여기에서는 128 bit 키를 생성하였음
     KeyPair pair = generator.generateKeyPair();
     Key pubKey = pair.getPublic();  // Kb(pub) 공개키
     Key privKey = pair.getPrivate();// Kb(pri) 개인키

     // 공개키를 전달하여 암호화
     cipher.init(Cipher.ENCRYPT_MODE, pubKey);
     byte[] cipherText = cipher.doFinal(input);
     System.out.println("cipher: ("+ cipherText.length +")"+ new String(cipherText));
     
     // 개인키를 가지고있는쪽에서 복호화
     cipher.init(Cipher.DECRYPT_MODE, privKey);
     byte[] plainText = cipher.doFinal(cipherText);
     System.out.println("plain : " + new String(plainText));
 }
}
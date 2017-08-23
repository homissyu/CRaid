package com.jay.cipher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.jay.util.CommonConst;

public class AsymmetricCipherFactory {
	private static void createKey(char [] password) throws Exception {

        // RSA 키 쌍 생성

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.genKeyPair();

        //공개키를 파일에 쓴다.
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        FileOutputStream fos = new FileOutputStream("c:\\publicKey");
        fos.write(publicKeyBytes);
        fos.close();

        // 개인 키를 암호화한 후에 파일에 쓴다.
        byte[] privateKeyBytes = passwordEncrypt(password, keyPair.getPrivate().getEncoded());
        fos = new FileOutputStream("c:\\privateKey");
        fos.write(publicKeyBytes);
        fos.close();
  }

  private static byte[] passwordEncrypt(char[] password, byte[] plaintext) throws Exception {
        // salt 생성
        byte[] salt = new byte[9];
        Random random = new Random();
        random.nextBytes(salt);

        // PBE 키와 사이퍼 생성
        PBEKeySpec keySpec = new PBEKeySpec(password);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithSHAAndTwofish-CBC");
        SecretKey key = keyFactory.generateSecret(keySpec);
        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, CommonConst.ITERATION_CNT);

        Cipher cipher = Cipher.getInstance("PBEWithSHAAndTwofish-CBC");
        cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

        byte[] cipherText = cipher.doFinal(plaintext);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(salt);
        baos.write(cipherText);

        return baos.toByteArray();
  }

  public static void encrypt(String fileInput) throws Exception {
        String publicKeyFileName = "c:\\publicKey";

        // 공개 키가 저장된 파일로부터 keyByte의 바이트 배열을 생성한다.
        FileInputStream fis = new FileInputStream(publicKeyFileName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int theByte = 0;
        while((theByte = fis.read()) != -1)
               baos.writeTo(baos);
        fis.close();
        byte[] keyBytes = baos.toByteArray();
        baos.close();

        // 인코딩된 키를 RSA 공개 키의 인스턴스로 바꾼다.
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        String fileOutput = fileInput + CommonConst.CURRENT_DIR + CommonConst.ENCRYPTED;
        DataOutputStream output = new DataOutputStream(new FileOutputStream(fileOutput));

        // RSA 공개 키를 이용하여 세션 키를 암호화할 사이퍼를 생성한다.
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        // 세션 키 생성
        KeyGenerator rijndaelKeyGenerator = KeyGenerator.getInstance("Rijndael");
        rijndaelKeyGenerator.init(256);
        Key rijndaelKey = rijndaelKeyGenerator.generateKey();

        // RSA 사이퍼를 이용하여 세션 키를 암호화 하고 파일에 저장한다.
        // 키의 길이, 인코딩된 세션 키 형식이다.
        byte[] encodedKeyBytes = rsaCipher.doFinal(rijndaelKey.getEncoded());
        output.writeInt(encodedKeyBytes.length);
        output.write(encodedKeyBytes);

        // 초기화 벡터
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[16];
        random.nextBytes(iv);

        //IV를 파일에 쓴다
        output.write(iv);

        //IV와 생성한 세션 키를 이용하여 파일의 내용을 암호화한다.
        IvParameterSpec spec = new IvParameterSpec(iv);
        Cipher symmetricCipher = Cipher.getInstance("Rijndael/CBC/PKCS5Padding");
        symmetricCipher.init(Cipher.ENCRYPT_MODE, rijndaelKey, spec);
        CipherOutputStream cos = new CipherOutputStream(output, symmetricCipher);

        FileInputStream input = new FileInputStream(fileInput);
        theByte = 0;
        while((theByte = input.read()) != -1)
               cos.write(theByte);

        input.close();
        cos.close();
        return;
  }
  
  private static byte[] passwordDecrypt(char[] password, byte[] ciphertext) throws Exception {

      // salt를 읽는다. 개인키는 8byte salt를 사용했다.

      byte[] salt = new byte[8];
      ByteArrayInputStream bais = new ByteArrayInputStream(ciphertext);
      bais.read(salt, 0 ,8);

      byte[] remainingCiphertext = new byte[ciphertext.length-8];
      bais.read(remainingCiphertext, 0, ciphertext.length-8);

      //PBE 사이퍼를 생성하여 세션 키를 복원한다. 
      PBEKeySpec keySpec = new PBEKeySpec(password);
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithSHAAndTwofish-CBC");
      SecretKey key = keyFactory.generateSecret(keySpec);
      PBEParameterSpec paramSpec = new PBEParameterSpec(salt, CommonConst.ITERATION_CNT);
      Cipher cipher = Cipher.getInstance("PBEWithSHAAndTwofish-CBC");

      // 키 복호화
      cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
      return cipher.doFinal(remainingCiphertext);
	}
	
	
	
	public static void decrypt(char [] password, String fileInput) throws Exception {
	
	      String privateKeyFilename = "c:\\privateKey";
	
	      // 파일로부터 개인 키를 읽어들인다.
	      FileInputStream fis = new FileInputStream(privateKeyFilename);
	      ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	      int theByte = 0;
	      while((theByte = fis.read()) != -1)
	             baos.write(theByte);
	
	      fis.close();
	      byte[] keyByte = baos.toByteArray();
	      baos.close();
	
	       // 암호화된 개인 키 바이트를 복원한다.
	      keyByte = passwordDecrypt(password, keyByte);
	
	      // RSA 개인 키를 복원한다.
	      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyByte);
	      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	      PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
	
	      
	
	      // 개인 키를 이용하여 사이퍼를 생성하고 세션 키를 복호화한다
	
	      Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	      DataInputStream dis = new DataInputStream(new FileInputStream(fileInput));
	
	      byte[] encryptedKeyBytes = new byte[dis.readInt()];
	      dis.readFully(encryptedKeyBytes);
	
	      rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
	      byte[] rijndaelKeyByte = rsaCipher.doFinal(encryptedKeyBytes);
	
	      SecretKey rijndaelKey = new SecretKeySpec(rijndaelKeyByte, "Rijndael");
	
	      byte[] iv = new byte[16];
	      dis.readFully(iv);
	
	      IvParameterSpec spec = new IvParameterSpec(iv);
	      
	      Cipher cipher = Cipher.getInstance("Rijndael/CBC/PKCS5padding");
	      cipher.init(Cipher.DECRYPT_MODE, rijndaelKey, spec);
	      CipherInputStream cis = new CipherInputStream(dis, cipher);
	
	      FileOutputStream fos = new FileOutputStream(fileInput + CommonConst.CURRENT_DIR + CommonConst.DECRYPTED);
	
	      theByte = 0;
	      while((theByte = cis.read()) != -1)
	             fos.write(theByte);
	      
	      cis.close();
	      fos.close();
	
	      return;      
	}


}

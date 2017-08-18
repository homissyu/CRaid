package com.jay.cipher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.jay.util.CommonConst;

public class SymmetricCipherFactory {
	
	private static String KEY_FILENAME = System.getProperty(CommonConst.USER_DIR_PROP_KEY)+File.separator+CommonConst.LIB_DIR+File.separator+CommonConst.SECRET_KEY_FILE;
	
	/**
	 * 1. 256bit AES(Rijndael)키를  생성
	 * 2. PBE를 이용하여 생성한 키를 암호화하여 파일에 저장
	 */
	private static void createKey(char[] password) {
		System.out.println("Generating a RijnDael key...");
		// AES 대칭 키 생성기 객체 생성
		KeyGenerator keyGenerator = null;
		try {
			keyGenerator = KeyGenerator.getInstance("Rijndael");
		} catch (NoSuchAlgorithmException e) {}
		// AES 키 생성
		keyGenerator.init(256);
		Key key = keyGenerator.generateKey();
		System.out.println("Done generating the key");
		
		// PBE 시작
		// salt 생성
		byte[] salt = new byte[8];
		SecureRandom random = new SecureRandom();
		random.nextBytes(salt);
		
		// password로 PBEKeySpec 생성 
		PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
		SecretKeyFactory keyFactory = null;;
		SecretKey pbeKey = null;
		try {
			keyFactory = SecretKeyFactory.getInstance("PBEWithSHAAndTwofish-CBC");
			pbeKey = keyFactory.generateSecret(pbeKeySpec);
		} catch (NoSuchAlgorithmException e) {} 
		catch (InvalidKeySpecException e) {}
		// PBEParameterSpec 생성
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, CommonConst.ITERATION_CNT);
		// Cipher 생성 및 초기화
		Cipher cipher = null;
		byte[] encryptedKeyBytes = null;
		try {
			cipher = Cipher.getInstance("PBEWithSHAAndTwofish-CBC");
			cipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
			
			// AES키 암호화
			encryptedKeyBytes = cipher.doFinal(key.getEncoded());
		} catch (NoSuchAlgorithmException e) {} 
		catch (NoSuchPaddingException e) {} 
		catch (InvalidKeyException e) {} 
		catch (InvalidAlgorithmParameterException e) {}
		catch (IllegalBlockSizeException e) {} 
		catch (BadPaddingException e) {}
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(KEY_FILENAME);
			//salt와 암호화된 키 바이트를 쓴다.
			fos.write(salt);
			fos.write(encryptedKeyBytes);
			fos.close();
		} catch (FileNotFoundException e) {} 
		catch (IOException e) {}
	}
	
	private static Key loadKey(char[] password) throws Exception {
		// 파일로부터 읽어들임
		FileInputStream fis = new FileInputStream(KEY_FILENAME);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = 0;
		while((i=fis.read()) != -1)
			baos.write(i);
		fis.close();
		byte[] saltAndKeyBytes = baos.toByteArray();
		baos.close();
		// salt를 분리함. Base64 인코딩을 하지 않았기에 8byte 그대로 유지됨
		byte[] salt = new byte[8];
		System.arraycopy(saltAndKeyBytes, 0, salt, 0, 8);
		
		// key를 분리함
		int length = saltAndKeyBytes.length - 8;
		byte[] encryptedKeyBytes = new byte[length];
		System.arraycopy(saltAndKeyBytes, 8, encryptedKeyBytes, 0, length);
		
		// PBE 시작
		PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithSHAAndTwofish-CBC");
		SecretKey pbeKey = keyFactory.generateSecret(pbeKeySpec);
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, CommonConst.ITERATION_CNT);
		// Cipher 생성 및 초기화
		Cipher cipher = Cipher.getInstance("PBEWithSHAAndTwofish-CBC");
		cipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
		// 키 복호화
		byte[] decryptedKeyBytes = cipher.doFinal(encryptedKeyBytes);
		SecretKeySpec key = new SecretKeySpec(decryptedKeyBytes, "Rijndael");
		
		return key;
	}

	private static void encrypt(char[] passowrd, String fileInput, String fileOutput) throws Exception {
		// key 로드
		System.out.println("Loading the key.");
		Key key = loadKey(passowrd);
		System.out.println("Loaded the key.");
		
		// 사이퍼 생성
		Cipher cipher = Cipher.getInstance("Rijndael/CBC/PKCS5Padding");
		System.out.println("Initializing SecureRandom...");
		// SecureRandom 생성하여 IV 초기화
		SecureRandom random = new SecureRandom();
		byte[] iv = new byte[16];
		random.nextBytes(iv);
		// 입력 파일과 출력 파일의 stream을 연다 
		FileInputStream fis = new FileInputStream(fileInput);
		FileOutputStream fos = new FileOutputStream(fileOutput);
		
		// 먼저 출력파일에 iv를 쓴다.
		fos.write(iv);
		// IvParameterSpec를 생성하고 사이퍼를 생성 및 초기화한다.
		IvParameterSpec spec = new IvParameterSpec(iv);
		System.out.println("Initializing the cipher...");
		cipher.init(Cipher.ENCRYPT_MODE, key, spec);
		// 출력 스트림과 사이퍼를 인자로 사이퍼 스트림을 생성한다. 
		CipherOutputStream cos = new CipherOutputStream(fos, cipher);
		
		System.out.println("Encrypting the file");
		// 입력 파일로부터 읽어들여 사이퍼 스트림에다 쓴다.
		int theByte = 0;
		while((theByte = fis.read()) != -1)
			cos.write(theByte);
		// 스트림을 모두 닫아준다.
		fis.close();
		cos.close();
	}
	
	private static void decrypt(char[] password, String fileInput, String fileOutput) throws Exception {
		// key 로드
		System.out.println("Loading the key");
		Key key = loadKey(password);
		System.out.println("Loaded the key.");
		// 사이퍼 생성
		Cipher cipher = Cipher.getInstance("Rijndael/CBC/PKCS5Padding");
		// 입력 파일과 출력 파일의 stream을 연다 
		FileInputStream fis = new FileInputStream(fileInput);
		FileOutputStream fos = new FileOutputStream(fileOutput);
		// 입력파일에서 초기화 벡터 16byte를 읽어들인다. 
		// Base64인코딩을 하지 않았으므로 길이는 그대로다.
		byte[] iv = new byte[16];
		fis.read(iv);
		// IvParameterSpec 생성 및 사이퍼 초기화
		IvParameterSpec spec = new IvParameterSpec(iv);
		System.out.println("Initializing the cipher...");
		cipher.init(Cipher.DECRYPT_MODE, key, spec);
		// 입력파일과 사이퍼를 인자로 사이퍼 스트림을 생성한다.
		CipherInputStream cis = new CipherInputStream(fis, cipher);
		System.out.println("Decrypting the file...");
		// 입력파일로 부터 읽어들여 출력파일에 쓴다.
		int theByte = 0;
		while((theByte = cis.read()) != -1)
			fos.write(theByte);
		// 스트림을 모두 닫아준다.
		cis.close();
		fos.close();
	}
}

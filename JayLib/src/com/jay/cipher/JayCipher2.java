/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jay.cipher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.jay.util.CommonConst;
import com.jay.util.FileHandler;


/**
 *
 * @author Jay
 */
public class JayCipher2 {
	
	Cipher cipher = null;
	SecretKeySpec secretKeySpec = null;
	Key pubKey = null;
	Key privateKey = null;
	
	public void encrypt(int cipherType, Serializable object, OutputStream ostream) throws InvalidKeyException, IllegalBlockSizeException, IOException, InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, BadPaddingException {
		encrypt(cipherType, null, object, ostream);
	}
	
	public void encrypt(int cipherType, String password, Serializable object, OutputStream ostream) throws InvalidKeyException, IllegalBlockSizeException, IOException, InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, BadPaddingException {
		switch(cipherType) {
			case CommonConst.SECRET_KEY_TYPE:
				SecretKeyCipherFactory skcf = new SecretKeyCipherFactory();
				cipher = skcf.getCipher();
				cipher.init(Cipher.ENCRYPT_MODE, skcf.getKeySpec());
	            break;
			case CommonConst.PKI_KEY_TYPE:
				PKICipherFactory pkif = new PKICipherFactory();
				cipher = pkif.getCipher();
				cipher.init(Cipher.ENCRYPT_MODE, pkif.getPubKey());
				break;
			case CommonConst.PBE_KEY_TYPE:
				PBECipherFactory pbef = new PBECipherFactory(password);
				cipher = pbef.getCipher();
				cipher.init(Cipher.ENCRYPT_MODE, pbef.getSecretKey());
				break;
			default:
				skcf = new SecretKeyCipherFactory();
				cipher = skcf.getCipher();
	            cipher.init(Cipher.ENCRYPT_MODE, skcf.getKeySpec());
				break;				
		}
		synchronized(cipher){
			encrypt(object, ostream);
		}
	}
	
	private void encrypt(Serializable object, OutputStream ostream) throws IllegalBlockSizeException, IOException, BadPaddingException {
		SealedObject sealedObject = new SealedObject(object, cipher);
        CipherOutputStream cos = new CipherOutputStream(ostream, cipher);
        ObjectOutputStream outputStream = new ObjectOutputStream(cos);
        outputStream.writeObject(sealedObject);
        outputStream.close();
	}
	
//	private String encrypt(Object plainText) throws IllegalBlockSizeException, BadPaddingException {
//		return new String(cipher.doFinal(((String)plainText).getBytes()));		
//	}
	
	public Object decrypt(int cipherType, InputStream istream) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, NoSuchProviderException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		return decrypt(cipherType, null, istream);
	}
	
	public Object decrypt(int cipherType,String password, InputStream istream) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, NoSuchProviderException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		switch(cipherType) {
			case CommonConst.SECRET_KEY_TYPE:
				SecretKeyCipherFactory skcf = new SecretKeyCipherFactory();
				cipher = skcf.getCipher();
				cipher.init(Cipher.DECRYPT_MODE, skcf.getKeySpec());
				break;
			case CommonConst.PKI_KEY_TYPE:
				PKICipherFactory pkif = new PKICipherFactory();
				cipher = pkif.getCipher();
				cipher.init(Cipher.DECRYPT_MODE, pkif.getPrivateKey());
				break;
			case CommonConst.PBE_KEY_TYPE:
				PBECipherFactory pbef = new PBECipherFactory(password);
				cipher = pbef.getCipher();
				cipher.init(Cipher.DECRYPT_MODE, pbef.getSecretKey(), new IvParameterSpec(cipher.getIV()));
				break;
			default:
				break;				
		}
		synchronized(cipher){
			return decrypt(istream);
		}
		
	}
	
	private Object decrypt(InputStream istream) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		CipherInputStream cipherInputStream = new CipherInputStream(istream, cipher);
	    ObjectInputStream inputStream = new ObjectInputStream(cipherInputStream);
	    SealedObject sealedObject;
	    try {
	        sealedObject = (SealedObject) inputStream.readObject();
	        return sealedObject.getObject(cipher);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    } finally {
	    	if(cipherInputStream != null)cipherInputStream.close();
	    	if(inputStream != null)inputStream.close();
	    }
		
	}
	
	private String decrypt2(InputStream istream) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		String ret = null;
		byte [] buffer = new byte[2048];
		int bytes_read = 0;
		
		while(true) {
			bytes_read = istream.read(buffer);
			if(bytes_read == -1)
				break;
		}
		
		System.out.println(buffer);
		ret = cipher.doFinal(buffer).toString();
		System.out.println(ret);
		return ret;		
	}
	
	public static void main(String [] args) {
		String aTemp = "123456abcde";
		String password = "1234";
		ObjectOutputStream oos = null;
        File secFile = null;
        File pbeFile = null;
        File pkiFile = null;
        String secFilePath = "C:\\secJayEncTest.txt";
        String pbeFilePath = "C:\\pbeJayEncTest.txt";
        String pkiFilePath = "C:\\pkiJayEncTest.txt";
        try{
        	
        	//SecretKey
//        	secFile = new File(secFilePath);
//            if(!secFile.exists())
//            	secFile.createNewFile(); 
//            
//            oos = new ObjectOutputStream(new FileOutputStream(secFile));
//			JayCipher2 jc_sec = new JayCipher2();
//			
//			jc_sec.encrypt(CommonConst.SECRET_KEY_TYPE, aTemp, oos);
//			
//			if(secFile.exists()){
//            	System.out.println(jc_sec.decrypt(CommonConst.SECRET_KEY_TYPE, new ObjectInputStream(new FileInputStream(secFile))));
//            }
			
			
			//PBEKey
			pbeFile = new File(pbeFilePath);
            if(!pbeFile.exists())
            	pbeFile.createNewFile(); 
            
            oos = new ObjectOutputStream(new FileOutputStream(pbeFile));
			JayCipher2 jc_pbe = new JayCipher2();
			
			jc_pbe.encrypt(CommonConst.PBE_KEY_TYPE, password, aTemp, oos);
			
			if(pbeFile.exists()){
            	System.out.println(jc_pbe.decrypt(CommonConst.PBE_KEY_TYPE, password, new ObjectInputStream(new FileInputStream(pbeFile))));
            }
//			
//			
			//PKIKey
//			pkiFile = new File(pkiFilePath);
//            if(!pkiFile.exists())
//            	pkiFile.createNewFile(); 
//            
//            oos = new ObjectOutputStream(new FileOutputStream(pkiFile));
//			JayCipher2 jc_pki = new JayCipher2();
//			
//			jc_pki.encrypt(CommonConst.PKI_KEY_TYPE, aTemp, oos);
//			
//			if(pkiFile.exists()){
//				System.out.println(jc_pki.decrypt(CommonConst.PKI_KEY_TYPE, new ObjectInputStream(new FileInputStream(pkiFile))));
//            }
            
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		FileInputStream fis = new FileInputStream(CommonConst);
//		jc.decrypt(CommonConst.SECRET_KEY_TYPE, aTemp, oos);
//		jc.decrypt(CommonConst.PBE_KEY_TYPE, "1234", aTemp, oos);
//		jc.decrypt(CommonConst.PKI_KEY_TYPE, aTemp, oos);
	}
}

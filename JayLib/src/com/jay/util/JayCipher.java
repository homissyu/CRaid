/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jay.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;


/**
 *
 * @author Jay
 */
public class JayCipher {
    
	private byte[] raw = null;
    private SecretKeySpec keySpec = null;
    private Cipher cipher = null; 
    private static SecretKey sk = null;
    
    public JayCipher(){
    	try {
			chkKeyFile();
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public JayCipher(SecretKey key) {
    	sk = key;  
    	init();
    }
    
    public final void init(){
    	try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            raw = sk.getEncoded();
            keySpec = new SecretKeySpec(raw, CommonConst.AES);
            cipher=Cipher.getInstance(CommonConst.AES);            
        } catch (Exception ex) {
            Logger.getLogger(JayCipher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Object readSerFile(String sFileName){
        FileInputStream fileIn = null;
        Object oRet = null;
        File file = null;
        try {
            file = new File(sFileName);
            if(file.exists()){
                fileIn = new FileInputStream(sFileName);
                ObjectInputStream ois = new ObjectInputStream(fileIn);
                oRet = ois.readObject();
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        } finally {
            try {
                if(fileIn!=null)fileIn.close();
            } catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
        return oRet;
    }
    
    public  String encrypt(String str){
        try{
            byte[] encrypted=null;
            
            synchronized(cipher){
                cipher.init(Cipher.ENCRYPT_MODE, keySpec);
                encrypted=cipher.doFinal(str.getBytes(CommonConst.ENCODING));
            }
            return new String(Base64.encodeBase64(encrypted));
        }catch(Exception e){
            throw new RuntimeException("encryption failure", e);
        }
    }

    public  String decrypt(String str){
        try{
            byte[] encrypted=null;
            encrypted = Base64.decodeBase64(str.getBytes(CommonConst.ENCODING));
            byte[] decrypted=null;
            
            synchronized(cipher){
                cipher.init(Cipher.DECRYPT_MODE, keySpec);
                decrypted=cipher.doFinal(encrypted);
            }
            return new String(decrypted);
        }catch(Exception e){
            throw new RuntimeException("decryption failuer", e);
        }
    }

    public static SecretKey generateRandomSecretKey(String algorithm) throws Exception{
        KeyGenerator keyGen=KeyGenerator.getInstance(algorithm);
        keyGen.init(128);
        SecretKey key=keyGen.generateKey();
        return key;
    }
    
    public byte[] encPKI(Key pubKey, String plainContents) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
    	Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
    	cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] cipherText = cipher.doFinal(plainContents.getBytes());
        System.out.println("cipher: ("+ cipherText.length +")"+ new String(cipherText));
        return cipherText;
    }
    
    public byte[] decPKI(Key privKey, String encContents) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    	cipher.init(Cipher.DECRYPT_MODE, privKey);
        byte[] plainText = cipher.doFinal(encContents.getBytes());
        System.out.println("plain : " + new String(plainText));
        return plainText;
    }
    
    public static KeyPair generateRandomPublicKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException{
    	SecureRandom random = new SecureRandom();
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        generator.initialize(128, random); 
        KeyPair pair = generator.generateKeyPair();
        Key pubKey = pair.getPublic();
        Key privKey = pair.getPrivate();
        return pair;
    }

//    private static String bytesToString(byte[] bytes){
//        byte[] b2=new byte[bytes.length+1];
//        b2[0]=1;
//        System.arraycopy(bytes, 0, b2, 1, bytes.length);
//        return new BigInteger(b2).toString(Character.MAX_RADIX);
//    }
//
//    private static byte[] stringToBytes(String str){
//        byte[] bytes=new BigInteger(str, Character.MAX_RADIX).toByteArray();
//        return Arrays.copyOfRange(bytes, 1, bytes.length);
//    }
    
    private static void chkKeyFile() throws Exception{
        File file = new File(System.getProperty(CommonConst.USER_DIR_PROP_KEY)+File.separator+CommonConst.LIB_DIR+File.separator+CommonConst.KEY_FILE);
//        System.out.println(file.getAbsolutePath());
        if(!file.exists()){
//        	System.out.println(file.exists());
            Key aKey = generateRandomSecretKey(CommonConst.AES);
            FileHandler.writeSerFile( aKey, System.getProperty(CommonConst.USER_DIR_PROP_KEY)+File.separator+CommonConst.LIB_DIR, CommonConst.KEY_FILE);
        }else sk = (SecretKey) FileHandler.readSerFile(System.getProperty(CommonConst.USER_DIR_PROP_KEY)+File.separator+CommonConst.LIB_DIR+File.separator+CommonConst.KEY_FILE);
    }
    
    
    
    public void encrypt(Serializable object, OutputStream ostream) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
	    try {
	    	
	    	// Create cipher
	        synchronized(cipher){
                cipher.init(Cipher.ENCRYPT_MODE, keySpec);
    	        SealedObject sealedObject = new SealedObject(object, cipher);

    	        // Wrap the output stream
    	        CipherOutputStream cos = new CipherOutputStream(ostream, cipher);
    	        ObjectOutputStream outputStream = new ObjectOutputStream(cos);
    	        outputStream.writeObject(sealedObject);
    	        outputStream.close();
            }
	        
	    } catch (IllegalBlockSizeException e) {
	        e.printStackTrace();
	    }
	}

	public Object decrypt(InputStream istream) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		
		synchronized(cipher){
			cipher.init(Cipher.DECRYPT_MODE, keySpec);

		    CipherInputStream cipherInputStream = new CipherInputStream(istream, cipher);
		    ObjectInputStream inputStream = new ObjectInputStream(cipherInputStream);
		    SealedObject sealedObject;
		    try {
		        sealedObject = (SealedObject) inputStream.readObject();
		        return sealedObject.getObject(cipher);
		    } catch (Exception e) {
		        e.printStackTrace();
		        return null;
		    }
        }
	}
}

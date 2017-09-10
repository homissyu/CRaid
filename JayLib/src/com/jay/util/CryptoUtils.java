package com.jay.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

import org.bouncycastle.crypto.CryptoException;
 
public class CryptoUtils {
	private static final String SUB_SYSTEM = "com.jay.util.CryptoUtils";
    private static final String TRANSFORMATION = CommonConst.AES;
    
    /**
     * 
     * @param key
     * @param inputFile
     * @param outputFile
     * @throws CryptoException
     */
    public static void encrypt(Key key, File inputFile, File outputFile) throws CryptoException {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }
 
    /**
     * 
     * @param key
     * @param inputFile
     * @param outputFile
     * @throws CryptoException
     */
    public static void decrypt(Key key, File inputFile, File outputFile) throws CryptoException {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }
    
    /**
     * 
     * @param algorithm
     * @return SecretKey
     * @throws Exception
     */
    public static SecretKey generateRandomSecretKey(String algorithm) throws Exception{
        KeyGenerator keyGen=KeyGenerator.getInstance(algorithm);
        keyGen.init(128);
        SecretKey key=keyGen.generateKey();
        return key;
    }
    
    /**
     * 
     * @return Key
     * @throws Exception
     */
    private static Key chkKeyFile() throws Exception{
    		SecretKey aKey = null;
        File file = new File(System.getProperty(CommonConst.USER_DIR_PROP_KEY)+File.separator+CommonConst.LIB_DIR+File.separator+CommonConst.SECRET_KEY_FILE);
        Debug.trace(SUB_SYSTEM, CommonConst.DEVELOPING_MODE, file.getAbsolutePath());
        if(!file.exists()){
        		aKey = generateRandomSecretKey(CommonConst.AES);
            FileHandler.writeSerFile(aKey, System.getProperty(CommonConst.USER_DIR_PROP_KEY)+File.separator+CommonConst.LIB_DIR, CommonConst.SECRET_KEY_FILE);
        }else aKey = (SecretKey) FileHandler.readSerFile(System.getProperty(CommonConst.USER_DIR_PROP_KEY)+File.separator+CommonConst.LIB_DIR+File.separator+CommonConst.SECRET_KEY_FILE);
        
        return aKey;
    }
 
    /**
     * 
     * @param cipherMode
     * @param sk
     * @param inputFile
     * @param outputFile
     */
    private static void doCrypto(int cipherMode, Key sk, File inputFile,File outputFile) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, sk);
             
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
            byte[] outputBytes = cipher.doFinal(inputBytes);
             
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
             
            inputStream.close();
            outputStream.close();
             
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * 
     * @param object
     * @param ostream
     * @throws Exception
     */
    public static void encryptObj(Serializable object, OutputStream ostream) throws Exception {
	    try {
	    		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
	    		// Create cipher
	        synchronized(cipher){
                cipher.init(Cipher.ENCRYPT_MODE, chkKeyFile());
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
    
    /**
     * 
     * @param istream
     * @return Object
     * @throws Exception
     */
	public static Object decryptObj(InputStream istream) throws Exception {
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		synchronized(cipher){
			cipher.init(Cipher.DECRYPT_MODE, chkKeyFile());

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
    
	/**
	 * 
	 * @param key
	 * @param in
	 * @param out
	 * @throws Exception
	 */
    public static void encryptStream(Key key, InputStream in, OutputStream out)  throws Exception{
    		byte[] buf = new byte[1024];
    		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        out = new javax.crypto.CipherOutputStream(out, cipher);

        int numRead = 0;
        while ((numRead = in.read(buf)) >= 0) {
        		out.write(buf, 0, numRead);
        }
        out.close();
    }

    /**
     * 
     * @param sk
     * @param in
     * @param out
     * @throws Exception
     */
    public static void decryptStream(Key sk, InputStream in, OutputStream out)  throws Exception{
	    	byte[] buf = new byte[1024];
	    	Cipher cipher = Cipher.getInstance(TRANSFORMATION);
	    	cipher.init(Cipher.ENCRYPT_MODE, sk);
        in = new javax.crypto.CipherInputStream(in, cipher);

        int numRead = 0;
        while ((numRead = in.read(buf)) >= 0) {
        		out.write(buf, 0, numRead);
        }
//        out.close();
      }
    
    /**
     * 
     * @param password
     * @return KeyPair
     */
    public static KeyPair getKeyPair(String password) {
	    try {   
	    		ByteArrayInputStream bis = new ByteArrayInputStream(getSuperSeed(password));
		    ObjectInput in = new ObjectInputStream(bis);
		    SecureRandom sr = (SecureRandom)in.readObject(); 

	        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(CommonConst.RSA);
	        keyGen.initialize(1024, sr);
	        KeyPair keyPair = keyGen.generateKeyPair();
	        return keyPair;
	    } catch (Exception e) {
	        Debug.trace(SUB_SYSTEM, CommonConst.OPERATION_MODE, "Failed to generate key pair!");
	        e.printStackTrace();
	    }
	    return null;
	}
	
    /**
     * 
     * @param password
     * @return byte[]
     * @throws IOException
     */
	private static byte[] getSuperSeed(String password) throws IOException {
		byte [] ret = null;
		SecureRandom sr = new SecureRandom(password.getBytes());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(bos);   
		out.writeObject(sr);
		ret = bos.toByteArray();
		return ret;
	}
}
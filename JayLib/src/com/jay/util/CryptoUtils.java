package com.jay.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

import org.bouncycastle.crypto.CryptoException;
 
/**
 * A utility class that encrypts or decrypts a file.
 * @author www.codejava.net
 *
 */
public class CryptoUtils {
    private static final String TRANSFORMATION = "AES";
 
    public static void encrypt(Key key, File inputFile, File outputFile)
            throws CryptoException {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }
 
    public static void decrypt(Key key, File inputFile, File outputFile)
            throws CryptoException {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }
    
    public static SecretKey generateRandomSecretKey(String algorithm) throws Exception{
        KeyGenerator keyGen=KeyGenerator.getInstance(algorithm);
        keyGen.init(128);
        SecretKey key=keyGen.generateKey();
        return key;
    }
    
    public static Key chkKeyFile() throws Exception{
    	SecretKey aKey = null;
        File file = new File(System.getProperty(CommonConst.USER_DIR_PROP_KEY)+File.separator+CommonConst.LIB_DIR+File.separator+CommonConst.SECRET_KEY_FILE);
        System.out.println(file.getAbsolutePath());
        if(!file.exists()){
        	System.out.println(file.exists());
        	aKey = generateRandomSecretKey(CommonConst.AES);
            FileHandler.writeSerFile( aKey, System.getProperty(CommonConst.USER_DIR_PROP_KEY)+File.separator+CommonConst.LIB_DIR, CommonConst.SECRET_KEY_FILE);
        }else aKey = (SecretKey) FileHandler.readSerFile(System.getProperty(CommonConst.USER_DIR_PROP_KEY)+File.separator+CommonConst.LIB_DIR+File.separator+CommonConst.SECRET_KEY_FILE);
        
        return aKey;
    }
 
    private static void doCrypto(int cipherMode, Key sk, File inputFile,
            File outputFile) throws CryptoException {
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
             
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }
    
    public static void encryptObj(Serializable object, OutputStream ostream, Key key) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
	    try {
	    	Cipher cipher = Cipher.getInstance(TRANSFORMATION);
	    	// Create cipher
	        synchronized(cipher){
                cipher.init(Cipher.ENCRYPT_MODE, key);
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

	public static Object decryptObj(InputStream istream,  Key key) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		synchronized(cipher){
			cipher.init(Cipher.DECRYPT_MODE, key);

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
    
    public static void encrypt(Key key, InputStream in, OutputStream out)  throws Exception{
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

    public static void decrypt(Key sk, InputStream in, OutputStream out)  throws Exception{
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
}
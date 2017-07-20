package com.jay.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
    
    public static void encryptStream(InputStream in, OutputStream out, Key sk)  throws Exception{
    	byte[] buf = new byte[1024];
    	Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, sk);
        
        out = new javax.crypto.CipherOutputStream(out, cipher);

        int numRead = 0;
        while ((numRead = in.read(buf)) >= 0) {
          out.write(buf, 0, numRead);
        }
        out.close();
    }

    public static void decryptStream(InputStream in, OutputStream out, Key sk)  throws Exception{
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
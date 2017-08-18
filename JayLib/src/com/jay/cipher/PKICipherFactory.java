package com.jay.cipher;

import java.io.File;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.jay.util.CommonConst;
import com.jay.util.FileHandler;

public class PKICipherFactory {
	KeyPair pair = null;
	Cipher cipher = null;
	
	public PKICipherFactory(){
    	try {
			chkKeyFile();
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private final void init(){
    	try {
    		cipher = Cipher.getInstance(CommonConst.RSA_STRING);
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
    
    private void chkKeyFile() throws Exception{
    	File file = new File(System.getProperty(CommonConst.USER_DIR_PROP_KEY)+File.separator+CommonConst.LIB_DIR+File.separator+CommonConst.PKI_KEY_FILE);
//        System.out.println(file.getAbsolutePath());
        if(!file.exists()){
        	pair = generateRandomPublicKeyPair();
    		FileHandler.writeSerFile( pair, System.getProperty(CommonConst.USER_DIR_PROP_KEY)+File.separator+CommonConst.LIB_DIR, CommonConst.PKI_KEY_FILE);
        }else {
        	pair = (KeyPair) FileHandler.readSerFile(System.getProperty(CommonConst.USER_DIR_PROP_KEY)+File.separator+CommonConst.LIB_DIR+File.separator+CommonConst.PKI_KEY_FILE);
        }
    }
	
	public synchronized final Key getPubKey() {
		// TODO Auto-generated method stub
		return pair.getPublic();
	}

	public synchronized final Cipher getCipher() {
		// TODO Auto-generated method stub
		return cipher;
	}

	public synchronized final Key getPrivateKey() {
		// TODO Auto-generated method stub
		return pair.getPrivate();
	}
	
	
	private KeyPair generateRandomPublicKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException{
    	SecureRandom random = new SecureRandom();
        KeyPairGenerator generator = KeyPairGenerator.getInstance(CommonConst.RSA_STRING);
        generator.initialize(CommonConst.ENC_BYTE_64, random); 
        return generator.generateKeyPair();        
    }
}

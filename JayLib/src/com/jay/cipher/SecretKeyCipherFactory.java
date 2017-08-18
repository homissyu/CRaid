package com.jay.cipher;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.jay.util.CommonConst;
import com.jay.util.FileHandler;

public class SecretKeyCipherFactory {
	private SecretKey sk;
	private Cipher cipher = null;
	private SecretKeySpec keySpec;

	public synchronized final Cipher getCipher() {
		return cipher;		
	}
	
	public synchronized final SecretKeySpec getKeySpec() {
		return keySpec;		
	}
	
	public SecretKeyCipherFactory(){
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
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            keySpec = new SecretKeySpec(sk.getEncoded(), CommonConst.AES);
            cipher=Cipher.getInstance(CommonConst.AES); 
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
    
    private void chkKeyFile() throws Exception{
    	File file = new File(System.getProperty(CommonConst.USER_DIR_PROP_KEY)+File.separator+CommonConst.LIB_DIR+File.separator+CommonConst.SECRET_KEY_FILE);
//        System.out.println(file.getAbsolutePath());
        if(!file.exists()){
        	sk = generateRandomSecretKey(CommonConst.AES);
            FileHandler.writeSerFile( sk, System.getProperty(CommonConst.USER_DIR_PROP_KEY)+File.separator+CommonConst.LIB_DIR, CommonConst.SECRET_KEY_FILE);
        }else {
        	sk = (SecretKey) FileHandler.readSerFile(System.getProperty(CommonConst.USER_DIR_PROP_KEY)+File.separator+CommonConst.LIB_DIR+File.separator+CommonConst.SECRET_KEY_FILE);
        }
    }

	private static SecretKey generateRandomSecretKey(String algorithm) throws NoSuchAlgorithmException {
		KeyGenerator keyGen=KeyGenerator.getInstance(algorithm);
        keyGen.init(CommonConst.ENC_BYTE_16);
        SecretKey key=keyGen.generateKey();
        return key;
	}
}

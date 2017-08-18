package com.jay.cipher;

import java.io.File;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.jay.util.CommonConst;
import com.jay.util.FileHandler;

public class PBECipherFactory {
	
	private SecretKeyFactory factory = null;
	private KeySpec pbeKeySpec = null;
	private SecretKey sk = null;;
	private Cipher cipher = null;
	private IvParameterSpec ivParameterSpec = null;

	private byte[] salt = {
		    (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
		    (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
		};
	
	
//	Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	
	public Key getSecretKey() {
		// TODO Auto-generated method stub
		return sk;
	}
	
	public synchronized final Cipher getCipher() {
		return cipher;		
	}
	
	public synchronized final KeySpec getKeySpec() {
		return pbeKeySpec;		
	}
	
	public PBECipherFactory(String password){
    	try {
			chkKeyFile(password);
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	
	
    private final void init(){
    	try {
    		cipher = Cipher.getInstance(CommonConst.AES_CBC_PKCS5);
    		    		
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
    
    private boolean chkKeyFile(String password) throws Exception{
    	boolean ret;
        File file = new File(System.getProperty(CommonConst.USER_DIR_PROP_KEY)+File.separator+CommonConst.LIB_DIR+File.separator+CommonConst.PBE_KEY_FILE);
//        System.out.println(file.getAbsolutePath());
        if(!file.exists()){
        	sk = generateRandomSecretKey(password, CommonConst.AES);
            FileHandler.writeSerFile( sk, System.getProperty(CommonConst.USER_DIR_PROP_KEY)+File.separator+CommonConst.LIB_DIR, CommonConst.PBE_KEY_FILE);
            ret = true;
        }else {
        	sk = (SecretKey) FileHandler.readSerFile(System.getProperty(CommonConst.USER_DIR_PROP_KEY)+File.separator+CommonConst.LIB_DIR+File.separator+CommonConst.PBE_KEY_FILE);
        	ret = true;
        }
        return ret;
    }

	private SecretKey generateRandomSecretKey(String password, String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException {
		factory = SecretKeyFactory.getInstance(CommonConst.PBKDF);
		pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, CommonConst.ITERATION_CNT, CommonConst.ENC_BYTE_16);
		SecretKey tmp = factory.generateSecret(pbeKeySpec);
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), algorithm);
		return secret;
		
	}
}

package com.jay.util;

import java.io.Serializable;
import java.util.ArrayList;

import javax.crypto.SecretKey;

public class MetaCraid implements Serializable {
	private static final long serialVersionUID = -2518143671167959230L;
	public static final String SERIAL_ID = CommonUtil.makeUniqueID(16);
	private String mId = null;
	private ArrayList<Integer> mSaltLength = new ArrayList<Integer>();
	private ArrayList<Integer> mSaltPos = new ArrayList<Integer>();
	private String mOriginFilePath = null;
	private ArrayList<String> mSplitFileNames = new ArrayList<String>();
	private int mOriginFileType = CommonConst.ASCII;
	private ArrayList<ManipulationInfo> mManipulationInfo = new ArrayList<ManipulationInfo>();
	private int mOperationType = CommonConst.ENCRYPT;
	private SecretKey mSecretKey = null; 
	
	public MetaCraid() {
		try {
			mSecretKey = JayCipher.generateRandomSecretKey(CommonConst.AES);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public synchronized final SecretKey getSecretKey() {
		return mSecretKey;
	}
	public synchronized final void setSecretKey(SecretKey sk) {
		this.mSecretKey = sk;
	}
	public synchronized final int getOperationType() {
		return mOperationType;
	}
	public synchronized final void setOperationType(int mOperationType) {
		this.mOperationType = mOperationType;
	}
	public synchronized final ArrayList<ManipulationInfo> getManipulationInfo() {
		return mManipulationInfo;
	}
	public synchronized final void setManipulationInfo(ArrayList<ManipulationInfo> manipulationInfo) {
		this.mManipulationInfo = manipulationInfo;
	}
	public synchronized final String getId() {
		return mId;
	}
	public synchronized final void setId(String mId) {
		this.mId = mId;
	}
	public synchronized final ArrayList<Integer> getSaltLength() {
		return mSaltLength;
	}
	public synchronized final void setSaltLength(ArrayList<Integer> mSaltLength) {
		this.mSaltLength = mSaltLength;
	}
	public synchronized final ArrayList<Integer> getSaltPos() {
		return mSaltPos;
	}
	public synchronized final void setSaltPos(ArrayList<Integer> mSaltPos) {
		this.mSaltPos = mSaltPos;
	}
	public synchronized final String getOriginFilePath() {
		return mOriginFilePath;
	}
	public synchronized final void setOriginFilePath(String mOriginFilePath) {
		this.mOriginFilePath = mOriginFilePath;
	}
	public synchronized final ArrayList<String> getSplitFileNames() {
		return mSplitFileNames;
	}
	public synchronized final void setSplitFileNames(ArrayList<String> mSplitFileNames) {
		this.mSplitFileNames = mSplitFileNames;
	}
	public synchronized final int getOriginFileType() {
		return mOriginFileType;
	}
	public synchronized final void setOriginFileType(int mOriginFileType) {
		this.mOriginFileType = mOriginFileType;
	}
//	public synchronized final String getmOriginFileName() {
//		return mOriginFileName;
//	}
//	public synchronized final void setmOriginFileName(String mOriginFileName) {
//		this.mOriginFileName = mOriginFileName;
//	}
	
	public String toString() {
		StringBuffer retBuf = new StringBuffer();
		
		retBuf.append(CommonConst.ID);
		retBuf.append("=");
		retBuf.append(this.getId());
		retBuf.append(System.lineSeparator());
		retBuf.append(CommonConst.SALT_LENGTH);
		retBuf.append("=");
		retBuf.append(this.getSaltLength());
		retBuf.append(System.lineSeparator());
		retBuf.append(CommonConst.SALT_POSITION);
		retBuf.append("=");
		retBuf.append(this.getSaltPos());
		retBuf.append(System.lineSeparator());
		retBuf.append(CommonConst.FILE_TYPE);
		retBuf.append("=");
		retBuf.append(this.getOriginFileType());
		retBuf.append(System.lineSeparator());
		retBuf.append(CommonConst.ORIGIN_FILE_PATH);
		retBuf.append("=");
		retBuf.append(this.getOriginFilePath());
		retBuf.append(System.lineSeparator());
		retBuf.append(CommonConst.SPLIT_FILE_NAMES);
		retBuf.append("=");
		retBuf.append(this.getSplitFileNames());
		retBuf.append(System.lineSeparator());
		retBuf.append(CommonConst.SYMMETRIC_KEY_STR);
		retBuf.append("=");
		retBuf.append(this.getSecretKey());
		retBuf.append(CommonConst.MANIPULATION_INFO);
		retBuf.append("=");
		retBuf.append(this.getManipulationInfo());
		
		return retBuf.toString();
	}
}

package com.jay.craid;

import java.io.Serializable;
import java.util.ArrayList;

import javax.crypto.SecretKey;

import com.jay.util.CommonConst;

public class MetaCraid implements Serializable {
	private static final long serialVersionUID = -2518143671167959230L;
	private String mId = null;
	private ArrayList<Integer> mSplitRatio = new ArrayList<Integer>();
	private String mOriginFilePath = null;
	private ArrayList<String> mSplitFileNames = new ArrayList<String>();
	private int mOriginFileType = CommonConst.ASCII;
	private boolean mOperationType = CommonConst.ENCRYPT;
	private SecretKey mSecretKey = null; 
	private String mParityFileName = null;
	private byte[] mRemainingBytes = null;
	private boolean mRaidType = false;
	
	/**
	 * 
	 * @return boolean
	 */
	public synchronized final boolean isRaidType() {
		return mRaidType;
	}

	/**
	 * 
	 * @param mRaidType
	 */
	public synchronized final void setRaidType(boolean mRaidType) {
		this.mRaidType = mRaidType;
	}
	
	/**
	 * 
	 * @return byte[]
	 */
	public synchronized final byte[] getRemainingBytes() {
		return mRemainingBytes;
	}

	/**
	 * 
	 * @param mRemainingBytes
	 */
	public synchronized final void setRemainingBytes(byte[] mRemainingBytes) {
		this.mRemainingBytes = mRemainingBytes;
	}
	
	/**
	 * 	
	 * @return SecretKey
	 */
	public synchronized final SecretKey getSecretKey() {
		return mSecretKey;
	}

	/**
	 * 
	 * @param sk
	 */
	public synchronized final void setSecretKey(SecretKey sk) {
		this.mSecretKey = sk;
	}
	
	/**
	 * 
	 * @return boolean
	 */
	public synchronized final boolean getOperationType() {
		return mOperationType;
	}
	
	/**
	 * 
	 * @param mOperationType
	 */
	public synchronized final void setOperationType(boolean mOperationType) {
		this.mOperationType = mOperationType;
	}
	
	/**
	 * 
	 * @return String
	 */
	public synchronized final String getId() {
		return mId;
	}
	
	/**
	 * 
	 * @param mId
	 */
	public synchronized final void setId(String mId) {
		this.mId = mId;
	}
	
	/**
	 * 
	 * @return ArrayList<Integer>
	 */
	public synchronized final ArrayList<Integer> getSplitRatio() {
		return mSplitRatio;
	}
	
	/**
	 * 
	 * @param mSplitRatio
	 */
	public synchronized final void setSplitRatio(ArrayList<Integer> mSplitRatio) {
		this.mSplitRatio = mSplitRatio;
	}
	
	/**
	 * 
	 * @return String
	 */
	public synchronized final String getOriginFilePath() {
		return mOriginFilePath;
	}
	
	/**
	 * 
	 * @param mOriginFilePath
	 */
	public synchronized final void setOriginFilePath(String mOriginFilePath) {
		this.mOriginFilePath = mOriginFilePath;
	}
	
	/**
	 * 
	 * @return ArrayList<String>
	 */
	public synchronized final ArrayList<String> getSplitFileNames() {
		return mSplitFileNames;
	}
	
	/**
	 * 
	 * @param mSplitFileNames
	 */
	public synchronized final void setSplitFileNames(ArrayList<String> mSplitFileNames) {
		this.mSplitFileNames = mSplitFileNames;
	}
	
	/**
	 * 
	 * @return int
	 */
	public synchronized final int getOriginFileType() {
		return mOriginFileType;
	}
	
	/**
	 * 
	 * @param mOriginFileType
	 */
	public synchronized final void setOriginFileType(int mOriginFileType) {
		this.mOriginFileType = mOriginFileType;
	}
	
	/**
	 * 
	 * @return String
	 */
	public String getParityFileName() {
		return mParityFileName;
	}
	
	/**
	 * 
	 * @param mParityFileName
	 */
	public void setParityFileName(String mParityFileName) {
		this.mParityFileName = mParityFileName;
	}
	
	/**
	 * @return String
	 */
	public String toString() {
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(CommonConst.ID);
		retBuf.append("=");
		retBuf.append(this.getId());
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
		retBuf.append(CommonConst.SPLIT_RATIO);
		retBuf.append("=");
		retBuf.append(this.getSplitRatio());
		retBuf.append(System.lineSeparator());
		retBuf.append(CommonConst.ENCRYPTED);
		retBuf.append("=");
		retBuf.append(this.getOperationType());
		retBuf.append(System.lineSeparator());
		retBuf.append(CommonConst.SYMMETRIC_KEY_STR);
		retBuf.append("=");
		retBuf.append(this.getSecretKey());
		retBuf.append(System.lineSeparator());
		retBuf.append(CommonConst.IS_RAID);
		retBuf.append("=");
		retBuf.append(this.isRaidType());
		retBuf.append(System.lineSeparator());
		retBuf.append(CommonConst.PARITY_FILE_NAME);
		retBuf.append("=");
		retBuf.append(this.getParityFileName());
		retBuf.append(System.lineSeparator());
		retBuf.append(CommonConst.REMAINING_BYTES_LENGTH);
		retBuf.append("=");
		retBuf.append(this.getRemainingBytes().length);
		return retBuf.toString();
	}
}

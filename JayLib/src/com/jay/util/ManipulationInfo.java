package com.jay.util;

import java.io.Serializable;
import java.util.ArrayList;

public class ManipulationInfo implements Serializable {
	private static final long serialVersionUID = -2518146671167859230L;
	private String mFileName = null;
	

	private ArrayList<Integer> mManipulationPosPerFile = new ArrayList<Integer>();
	private ArrayList<Integer> mManipulationLengthPerFile = new ArrayList<Integer>();
	private int mManipulationCntPerFile =  0;
	
	public ManipulationInfo(String fileName) {
		mFileName = fileName;
	}
	
	public synchronized final String getFileName() {
		return mFileName;
	}
	
	public synchronized final ArrayList<Integer> getManipulationPosPerFile() {
		return mManipulationPosPerFile;
	}

	public synchronized final void setManipulationPosPerFile(ArrayList<Integer> mManipulationPosPerFile) {
		this.mManipulationPosPerFile = mManipulationPosPerFile;
	}

	public synchronized final ArrayList<Integer> getManipulationLengthPerFile() {
		return mManipulationLengthPerFile;
	}

	public synchronized final void setManipulationLengthPerFile(ArrayList<Integer> mManipulationLengthPerFile) {
		this.mManipulationLengthPerFile = mManipulationLengthPerFile;
	}

	public synchronized final int getManipulationCntPerFile() {
		return mManipulationCntPerFile;
	}

	public synchronized final void setManipulationCntPerFile(int mManipulationCnt) {
		this.mManipulationCntPerFile = mManipulationCnt;
	}
	
	public String toString() {
		StringBuffer retBuf = new StringBuffer();
		
		retBuf.append(CommonConst.SPLIT_FILE_NAME);
		retBuf.append("=");
		retBuf.append(this.getFileName());
		retBuf.append(System.lineSeparator());
		retBuf.append(CommonConst.MANIPULATION_COUNT);
		retBuf.append("=");
		retBuf.append(this.getManipulationCntPerFile());
		retBuf.append(System.lineSeparator());
		retBuf.append(CommonConst.MANIPULATION_POSITION);
		retBuf.append("=");
		retBuf.append(this.getManipulationPosPerFile());
		retBuf.append(System.lineSeparator());
		retBuf.append(CommonConst.MANIPULATION_LENGTH);
		retBuf.append("=");
		retBuf.append(this.getManipulationLengthPerFile());
		retBuf.append(System.lineSeparator());
		
		return retBuf.toString();
	}
}

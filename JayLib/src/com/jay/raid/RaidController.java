package com.jay.raid;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import com.jay.craid.MetaCraid;
import com.jay.util.CommonConst;
import com.jay.util.CommonUtil;
import com.jay.util.Debug;
import com.jay.util.FileHandler;

public class RaidController {
	
	private static ArrayList <byte[]> splitBufList = new ArrayList<byte[]>();
	FileHandler fh = new FileHandler();
	String mSubSystem = (this.getClass()).getCanonicalName();
	public RaidController() {
		Debug.addSubsystems(mSubSystem);
	}
	
	/**
	 * 
	 * @param fileNames
	 * @throws IOException
	 */
	private void doRaid(ArrayList<String> fileNames) throws IOException {
		Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, "doRaid fileNames:"+fileNames);
		int bufSize = 0;
		RandomAccessFile raf = null;
		for(int i=0;i<fileNames.size();i++) {
			raf = new RandomAccessFile(fileNames.get(i), "r");
			if(i==0) bufSize = (int) raf.length();
			byte[] buf = new byte[bufSize];
		    raf.read(buf);
		    splitBufList.add(buf);
		}
		
		byte [] parityBuf = new byte[bufSize];

		for(int i=0;i<bufSize;i++) {
			for(int k=0;k<splitBufList.size()-1;k++) {
				if(k==0) parityBuf[i] = (byte) ((splitBufList.get(k))[i]^(splitBufList.get(k+1))[i]);
				else parityBuf[i] ^= (byte)(splitBufList.get(k+1))[i];
			}
			Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, String.valueOf((char)parityBuf[i]));
		}
		
		splitBufList.add(parityBuf);	
		Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, "new String(parityBuf):"+new String(parityBuf));
	}
	
	/**
	 * 
	 * @param sPath
	 * @param meta
	 * @return boolean
	 */
	@SuppressWarnings("finally")
	public boolean backup(String sPath, MetaCraid meta) {
		boolean ret = false;
		try {
			meta.setParityFileName(CommonUtil.makeUniqueID(24));
			doRaid(meta.getSplitFileNames());
			fh.writeFile(meta.getParityFileName(), splitBufList.get(splitBufList.size()-1), sPath.substring(0,sPath.lastIndexOf(File.separator)+1));
			Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, "new String(splitBufList.get(splitBufList.size()-1)):"+new String(splitBufList.get(splitBufList.size()-1)));
			ret = true;
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			return ret;
		}
	}
	
	/**
	 * 
	 * @param sPath
	 * @param meta
	 * @return boolean
	 */
	@SuppressWarnings({ "finally", "unchecked" })
	public boolean recover(String sPath, MetaCraid meta) {
		boolean ret = false;
		ArrayList<String> aTargetPaths = new ArrayList<String>();
		ArrayList<String> tempList = (ArrayList<String>)(meta.getSplitFileNames().clone());
		Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, "tempList:"+tempList);
		String targetFileName = null;
		try {
			fh.recursiveFind(Paths.get(sPath.substring(0,sPath.lastIndexOf(File.separator)+1)), p -> {
				aTargetPaths.add(p.toString());
			});
			Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, "aTargetPaths:"+aTargetPaths);
			Iterator<String> it = tempList.iterator();
			while(it.hasNext()) {
				targetFileName = it.next();
				if(!aTargetPaths.contains(targetFileName)) {
					Debug.trace(mSubSystem, CommonConst.DEBUG_MODE, "missing file:"+targetFileName);
					break;
				}
			}	
			tempList.remove(targetFileName);
			tempList.add(sPath.substring(0,sPath.lastIndexOf(File.separator)+1)+File.separator+meta.getParityFileName());
			doRaid(tempList);
			
			fh.writeFile(targetFileName, splitBufList.get(splitBufList.size()-1));
			ret = true;
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			return ret;
		}
	}
}

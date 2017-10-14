package com.jay.craid;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import com.jay.raid.RaidController;
import com.jay.util.CommonConst;
import com.jay.util.CommonUtil;
import com.jay.util.CryptoUtils;
import com.jay.util.Debug;
import com.jay.util.FileHandler;

/**
 * 
 * @author karl
 *
 */

public class CRaid {
	private String mSubSystem = (this.getClass()).getCanonicalName();
	private FileHandler fh = null;
	
	public CRaid() {
		Debug.addSubsystems(mSubSystem);
		fh = new FileHandler();
	}
	
	/**
	 * 
	 * @param sSourceFilePath
	 * @param aSplitRatio
	 * @param doEncrypt
	 * @param doRaid
	 */
	public void splitFile(String sSourceFilePath, String sSplitPath, ArrayList<Integer> aSplitRatio, boolean doEncrypt, boolean doRaid, String sMetaFilePath) {
		// TODO Auto-generated method stub
		try {
    			MetaCraid meta = splitOperation(sSourceFilePath, sSplitPath, aSplitRatio, doEncrypt, doRaid);
    			if(doRaid) {
    				RaidController rc = new RaidController();
		    		if(rc.backup(CommonConst.PARITY_PATH, meta))
		    			fh.writeSerEncFile(meta, sMetaFilePath);
		    		else throw new Exception("Failed RAID");
	    		}else fh.writeSerEncFile(meta, sMetaFilePath);
    			Debug.trace(mSubSystem, CommonConst.QA_MODE, meta.toString(), Thread.currentThread().getStackTrace()[1].getLineNumber());
	    } catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param sMetaFilePath
	 * @param sTargetFilePath
	 */
	public void mergeFile(String sTargetFilePath, String sSplitPath, String sMetaFilePath) {
		// TODO Auto-generated method stub
		try {
			MetaCraid meta = (MetaCraid)FileHandler.readSerEncFile(sMetaFilePath);
			Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, "sTargetFilePath:"+sTargetFilePath, Thread.currentThread().getStackTrace()[1].getLineNumber());
			Debug.trace(mSubSystem, CommonConst.QA_MODE, meta.toString(), Thread.currentThread().getStackTrace()[1].getLineNumber());
    		if(meta.isRaidType()) {
    			RaidController rc = new RaidController();
	    		if(rc.recover(sSplitPath, meta))
	    			mergeOperation(meta, sTargetFilePath, meta.getOperationType());
	    		else throw new Exception("Failed Recover from RAID");
    		}else mergeOperation(meta, sTargetFilePath, meta.getOperationType());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
     * @param sSourcePath
     * @param sSplitRatio
     * @param isEncrypt
     * @param doRaid
     * @return
     */
    private MetaCraid splitOperation(String sSourcePath, String sSplitPath, ArrayList<Integer> sSplitRatio, boolean isEncrypt, boolean doRaid) {
		MetaCraid meta = null;
		ArrayList<String> splitFileNames = null;
		BufferedOutputStream bw = null;	
		RandomAccessFile raf = null;
		File sourceFile = null;
		File encryptedFile = null;
		String encryptedFilePath = null;
		
		try{
			encryptedFilePath = sSourcePath;
			if(isEncrypt) encryptedFilePath = sSourcePath+CommonConst.CURRENT_DIR+CommonConst.ENCRYPTED;
			sourceFile = new File(sSourcePath);
			encryptedFile = new File(encryptedFilePath);
			if(!encryptedFile.exists()) encryptedFile.createNewFile();
			splitFileNames = new ArrayList<String>();
			
			meta = new MetaCraid();
			meta.setOriginFileType(CommonConst.BINARY);
    		meta.setOriginFilePath(sSourcePath);
    		meta.setId(CommonUtil.makeUniqueTimeID(CommonConst.ENC_BYTE_16));
    		meta.setOperationType(isEncrypt);
    		meta.setSecretKey(CryptoUtils.generateRandomSecretKey(CommonConst.AES));
    		meta.setRaidType(doRaid);

    		if(isEncrypt)CryptoUtils.encrypt(meta.getSecretKey(), sourceFile, encryptedFile);
    		raf = new RandomAccessFile(encryptedFilePath, "r");
    		long sourceSize = raf.length();
            sourceSize = raf.length();
            calcSplitRatio(sourceSize, sSplitRatio, doRaid);
            meta.setSplitRatio(sSplitRatio);
            Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, "sSplitRatio:"+sSplitRatio, Thread.currentThread().getStackTrace()[1].getLineNumber());
            int iSplitCnt = sSplitRatio.size();
            int maxReadBufferSize = (int) sourceSize;
            if (maxReadBufferSize  > 1024*8) maxReadBufferSize = 1024*8;
            Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, "iSplitCnt:"+iSplitCnt, Thread.currentThread().getStackTrace()[1].getLineNumber());
            Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, "maxReadBufferSize:"+maxReadBufferSize, Thread.currentThread().getStackTrace()[1].getLineNumber());

            long bytesPerSplit = 0 ;
            String aTempFileName = null;
            for(int destIx=0; destIx < iSplitCnt; destIx++) {
            	bytesPerSplit = sSplitRatio.get(destIx);
            	Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, destIx+":bytesPerSplit:"+bytesPerSplit, Thread.currentThread().getStackTrace()[1].getLineNumber());
            	aTempFileName = sSplitPath + File.separator + CommonUtil.makeUniqueID(24);
            	File aTempFile = new File(aTempFileName);
            	if(!aTempFile.exists()) aTempFile.createNewFile();
	 			bw = new BufferedOutputStream(new FileOutputStream(aTempFileName));
	 			
	 			if(bytesPerSplit > maxReadBufferSize) {
	                long numReads = bytesPerSplit/maxReadBufferSize;
	                Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, destIx+":numReads:"+numReads, Thread.currentThread().getStackTrace()[1].getLineNumber());
	                long numRemainingRead = bytesPerSplit % maxReadBufferSize;
	                Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, destIx+":numRemainingRead:"+numRemainingRead, Thread.currentThread().getStackTrace()[1].getLineNumber());

	               for(int i=0; i<numReads; i++) {
	                    fh.readWrite(raf, bw, maxReadBufferSize);
	               }
	               if(numRemainingRead > 0) fh.readWrite(raf, bw, numRemainingRead);
	            }else fh.readWrite(raf, bw, bytesPerSplit);
	            
	 			bw.flush();
	            splitFileNames.add(aTempFileName);
            }
            
            long remainingBytes = sourceSize - raf.getFilePointer();
            Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, "Last:remainingBytes:"+remainingBytes, Thread.currentThread().getStackTrace()[1].getLineNumber());

            if(remainingBytes > 0) {
//            	aTempFileName = sSourcePath.substring(0,sSourcePath.lastIndexOf(File.separator)+1)+CommonUtil.makeUniqueID(24);
//            	bw = new BufferedOutputStream(new FileOutputStream(aTempFileName));
            	
        		byte[] buf = new byte[(int) remainingBytes];
                int val = raf.read(buf);
                if(val != -1) {
            		meta.setRemainingBytes(buf);
                } 
            	
//            	readWrite(raf, bw, remainingBytes);
//                bw.flush();
//                splitFileNames.add(aTempFileName);
//                sSplitRatio.set(sSplitRatio.size(), (int) remainingBytes);
            }
            meta.setSplitFileNames(splitFileNames);
		}catch(Exception e){
			e.printStackTrace();
		}finally {
            try {
                bw.close();
                raf.close();
                if(isEncrypt) encryptedFile.delete();
            } catch (IOException ex) {
        		ex.printStackTrace();
            }
		}
		return meta;
    }

    /**
     * @param fileLength
     * @param sSplitRatio
     * @param doRaid
     */
    private void calcSplitRatio(long fileLength, ArrayList<Integer> sSplitRatio, boolean doRaid){
    	int totalLength = 0;
    	int itemCnt = sSplitRatio.size();
    	int unitLength = 0;
    	Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, "fileLength:"+fileLength, Thread.currentThread().getStackTrace()[1].getLineNumber());
    	Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, "itemCnt:"+itemCnt, Thread.currentThread().getStackTrace()[1].getLineNumber());
    	for(int i=0;i<itemCnt;i++) {
    		totalLength += sSplitRatio.get(i);
    	} 
    	Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, "totalLength:"+totalLength, Thread.currentThread().getStackTrace()[1].getLineNumber());
    	if(doRaid) {
    		unitLength = (int) (fileLength / itemCnt);        	
    	}else{
    		if(totalLength % itemCnt == 0) unitLength = (int) (fileLength / totalLength);
        	else unitLength = (int) ((fileLength/totalLength)+1);
    	}
    	Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, "unitLength:"+unitLength, Thread.currentThread().getStackTrace()[1].getLineNumber());
    	for(int i=0;i<itemCnt;i++) {
    		sSplitRatio.set(i, (doRaid?1:sSplitRatio.get(i)) * unitLength);
    	}
    	Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, "sSplitRatio:"+sSplitRatio, Thread.currentThread().getStackTrace()[1].getLineNumber());
    }
    
    /**
     * @param meta
     * @param sOutPutFilePath
     * @param doEncrypt
     */
    private void mergeOperation(MetaCraid meta, String sOutPutFilePath, boolean doEncrypt){
        ArrayList<String> aSplitFileList = meta.getSplitFileNames();
        Debug.trace(mSubSystem, CommonConst.DEVELOPING_MODE, "aSplitFileList:"+aSplitFileList, Thread.currentThread().getStackTrace()[1].getLineNumber());
        FileOutputStream fOs = null;
        try{
        	File outputFile = new File(sOutPutFilePath);
        	fOs = new FileOutputStream(outputFile);
        	for(int destIx=0; destIx < aSplitFileList.size() ; destIx++) {
            	RandomAccessFile raf = new RandomAccessFile((String)aSplitFileList.get(destIx), "r");
            	fh.readWrite(raf, fOs);
        	}
        	if(meta.getRemainingBytes() != null) fOs.write(meta.getRemainingBytes());
            fOs.flush();
            fOs.close();
            if(doEncrypt)CryptoUtils.decrypt(meta.getSecretKey(), outputFile, outputFile);
        }catch(Exception ex){
    		ex.printStackTrace();
        }finally{
			try {
				if(fOs != null) fOs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
}

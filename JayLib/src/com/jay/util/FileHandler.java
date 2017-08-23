package com.jay.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import org.apache.commons.codec.binary.Base64OutputStream;

/**
 * 
 * @author Jay
 * @date 2006. 6. 19.
 */

public class FileHandler {
    long lFileSize;
    static String sFileName;
    HashMap<File, ArrayList<Integer>> mFileInfoMap = new HashMap<File, ArrayList<Integer>>();
	ArrayList<ArrayList<String>> mFileInfoList = new ArrayList<ArrayList<String>>();
//	static JayCipher jc = new JayCipher();
//	JayCipher userJc = null;
	public FileHandler(){
	}
    

    /**
     * @param sSourcePath
     * @param sTargetPath
     * @param saFileList
     * @throws IOException
     * @throws JayException
     */
    public static void copyFile(String sSourcePath, String sTargetPath, String[] saFileList) throws JayException, IOException {
        for(int i=0;i<saFileList.length;i++) {
            File fInFile = new File(sSourcePath+File.separator+saFileList[i]);
            File fOutFile = new File(sTargetPath+File.separator+saFileList[i]);
            FileInputStream in = null;
            FileOutputStream out = null;
            byte[] buffer;
            int bytes_read;

            try {
                if(!fInFile.exists() || !fInFile.isFile())
                    throw new JayException("FileCopy:no such file or directory:"+"fInFile:"+sSourcePath+File.separator+saFileList[i]);
                if(!fInFile.canRead())
                    throw new JayException("FileCopy:source file is unreadable:"+"fInFile:"+sSourcePath+File.separator+saFileList[i]);
                in = new FileInputStream(fInFile);
                out = new FileOutputStream(fOutFile);

                buffer = new byte[(int)fInFile.length()];
                while(true) {
                    bytes_read = in.read(buffer);
                    if(bytes_read == -1)
                        break;
                }

                out.write(buffer);
                out.flush();
            }catch(Exception e){
            	throw new JayException(e);
            }finally {
                if(in != null) in.close();
                if(out != null) out.close();
            }
        }        
    }
    
    /**
     * @param sSourcePath
     * @param sTargetPath
     * @throws IOException
     * @throws JayException
     */
    public static void copyFile(String sSourcePath, String sTargetPath) throws JayException {
        File fInFile = new File(sSourcePath);
        File fOutFile = new File(sTargetPath);
        FileInputStream in = null;
        FileOutputStream out = null;
        byte[] buffer;
        int bytes_read;

        try {
            if(!fInFile.exists() || !fInFile.isFile())
                throw new JayException("FileCopy:no such file or directory:"+sSourcePath);
            if(!fInFile.canRead())
                throw new JayException("FileCopy:source file is unreadable:"+sSourcePath);
            in = new FileInputStream(fInFile);
            out = new FileOutputStream(fOutFile);

            buffer = new byte[(int)fInFile.length()];
            while(true) {
                bytes_read = in.read(buffer);
                if(bytes_read == -1)
                    break;
            }

            out.write(buffer);
            out.flush();
        }catch(Exception e){
            throw new JayException(e);
        }finally {
            try {
            	if(in != null) in.close();
                if(out != null) out.close();
            } catch (IOException ex) {
                throw new JayException(ex);
            }
        }
    }
    
    public static void copy2Base64File(String sSourcePath, String sTargetPath, boolean bFlag) throws JayException {
        File fInFile = new File(sSourcePath);
        File fOutFile = new File(sTargetPath);
        FileInputStream in = null;
        Base64OutputStream out = null;
        byte[] buffer;
        int bytes_read;

        try {
            if(!fInFile.exists() || !fInFile.isFile())
                throw new JayException("FileCopy:no such file or directory:"+sSourcePath);
            if(!fInFile.canRead())
                throw new JayException("FileCopy:source file is unreadable:"+sSourcePath);
            in = new FileInputStream(fInFile);
            out = new Base64OutputStream(new FileOutputStream(fOutFile), bFlag);

            buffer = new byte[(int)fInFile.length()];
            while(true) {
                bytes_read = in.read(buffer);
                if(bytes_read == -1)
                    break;
            }

            out.write(buffer);
            out.flush();
        }catch(Exception e){
            throw new JayException(e);
        }finally {
            try {
            	if(in != null) in.close();
                if(out != null) out.close();
            } catch (IOException ex) {
                throw new JayException(ex);
            }
        }
    }
    
    private static MetaCraid splitOperation(String sSourcePath, ArrayList<Integer> sSplitRatio) {
    	MetaCraid meta = null;
    	ArrayList<String> splitFileNames = null;
    	BufferedOutputStream bw = null;	
		RandomAccessFile raf = null;
		File sourceFile = null;
		File encryptedFile = null;
		String encryptedFilePath = null;
		
		try{
			encryptedFilePath = sSourcePath+CommonConst.CURRENT_DIR+CommonConst.ENCRYPTED;
			sourceFile = new File(sSourcePath);
			encryptedFile = new File(encryptedFilePath);
			splitFileNames = new ArrayList<String>();
			
			meta = new MetaCraid();
			meta.setOriginFileType(CommonConst.BINARY);
	    	meta.setOriginFilePath(sSourcePath);
	    	meta.setId(CommonUtil.makeUniqueTimeID());
	    	meta.setOperationType(CommonConst.ENCRYPT);
	    	meta.setSecretKey(CryptoUtils.generateRandomSecretKey(CommonConst.AES));
    		
	    	CryptoUtils.encrypt(meta.getSecretKey(), sourceFile, encryptedFile);
    		raf = new RandomAccessFile(encryptedFilePath, "r");
    		long sourceSize = raf.length();
            sourceSize = raf.length();
            getSplitRatio(sourceSize, sSplitRatio);
            meta.setSplitRatio(sSplitRatio);
            
            int iSplitCnt = sSplitRatio.size();
            int maxReadBufferSize = 8 * 1024;
            long bytesPerSplit = 0 ;
            String aTempFileName = null;
            for(int destIx=0; destIx < (iSplitCnt-1); destIx++) {
            	bytesPerSplit = sSplitRatio.get(destIx);
            	aTempFileName = sSourcePath.substring(0,sSourcePath.lastIndexOf("\\")+1)+CommonUtil.makeUniqueID(24);
     			bw = new BufferedOutputStream(new FileOutputStream(aTempFileName));
     			
     			if(bytesPerSplit > maxReadBufferSize) {
                    long numReads = bytesPerSplit/maxReadBufferSize;
                    long numRemainingRead = bytesPerSplit % maxReadBufferSize;
                    for(int i=0; i<numReads; i++) {
                        readWrite(raf, bw, maxReadBufferSize);
                    }
                    if(numRemainingRead > 0) {
                        readWrite(raf, bw, numRemainingRead);
                    }
                }else {
                    readWrite(raf, bw, bytesPerSplit);
                }
                bw.flush();
                splitFileNames.add(aTempFileName);
            }
            
            long remainingBytes = sourceSize - raf.getFilePointer();

            if(remainingBytes > 0) {
            	aTempFileName = sSourcePath.substring(0,sSourcePath.lastIndexOf("\\")+1)+CommonUtil.makeUniqueID(24);
            	bw = new BufferedOutputStream(new FileOutputStream(aTempFileName));
            	
            	readWrite(raf, bw, remainingBytes);
                bw.flush();
                splitFileNames.add(aTempFileName);
                sSplitRatio.set(sSplitRatio.size()-1, (int) remainingBytes);
            }
            meta.setSplitFileNames(splitFileNames);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally {
            try {
                bw.close();
                raf.close();
                encryptedFile.delete();
            } catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
    	return meta;
    }
    
    private static void getSplitRatio(long fileLength, ArrayList<Integer> sSplitRatio){
    	int totalLength = 0;
    	int itemCnt = sSplitRatio.size();
    	int unitLength = 0;
    	
    	for(int i=0;i<itemCnt;i++) {
    		totalLength = totalLength + sSplitRatio.get(i);
    	} 
    	
    	if(totalLength % itemCnt == 0) unitLength = (int) (fileLength / totalLength);
    	else unitLength = (int) ((fileLength/totalLength)+1);

    	for(int i=0;i<itemCnt;i++) {
    		sSplitRatio.set(i, sSplitRatio.get(i) * unitLength);
    	}
    }
    
    public static void splitFile(String sSourcePath, ArrayList<Integer> sSplitRatio) {
    	try {
    		FileHandler.writeSerEncFile(splitOperation(sSourcePath, sSplitRatio), CommonConst.META_FILE_PATH, CommonConst.META_FILE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void mergeFile(String sMetaFilePath, String sTargetPath) {
    	try {
    		MetaCraid meta = (MetaCraid)readSerEncFile(sMetaFilePath);
    		mergeOperation(meta, sTargetPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
//    private ManipulationInfo makeManipulationInfo(String fileName, int targetLength) {
//    	ManipulationInfo meta = new ManipulationInfo(fileName);
//    	ArrayList<Integer> aTempManipulationPos = new ArrayList<Integer>();
//		ArrayList<Integer> aTempManipulationLength = new ArrayList<Integer>();
//		int iTempManipulationPosLimit = 0;
//		int iTempManipulationCnt = 0;
//		while(true) {
//			int iTempManipulationPos = ThreadLocalRandom.current().nextInt(iTempManipulationPosLimit, targetLength + 1);
//			int iTempManipulationLength = ThreadLocalRandom.current().nextInt(0, CommonConst.MAX_MANIPULATION_LENGTH);
//			iTempManipulationPosLimit = iTempManipulationPos+iTempManipulationLength;
//			if(targetLength<=iTempManipulationPosLimit) break;
//			aTempManipulationLength.add(iTempManipulationLength);
//			aTempManipulationPos.add(iTempManipulationPos);
//			iTempManipulationCnt++;
//		}
//		meta.setManipulationLengthPerFile(aTempManipulationLength);
//		meta.setManipulationPosPerFile(aTempManipulationPos);
//		meta.setManipulationCntPerFile(iTempManipulationCnt);
//		System.out.println(meta);	
//		return meta;
//    }
    
    /**
     * 
     * @param saInputFileList
     * @param sSalt
     * @param sOutPutFilePath
     * @throws JayException
     */
//    @SuppressWarnings("resource")
//	public static void mergeFile4ASCII(MetaCraid meta, String sOutPutFilePath) throws JayException{
//    	File file = null;
//        FileOutputStream fos = null;
//        OutputStreamWriter osw = null;
//        BufferedWriter bw = null;
//        PrintWriter pw = null;
//        
//        try{
////  			MetaCraid meta = (MetaCraid)readSerEncFile(sMetaFilePath);
//  System.out.println(meta);
//  			JayCipher userCipher = new JayCipher(meta.getSecretKey());
//  
//            ArrayList <String>aSplitFileList = meta.getSplitFileNames();
//            
//            file = new File(sOutPutFilePath);
//            if(!file.exists())
//                file.createNewFile(); 
//
//            fos = new FileOutputStream(sOutPutFilePath);
//            osw = new OutputStreamWriter(fos, CommonConst.ENCODING);
//            bw = new BufferedWriter(osw);
//            pw = new PrintWriter(bw);
//            
//            File fInFile = null;
//            FileInputStream fIn = null;
//            byte[] buffer;
//            int bytes_read;
//            StringBuffer asBuf = new StringBuffer();
//            for(int i=0;i<aSplitFileList.size();i++) {
//            	fInFile = new File(aSplitFileList.get(i));
//                
//                if(!fInFile.exists() || !fInFile.isFile())
//                    throw new JayException("FileMerge:no such file or directory:"+"fInFile:"+aSplitFileList.get(i));
//                if(!fInFile.canRead())
//                    throw new JayException("FileMerge:source file is unreadable:"+"fInFile:"+aSplitFileList.get(i));
//                
//                fIn = new FileInputStream(fInFile);
//
//                buffer = new byte[(int)fInFile.length()];
//                while(true) {
//                    bytes_read = fIn.read(buffer);
//                    if(bytes_read == -1)
//                        break;
//                }
//                asBuf.append(new String(buffer));
//            }
//            pw.print(userCipher.decrypt(asBuf.toString()));
//            fIn.close();
//            pw.close();
//            osw.close();
//            bw.close();
//            fos.close();
//            
//        }catch(Exception ex){
//        	ex.printStackTrace();
//            throw new JayException(ex);
//        }
//    }
    
    /**
     * 
     * @param sSourcePath
     * @param iSplitCnt
     * @return
     * @throws JayException
     */
//    public static MetaCraid splitFile4Binary(String sSourcePath, int iSplitCnt) throws JayException{
//    	//try to use MetaCraid
//    	MetaCraid meta = new MetaCraid();
//    	
//    	//try to use MetaCraid
//    	meta.setOriginFileType(CommonConst.BINARY);
//    	meta.setOriginFilePath(sSourcePath);
//    	meta.setId(CommonUtil.makeUniqueTimeID());
//    	meta.setOperationType(CommonConst.ENCRYPT);
//    	
//    	ArrayList<String> splitFileNames = new ArrayList<String>();
//        
//        BufferedOutputStream bw = null;	
//		RandomAccessFile raf = null;
//		File sourceFile = new File(sSourcePath);
//		File encryptedFile = new File(sSourcePath+CommonConst.CURRENT_DIR+CommonConst.ENCRYPTED);
//    	try{
//    		CryptoUtils.encrypt(meta.getSecretKey(), sourceFile, encryptedFile);
//    		raf = new RandomAccessFile(sSourcePath+CommonConst.CURRENT_DIR+CommonConst.ENCRYPTED, "r");
//            long sourceSize = raf.length();
//            long remainingBytes = sourceSize % iSplitCnt;
//            long bytesPerSplit = sourceSize/iSplitCnt ;
//            if((sourceSize % iSplitCnt) != 0)bytesPerSplit = bytesPerSplit+1;
//     		int maxReadBufferSize = 8 * 1024; //8KB
//     		
//     		String aTempFileName = null;
//
//     		for(int destIx=0; destIx < (iSplitCnt-1); destIx++) {
//     			aTempFileName = sSourcePath.substring(0,sSourcePath.lastIndexOf("\\")+1)+CommonUtil.makeUniqueID(24);
//     			bw = new BufferedOutputStream(new FileOutputStream(aTempFileName));
//            	if(bytesPerSplit > maxReadBufferSize) {
//                    long numReads = bytesPerSplit/maxReadBufferSize;
//                    long numRemainingRead = bytesPerSplit % maxReadBufferSize;
//                    for(int i=0; i<numReads; i++) {
//                        readWrite(raf, bw, maxReadBufferSize);
//                    }
//                    if(numRemainingRead > 0) {
//                        readWrite(raf, bw, numRemainingRead);
//                    }
//                }else {
//                    readWrite(raf, bw, bytesPerSplit);
//                }
//                bw.flush();
//                splitFileNames.add(aTempFileName);
//            }
//            if(raf.getFilePointer()!=sourceSize || remainingBytes > 0) {
//            	aTempFileName = sSourcePath.substring(0,sSourcePath.lastIndexOf("\\")+1)+CommonUtil.makeUniqueID(24);
//            	bw = new BufferedOutputStream(new FileOutputStream(aTempFileName));
//            	readWrite(raf, bw, bytesPerSplit-remainingBytes);
//                bw.flush();
//                splitFileNames.add(aTempFileName);
//            }
//            meta.setSplitFileNames(splitFileNames);
//            
//    	}catch(Exception e){
//    		e.printStackTrace();
//    		throw new JayException(e);
//    	}finally {
//            try {
//                bw.close();
//                raf.close();
//                encryptedFile.delete();
//            } catch (IOException ex) {
//                throw new JayException(ex);
//            }
//        }
//    	return meta;
//    }
    
    private static void readWrite(RandomAccessFile raf, OutputStream Os, long numBytes) throws IOException {
        byte[] buf = new byte[(int) numBytes];
        int val = raf.read(buf);
        if(val != -1) {
        	Os.write(buf);
        }   	
    }
    
    private static void readWrite(RandomAccessFile raf, OutputStream Os) throws IOException {
        byte[] buf = new byte[(int)raf.length()];
        int val = raf.read(buf);
        if(val != -1) {
        	Os.write(buf);
        }   	
      }
    
    public static void mergeOperation(MetaCraid meta, String sOutPutFilePath) throws JayException{
    	System.out.println(meta);
        ArrayList<String> aSplitFileList = meta.getSplitFileNames();
    	
        FileOutputStream fOs = null;
        try{
        	File outputFile = new File(sOutPutFilePath);
        	fOs = new FileOutputStream(outputFile);
        	for(int destIx=0; destIx < aSplitFileList.size() ; destIx++) {
            	RandomAccessFile raf = new RandomAccessFile((String)aSplitFileList.get(destIx), "r");
            	readWrite(raf, fOs);
            }
            fOs.flush();
            fOs.close();
            CryptoUtils.decrypt(meta.getSecretKey(), outputFile, outputFile);
        }catch(Exception ex){
        	ex.printStackTrace();
            throw new JayException(ex);
        }finally{
				try {
					if(fOs != null) fOs.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new JayException(e);
				}
        }
    }
    
    /**
     * @param sFileName
     * @param content
     * @param sPath
     * @throws JayException
     */
    public static void writeFile(String sFileName, String content, String sPath) throws JayException{
        String sFilePath = sPath + File.separator + sFileName;
        File file = null;
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;
        try{
            file = new File(sPath);
            if(!file.exists())
                file.mkdirs();
            file = new File(sFilePath);
            if(!file.exists())
                file.createNewFile(); 

            fos = new FileOutputStream(sFilePath);
            osw = new OutputStreamWriter(fos, CommonConst.ENCODING);
            bw = new BufferedWriter(osw);
            pw = new PrintWriter(bw);
            
            pw.print(content);

            pw.close();
            osw.close();
            bw.close();
            fos.close();
        }catch(Exception ex){
            throw new JayException(ex);
        }
    }
    
    /**
     * 
     * @param sFileName
     * @param obj
     * @param sPath
     * @throws JayException
     */
    public static void writeSerFile(Object obj, String sPath, String sFileName) throws JayException{
        ObjectOutputStream oos = null;
        File file = null;
        String sFilePath = sPath + File.separator + sFileName;
        try{
        	file = new File(sPath);
            if(!file.exists())
                file.mkdirs();
            file = new File(sFilePath);
            if(!file.exists())
                file.createNewFile(); 
            
            oos = new ObjectOutputStream(new FileOutputStream(sPath+File.separator+sFileName));
            oos.writeObject(obj);
            oos.close();
        }catch(Exception ex){
        	ex.printStackTrace();
            throw new JayException(ex);
        }
    }
    
    /**
     * 
     * @param sFileName
     * @return oRet
     * @throws JayException
     */
    public static Object readSerFile(String sFileName){
        FileInputStream fileIn = null;
        Object oRet = null;
        File file = null;
        ObjectInputStream ois = null;
        try {
            file = new File(sFileName);
            if(file.exists()){
                fileIn = new FileInputStream(sFileName);
                ois = new ObjectInputStream(fileIn);
                oRet = ois.readObject();
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        } finally {
            try {
                if(fileIn!=null)fileIn.close();
            } catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
        return oRet;
    }
    
    /**
     * 
     * @param sFileName
     * @param obj
     * @param sPath
     * @throws JayException
     */
    private static void writeSerEncFile(Object obj, String sPath, String sFileName) throws JayException{
        ObjectOutputStream oos = null;
        File file = null;
        String sFilePath = sPath + File.separator + sFileName;
        try{
        	file = new File(sPath);
            if(!file.exists())
                file.mkdirs();
            file = new File(sFilePath);
            if(!file.exists())
                file.createNewFile(); 
            
            oos = new ObjectOutputStream(new FileOutputStream(sFilePath));
            CryptoUtils.encryptObj((Serializable) obj, oos, CryptoUtils.chkKeyFile());
            
        }catch(Exception ex){
        	ex.printStackTrace();
            throw new JayException(ex);
        }
    }
    
    /**
     * 
     * @param sFileName
     * @return oRet
     * @throws JayException
     */
    private static Object readSerEncFile(String sFileName){
        FileInputStream fileIn = null;
        Object oRet = null;
        File file = null;
        try {
            file = new File(sFileName);
            if(file.exists()){
                fileIn = new FileInputStream(sFileName);
                ObjectInputStream ois = new ObjectInputStream(fileIn);
                
                oRet = CryptoUtils.decryptObj(ois, CryptoUtils.chkKeyFile());
                
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        } finally {
            try {
                if(fileIn!=null)fileIn.close();
            } catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
        return oRet;
    }
    
    /**
     * @return lFileSize
     */
    public static long getFileSize(String filePath){
    	File file = new File(filePath);
        return file.length();
    }
    
    /**
     * @return sFileName
     */
    public static String getFileName(String filePath){
    	return filePath.substring(0,filePath.lastIndexOf("\\")+1);
    }

    /**
     * @param sFileName
     * @throws Exception
     */
    public static boolean remove(String sFileName) throws Exception{
        File fFile = new File(sFileName);
        return fFile.delete();
    }

    /**
     * @param sFilePath
     * @param sFileName
     * @throws Exception
     */
    public static void remove(String sFilePath, String sFileName) throws Exception{
        File fFile = new File(sFilePath+File.separator+sFileName);
        fFile.delete();
    }
    
    /**
     * @param sFilePath
     * @param vecFileList
     * @throws IOException
     * @throws JayException
     */
    public static void removeFiles(String sFilePath, Vector<?> vecFileList) throws IOException, JayException {
        for(int i=0;i<vecFileList.size();i++) {
            File fFile = new File(sFilePath+File.separator+vecFileList.elementAt(i).toString());
            fFile.delete();
        }   
        File fileDir = new File(sFilePath);
        if(fileDir.list().length == 0)
            fileDir.delete();
    }    
    
    /**
     * @param sSourcePath
     * @param sTargetPath
     * @param saFileList
     * @param saFileUploadList
     * @throws IOException
     * @throws JayException
     */
    public static void copyFile(String sSourcePath, String sTargetPath, String[] saFileList, String[] saFileUploadList) throws IOException, JayException {
        for(int i=0;i<saFileList.length;i++) {
            File fInFile = new File(sSourcePath+File.separator+saFileUploadList[i]);
            File fOutFile = new File(sTargetPath+File.separator+saFileList[i]);
            FileInputStream in = null;
            FileOutputStream out = null;
            byte[] buffer;
            int bytes_read;
            try {
                if(!fInFile.exists() || !fInFile.isFile())
                    throw new JayException("FileCopy:no such file or directory:"+sSourcePath);
                if(!fInFile.canRead())
                    throw new JayException("FileCopy:source file is unreadable:"+sSourcePath);
                in = new FileInputStream(fInFile);
                out = new FileOutputStream(fOutFile);

                buffer = new byte[(int)fInFile.length()];
                while(true) {
                    bytes_read = in.read(buffer);
                    if(bytes_read == -1)
                        break;
                }

                out.write(buffer);
                out.flush();
            }finally {
                if(in != null) in.close();
                if(out != null) out.close();
            }
        }
    }
    
    /**
     * 
     * @param filename
     * @return bResult
     * @throws JayException 
     */
    public static boolean isASCII(String filename) throws JayException {
		 
		boolean bResult = false;
		 
        FileReader inputStream = null;
 
        try {
            inputStream = new FileReader(filename);
 
//            File f = new File(filename);
//            byte [] buffer = new byte[(int)f.length()]; 
        	int c;
            while ((c = inputStream.read()) != -1) {
 
                Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
                
                if (block == Character.UnicodeBlock.BASIC_LATIN || block == Character.UnicodeBlock.GREEK) {
//                         (9)Horizontal Tab (10)Line feed  (11)Vertical tab (13)Carriage return (32)Space (126)tilde
                    if (c==9 || c == 10 || c == 11 || c == 13 || (c >= 32 && c <= 126)) {
                    	bResult = true;
 
//                                (153)Superscript two (160)ϊ  (255) No break space                     
                    } else if (c == 153 || c >= 160 && c <= 255) {
                    	bResult = true;
 
//                                (884)ʹ (885)͵ (890)ͺ (894); (900)' (974)ώ     
                    } else if (c == 884 || c == 885 || c == 890 || c == 894 || c >= 900 && c <= 1019) {
                    	bResult = true;
 
                    } else {                        
                    	bResult = false;
                    break;
                    }
                }                
            }
            
        } catch (Exception ex) {
        	throw new JayException(ex);
        } finally {
 
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                	throw new JayException(ex);
                }
            }
        }
        System.out.println("Is this file ASCII ? : "+ bResult);
        return bResult;
    }
    
    
    /**
     * 
     * @param aFile
     * @return mFileInfoList
     * @throws Exception
     */
    public static ArrayList<ArrayList<String>> getFileInfoList(File aFile) throws Exception{
    	ArrayList<ArrayList<String>> ret = new ArrayList<ArrayList<String>>();
		if (aFile.isDirectory()) {
            String[] children = aFile.list();
            for (int i=0; i<children.length; i++) {            	
            	getFileInfoList(new File(aFile, children[i]));
            }
        } else {
        	ret.add(getFileInfo(aFile));
        }
		return ret;
	}
	
    /**
     * 
     * @param src
     * @return aFileInfoList
     * @throws Exception
     */
	private static ArrayList<String> getFileInfo(File src) throws JayException{
		ArrayList<String> aFileInfoList = new ArrayList<String>();
		aFileInfoList.add(0,src.getName());
		aFileInfoList.add(1,src.getAbsolutePath());
		aFileInfoList.add(2, src.canRead()?"Yes":"No");
		aFileInfoList.add(3, src.canWrite()?"Yes":"No");
		aFileInfoList.add(4, src.canExecute()?"Yes":"No");
		aFileInfoList.add(5, src.isHidden()?"Yes":"No");
		FileInputStream fis = null;
		InputStreamReader isr = null;
		try{
			fis = new FileInputStream(src);
			isr = new InputStreamReader(fis);
			aFileInfoList.add(6,isr.getEncoding());
		}catch(FileNotFoundException ef){
			aFileInfoList.add(6,"\""+src.getName()+"\" is not found.");
			throw new JayException(ef);
		}finally{
			
				try {
					if(isr!=null)isr.close();
					if(fis!=null)fis.close();
				} catch (IOException e) {
					throw new JayException(e);
				}
		}
		Date dt = new Date(src.lastModified());
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.KOREAN);
		aFileInfoList.add(7, df.format(dt));
		
		return aFileInfoList;
	}
}
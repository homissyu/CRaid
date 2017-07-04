package com.jay.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.DateFormat;
import java.util.*;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.mozilla.intl.chardet.HtmlCharsetDetector;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;

/**
 * 
 * @author Jay
 * @date 2006. 6. 19.
 */

public class FileHandler {
    long lFileSize;
    static String sFileName;
    private static final String sSubSystem = "FileHandler";
    HashMap<File, ArrayList<Integer>> mFileInfoMap = new HashMap<File, ArrayList<Integer>>();
	ArrayList<ArrayList<String>> mFileInfoList = new ArrayList<ArrayList<String>>();
	static JayCipher jc = new JayCipher();
	public FileHandler(){
	}
    

    /**
     * @param sSourcePath
     * @param sTargetPath
     * @param saFileList
     * @throws IOException
     * @throws JayException
     */
    public void copyFile(String sSourcePath, String sTargetPath, String[] saFileList) throws JayException, IOException {
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
    public void copyFile(String sSourcePath, String sTargetPath) throws JayException {
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
    
    public void copy2Base64File(String sSourcePath, String sTargetPath, boolean bFlag) throws JayException {
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
    
    /**
     * 
     * @param sSourcePath
     * @param iSplitCnt
     * @return
     * @throws JayException
     */
    public HashMap splitFile4ASCII(String sSourcePath, int iSplitCnt) throws JayException{
    	HashMap ret = new HashMap();
    	ret.put(CommonConst.FILE_TYPE, CommonConst.ASCII);
    	ret.put(CommonConst.FILE_PATH, sSourcePath);
    	ret.put(CommonConst.ID, CommonUtil.makeUniqueTimeID());
    	int iEncArrayLength = 0;
    	File fInFile = new File(sSourcePath);
    	File fOutFile = null;
        FileInputStream fIn = null;
        FileOutputStream fOut = null;
        
        int iByteRead = 0;
        int iBufferSize = 0;
        byte[] encFirstArray = null;
        byte[] encLastArray = null;
         
        ArrayList iSaltLengths = new ArrayList();
        String[] saUniqueKey = new String[iSplitCnt];
        ArrayList iSaltPositions = new ArrayList();
        int iTemp = 0;
//        System.out.println((int)(Math.random() * 1000));
        for(int i=0;i<iSplitCnt;i++){
        	iTemp = (int)(Math.random() * 20);
        	if(iTemp==0) iTemp = 1;     
        	iSaltLengths.add(iTemp);
        	saUniqueKey[i] = CommonUtil.makeUniqueID(iTemp);
        }
        ret.put(CommonConst.SALT_LENGTH, iSaltLengths);
        
        ArrayList<String> splitFileNames = new ArrayList();
        
        for(int i=0;i<iSplitCnt;i++){
        	splitFileNames.add(sSourcePath.substring(0,sSourcePath.lastIndexOf("\\")+1)+CommonUtil.makeUniqueID(24));
        }
        
        ret.put(CommonConst.SPLIT_FILE_NAMES, splitFileNames);
        
        
//for(int i=0;i<saUniqueKey.length;i++){
//	System.out.println("saUniqueKey["+i+"] : "+saUniqueKey[i]);
//}
        
        try {
        	if(!fInFile.exists() || !fInFile.isFile())
                throw new JayException("Can't split file : No such file or directory:"+sSourcePath);
            if(!fInFile.canRead())
                throw new JayException("Can't split file : Source file is unreadable:"+sSourcePath);
            
            fIn = new FileInputStream(fInFile);
            
            String[] charsetsToBeTested = {"UTF-8", "MS949"};
            CharsetDetector cd = new CharsetDetector();
            Charset charset = cd.detectCharset(fInFile, charsetsToBeTested);

        	byte[] aOriginArray = new byte[(int)fInFile.length()];
            
            while(true) {
            	iByteRead = fIn.read(aOriginArray);
                if(iByteRead == -1)
                    break;
            }
// System.out.println("aOriginArray : "+ new String(aOriginArray,charset));            
            aOriginArray = Base64.getEncoder().encode(new String(aOriginArray,charset).getBytes());
            iEncArrayLength = aOriginArray.length;
// System.out.println("aEncryptedArray : "+new String(aOriginArray));
// System.out.println("aEncryptedArray length: "+iEncArrayLength);
 			
//System.out.println("aDecryptedArray : "+ new String(Base64.getDecoder().decode(aOriginArray),charset));

            if(iEncArrayLength%iSplitCnt==0) iBufferSize = iEncArrayLength/iSplitCnt;
            else iBufferSize = (iEncArrayLength/iSplitCnt)+1;
 			byte [] aTempArray = new byte[iBufferSize];
// System.out.println("iBufferSize: "+iBufferSize);
 
 
			for(int i=0;i<iSplitCnt;i++){
				iTemp = (int)(Math.random() * 20);
			 	if(iTemp==0) iTemp = 1;        
			 	else if(iTemp > iEncArrayLength) iTemp = iEncArrayLength;
			 	iSaltPositions.add(iTemp);
			}
			ret.put(CommonConst.SALT_POSITION, iSaltPositions);			  
// System.out.println(ret);           
        	        	
 			for(int i=0;i<iSplitCnt;i++){
     			fOutFile = new File(splitFileNames.get(i));
            	fOut = new FileOutputStream(fOutFile);
            	
            	aTempArray = Arrays.copyOfRange(aOriginArray, i*iBufferSize,  (i+1)*iBufferSize);
//  System.out.println("aTempArray.length: "+aTempArray.length);
    			encFirstArray = Arrays.copyOfRange(aTempArray, 0, (Integer)iSaltPositions.get(i));
    			encLastArray = Arrays.copyOfRange(aTempArray, (Integer)iSaltPositions.get(i), aTempArray.length);
    			
//    			System.out.println("encFirstArray:"+new String(encFirstArray));
//    			System.out.println("encLastArray:"+new String(encLastArray));
    			
    			fOut.write(CaseManipulation.toToggleCase(new String(encFirstArray)).getBytes());
    			fOut.write(saUniqueKey[i].getBytes());
//    			fOut.write(CaseManipulation.toToggleCase(new String(encLastArray)).getBytes());
    			fOut.write(new String(encLastArray).getBytes());
    			fOut.flush();
            }            
        }catch(Exception e){
        	e.printStackTrace();
            throw new JayException(e);
        }finally {
            try {
            	if(fIn != null) fIn.close();
                if(fOut != null) fOut.close();
            } catch (IOException ex) {
                throw new JayException(ex);
            }
        }
//        System.out.println(ret);
		return ret;
    }
    
    /**
     * 
     * @param saInputFileList
     * @param sSalt
     * @param sOutPutFilePath
     * @throws JayException
     */
    public void mergeFile4ASCII(String sMetaFilePath, String sOutPutFilePath) throws JayException{
    	File file = null;
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;
        HashMap oMetaInfo = new HashMap();
        
        String sInputContents = null;
        String sFirstContents = null;
        String sLastContents = null;
        String sSalt = null;
        
        try{
        	oMetaInfo = (HashMap)this.readSerEncFile(sMetaFilePath);
        System.out.println(oMetaInfo);
            ArrayList <String>aSplitFileList = (ArrayList)oMetaInfo.get(CommonConst.SPLIT_FILE_NAMES);
            
            file = new File(sOutPutFilePath);
            if(!file.exists())
                file.createNewFile(); 

            fos = new FileOutputStream(sOutPutFilePath);
            osw = new OutputStreamWriter(fos, CommonConst.ENCODING);
            bw = new BufferedWriter(osw);
            pw = new PrintWriter(bw);
            
            File fInFile = null;
            FileInputStream fIn = null;
            byte[] buffer;
            int bytes_read;
            StringBuffer asBuf = new StringBuffer();          
            for(int i=0;i<aSplitFileList.size();i++) {
            	fInFile = new File(aSplitFileList.get(i));
                
                if(!fInFile.exists() || !fInFile.isFile())
                    throw new JayException("FileMerge:no such file or directory:"+"fInFile:"+aSplitFileList.get(i));
                if(!fInFile.canRead())
                    throw new JayException("FileMerge:source file is unreadable:"+"fInFile:"+aSplitFileList.get(i));
                fIn = new FileInputStream(fInFile);

                buffer = new byte[(int)fInFile.length()];
                while(true) {
                    bytes_read = fIn.read(buffer);
                    if(bytes_read == -1)
                        break;
                }
   				sInputContents = new String(buffer);
//   	System.out.println(saInputFileList[i]+" before process :"+sInputContents); 
//   	System.out.println(saInputFileList[i]+" Salt Position :"+oMetaInfo);
   				sFirstContents = sInputContents.substring(0, (Integer)((ArrayList)oMetaInfo.get(CommonConst.SALT_POSITION)).get(i));
//   	System.out.println("sFirstContents :"+sFirstContents); 
   				sFirstContents = CaseManipulation.toToggleCase(sFirstContents);
//   				sLastContents = CaseManipulation.toToggleCase(sInputContents.substring((int)((ArrayList)oMetaInfo.get(CommonConst.SALT_POSITION)).get(i)+(int)((ArrayList)oMetaInfo.get(CommonConst.SALT_LENGTH)).get(i)));
   				sLastContents = sInputContents.substring((Integer)((ArrayList)oMetaInfo.get(CommonConst.SALT_POSITION)).get(i)+(Integer)((ArrayList)oMetaInfo.get(CommonConst.SALT_LENGTH)).get(i));
//   	System.out.println("sLastContents :"+sLastContents);  
//   System.out.println(saInputFileList[i]+" after process :"+sFirstContents+sLastContents);
                asBuf.append(sFirstContents+sLastContents);
            }
//    System.out.println(sOutPutFilePath+":"+asBuf);
            pw.print(new String(Base64.getDecoder().decode(asBuf.toString())));
            fIn.close();
            pw.close();
            osw.close();
            bw.close();
            fos.close();
            
        }catch(Exception ex){
        	ex.printStackTrace();
            throw new JayException(ex);
        }
    }
    
    /**
     * 
     * @param sSourcePath
     * @param iSplitCnt
     * @return
     * @throws JayException
     */
    public HashMap splitFile4Binary(String sSourcePath, int iSplitCnt) throws JayException{
    	HashMap ret = new HashMap();
    	ret.put(CommonConst.ID, CommonUtil.makeUniqueTimeID());

    	ret.put(CommonConst.FILE_TYPE, CommonConst.BINARY);
    	ret.put(CommonConst.ORIGIN_FILE_PATH, sSourcePath);
    	
    	ArrayList<String> splitFileNames = new ArrayList();
        
        BufferedOutputStream bw = null;	
		RandomAccessFile raf = null;
    	try{  
    		raf = new RandomAccessFile(sSourcePath, "r");
            long sourceSize = raf.length();
            long remainingBytes = sourceSize % iSplitCnt;
            long bytesPerSplit = sourceSize/iSplitCnt ;
//   System.out.println("sourceSize:"+sourceSize);  
//   System.out.println("remainingBytes:"+remainingBytes); 
//   System.out.println("bytesPerSplit:"+bytesPerSplit);  
            if((sourceSize % iSplitCnt) != 0)bytesPerSplit = bytesPerSplit+1;
//   System.out.println("bytesPerSplit:"+bytesPerSplit);  
     		int maxReadBufferSize = 8 * 1024; //8KB
     		
     		String aTempFileName = null;
     		
     		for(int destIx=0; destIx < (iSplitCnt-1); destIx++) {
     			aTempFileName = sSourcePath.substring(0,sSourcePath.lastIndexOf("\\")+1)+CommonUtil.makeUniqueID(24);
     			bw = new BufferedOutputStream(new FileOutputStream(aTempFileName));
//     			bw = new BufferedOutputStream(new FileOutputStream(sSourcePath+CommonConst.CURRENT_DIR+destIx+CommonConst.CURRENT_DIR+CommonConst.ORIGIN_STRING));
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
//     		System.out.println(raf.getFilePointer());
//     		System.out.println(remainingBytes);
            if(raf.getFilePointer()!=sourceSize || remainingBytes > 0) {
            	aTempFileName = sSourcePath.substring(0,sSourcePath.lastIndexOf("\\")+1)+CommonUtil.makeUniqueID(24);
            	bw = new BufferedOutputStream(new FileOutputStream(aTempFileName));
//            	bw = new BufferedOutputStream(new FileOutputStream(sSourcePath+CommonConst.CURRENT_DIR+(iSplitCnt-1)+CommonConst.CURRENT_DIR+CommonConst.ORIGIN_STRING));
            	readWrite(raf, bw, bytesPerSplit-remainingBytes);
                bw.flush();
                splitFileNames.add(aTempFileName);
            }
            
            ret.put(CommonConst.SPLIT_FILE_NAMES, splitFileNames);
            
    	}catch(Exception e){
    		throw new JayException(e);
    	}finally {
            try {
                bw.close();
                raf.close();
            } catch (IOException ex) {
                throw new JayException(ex);
            }
        }
    	return ret;
    }
    

    private void readWrite(RandomAccessFile raf, OutputStream Os, long numBytes, String sUniqueKey) throws IOException {
        byte[] buf = new byte[(int) numBytes];
        int val = raf.read(buf);
        if(val != -1) {
        	Os.write(buf);
        	Os.write(sUniqueKey.getBytes());
        }   	
    }
    
    private void readWrite(RandomAccessFile raf, OutputStream Os, long numBytes) throws IOException {
        byte[] buf = new byte[(int) numBytes];
        int val = raf.read(buf);
        if(val != -1) {
        	Os.write(buf);
        }   	
    }
    
    private void readWrite(RandomAccessFile raf, OutputStream Os) throws IOException {
        byte[] buf = new byte[(int)raf.length()];
        int val = raf.read(buf);
        if(val != -1) {
        	Os.write(buf);
        }   	
      }
    
    public void mergeFile4Binary(String sMetaFilePath, String sOutPutFilePath) throws JayException{
    	HashMap oMetaInfo = (HashMap)this.readSerEncFile(sMetaFilePath);
    System.out.println(oMetaInfo);
        ArrayList aSplitFileList = (ArrayList)oMetaInfo.get(CommonConst.SPLIT_FILE_NAMES);
    	
        FileOutputStream Os = null;
        
        try{
        	Os = new FileOutputStream(new File(sOutPutFilePath));
        	for(int destIx=0; destIx < aSplitFileList.size() ; destIx++) {
            	RandomAccessFile raf = new RandomAccessFile((String)aSplitFileList.get(destIx), "r");
            	readWrite(raf, Os);
            }
            Os.flush();
            Os.close();
        }catch(Exception ex){
        	ex.printStackTrace();
            throw new JayException(ex);
        }finally{
				try {
					if(Os != null) Os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
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
    public void writeFile(String sFileName, String content, String sPath) throws JayException{
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
        try {
            file = new File(sFileName);
            if(file.exists()){
                fileIn = new FileInputStream(sFileName);
                ObjectInputStream ois = new ObjectInputStream(fileIn);
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
    public static void writeSerEncFile(Object obj, String sPath, String sFileName) throws JayException{
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
            
            jc.encrypt((Serializable) obj, oos);
            
        	
//            oos.writeObject(obj);

//            oos.close();
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
    public static Object readSerEncFile(String sFileName){
        FileInputStream fileIn = null;
        Object oRet = null;
        File file = null;
        try {
            file = new File(sFileName);
            if(file.exists()){
                fileIn = new FileInputStream(sFileName);
                ObjectInputStream ois = new ObjectInputStream(fileIn);
                oRet = jc.decrypt(ois);
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
    public long getFileSize(){
        return lFileSize;
    }
    
    /**
     * @return sFileName
     */
    public String getFileName(){
        return sFileName;
    }

    /**
     * @param sFileName
     * @throws Exception
     */
    public static void remove(String sFileName) throws Exception{
        File fFile = new File(sFileName);
        fFile.delete();
    }

    /**
     * @param sFilePath
     * @param sFileName
     * @throws Exception
     */
    public void remove(String sFilePath, String sFileName) throws Exception{
        File fFile = new File(sFilePath+File.separator+sFileName);
        fFile.delete();
    }
    
    /**
     * @param sFilePath
     * @param vecFileList
     * @throws IOException
     * @throws JayException
     */
    public void removeFiles(String sFilePath, Vector vecFileList) throws IOException, JayException {
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
    public void copyFile(String sSourcePath, String sTargetPath, String[] saFileList, String[] saFileUploadList) throws IOException, JayException {
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
     * @param sPath
     * @param iOffSet
     * @param iLength
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void accessFile(String sPath, long iOffSet, int iLength) throws FileNotFoundException, IOException{
        iOffSet = 0L;
        String sMode = "r";
        RandomAccessFile raf = new RandomAccessFile(sPath, sMode);
        raf.seek(iOffSet);
    }
    
    public boolean isBinaryFile(File f) throws IOException {
    	boolean ret = false;
        String type = Files.probeContentType(f.toPath());
        if (type == null) {
            //type couldn't be determined, assume binary
        	ret = true;
        } else if (type.startsWith("text")) {
        	ret = false;
        } else {
            //type isn't text
        	ret = true;
        }
//        System.out.println("Is this file Binary ? : "+ ret);
        return ret;
    }
    
    private boolean isBinary(byte[] bytes, int len){
    	int count = 0; // for checking EOF
    	for (byte thisByte : bytes) {
    		if (thisByte == 0 && count < len-1){
    			return true;
    		}
    		count++;
    	}
    	return false;
	}
    
    /**
     * 
     * @param filename
     * @return bResult
     * @throws JayException 
     */
    public boolean isASCII(String filename) throws JayException {
		 
		boolean bResult = false;
		 
        FileReader inputStream = null;
 
        try {
            inputStream = new FileReader(filename);
 
            File f = new File(filename);
            byte [] buffer = new byte[(int)f.length()]; 
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
    public ArrayList getFileInfoList(File aFile) throws Exception{
		if (aFile.isDirectory()) {
            String[] children = aFile.list();
            for (int i=0; i<children.length; i++) {            	
            	getFileInfoList(new File(aFile, children[i]));
            }
        } else {
        	mFileInfoList.add(getFileInfo(aFile));
        }
		return mFileInfoList;
	}
	
    /**
     * 
     * @param src
     * @return aFileInfoList
     * @throws Exception
     */
	private ArrayList<String> getFileInfo(File src) throws JayException{
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
					// TODO Auto-generated catch block
					throw new JayException(e);
				}
		}
		Date dt = new Date(src.lastModified());
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.KOREAN);
		aFileInfoList.add(7, df.format(dt));
		
		return aFileInfoList;
	}
	
	

}

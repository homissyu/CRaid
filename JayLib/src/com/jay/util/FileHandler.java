package com.jay.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

	public FileHandler(){
	}
    
	/**
	 * @param path
	 * @param c
	 */
	public static void recursiveFind(Path path, Consumer<Path> c) {
		try (DirectoryStream<Path> newDirectoryStream = Files.newDirectoryStream(path)) {
			StreamSupport.stream(newDirectoryStream.spliterator(), false).peek(p -> {
				c.accept(p);
	            if (p.toFile().isDirectory()) recursiveFind(p, c);
			}).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    /**
     * @param raf
     * @param Os
     * @param numBytes
     * @throws IOException
     */
    public static void readWrite(RandomAccessFile raf, OutputStream Os, long numBytes) throws IOException {
        byte[] buf = new byte[(int) numBytes];
// System.out.println("numBytes:"+numBytes);           
        int val = raf.read(buf);
//        for(int i=0;i<numBytes;i++) {
//        	System.out.println((char)(buf[i]));
//        }
        if(val != -1) {
        	Os.write(buf);
        }   	
    }
    
    /**
     * @param raf
     * @param Os
     * @throws IOException
     */
    public static void readWrite(RandomAccessFile raf, OutputStream Os) throws IOException {
        byte[] buf = new byte[(int)raf.length()];
        int val = raf.read(buf);
        if(val != -1) {
        	Os.write(buf);
        }   	
    }
    
    /**
     * @param sFileName
     * @param content
     * @param sPath
     * @throws JayException
     */
    public static void writeFile(String sFileName, byte[] contentBuf, String sPath){
        String sFilePath = sPath + sFileName;
        File file = null;
        FileOutputStream fos = null;
        
//        System.out.println(sPath);
//        System.out.println(sFilePath);
        
        try{
            file = new File(sPath);
            if(!file.exists())
                file.mkdirs();
            file = new File(sFilePath);
            if(!file.exists())
                file.createNewFile(); 

            fos = new FileOutputStream(sFilePath);
            fos.write(contentBuf);

            fos.close();
// System.out.println(file.exists());
        }catch(Exception ex){
        	ex.printStackTrace();
        }
    }
    
    /**
     * @param sFilePath
     * @param contentBuf
     */
    public static void writeFile(String sFilePath, byte[] contentBuf){
        File file = null;
        FileOutputStream fos = null;
        
        try{
            file = new File(sFilePath);
            if(!file.exists())
                file.createNewFile(); 

            fos = new FileOutputStream(sFilePath);
            fos.write(contentBuf);

            fos.close();
        }catch(Exception ex){
        	ex.printStackTrace();
        }
    }
    
    /**
     * 
     * @param sFileName
     * @param obj
     * @param sPath
     * @throws JayException
     */
    public static void writeSerFile(Object obj, String sPath, String sFileName){
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
    public static void writeSerEncFile(Object obj, String sPath, String sFileName){
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
// System.out.println(sFilePath);   
            oos = new ObjectOutputStream(new FileOutputStream(sFilePath));
            CryptoUtils.encryptObj((Serializable) obj, oos);
            
        }catch(Exception ex){
        	ex.printStackTrace();
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
                
                oRet = CryptoUtils.decryptObj(ois);
                
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
     * @param filename
     * @return boolean bResult 
     */
    public static boolean isASCII(String filename) {
		boolean bResult = false;
        FileReader inputStream = null;
        try {
            inputStream = new FileReader(filename);
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
        	ex.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                	ex.printStackTrace();
                }
            }
        }
        System.out.println("Is this file ASCII ? : "+ bResult);
        return bResult;
    }
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jay.util;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author Jay
 */
public class CommonUtil {
    Dimension dm = null;
    
    public CommonUtil(){
//        Toolkit.getDefaultToolkit().getScreenSize();
    }
    
    /**
     * 
     * @param aFormat
     * @return String
     */
    public static String getCurrentTime(String aFormat){
    	String currTime = null;
        Calendar ca = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        currTime = new SimpleDateFormat(aFormat).format(ca.getTime());
        ca = null;
        return currTime;
    }

    /**
     * 
     * @param iLength
     * @return String
     */
    public static String makeUniqueTimeID(int iLength){
        StringBuilder sUniqueID = new StringBuilder();
    	Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String strMillSec = sdf.format(dt);
        sUniqueID.append(strMillSec);
        int iTempIdx = 0;
        while(true){
            iTempIdx = (int)(Math.random() * 100);
            if(iTempIdx<CommonConst.sAlphabetDecimalChar.length){
                sUniqueID.append(CommonConst.sAlphabetDecimalChar[iTempIdx]);
            }
            if(sUniqueID.length()==iLength) break;
        }
        sUniqueID.append(((int)(Math.random() * 100)));
        return sUniqueID.toString();
    }
    
    /**
     * 
     * @param iLength
     * @return String
     */
    public static String makeUniqueID(int iLength){
        StringBuilder sUniqueID = new StringBuilder();
    	int iTempIdx = 0;
        while(true){
            iTempIdx = (int)(Math.random() * 100);
            if(iTempIdx<CommonConst.sAlphabetDecimalChar.length){
                sUniqueID.append(CommonConst.sAlphabetDecimalChar[iTempIdx]);
            }
            if(sUniqueID.length()==iLength) break;
        }
        return sUniqueID.toString();
    }
    
//    public void printSystemInfo(){
//        Properties prop = System.getProperties();
//        Iterator<?> it = prop.keySet().iterator();
//        String aTempKey = null;
//        System.out.println("System properties");
//        while(it.hasNext()){
//            aTempKey = (String)it.next();
//            System.out.println("Key:"+aTempKey+"==>"+prop.get(aTempKey));
//
//        }
//        Map<?, ?> env = System.getenv();
//        it = env.keySet().iterator();
//        System.out.println("System env");
//        while(it.hasNext()){
//            aTempKey = (String)it.next();
//            System.out.println("Key:"+aTempKey+"==>"+env.get(aTempKey));
//        }
//    }
//    
//    public static void getSystemProps(){
//        Properties pt = System.getProperties();
//        Iterator<?> it = pt.keySet().iterator();
//        String tempKey = null;
//        while(it.hasNext()){
//            tempKey = (String)it.next();
//            System.out.println("propKey:"+tempKey+":"+System.getProperty(tempKey));
//        }
//    }
//    
//    public static void getSystemEnvs(){
//        String tempKey = null;
//        Map<?, ?> envMap = System.getenv();
//        Iterator<?> it = envMap.keySet().iterator();
//        while(it.hasNext()){
//            tempKey = (String)it.next();
//            System.out.println("envKey:"+tempKey+":"+System.getenv(tempKey));
//        }
//    }
}
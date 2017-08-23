/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jay.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

/**
 *
 * @author Jay
 */
public class CommonUtil {
    Dimension dm = null;
    
    public CommonUtil(){
        Toolkit.getDefaultToolkit().getScreenSize();
    }
    
    public static String toString(Object s) {
            return (s == null) ? "" : s.toString();
    }

    
    public static String makeString(String data, int offset, int length, String charset) throws Exception{
        byte [] b = null;
        byte [] ret = null;
        String result = "";

        try{
            if (charset == null) {
                    charset = CommonConst.ENCODING;
            }
            b = data.getBytes();
            ret = new byte[length];

            if( b.length < length) {
                int space = length - b.length;
                System.arraycopy(data.getBytes(), offset, b, 0, (data.getBytes()).length);
                result = new String(b);
                result = result + lpad(" ", space," ");
                return result;
            } else if( b.length <= offset ) { // 占쏙옙占쏙옙占싶깍옙占싱븝옙占쏙옙 占쌘몌옙占퐐占� offset占쏙옙 커占쏙옙占� 占쏙옙占쏙옙 占쏙옙占쏙옙占싶몌옙 占쏙옙占쏙옙占쏘만큼占쏙옙 占쏙옙占�8占쏙옙 채占쏙옙占�.
                return lpad(" ", length," ");
            } else {
                System.arraycopy( b,
                offset,
                ret,
                0,
                length);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }

        result = new String(ret);

        return result;
    }


    public static String lpad(String srcstr, int ilen, String rpcstr) {

        String strblank = "";
        int ibyte = 0;

        try {

            ibyte = lenB(srcstr);
            //ibyte = len(srcstr);

            if (ibyte < ilen)
                    for (int i=ibyte; i<ilen; i++)
                            strblank = strblank + rpcstr;

            srcstr = strblank + srcstr;

            return srcstr;

        } catch (Exception err) {

            return srcstr;
        }

    }

    public static int lenB(String srcstr) {
        byte[] bytesrc;
        int ibyte = 0;

        try {
            //bytesrc = strsrc.getBytes();			//
            bytesrc = srcstr.getBytes("KSC5601");	//
            ibyte = bytesrc.length;

            return ibyte;
        } catch (Exception err) {
            return ibyte;
        }
    }

    public static String replace(String s, String old, String replacement) {
        int i = s.indexOf(old);
        StringBuilder r = new StringBuilder();
        if (i == -1) return s;
        r.append(s.substring(0,i)).append(replacement);
        if (i + old.length() < s.length())
           r.append(replace(s.substring(i + old.length(), s.length()), old, replacement));
        return r.toString();
    }

    public static String getCurrentTime(String aFormat){
    	String currTime = null;
        Calendar ca = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        currTime = new SimpleDateFormat(aFormat).format(ca.getTime());
        ca = null;
        return currTime;
    }

    public static byte[] insertElementAtByteArray(byte[] array, byte element, int position) {
        byte[] result = new byte[array.length + 1];
        for (int i = 0; i <= position - 1; i++)
            result[i] = array[i];
        result[position] = element;
        for (int i = position + 1; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }
    
    public static String makeUniqueTimeID(){
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
            if(sUniqueID.length()==24) break;
        }
        sUniqueID.append(((int)(Math.random() * 100)));
        return sUniqueID.toString();
    }
    
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
    
    public void printSystemInfo(){
        Properties prop = System.getProperties();
        Iterator<?> it = prop.keySet().iterator();
        String aTempKey = null;
        System.out.println("System properties");
        while(it.hasNext()){
            aTempKey = (String)it.next();
            System.out.println("Key:"+aTempKey+"==>"+prop.get(aTempKey));

        }
        Map<?, ?> env = System.getenv();
        it = env.keySet().iterator();
        System.out.println("System env");
        while(it.hasNext()){
            aTempKey = (String)it.next();
            System.out.println("Key:"+aTempKey+"==>"+env.get(aTempKey));
        }
    }
    
    public static void getSystemProps(){
        Properties pt = System.getProperties();
        Iterator<?> it = pt.keySet().iterator();
        String tempKey = null;
        while(it.hasNext()){
            tempKey = (String)it.next();
            System.out.println("propKey:"+tempKey+":"+System.getProperty(tempKey));
        }
    }
    
    public static void getSystemEnvs(){
        String tempKey = null;
        Map<?, ?> envMap = System.getenv();
        Iterator<?> it = envMap.keySet().iterator();
        while(it.hasNext()){
            tempKey = (String)it.next();
            System.out.println("envKey:"+tempKey+":"+System.getenv(tempKey));
        }
    }
}

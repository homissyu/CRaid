package com.jay.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtil {
	public static String getCurrentTime(String aFormat){
    	String currTime = null;
        Calendar ca = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        currTime = new SimpleDateFormat(aFormat).format(ca.getTime());
        ca = null;
        return currTime;
    }	
}
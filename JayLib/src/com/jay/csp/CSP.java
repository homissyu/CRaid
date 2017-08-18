package com.jay.csp;

import java.net.URL;
import java.util.HashMap;

import com.jay.util.CommonConst;

public class CSP {
	private String Id = null;
	
	private URL MainUrl = null;
	private String Name = null;
	
	//Stream or JSON
	private int UploadType = CommonConst.JSON_TYPE;
	private int DownloadType = CommonConst.STREAM_TYPE;
	
	private String uploadString = null;
	private String uploadIP = null;
	
	//Kilo Byte
	private long Quota = 0;
	private long Usage = 0;
	
	private String UserId = null;
	private String PassWord = null;
	
	private HashMap<String, String> ErrorCode = new HashMap<String, String>();
}

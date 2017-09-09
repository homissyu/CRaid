package com.jay.csp;

import java.net.URL;
import java.util.HashMap;

import com.jay.util.CommonConst;

public class CSP {
	private String Id = null;
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
	private URL MainUrl = null;
	
	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public URL getMainUrl() {
		return MainUrl;
	}

	public void setMainUrl(URL mainUrl) {
		MainUrl = mainUrl;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public int getUploadType() {
		return UploadType;
	}

	public void setUploadType(int uploadType) {
		UploadType = uploadType;
	}

	public int getDownloadType() {
		return DownloadType;
	}

	public void setDownloadType(int downloadType) {
		DownloadType = downloadType;
	}

	public String getUploadString() {
		return uploadString;
	}

	public void setUploadString(String uploadString) {
		this.uploadString = uploadString;
	}

	public String getUploadIP() {
		return uploadIP;
	}

	public void setUploadIP(String uploadIP) {
		this.uploadIP = uploadIP;
	}

	public long getQuota() {
		return Quota;
	}

	public void setQuota(long quota) {
		Quota = quota;
	}

	public long getUsage() {
		return Usage;
	}

	public void setUsage(long usage) {
		Usage = usage;
	}

	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	public String getPassWord() {
		return PassWord;
	}

	public void setPassWord(String passWord) {
		PassWord = passWord;
	}

	public HashMap<String, String> getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(HashMap<String, String> errorCode) {
		ErrorCode = errorCode;
	}

	
}

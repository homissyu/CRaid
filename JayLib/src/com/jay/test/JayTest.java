package com.jay.test;

import java.io.File;
import java.util.ArrayList;

import com.jay.util.CommonConst;
import com.jay.util.CommonUtil;
import com.jay.util.FileHandler;

public class JayTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String sSourcePath = "c:\\";
		String sSourceFileName = "Arare_windows_0_9_2.exe";
		String sSourceFilePath = sSourcePath + sSourceFileName;
		String sTargetFilePath = sSourcePath+CommonConst.MERGE_STR+sSourceFileName;
		String sMetaFilePath = CommonConst.META_FILE_PATH+File.separator+CommonConst.META_FILE_NAME;
		
		try {
			ArrayList<Integer> aSplitRatio = new ArrayList<Integer>();
			for(int i=0;i<CommonConst.CSP_FREE_AMOUNT.length;i++) {
				aSplitRatio.add(CommonConst.CSP_FREE_AMOUNT[i]);
			}
			
			System.out.println("Start Split : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			FileHandler.splitFile(sSourceFilePath, aSplitRatio);
			System.out.println("End Split : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			
			System.out.println("Start Merge : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			FileHandler.mergeFile(sMetaFilePath, sTargetFilePath);
			System.out.println("End Merge : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			
//			AWSService AWS = new AWSService();
//			AWS.uploadFile(new File(sMetaPath+sMetaFile));
//			AZUREService AZS = new AZUREService();
//			AZS.uploadFile(new File(sMetaPath+sMetaFile));
//			GCPService GCS = new GCPService();
//			GCS.uploadFile(new File(sMetaPath+sMetaFile));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package com.jay.test;

import java.io.File;
import java.util.ArrayList;

import com.jay.util.CommonConst;
import com.jay.util.FileHandler;
import com.jay.util.TimeUtil;

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
			aSplitRatio.add(10);
			aSplitRatio.add(30);
			aSplitRatio.add(2);
			aSplitRatio.add(5);
			
			System.out.println("Start Split : "+TimeUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			FileHandler.splitFile(sSourceFilePath, aSplitRatio);
			System.out.println("End Split : "+TimeUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			
			System.out.println("Start Merge : "+TimeUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			FileHandler.mergeFile(sMetaFilePath, sTargetFilePath);
			System.out.println("End Merge : "+TimeUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			
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

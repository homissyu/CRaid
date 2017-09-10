package com.jay.test;

import java.util.ArrayList;

import org.apache.commons.logging.impl.SimpleLog;

import com.jay.craid.CRaid;
import com.jay.util.CommonConst;
import com.jay.util.CommonUtil;
import com.jay.util.Debug;

public class JayTest {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		 * File rootDir = File.listRoots()[0];
			File dir = new File(new File(new File(rootDir, "Users"), "Mymac"), "Desktop");
			if (!dir.exists()){
			    dir.mkdirs();
			}
		 */
		String sSourcePath = "/Users/karl/CRaid/";
		String sSourceFileName = "astx.dmg";
		String sSourceFilePath = sSourcePath + sSourceFileName;
		String sTargetFilePath = sSourcePath+CommonConst.MERGE_STR+sSourceFileName;
		String sMetaFilePath = sSourcePath+CommonConst.META_FILE_NAME;
		String sLogFilePath = sSourcePath+CommonConst.LOG_FILE_NAME;
		String mSubSystem = "com.jay.test.JayTest";
		try {
			CRaid craid = new CRaid();
			
			Debug.setVerbosity(CommonConst.DEBUG_MODE);
			Debug.setErrLog(sLogFilePath);
			Debug.addSubsystems(mSubSystem);
			
			ArrayList<Integer> aSplitRatio = new ArrayList<Integer>();
//			for(int i=0;i<CommonConst.CSP_FREE_AMOUNT.length;i++) {
//				aSplitRatio.add(CommonConst.CSP_FREE_AMOUNT[i]);
//			}

			aSplitRatio.add(10);
			aSplitRatio.add(10);
			aSplitRatio.add(10);

			Debug.trace(mSubSystem, 0, "Start Split");
			System.out.println("Start Split : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			craid.splitFile(sSourceFilePath, aSplitRatio, CommonConst.ENCRYPT, CommonConst.DO_RAID, sMetaFilePath);
			Debug.trace(mSubSystem, 0, "End Split");
			System.out.println("End Split : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT)+System.lineSeparator());

			Debug.trace(mSubSystem, 0, "Start Merge");
			System.out.println("Start Merge : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			craid.mergeFile(sTargetFilePath, sMetaFilePath);
			Debug.trace(mSubSystem, 0, "End Merge");
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

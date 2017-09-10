package com.jay.test;

import java.io.File;
import java.util.ArrayList;

import com.jay.craid.CRaid;
import com.jay.util.CommonConst;
import com.jay.util.CommonUtil;
import com.jay.util.Debug;

public class JayTest {
	
	private String mSubSystem = null;
	public JayTest() {
		Debug.setVerbosity(CommonConst.DEBUG_MODE);
		Debug.addSubsystems((this.getClass()).getCanonicalName());
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JayTest test = new JayTest();
		
		File rootDir = File.listRoots()[0];
		File dir = new File(new File(new File(rootDir, "Users"), "karl"), "CRaid");
		
		String sSourcePath = dir.getAbsolutePath();
		String sSourceFileName = "astx.dmg";
		String sSourceFilePath = sSourcePath + File.separator + sSourceFileName;
		String sTargetFilePath = sSourcePath + File.separator + CommonConst.MERGE_STR + sSourceFileName;
		String sMetaFilePath = sSourcePath + File.separator + CommonConst.META_FILE_NAME;
		String sLogFilePath = sSourcePath + File.separator + CommonConst.LOG_FILE_NAME;
		
		try {
			CRaid craid = new CRaid();
			Debug.setErrLog(sLogFilePath);
			
			
			ArrayList<Integer> aSplitRatio = new ArrayList<Integer>();
//			for(int i=0;i<CommonConst.CSP_FREE_AMOUNT.length;i++) {
//				aSplitRatio.add(CommonConst.CSP_FREE_AMOUNT[i]);
//			}

			aSplitRatio.add(10);
			aSplitRatio.add(10);
			aSplitRatio.add(10);

			Debug.trace(test.mSubSystem, 0, "Start Split");
			System.out.println("Start Split : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			craid.splitFile(sSourceFilePath, aSplitRatio, CommonConst.ENCRYPT, CommonConst.DO_RAID, sMetaFilePath);
			Debug.trace(test.mSubSystem, 0, "End Split");
			System.out.println("End Split : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT)+System.lineSeparator());

			Debug.trace(test.mSubSystem, 0, "Start Merge");
			System.out.println("Start Merge : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			craid.mergeFile(sTargetFilePath, sMetaFilePath);
			Debug.trace(test.mSubSystem, 0, "End Merge");
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

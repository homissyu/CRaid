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
		mSubSystem = (this.getClass()).getCanonicalName();
		Debug.addSubsystems(mSubSystem);
		Debug.setVerbosity(CommonConst.OPERATION_MODE);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JayTest test = new JayTest();
		
		File dir =  new File(File.listRoots()[0], "CRaid");
		
		String sSourcePath = dir.getAbsolutePath();
		String sSourceFileName = "background.bmp";
		String sSourceFilePath = sSourcePath + File.separator + sSourceFileName;
		String sTargetFilePath = sSourcePath + File.separator + CommonConst.MERGE_STR + sSourceFileName;
		String sMetaFilePath = sSourcePath + File.separator + CommonConst.META_FILE_NAME;
		String sLogFilePath = sSourcePath + File.separator + CommonConst.LOG_FILE_NAME;
		
		
		try {
			Debug.setErrLog(sLogFilePath);
			Debug.setLogFilePath(sSourcePath);
			
			CRaid craid = new CRaid();
			
			ArrayList<Integer> aSplitRatio = new ArrayList<Integer>();
//			for(int i=0;i<CommonConst.CSP_FREE_AMOUNT.length;i++) {
//				aSplitRatio.add(CommonConst.CSP_FREE_AMOUNT[i]);
//			}

			aSplitRatio.add(10);
			aSplitRatio.add(10);
			aSplitRatio.add(10);

			Debug.trace(test.mSubSystem, CommonConst.OPERATION_MODE, "Start Split" ,Thread.currentThread().getStackTrace()[1].getLineNumber());
			System.out.println("Start Split : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			craid.splitFile(sSourceFilePath, aSplitRatio, CommonConst.ENCRYPT, CommonConst.DO_RAID, sMetaFilePath);
			Debug.trace(test.mSubSystem, CommonConst.OPERATION_MODE, "End Split",Thread.currentThread().getStackTrace()[1].getLineNumber());
			System.out.println("End Split : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT)+System.lineSeparator());

			Debug.trace(test.mSubSystem, CommonConst.OPERATION_MODE, "Start Merge",Thread.currentThread().getStackTrace()[1].getLineNumber());
			System.out.println("Start Merge : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			craid.mergeFile(sTargetFilePath, sMetaFilePath);
			Debug.trace(test.mSubSystem, CommonConst.OPERATION_MODE, "End Merge",Thread.currentThread().getStackTrace()[1].getLineNumber());
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

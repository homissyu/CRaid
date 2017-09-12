package com.jay.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import com.jay.craid.CRaid;
import com.jay.util.CommonConst;
import com.jay.util.CommonUtil;
import com.jay.util.Debug;

public class JayTest {
	
	private String mSubSystem = null;
	private File dir =  null;
	private String sSourcePath = null;
	private String sSourceFileName = null;
	private String sSourceFilePath = null;
	private String sTargetFilePath = null;
	private String sMetaFilePath = null;
	private String sLogFilePath = null;
	private CRaid mCRaid = null;
	
	private boolean mEncryption = false;
	private boolean mRaid = false;
	
	private Scanner mScanner = null;
	
	public JayTest() {
		mSubSystem = (this.getClass()).getCanonicalName();
		Debug.addSubsystems(mSubSystem);
		Debug.setVerbosity(CommonConst.QA_MODE);
		
		dir =  new File(new File(new File(File.listRoots()[0], "Users"), "karl"), "CRaid");
		
		sSourcePath = dir.getAbsolutePath();
		sSourceFileName = "NPI3GManager.log";
		sSourceFilePath = sSourcePath + File.separator + sSourceFileName;
		sTargetFilePath = sSourcePath + File.separator + CommonConst.MERGE_STR + sSourceFileName;
		sMetaFilePath = sSourcePath + File.separator + CommonConst.META_FILE_NAME;
		sLogFilePath = sSourcePath + File.separator + CommonConst.LOG_FILE_NAME;
		
		try {
			Debug.setErrLog(sLogFilePath);
			Debug.setLogFilePath(sSourcePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mCRaid = new CRaid();
	}
	
	public int doCRaid(ArrayList<Integer> aSplitRatio) {
		int iTemp = 0;
		System.out.println("#################");
		System.out.println("Choose Operation Type");
		System.out.println("1. Split");
		System.out.println("2. Merge");
		System.out.println("0. Exit");
		System.lineSeparator();
		mScanner = new Scanner(System.in);
		iTemp = Integer.parseInt(mScanner.next());
		switch(iTemp) {
		case 0:
			System.exit(0);
			break;
		case 1:
			System.out.println("Choose Split Method");
			System.out.println("4. Encrypt Only");
			System.out.println("5. Raid Only");
			System.out.println("6. Encrypt & Raid");
			System.out.println("7. Nothing");
			System.out.println("0. Exit");
			System.lineSeparator();
			switch(Integer.parseInt(mScanner.next())) {
			case 0:
				System.exit(0);
				break;
			case 4:
				mEncryption = true;
				break;
			case 5:
				mRaid = true;
				break;
			case 6:
				mEncryption = true;
				mRaid = true;
				break;
			case 7:
				break;
			default:
				doCRaid(aSplitRatio);
				break;
			}
			
			Debug.trace(mSubSystem, CommonConst.OPERATION_MODE, "Start Split" ,Thread.currentThread().getStackTrace()[1].getLineNumber());
			System.out.println("Start Split : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			mCRaid.splitFile(sSourceFilePath, aSplitRatio, mEncryption, mRaid, sMetaFilePath);
			Debug.trace(mSubSystem, CommonConst.OPERATION_MODE, "End Split",Thread.currentThread().getStackTrace()[1].getLineNumber());
			System.out.println("End Split : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT)+System.lineSeparator());
			
			break;
		case 2:
			Debug.trace(mSubSystem, CommonConst.OPERATION_MODE, "Start Merge",Thread.currentThread().getStackTrace()[1].getLineNumber());
			System.out.println("Start Merge : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			mCRaid.mergeFile(sTargetFilePath, sMetaFilePath);
			Debug.trace(mSubSystem, CommonConst.OPERATION_MODE, "End Merge",Thread.currentThread().getStackTrace()[1].getLineNumber());
			System.out.println("End Merge : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			break;
		default:
			doCRaid(aSplitRatio);
			break;
		}
		return iTemp;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			JayTest test = new JayTest();
			
			ArrayList<Integer> aSplitRatio = new ArrayList<Integer>();
			
//			for(int i=0;i<CommonConst.CSP_FREE_AMOUNT.length;i++) {
//				aSplitRatio.add(CommonConst.CSP_FREE_AMOUNT[i]);
//			}

			aSplitRatio.add(10);
			aSplitRatio.add(10);
			aSplitRatio.add(10);
			
			test.doCRaid(aSplitRatio);
			
//			AWSService AWS = new AWSService();
//			AWS.uploadFile(new File(sMetaFilePath));
//			AZUREService AZS = new AZUREService();
//			AZS.uploadFile(new File(sMetaFilePath));
//			GCPService GCS = new GCPService();
//			GCS.uploadFile(new File(sMetaFilePath));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

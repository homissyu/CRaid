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
	private static boolean mEncrypt = false;
	private static boolean mRaid = false;
	
	public JayTest() {
		mSubSystem = (this.getClass()).getCanonicalName();
		Debug.addSubsystems(mSubSystem);
		Debug.setVerbosity(CommonConst.QA_MODE);
	}
	
	public static int start() throws Exception {
		int ret = 0;
		try{
			Scanner sc = new Scanner(System.in);
			System.out.println("#########################");
			System.out.println("Choose operation !");
			System.lineSeparator();
			System.out.println("1.Split Only 	2.Split&Merge	3.Mergey Only	0.exit");
			ret = Integer.parseInt(sc.nextLine());
			switch(ret) {
				case 0:
					System.out.println("Goobye!");
					System.exit(0);
					break;
				case 1: case 2:
					System.out.println("Choose split type !");
					System.lineSeparator();
					System.out.println("1.Encrypt + Raid	2.Encrypt + Not Raid	3.Not Encrypt + Raid	4.Not Encrypt + Not Raid	0.exit");
					switch(Integer.parseInt(sc.nextLine())) {
						case 0:
							System.out.println("Goobye!");
							System.exit(0);
						case 1:
							mEncrypt = true;
							mRaid = true;
							break;
						case 2:
							mEncrypt = true;
							break;
						case 3:
							mRaid = true;
							break;
						case 4:
							break;
						default:
							System.lineSeparator();
							System.out.println("Invaild Argument! Retry again");
							System.lineSeparator();
							start();
							break;
					};
					break;
				case 3:
					break;
				default:
					System.lineSeparator();
					System.out.println("Invaild Argument! Retry again");
					System.lineSeparator();
					start();
					break;
			}
		}catch(Exception ex) {
			System.lineSeparator();
			System.out.println("Invaild Argument! Retry again");
			System.lineSeparator();
			start();
			throw new Exception();
		}
		return ret;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JayTest test = new JayTest();
		
		File dir =  new File(File.listRoots()[0], "CRaid");
		
		String sSourcePath = dir.getAbsolutePath();
		String sSourceFileName = "test.log";
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

			switch(start()) {
				case 1:
					Debug.trace(test.mSubSystem, CommonConst.OPERATION_MODE, "Start Split" ,Thread.currentThread().getStackTrace()[1].getLineNumber());
					System.out.println("Start Split : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
					craid.splitFile(sSourceFilePath, aSplitRatio, test.mEncrypt, test.mRaid, sMetaFilePath);
					Debug.trace(test.mSubSystem, CommonConst.OPERATION_MODE, "End Split",Thread.currentThread().getStackTrace()[1].getLineNumber());
					System.out.println("End Split : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT)+System.lineSeparator());
				break;
				case 2:
					Debug.trace(test.mSubSystem, CommonConst.OPERATION_MODE, "Start Split" ,Thread.currentThread().getStackTrace()[1].getLineNumber());
					System.out.println("Start Split : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
					craid.splitFile(sSourceFilePath, aSplitRatio, test.mEncrypt, test.mRaid, sMetaFilePath);
					Debug.trace(test.mSubSystem, CommonConst.OPERATION_MODE, "End Split",Thread.currentThread().getStackTrace()[1].getLineNumber());
					System.out.println("End Split : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT)+System.lineSeparator());
				
					Debug.trace(test.mSubSystem, CommonConst.OPERATION_MODE, "Start Merge",Thread.currentThread().getStackTrace()[1].getLineNumber());
					System.out.println("Start Merge : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
					craid.mergeFile(sTargetFilePath, sMetaFilePath);
					Debug.trace(test.mSubSystem, CommonConst.OPERATION_MODE, "End Merge",Thread.currentThread().getStackTrace()[1].getLineNumber());
					System.out.println("End Merge : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
				break;
				case 3:
					Debug.trace(test.mSubSystem, CommonConst.OPERATION_MODE, "Start Merge",Thread.currentThread().getStackTrace()[1].getLineNumber());
					System.out.println("Start Merge : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
					craid.mergeFile(sTargetFilePath, sMetaFilePath);
					Debug.trace(test.mSubSystem, CommonConst.OPERATION_MODE, "End Merge",Thread.currentThread().getStackTrace()[1].getLineNumber());
					System.out.println("End Merge : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
					break;
				default:
					break;
			}
			
			
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

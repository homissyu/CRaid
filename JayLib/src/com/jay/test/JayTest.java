package com.jay.test;

import java.io.File;
import java.nio.file.spi.FileTypeDetector;

import com.jay.csp.aws.AWSService;
import com.jay.csp.azure.AZUREService;
import com.jay.csp.gcp.GCPService;
import com.jay.util.CommonConst;
import com.jay.util.FileEncoding;
import com.jay.util.FileHandler;
import com.jay.util.TimeUtil;

public class JayTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String sSourcePath = "c:\\";
		String sSourceFile = ".bak_0.log";
		String sSourceFilePath = sSourcePath + sSourceFile;
		String sASCIITargetPath = sSourcePath + CommonConst.MERGE_STR + sSourceFile;
		String sBINTargetFile = sSourcePath + CommonConst.MERGE_STR + sSourceFile;
		
		String sMetaPath = "c:\\";
		String sMetaFile = "Meta.craid";
		
		FileHandler fh = new FileHandler();
		
		try {
			System.out.println("Start Split : "+TimeUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			if(fh.isASCII(sSourceFilePath)){
				fh.writeSerEncFile(fh.splitFile4ASCII(sSourceFilePath, 4), sMetaPath, sMetaFile);
				System.out.println("Split END : "+TimeUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
				fh.mergeFile4ASCII(sMetaPath+sMetaFile, sASCIITargetPath);
				System.out.println("Merge END : "+TimeUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			}else{
				fh.writeSerEncFile(fh.splitFile4Binary(sSourceFilePath, 4), sMetaPath, sMetaFile);
				System.out.println("Split END : "+TimeUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
				fh.mergeFile4Binary(sMetaPath+sMetaFile, sBINTargetFile);
				System.out.println("Merge END : "+TimeUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
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

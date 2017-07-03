package com.jay.test;

import java.io.File;

import com.jay.csp.aws.AWSService;
import com.jay.csp.azure.AZUREService;
import com.jay.csp.gcp.GCPService;
import com.jay.util.CommonConst;
import com.jay.util.FileEncoding;
import com.jay.util.FileHandler;

public class JayTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String sSourcePath = "c:\\";
		String sSourceFile = "Test4TXT.txt";
		String sSourceFilePath = sSourcePath + sSourceFile;
		String sASCIITargetPath = sSourcePath + CommonConst.MERGE_STR + sSourceFile;
		String sBINTargetFile = sSourcePath + CommonConst.MERGE_STR + sSourceFile;
		
		String sMetaPath = "c:\\";
		String sMetaFile = "Meta.craid";
		
		FileHandler fh = new FileHandler();
		
		try {
//			System.out.println(fh.isBinaryFile(new File(sSourcePath)));
//			System.out.println(FileEncoding.contentIsText(new File(sSourcePath), true));
//			System.out.println(FileEncoding.contentIsText(new File(sSourcePath), false));
			if(fh.isASCII(sSourceFilePath)){
				fh.writeSerEncFile(fh.splitFile4ASCII(sSourceFilePath, 4), sMetaPath, sMetaFile);
				fh.mergeFile4ASCII(sMetaPath+sMetaFile, sASCIITargetPath);
				System.out.println("ASCII END");
			}else{
				fh.writeSerEncFile(fh.splitFile4Binary(sSourceFilePath, 4), sMetaPath, sMetaFile);
				fh.mergeFile4Binary(sMetaPath+sMetaFile, sBINTargetFile);
				System.out.println("Binary END");
			}
			AWSService AWS = new AWSService();
			AWS.uploadFile(new File(sMetaPath+sMetaFile));
			AZUREService AZS = new AZUREService();
			AZS.uploadFile(new File(sMetaPath+sMetaFile));
			GCPService GCS = new GCPService();
			GCS.uploadFile(new File(sMetaPath+sMetaFile));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

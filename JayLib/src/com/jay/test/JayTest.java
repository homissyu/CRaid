package com.jay.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.BitSet;

import com.jay.util.BitInputStream;
import com.jay.util.CommonConst;
import com.jay.util.CommonUtil;
import com.jay.util.CryptoUtils;
import com.jay.util.FileHandler;

public class JayTest {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String sSourcePath = "c:\\";
		String sSourceFileName = "test.txt";
		String sSourceFilePath = sSourcePath + sSourceFileName;
		String sTargetFilePath = sSourcePath+CommonConst.MERGE_STR+sSourceFileName;
		String sMetaFilePath = CommonConst.META_FILE_PATH+File.separator+CommonConst.META_FILE_NAME;
		
		try {
//			ArrayList<Integer> aSplitRatio = new ArrayList<Integer>();
//			for(int i=0;i<CommonConst.CSP_FREE_AMOUNT.length;i++) {
//				aSplitRatio.add(CommonConst.CSP_FREE_AMOUNT[i]);
//			}
			
//			System.out.println("Start Split : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
//			FileHandler.splitFile(sSourceFilePath, aSplitRatio);
//			System.out.println("End Split : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			
			ArrayList <BitSet> aBitSetList = new ArrayList<BitSet>();
			BitSet bsA = new BitSet();
			BitSet bsB = new BitSet();
			BitSet bsC = new BitSet();
			BitSet bsParity = new BitSet();
			BitSet bsRemaining = new BitSet();
			aBitSetList.add(bsA);
			aBitSetList.add(bsB);
			aBitSetList.add(bsC);
			aBitSetList.add(bsParity);
			aBitSetList.add(bsRemaining);
			
			String originStr = "동해물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라 만세  무궁화 삼천리 화려강산 대한사람 대한으로 길이 보전하세.";
//			originStr = "Hello world!";
			
			byte [] tempByteArr = originStr.getBytes();
			System.out.println("tempByteArr.length:"+tempByteArr.length);
			long sourceSize = tempByteArr.length;
			long remainingBytes = sourceSize % (aBitSetList.size()-2);
			System.out.println("remainingBytes:"+remainingBytes);
			long bytesPerSplit = sourceSize/(aBitSetList.size()-2);
//			if((sourceSize % 3) != 0)bytesPerSplit = bytesPerSplit+1;
//			System.out.println("bytesPerSplit:"+bytesPerSplit);
			
			byte [] tempA = new byte[(int) bytesPerSplit];
			byte [] tempB = new byte[(int) bytesPerSplit];
			byte [] tempC = new byte[(int) bytesPerSplit];
			byte [] tempRemaining = new byte[(int) remainingBytes];
			byte [] tempParity = new byte[(int) bytesPerSplit];
			
			System.arraycopy(tempByteArr, 0, tempA, 0, (int) bytesPerSplit);
			System.arraycopy(tempByteArr, tempA.length, tempB, 0, (int) bytesPerSplit);
			System.arraycopy(tempByteArr, tempA.length+tempB.length, tempC, 0, (int) bytesPerSplit);
			System.arraycopy(tempByteArr, tempA.length+tempB.length+tempC.length, tempRemaining, 0, (int) remainingBytes);
			
			
			
			
//			bsA = BitSet.valueOf(tempA);
//			bsB = BitSet.valueOf(tempB);
//			bsC = BitSet.valueOf(tempC);
//			bsParity = new BitSet();
//			bsRemaining = BitSet.valueOf(tempRemaining);
//			
//			for(int i=0;i<bsA.length();i++) {
//				bsParity.set(i, bsA.get(i)^bsB.get(i)^bsC.get(i));
////				System.out.println(i+":"+bsA.get(i)+":"+bsB.get(i)+":"+bsC.get(i)+"=======>"+bsParity.get(i));
//			}
//			
//			byte [] recoverA = bsA.toByteArray();
//			byte [] recoverB = bsB.toByteArray();
//			byte [] recoverC = bsC.toByteArray();
//			byte [] recoverRemaining = bsRemaining.toByteArray();
//			byte [] recoverTTL = new byte[recoverA.length+recoverB.length+recoverC.length+recoverRemaining.length];
//			
//			System.arraycopy(recoverA, 0, recoverTTL, 0, recoverA.length);
//			System.arraycopy(recoverB, 0, recoverTTL, recoverA.length, recoverB.length);
//			System.arraycopy(recoverC, 0, recoverTTL, recoverA.length+recoverB.length, recoverC.length);
//			System.arraycopy(recoverRemaining, 0, recoverTTL, recoverA.length+recoverB.length+recoverC.length, recoverRemaining.length);
//			
//			System.out.println(new String(recoverTTL));
//			
//			recoverB = null;
//			recoverB = new byte[(int) bytesPerSplit];
//			
//			recoverTTL = null;
//			recoverTTL = new byte[recoverA.length+recoverB.length+recoverC.length+recoverRemaining.length];
//			
//			System.arraycopy(recoverA, 0, recoverTTL, 0, recoverA.length);
//			System.arraycopy(recoverB, 0, recoverTTL, recoverA.length, recoverB.length);
//			System.arraycopy(recoverC, 0, recoverTTL, recoverA.length+recoverB.length, recoverC.length);
//			System.arraycopy(recoverRemaining, 0, recoverTTL, recoverA.length+recoverB.length+recoverC.length, recoverRemaining.length);
//			
//			System.out.println(new String(recoverTTL));
//			
//			for(int i=0;i<bsParity.length();i++) {
//				bsB.set(i, bsA.get(i)^bsC.get(i)^bsParity.get(i));
//			}
//			
//			recoverB = bsB.toByteArray();
//			System.arraycopy(recoverB, 0, recoverTTL, recoverA.length, recoverB.length);
//		
//			System.out.println(new String(recoverTTL));
			
			
			for(int i=0;i<tempParity.length;i++) {
				tempParity[i]=(byte) (tempA[i]^tempB[i]^tempC[i]);
				System.out.println(i+":"+(char)tempA[i]+":"+(char)tempB[i]+":"+(char)tempC[i]+"=======>"+(char)tempParity[i]);
			}
			
			byte [] recoverTTL = new byte[tempA.length+tempB.length+tempC.length+tempRemaining.length];
			
			System.arraycopy(tempA, 0, recoverTTL, 0, tempA.length);
			System.arraycopy(tempB, 0, recoverTTL, tempA.length, tempB.length);
			System.arraycopy(tempC, 0, recoverTTL, tempA.length+tempB.length, tempC.length);
			System.arraycopy(tempRemaining, 0, recoverTTL, tempA.length+tempB.length+tempC.length, tempRemaining.length);
			
			System.out.println(new String(recoverTTL));
			
			tempB = null;
			tempB = new byte[(int) bytesPerSplit];
			
			recoverTTL = null;
			recoverTTL = new byte[tempA.length+tempB.length+tempC.length+tempRemaining.length];
			
			System.arraycopy(tempA, 0, recoverTTL, 0, tempA.length);
			System.arraycopy(tempB, 0, recoverTTL, tempA.length, tempB.length);
			System.arraycopy(tempC, 0, recoverTTL, tempA.length+tempB.length, tempC.length);
			System.arraycopy(tempRemaining, 0, recoverTTL, tempA.length+tempB.length+tempC.length, tempRemaining.length);
			
			System.out.println(new String(recoverTTL));
			
			for(int i=0;i<tempB.length;i++) {
				tempB[i]=(byte) (tempA[i]^tempParity[i]^tempC[i]);
			}
			
			System.arraycopy(tempB, 0, recoverTTL, tempA.length, tempB.length);
			
			System.out.println(new String(recoverTTL));
			
			
			PublicKey pubKey1 = CryptoUtils.getKeyPair("1").getPublic();
			PublicKey pubKey2 = CryptoUtils.getKeyPair("1").getPublic();
			PrivateKey key1 = CryptoUtils.getKeyPair("1").getPrivate();
			PrivateKey key2 = CryptoUtils.getKeyPair("1").getPrivate();
			
			System.out.println(key1!=null&&key2!=null&&key1.equals(key2));
			System.out.println(pubKey1!=null&&pubKey2!=null&&pubKey1.equals(pubKey2));
			
//			System.out.println("Start Merge : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
//			FileHandler.mergeFile(sMetaFilePath, sTargetFilePath);
//			System.out.println("End Merge : "+CommonUtil.getCurrentTime(CommonConst.DATETIME_FORMAT));
			
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

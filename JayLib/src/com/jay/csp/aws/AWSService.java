package com.jay.csp.aws;

import java.io.File;
import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class AWSService {
    private static final String BUCKET_NAME = "homissyu";
    private static final String ACCESS_KEY = "AKIAIY2Z7GV34G4KZISA";
    private static final String SECRET_KEY = "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";
 
    private static String bucketName     = BUCKET_NAME;;
	private static String keyName        = ACCESS_KEY;
	
	public static void uploadFile(File file) throws IOException {
        AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());
        try {
            System.out.println("Uploading a new object to S3 from a file\n");
            
            s3client.putObject(new PutObjectRequest(
            		                 bucketName, file.getAbsolutePath(), file));
            System.out.println("Uploaded Complete\n");
         } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
            		"means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
            		"means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
}
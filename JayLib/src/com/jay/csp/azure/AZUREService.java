package com.jay.csp.azure;

import java.io.File;
import java.io.FileInputStream;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

public class AZUREService {
	public static final String storageConnectionString =
		    "DefaultEndpointsProtocol=http;" +
		    "AccountName=homissyu;" +
		    "AccountKey=jkzsPBCmvINMZ5/Rn8CnHOBMTcOH2nlj6qIMz9FICkjYhh4iH6RhS1VLMrW0/vjYkxpAmgwQus37xZ/VfA3y9Q==";
	
	public void uploadFile(File file){
		try{
			System.out.println("Uploading a new object to AZURE from a file\n");
		    // Retrieve storage account from connection-string.
		    CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

		    // Create the blob client.
		    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

		    // Retrieve reference to a previously created container.
		    CloudBlobContainer container = blobClient.getContainerReference("homissyu");

		    // Define the path to a local file.
		    final String filePath = file.getAbsolutePath();

		    // Create or overwrite the "myimage.jpg" blob with contents from a local file.
		    CloudBlockBlob blob = container.getBlockBlobReference(file.getName());
		    blob.upload(new FileInputStream(file), file.length());
		    System.out.println("Uploaded Complete\n");
		}catch (Exception e){
		    // Output the stack trace.
		    e.printStackTrace();
		    System.out.println("Caught an AmazonServiceException, which " +
            		"means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + e.getMessage());
		}
	}
}

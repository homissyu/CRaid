package com.jay.csp.gcp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFileOptions.Builder;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

public class GCPService{
	public void uploadFile(File file) throws IOException{
		 // read the input stream
	    byte[] buffer = new byte[1024];
	    List<byte[]> allBytes = new LinkedList<byte[]>();
	    FileInputStream reader = new FileInputStream(file);
	    while(true) {
	        int bytesRead = reader.read(buffer);
	        if (bytesRead == -1) {
	            break; // have a break up with the loop.
	        } else if (bytesRead < 1024) {
	            byte[] temp = Arrays.copyOf(buffer, bytesRead);
	            allBytes.add(temp);
	        } else {
	            allBytes.add(buffer);
	        }
	    }

	    // init the bucket access
	    GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
	    GcsFilename filename = new GcsFilename("homissyu", file.getName());
	    Builder fileOptionsBuilder = new GcsFileOptions.Builder();
//	    fileOptionsBuilder.mimeType("text/html"); // or "image/jpeg" for image files
	    GcsFileOptions fileOptions = fileOptionsBuilder.build();
	    GcsOutputChannel outputChannel = gcsService.createOrReplace(filename, fileOptions);

	    // write file out
	    BufferedOutputStream outStream = new BufferedOutputStream(Channels.newOutputStream(outputChannel));
	    for (byte[] b : allBytes) {
	        outStream.write(b);
	    }
	    outStream.close();
	    outputChannel.close();
	    reader.close();
	}
}
	
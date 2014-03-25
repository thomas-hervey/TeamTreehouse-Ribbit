package com.teamtreehouse.ribbit;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 			  This class supports the MainActivity.java class by providing
 * 			  getting media byte array information and checking media for
 * 			  use requirements.
 *
 * 			  This project was created while following the teamtreehouse.com
 * 			  Build a Self-Destructing Message Android App project
 *
 * @version   Completed Feb 18, 2014
 * @author    Thomas Hervey <thomasahervey@gmail.com>
 */
public class FileHelper {
	
	public static final String TAG = FileHelper.class.getSimpleName();
	
	public static final int SHORT_SIDE_TARGET = 1280;

    /**
     * Getter method opening input stream, retrieving byte stream,
     * writing out the media, and then closing the stream.
     *
     * @param context
     * @param uri
     * @return byte[] fileBytes - bytes from file
     */
	public static byte[] getByteArrayFromFile(Context context, Uri uri) {
		byte[] fileBytes = null;
        InputStream inStream = null;
        ByteArrayOutputStream outStream = null;
        
        if (uri.getScheme().equals("content")) {
        	try {
        		inStream = context.getContentResolver().openInputStream(uri);
        		outStream = new ByteArrayOutputStream();
            
        		byte[] bytesFromFile = new byte[1024*1024]; // buffer size (1 MB)
        		int bytesRead = inStream.read(bytesFromFile);
        		while (bytesRead != -1) {
        			outStream.write(bytesFromFile, 0, bytesRead);
        			bytesRead = inStream.read(bytesFromFile);
        		}
            
        		fileBytes = outStream.toByteArray();
        	}
	        catch (IOException e) {
	        	Log.e(TAG, e.getMessage());
	        }
	        finally {
	        	try {
	        		inStream.close();
	        		outStream.close();
	        	}
	        	catch (IOException e) { /*( Intentionally blank */ }
	        }
        }
        else {
        	try {
	        	File file = new File(uri.getPath());
	        	FileInputStream fileInput = new FileInputStream(file);
	        	fileBytes = IOUtils.toByteArray(fileInput);
        	}
        	catch (IOException e) {
        		Log.e(TAG, e.getMessage());
        	}
       	}
        
        return fileBytes;
	}

    /**
     * Reduce media size to 10MB required for back end usage
     *
     * @param imageData
     * @return byte[] - reduced data array
     */
	public static byte[] reduceImageForUpload(byte[] imageData) {
		// Convert file to bitmap
		Bitmap bitmap = ImageResizer.resizeImageMaintainAspectRatio(imageData, SHORT_SIDE_TARGET);
		
		// Alter bitmap information & convert to .png
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
		byte[] reducedData = outputStream.toByteArray();
		try {
			outputStream.close();
		}
		catch (IOException e) {
			// Intentionally blank
		}
		
		return reducedData;
	}

    /**
     * Getter method for retrieving the type of media file for display
     *
     * @param context
     * @param uri
     * @param fileType
     * @return String fileName - upload file name
     */
	public static String getFileName(Context context, Uri uri, String fileType) {
		String fileName = "uploaded_file.";
		
		if (fileType.equals(ParseConstants.TYPE_IMAGE)) {
			fileName += "png";
		}
		else {
			// For video, we want to get the actual file extension
			if (uri.getScheme().equals("content")) {
				// do it using the mime type
				String mimeType = context.getContentResolver().getType(uri);
				int slashIndex = mimeType.indexOf("/");
				String fileExtension = mimeType.substring(slashIndex + 1);
				fileName += fileExtension;
			}
			else {
				fileName = uri.getLastPathSegment();
			}
		}
		
		return fileName;
	}
}
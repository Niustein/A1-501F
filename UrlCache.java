/**
 * Assignment #1 - Refactoring
 * CPSC 501
 * Samuel Niu
 * 10047006
 */

import java.net.Socket;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.io.*;
import java.util.*;

public class UrlCache {

	HashMap <String, String> catalog;
	
    /**
     * Default constructor to initialize data structures used for caching/etc
	 * If the cache already exists then load it. If any errors then throw runtime exception.
	 *
     * @throws IOException if encounters any errors/exceptions
     */
	public UrlCache() throws IOException {		
		try {
			ObjectInputStream ObjectInputStream = new ObjectInputStream(new FileInputStream("catalogLoc"));
			catalog = (HashMap<String,String>) ObjectInputStream.readObject();
			ObjectInputStream.close();
		} catch (FileNotFoundException e) {
			catalog = new HashMap<String, String>();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	
    /**
     * Downloads the object specified by the parameter url if the local copy is out of date.
	 *
     * @param url	URL of the object to be downloaded. It is a fully qualified URL.
     * @throws IOException if encounters any errors/exceptions
     */
	public void getter(String url) throws IOException {
		
		String hostName;
		String pathName;
		int portNum;
		int findSplitIndex;
		int findColon;
		
		InputStream inputStream;
		PrintWriter outputStream;
		

		byte[] http_response_header_bytes = new byte[2048];
		byte[] http_object_bytes = new byte[1024];
		String httpResString = "";
		int indexCount = 0;
		int numByteRead = 0;
		
		Scanner headerS = new Scanner(httpResString);
		int totalLength = 0;
		String lastModified = "";
		String nextL = "";
		

		
		// find Index of : and / for url splitting
		findColon = url.indexOf(":");
		findSplitIndex = url.indexOf("/");
		
		// Set hostname/portNum based on above variables
		if(findColon == -1) {
			hostName = url.substring(0, findSplitIndex);	
			portNum = 80;
		} else {
			hostName = url.substring(0, findColon);
			portNum = (int) Integer.parseInt(url.substring(findColon + 1, findSplitIndex));
		}
		
		// Set pathname
		pathName = url.substring(findSplitIndex);
	
		// Create file structures
		File newFile = new File(hostName + pathName);
		newFile.getParentFile().mkdirs();
		FileOutputStream newFOS = new FileOutputStream(newFile);
		
		FileOutputStream modifiedFoS = new FileOutputStream("catalogLoc");
		ObjectOutputStream oosMod = new ObjectOutputStream(modifiedFoS);

		try {
			
			// Creating socket, opening input/output streams
			Socket socket = new Socket(hostName, portNum);
			
			outputStream = new PrintWriter(new DataOutputStream(
					socket.getOutputStream()));
			inputStream = socket.getInputStream();			
						
			outputStream.print("GET " + pathName + " HTTP/1.1\r\n");
			outputStream.print("If-modified-since: " + lastModified + "\r\n");
			outputStream.print("Host: " + hostName + ":" + portNum + "\r\n");
			outputStream.print("\r\n");
			
			outputStream.flush();
			
			try {
				//Read until end of HTTP header and then break
				while(true) {
					socket.getInputStream().read(http_response_header_bytes, indexCount, 1);
					indexCount++;
					httpResString = new String(http_response_header_bytes, 0, indexCount, "US-ASCII");
					if(httpResString.contains("\r\n\r\n")){
						break;
					}
				}
				
				
  			} catch (IOException e) {
  				//Exception Handling
  			}
			
			// Line by Line - Get total length of content and the last modified date
			while(headerS.hasNextLine()) {
				nextL = headerS.nextLine();
				
				if(nextL.contains("Content-Length")) {
					totalLength = Integer.parseInt(nextL.substring(nextL.indexOf(":") + 2));
				}
				
				if(nextL.contains("Last-Modified")) {
					lastModified = nextL.substring(nextL.indexOf(":") + 2);
				}		
			}
			
			if(httpResString.contains("304 Not Modified")) {
				// File has not been changed and is already downloaded, do nothing
			} else if (httpResString.contains("200 OK")) {
				int count = 0; 				// Initialize byte counter
								
				try {
					//Read until end of file
					while(numByteRead != -1) {
						if(count == totalLength) {
							break;
						}
						
						numByteRead = socket.getInputStream().read(http_object_bytes);
						newFOS.write(http_object_bytes);
						count += numByteRead;
						System.out.println(count);
					}
					newFOS.close();
				} catch(IOException e) {
					// didn't DL file
				}
				
				catalog.put(url, lastModified);
				oosMod.writeObject(catalog);
				oosMod.flush();
				oosMod.close();
				
			}
			
			
			socket.close();
			headerS.close();
	
			
		  } catch (Exception e) {
			  System.out.println("Error: " + e.getMessage());
		  }
			

		
	}
	
    /**
     * Returns the Last-Modified time associated with the object specified by the parameter url.
	 *
     * @param url 	URL of the object 
	 * @return the Last-Modified time in millisecond as in Date.getTime()
     */
	public long LaMo(String url, int counter) throws RuntimeException {

		if(catalog.containsKey(url)) {
			int counter;
			String lastModified = catalog.get(url);
			SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss zzz");
			Date date = format.parse(lastModified, new ParsePosition(0));
			long millis = date.getTime();
			
			return millis;			
		} else {
			throw new RuntimeException();
		}

	}
}

package A1;

/**
 * Assignment #1 - Refactoring
 * CPSC 501
 * Samuel Niu
 * 10047006
 */
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;


public class UrlCache {

	HashMap <String, String> catalog;
	private String httpResString = "";
	
    /**
     * Default constructor to initialize data structures used for caching/etc
	 * If the cache already exists then load it. If any errors then throw runtime exception.
	 *
     * @throws IOException if encounters any errors/exceptions
     */
	@SuppressWarnings("unchecked")
	public UrlCache(boolean ignoreCata) throws IOException {		
		try {
			ObjectInputStream ObjectInputStream = new ObjectInputStream(new FileInputStream("catalog"));
			catalog = (HashMap<String,String>) ObjectInputStream.readObject();
			ObjectInputStream.close();
		} catch (FileNotFoundException e) {
			catalog = new HashMap<String, String>();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		if (ignoreCata) {
			catalog = new HashMap<String, String>();
		}
		
	}

	public void getNames(String url, String hostName, String pathName, int portNum) {

	}
    /**
     * Downloads the object specified by the parameter url if the local copy is out of date.
	 *
     * @param url	URL of the object to be downloaded. It is a fully qualified URL.
     * @throws IOException if encounters any errors/exceptions
     */
	public void getter(String url) throws IOException {
		
		
		
		// Initialize input and output streams
		PrintWriter outputStream;
				
		// Initialize values pulled from header
		String catalogModifiedDate = "";



		byte[] http_object_bytes = new byte[4096];
		int numByteRead = 0;
		
		
		byte[] http_response_header_bytes = new byte[4096];
		int indexCount = 0;
		
		String hostName = hostNameParser(url);
		String pathName = pathNameParser(url);
		int portNum = portNumParser(url);

		try {
			
			// Creating socket, opening input/output streams
			Socket socket = new Socket(hostName, portNum);
			
			outputStream = new PrintWriter(new DataOutputStream(socket.getOutputStream()));
			
			//Gets the last modified date from the catalog
			if(catalog.containsKey(url)) {
				catalogModifiedDate = catalog.get(url);
			}
			
			// HTTP GET request and then checks if catalog has been modified
			outputStream.print("GET " + pathName + " HTTP/1.1\r\n");
			outputStream.print("If-modified-since: " + catalogModifiedDate + "\r\n");
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

			Scanner hScanner = new Scanner(httpResString);
			
			headerParserClass parseH = new headerParserClass(hScanner);
			int totalLength = parseH.getTotalLength();
			String lastModified = parseH.getLastModified();
			
			System.out.println(totalLength);
			System.out.println(lastModified);
			
			// File has not bhttpResStringeen changed and is already downloaded, do nothing
			if(httpResString.contains("304 Not Modified")) {
				System.out.println("Files from " + url + " have already been downloaded and have not been changed\n");
			} else if (httpResString.contains("200 OK")) {
				int count = 0; 				// Initialize byte counter
						
				System.out.println("Downloading Objects from " + url);
				// Create file structures
				File newFile = new File(hostName + pathName);
				newFile.getParentFile().mkdirs();
				FileOutputStream newFOS = new FileOutputStream(newFile);
				
				try {
					//Read until end of file
					while(true) {
						// At end of file, break out of loop
						if(count == totalLength) {
							break;
						}
						
						//Read bytes to output stream and increment count by number of bytes read
						numByteRead = socket.getInputStream().read(http_object_bytes);
						newFOS.write(http_object_bytes);
						newFOS.flush();
						newFOS.getFD().sync();
						count += numByteRead;
					}
					newFOS.close();
				} catch(IOException e) {
					// didn't DL file
					System.out.println("Caught IO Exception");
				}
				
				
				ObjectOutputStream oosMod = new ObjectOutputStream(new FileOutputStream("catalog"));
								
				// Write to catalog and close the object output stream
				catalog.put(url, lastModified);
				oosMod.writeObject(catalog);
				oosMod.flush();
				oosMod.close();
			}
		
			// Close the socket and headerS
			socket.close();
			hScanner.close();
	
			
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
			String lastModified = catalog.get(url);
			SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss zzz");
			Date date = format.parse(lastModified, new ParsePosition(0));
			long millis = date.getTime();
			
			return millis;			
		} else {
			throw new RuntimeException();
		}

	}
	
	public String hostNameParser(String url) {
		String hostName;
		
		int forwardSlashIndex = url.indexOf("/");
		int firstColonIndex = url.indexOf(":");
		
		if(firstColonIndex == -1) {
			hostName = url.substring(0, forwardSlashIndex);	
		} else {
			hostName = url.substring(0, firstColonIndex);
		}
		return hostName;
	}
	
	public String pathNameParser(String url) {

		int forwardSlashIndex = url.indexOf("/");
		
		return url.substring(forwardSlashIndex);
	}
	
	
	public int portNumParser(String url) {
		int portNum = 0;
		
		int firstColonIndex = url.indexOf(":");
		int forwardSlashIndex = url.indexOf("/");
		
		if(firstColonIndex == -1) {
			portNum = 80;
		} else {
			portNum = (int) Integer.parseInt(url.substring(firstColonIndex + 1, forwardSlashIndex));
		}
		
		return portNum;
	}
	
	
	public String gethttpResString() {
		return httpResString;
	}
}

package A1;

import java.util.Scanner;

public class headerParserClass {

	String nextL = "";
	int totalLength = 0;
	String lastModified = "";
	
	
	public headerParserClass(Scanner hScanner){
		parseHeader(hScanner);
	}
	
	public void parseHeader(Scanner hScanner){
	
		while(hScanner.hasNextLine()) {
			nextL = hScanner.nextLine();
			
			//Check if line contains Content-Length and passes the int into totalLength if it does
			if(nextL.contains("Content-Length")) {
				totalLength = Integer.parseInt(nextL.substring(nextL.indexOf(":") + 2));
			}
			
			// Check if line contains "Last-Modified" and passes the string into lastModified if it does
			if(nextL.contains("Last-Modified")) {
				lastModified = nextL.substring(nextL.indexOf(":") + 2);
			}		
		}
	}
	
	public int getTotalLength() {
		return totalLength;
	}
	
	public String getLastModified() {
		return lastModified;
	}
}


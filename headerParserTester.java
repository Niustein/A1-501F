package A1;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Scanner;

import org.junit.Test;

public class headerParserTester {

	@Test
	public void testHeaderParser() {
		
		UrlCache cache;
		try {
			cache = new UrlCache(true);
			cache.retrieveObject("people.ucalgary.ca/~mghaderi/index.html");
			headerParserClass HPC = new headerParserClass(new Scanner (cache.gethttpResString()));
			
			assertEquals(5974 , HPC.getTotalLength());
			assertEquals("Fri, 18 Aug 2017 20:44:24 GMT", HPC.getLastModified());
			
			cache.retrieveObject("people.ucalgary.ca/~mghaderi/test/uc.gif");
			HPC = new headerParserClass(new Scanner (cache.gethttpResString()));

			assertEquals(3090 , HPC.getTotalLength());
			assertEquals("Fri, 31 Aug 2007 04:21:06 GMT", HPC.getLastModified());
			
			cache.retrieveObject("people.ucalgary.ca/~mghaderi/test/a.pdf");
			HPC = new headerParserClass(new Scanner (cache.gethttpResString()));

			assertEquals(479301 , HPC.getTotalLength());
			assertEquals("Fri, 24 Jul 2015 20:05:00 GMT", HPC.getLastModified());
			
			cache.retrieveObject("people.ucalgary.ca:80/~mghaderi/test/test.html");
			HPC = new headerParserClass(new Scanner (cache.gethttpResString()));

			assertEquals(35 , HPC.getTotalLength());
			assertEquals("Fri, 02 Oct 2015 21:23:47 GMT", HPC.getLastModified());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		

	}
}

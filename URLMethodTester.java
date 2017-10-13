package A1;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class URLMethodTester {

	@Test
	public void testHeaderParser() {
			
		UrlCache cache;
		try {
			cache = new UrlCache(true);
			String url = "people.ucalgary.ca/~mghaderi/index.html";
			
			assertEquals("people.ucalgary.ca", cache.hostNameParser(url));
			assertEquals("/~mghaderi/index.html", cache.pathNameParser(url));
			assertEquals(80, cache.portNumParser(url));

			url = "people.ucalgary.ca/~mghaderi/test/uc.gif";
			
			assertEquals("people.ucalgary.ca", cache.hostNameParser(url));
			assertEquals("/~mghaderi/test/uc.gif", cache.pathNameParser(url));
			assertEquals(80, cache.portNumParser(url));
			
			url = "people.ucalgary.ca/~mghaderi/test/a.pdf";
			
			assertEquals("people.ucalgary.ca", cache.hostNameParser(url));
			assertEquals("/~mghaderi/test/a.pdf", cache.pathNameParser(url));
			assertEquals(80, cache.portNumParser(url));
			
			url = "people.ucalgary.ca:80/~mghaderi/test/test.html";

			assertEquals("people.ucalgary.ca", cache.hostNameParser(url));
			assertEquals("/~mghaderi/test/test.html", cache.pathNameParser(url));
			assertEquals(80, cache.portNumParser(url));
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}



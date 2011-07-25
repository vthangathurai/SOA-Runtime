package org.ebayopensource.turmeric.runtime.tests.failover;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class ServiceLocationAlgoTest extends TestCase {

	public void testGetLocations() throws Exception{
		List<URL> urls = new ArrayList<URL>();
		urls.add(new URL("http://url1"));
		urls.add(new URL("http://url2"));
		// there was no holder b4 this call, so this should be created		
		ServiceLocationHolder svcHolder = ServiceLocationFactory.getInstance().getServiceLocationHolder(urls, "cons", "svc");
		assertEquals(2, svcHolder.getNumOfAddresses());
		//go through the addresses
		URL u = svcHolder.getCurrentAddress();
		assertEquals("http://url1", u.toString());
		svcHolder.setInvokeSuccess(false);
		u = svcHolder.getCurrentAddress();
		assertEquals("http://url2", u.toString());
		// has it cycled through?
		svcHolder.setInvokeSuccess(false);
		assertTrue(svcHolder.cycledThrough());
		// go back to 1
		svcHolder.resetLocationCycling();
		u = svcHolder.getCurrentAddress();
		assertEquals("http://url1", u.toString());
		
		// now, set the locations to something else
		List<URL> urlsNew = new ArrayList<URL>();
		urlsNew.add(new URL("http://url3"));
		svcHolder.setLocations(urlsNew);
		assertEquals(1, svcHolder.getNumOfAddresses());
		u = svcHolder.getCurrentAddress();
		assertEquals("http://url3", u.toString());
		// has it cycled through? shd not matter since theres only 1 url
		svcHolder.setInvokeSuccess(false);
		assertTrue(svcHolder.cycledThrough());
		u = svcHolder.getCurrentAddress();
		assertEquals("http://url3", u.toString());
		
		// if we get this same svcholder back, we should get the new locations
		// even if we sent in a different list
		svcHolder = ServiceLocationFactory.getInstance().getServiceLocationHolder(urls, "cons", "svc");
		assertEquals(1, svcHolder.getNumOfAddresses());
		u = svcHolder.getCurrentAddress();
		assertEquals("http://url3", u.toString());
	}
	
	public void testSetLocation() throws Exception{
		List<URL> urls = new ArrayList<URL>();
		urls.add(new URL("http://url1"));
		urls.add(new URL("http://url2"));
		// there was no holder b4 this call, so this should be created and stored
		ServiceLocationHolder svcHolder = ServiceLocationFactory.getInstance().setServiceLocations(urls, "cons1", "svc1");
		assertEquals(2, svcHolder.getNumOfAddresses());
		ServiceLocationHolder svcHolder1 = ServiceLocationFactory.getInstance().getServiceLocationHolder(urls, "cons1", "svc1");
		assertEquals(2, svcHolder1.getNumOfAddresses());
		
		// they shd be the same
		assertEquals(svcHolder1, svcHolder);

		// now, overwrite
		List<URL> urlsNew = new ArrayList<URL>();
		urlsNew.add(new URL("http://url3"));
		ServiceLocationFactory.getInstance().setServiceLocations(urlsNew, "cons1", "svc1");
		svcHolder = ServiceLocationFactory.getInstance().getServiceLocationHolder(urls, "cons1", "svc1");
		assertEquals(1, svcHolder.getNumOfAddresses());
		URL u = svcHolder.getCurrentAddress();
		assertEquals("http://url3", u.toString());

	}

	
}

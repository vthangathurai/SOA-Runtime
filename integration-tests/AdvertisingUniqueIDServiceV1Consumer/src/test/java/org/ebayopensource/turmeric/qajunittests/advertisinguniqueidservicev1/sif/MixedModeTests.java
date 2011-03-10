package org.ebayopensource.turmeric.qajunittests.advertisinguniqueidservicev1.sif;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import junit.framework.Assert;

import org.ebayopensource.turmeric.advertising.v1.services.EchoMessageRequest;
import org.ebayopensource.turmeric.advertising.v1.services.TestEnhancedRest;
import org.ebayopensource.turmeric.advertising.v1.services.TestPrimitiveTypesRequest;
import org.ebayopensource.turmeric.advertisinguniqueservicev1.AdvertisingUniqueIDServiceV1SharedConsumer;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.common.types.SOAConstants;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithServerQETest;
import org.ebayopensource.turmeric.runtime.tests.common.util.HttpTestClient;
import org.junit.Test;



public class MixedModeTests extends AbstractWithServerQETest {
	public static HttpTestClient http = HttpTestClient.getInstance();
	public static String testURL = "";
	public Map<String, String> queryParams = new HashMap<String, String>();
	String response = null;
	@Test
	public void testMixedModePositiveCaseRemoteMode_2()  {
		try {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode2");
		EchoMessageRequest param0 = new EchoMessageRequest();
		param0.setIn("Foo");
		client.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1/"));
		Assert.assertEquals(client.echoMessage(param0).getOut(), " Echo Message = Foo");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertFalse(true);
		}
	}
	@Test
	public void testMixedModePositiveCaseRemoteMode() throws ServiceException, MalformedURLException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode1");
		EchoMessageRequest param0 = new EchoMessageRequest();
		param0.setIn("Foo");
		client.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/ws/spf?in=Remote"));
//		System.out.println(client.echoMessage(param0).getOut());
		Assert.assertEquals(client.echoMessage(param0).getOut(), " Echo Message = Remote");
	}
	
	
	@Test
	public void testMixedModePositiveCaseRemoteModeWithGetEnabled() throws ServiceException, MalformedURLException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode2");
		EchoMessageRequest param0 = new EchoMessageRequest();
		param0.setIn("Foo");
//		client.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1?in=Remote"));
		client.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1/"));
		client.getServiceInvokerOptions().setREST(Boolean.TRUE);
//		System.out.println(client.echoMessage(param0).getOut());
		Assert.assertEquals(client.echoMessage(param0).getOut(), " Echo Message = Foo");
	}
	@Test
	public void testMixedModePositiveCaseRemoteModeWithURL() throws ServiceException, MalformedURLException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode2");
		EchoMessageRequest param0 = new EchoMessageRequest();
		param0.setIn("Foo");
		client.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1/"));
//		System.out.println(client.echoMessage(param0).getOut());
		Assert.assertEquals(client.echoMessage(param0).getOut(), " Echo Message = Foo");
	}
	@Test
	public void testMixedModePositiveCaseRemoteModeWithURLGet() throws ServiceException, MalformedURLException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode1");
		client.getServiceInvokerOptions().setREST(Boolean.TRUE);
		EchoMessageRequest param0 = new EchoMessageRequest();
		param0.setIn("Foo");
		client.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/ws/spf/"));
		
//		System.out.println(client.echoMessage(param0).getOut());
		Assert.assertEquals(client.echoMessage(param0).getOut(), " Echo Message = Foo");
	}
	
	@Test
	public void testMixedModePositiveCaseLocalMode() throws ServiceException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode1");
		EchoMessageRequest param0 = new EchoMessageRequest();
		param0.setIn("Local");
		client.getServiceInvokerOptions().setTransportName(SOAConstants.TRANSPORT_LOCAL);
//		System.out.println(client.echoMessage(param0).getOut());
		Assert.assertEquals(client.echoMessage(param0).getOut(), " Echo Message = Local");
	}
	@Test
	public void testMixedModePositiveCaseRESTMode() throws ServiceException {
		queryParams.put("X-TURMERIC-OPERATION-NAME","echoMessage");
		queryParams.put("X-TURMERIC-SERVICE-NAME","AdvertisingUniqueIDServiceV1");
		queryParams.put("in(0)","Foo");	
		String url = serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1/";
		String response = http.getResponse(url, queryParams);
//		System.out.println(response);
		Assert.assertTrue(response.contains("Echo Message "));
	
	}
	@Test
	public void testMixedModePositiveCaseOtherOperationRemote() throws ServiceException, MalformedURLException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode2");
		TestEnhancedRest param0 = new TestEnhancedRest();
		param0.getIn().add(0, "MixedMode");
		client.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1/"));
//		System.out.println(client.testEnhancedRest(param0).getOut());
//		Assert.assertEquals(client.testEnhancedRest(param0).getOut(),"MixedMode" );
	}
	@Test
	public void testMixedModePositiveCaseOtherOperationLocal() throws ServiceException, MalformedURLException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode2");
		client.getServiceInvokerOptions().setTransportName(SOAConstants.TRANSPORT_LOCAL);
		
		TestEnhancedRest param0 = new TestEnhancedRest();
		param0.getIn().add(0, "MixedMode");
		client.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1/"));
//		System.out.println(client.testEnhancedRest(param0).getOut());
		Assert.assertEquals(client.testEnhancedRest(param0).getOut(),"MixedMode" );
	}
	@Test
	public void testMixedModePositiveCaseOtherOperationLocal1() throws ServiceException, MalformedURLException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "local");
//		client.getServiceInvokerOptions().setTransportName(SOAConstants.TRANSPORT_LOCAL);
		
		TestEnhancedRest param0 = new TestEnhancedRest();
		param0.getIn().add(0, "MixedMode");
		System.out.println(client.testEnhancedRest(param0).getOut());
		Assert.assertEquals(client.testEnhancedRest(param0).getOut(),"MixedMode" );
	}
//	@Test
//	public void testMixedModePositiveCaseOtherOperationREST() throws ServiceException {
//		http.port = serverUri.toASCIIString().substring(17);
//		queryParams.put("X-TURMERIC-OPERATION-NAME","testEnhancedRest");
//		queryParams.put("X-TURMERIC-SERVICE-NAME","AdvertisingUniqueIDServiceV1");
//		
//		queryParams.put("in(0)","Foo");	
//		String url = serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1/";
//		String response = http.getResponse(url, queryParams);
//		System.out.println(response);
//		Assert.assertTrue(response.contains("<out>Foo</out>"));
//	}
	@Test
	public void testMixedModePrimitiveTypebyte() throws ServiceException, MalformedURLException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode2");
		client.getService().getInvokerOptions().setTransportName(SOAConstants.TRANSPORT_HTTP_11);

		client.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1?typeByte=123"));
//		client.getServiceInvokerOptions().setREST(true);
		TestPrimitiveTypesRequest request = new TestPrimitiveTypesRequest();
		byte b = 12;
		request.setTypeByte(b);
		Assert.assertEquals("From Server 123", client.testPrimitiveTypes(request).getOut());
	}

	@Test
	public void testMixedModePrimitiveTypeshort() throws ServiceException, MalformedURLException, DatatypeConfigurationException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode2");
		client.getService().getInvokerOptions().setTransportName(SOAConstants.TRANSPORT_HTTP_11);
		client.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1?typeShort=12345"));
		TestPrimitiveTypesRequest request = new TestPrimitiveTypesRequest();
		short s = 123;
		request.setTypeShort(s);
		Assert.assertEquals("From Server 12345", client.testPrimitiveTypes(request).getOut());
	}
	
	@Test
	public void testMixedModePrimitiveTypeint() throws ServiceException, MalformedURLException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode2");
		client.getService().getInvokerOptions().setTransportName(SOAConstants.TRANSPORT_HTTP_11);
		client.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1?typeInt=12345"));

		TestPrimitiveTypesRequest request = new TestPrimitiveTypesRequest();
		request.setTypeInt(123);
		Assert.assertEquals("From Server 12345", client.testPrimitiveTypes(request).getOut());
	}
	@Test
	public void testMixedModePrimitiveTypelong() throws ServiceException, MalformedURLException, DatatypeConfigurationException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode1");
		client.getService().getInvokerOptions().setTransportName(SOAConstants.TRANSPORT_HTTP_11);
		client.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1?typeBoolean=true"));
		TestPrimitiveTypesRequest request = new TestPrimitiveTypesRequest();
		request.setTypeBoolean(false);
		Assert.assertEquals("From Server true", client.testPrimitiveTypes(request).getOut());
	}
	@Test
	public void testMixedModePrimitiveTypefloat() throws ServiceException, MalformedURLException, DatatypeConfigurationException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode1");
		client.getService().getInvokerOptions().setTransportName(SOAConstants.TRANSPORT_HTTP_11);
		client.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1?typeFloat=123.123f"));
		TestPrimitiveTypesRequest request = new TestPrimitiveTypesRequest();
		request.setTypeFloat(123.1f);
		Assert.assertEquals("From Server 123.123", client.testPrimitiveTypes(request).getOut());
	}
	@Test
	public void testMixedModePrimitiveTypedouble() throws ServiceException, MalformedURLException, DatatypeConfigurationException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode1");
		client.getService().getInvokerOptions().setTransportName(SOAConstants.TRANSPORT_HTTP_11);
		client.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1?typeDouble=123.123d"));
		TestPrimitiveTypesRequest request = new TestPrimitiveTypesRequest();
		request.setTypeDouble(123.1d);
		Assert.assertEquals("From Server 123.123", client.testPrimitiveTypes(request).getOut());
	}
	@Test
	public void testMixedModePrimitiveTypeboolean() throws ServiceException, MalformedURLException, DatatypeConfigurationException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode1");
		client.getService().getInvokerOptions().setTransportName(SOAConstants.TRANSPORT_HTTP_11);
		client.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1?typeBoolean=true"));
		TestPrimitiveTypesRequest request = new TestPrimitiveTypesRequest();
		request.setTypeBoolean(false);
		Assert.assertEquals("From Server true", client.testPrimitiveTypes(request).getOut());
	}
	@Test
	public void testMixedModeForForPrimitiveTypechar() throws ServiceException, MalformedURLException, DatatypeConfigurationException {
		AdvertisingUniqueIDServiceV1SharedConsumer client = new AdvertisingUniqueIDServiceV1SharedConsumer("AdvertisingUniqueIDServiceV1Consumer", "MixedMode1");
		client.getService().getInvokerOptions().setTransportName(SOAConstants.TRANSPORT_HTTP_11);
		client.getService().setServiceLocation(new URL(serverUri.toASCIIString() + "/services/advertise/UniqueIDService/v1?typeChar=30"));
		TestPrimitiveTypesRequest request = new TestPrimitiveTypesRequest();
		char c =20;
		request.setTypeChar(c);
		Assert.assertEquals("From Server 30", client.testPrimitiveTypes(request).getOut());
	}
	
}

package com.ebay.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.ebay.binding.BindingConstants;
import com.ebay.marketplace.services.AttrTypeEnum;
import com.ebay.marketplace.services.ComplexTypeSimpleContentWithAttrGp;
import com.ebay.marketplace.services.EmptyType;
import com.ebay.marketplace.services.GetAnonType;
import com.ebay.marketplace.services.GetAnonTypeResponse;
import com.ebay.marketplace.services.GetChoiceType;
import com.ebay.marketplace.services.GetChoiceTypeResponse;
import com.ebay.marketplace.services.GetEmptyAbsPolyType;
import com.ebay.marketplace.services.GetEmptyAbsPolyTypeResponse;
import com.ebay.marketplace.services.GetRecursionTypeResponse;
import com.ebay.marketplace.services.GetRegistrationInfo;
import com.ebay.marketplace.services.GetRegistrationInfoResponse;
import com.ebay.marketplace.services.GetReservedType;
import com.ebay.marketplace.services.GetReservedTypeResponse;
import com.ebay.marketplace.services.GetSecurityCredentials;
import com.ebay.marketplace.services.GetSecurityCredentialsResponse;
import com.ebay.marketplace.services.GetUserInfo;
import com.ebay.marketplace.services.GetUserInfoResponse;
import com.ebay.marketplace.services.GlobalType;
import com.ebay.marketplace.services.Items;
import com.ebay.marketplace.services.MultipleNS;
import com.ebay.marketplace.services.MyComplexType;
import com.ebay.marketplace.services.Pamphlet;
import com.ebay.marketplace.services.Plane;
import com.ebay.marketplace.services.PolymorphismTest;
import com.ebay.marketplace.services.PrimitiveTypes;
import com.ebay.marketplace.services.RecursionType;
import com.ebay.marketplace.services.RegistrationInfoType;
import com.ebay.marketplace.services.RestrictedType;
import com.ebay.marketplace.services.SampleComplexType;
import com.ebay.marketplace.services.SecurityCredentials;
import com.ebay.marketplace.services.Test1;
import com.ebay.marketplace.services.TestEnum;
import com.ebay.marketplace.services.UserInfoType;
import com.ebay.marketplace.services.interopmodified.GetUserInfo_faultMsg;
import com.ebay.marketplace.services.interopmodified.gen.SharedBlogsInterOpModifiedV1Consumer;
import com.ebay.soaframework.common.config.DataBindingConfig;
import com.ebay.soaframework.common.config.SerializerConfig;
import com.ebay.soaframework.common.exceptions.ServiceException;
import com.ebay.soaframework.common.exceptions.ServiceInvocationException;
import com.ebay.soaframework.common.types.ByteBufferWrapper;
import com.ebay.soaframework.common.types.SOAConstants;
import com.ebay.soaframework.sif.service.Service;
import com.ebay.soaframework.sif.service.ServiceFactory;
import com.ebay.soaframework.sif.service.ServiceInvokerOptions;

public class ProtoBufFormatTests {
	
	SharedBlogsInterOpModifiedV1Consumer testClient = null;
	private  Service svc = null;
	@Before
	public  void initService() throws IOException {
		try {
			 testClient =  new SharedBlogsInterOpModifiedV1Consumer(
					"BlogsInterOpModifiedV1Consumer", "production");
			svc = ServiceFactory.create("BlogsInterOpModifiedV1","production","BlogsInterOpModifiedV1Consumer",new URL("http://localhost:8080/_soa_/services/blogs/BlogsInterOpModifiedV1/v1"));
		
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetProtoFile() throws IOException{
		 boolean exception = false;
		 URL proto = new URL("http://localhost:8080/_soa_/services/blogs/BlogsInterOpModifiedV1/v1?proto");
	        URLConnection yc = proto.openConnection();
	        BufferedReader in = null;
	        try{
		        in = new BufferedReader(
		                                new InputStreamReader(
		                                yc.getInputStream()));
		        }
		        catch(Exception e){
		        	exception = true;
		        }
		        finally{
		        in.close();
		        }
	        
	        Assert.assertFalse("No exception was expected" ,exception);
	}

	//http://localhost:8080/services/advertise/UniqueIDService/v2?proto
	
	@Test
	public void testGetProtoFileNonProtobufService() throws IOException{
		boolean error =false;
		 URL proto = new URL("http://localhost:8080/services/advertise/UniqueIDService/v2?proto");
	        URLConnection yc = proto.openConnection();
	        BufferedReader in = null;
	        try{
	        in = new BufferedReader(
	                                new InputStreamReader(
	                                yc.getInputStream()));
	        }
	        catch(Exception e){
	        	error = true;
	        }
	        finally{
	        if( in!= null)	
	        in.close();
	        }
	        
	        Assert.assertTrue("No error message returned",error);
	}
	
	@Test
	public void testGetAnonTypeOperationWithProtoBuff() throws ServiceException, DatatypeConfigurationException {
		
		GetAnonType anonType = new GetAnonType();
		Items value = new Items();
		Test1 t = new Test1();
		t.setProductName("phone");
		t.setQuantity(12);
		GregorianCalendar greCal = new GregorianCalendar();
        greCal.setTimeInMillis(10000);
        t.setUSPrice(10000.334349d);
		t.setShipDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		value.getItem().add(t);
		anonType.setIn1(value);
		GlobalType gtype = new GlobalType();
		gtype.setGlobalName("gtype");
		anonType.setIn2(gtype);
		anonType.setIn3("value");
		GetAnonTypeResponse response = testClient.getAnonType(anonType);
//		Assert here
		Assert.assertEquals(response.getOut1().getItem().get(0).getProductName(),"SOA");
		Assert.assertEquals(response.getOut1().getItem().get(0).getQuantity(),100);
		Assert.assertEquals(response.getOut2().getGlobalName(),"value");
		Assert.assertEquals(response.getOut3(),"value");
		
		
	}
	
	@Test
	public void testUserInfoOperation() throws GetUserInfo_faultMsg{
		
		GetUserInfo userInfo = new GetUserInfo();
		UserInfoType type = new UserInfoType();
		type.setUserName("po");
		type.getCountry().add("US");
		RegistrationInfoType  regInfo = new RegistrationInfoType();
		regInfo.setEmail("email");
		regInfo.setFeedback(34.5f);
		regInfo.setSellerType("star");
		regInfo.setSite(10);
		regInfo.setUserID("user id");
		type.getRegInfo().add(regInfo);
		userInfo.setIn1(type);
		
		userInfo.setIn2("value");
		
		//get and retrieve response
		GetUserInfoResponse res =  testClient.getUserInfo(userInfo);
		UserInfoType info = res.getOut1();
		Assert.assertEquals(info.getCountry().get(0),"US");
		Assert.assertEquals(info.getRegInfo().get(0).getEmail(),"email");
		Assert.assertEquals(info.getRegInfo().get(0).getFeedback(),34.5f);
		Assert.assertEquals(info.getRegInfo().get(0).getSellerType(),"star");
		Assert.assertEquals(info.getRegInfo().get(0).getSite(),10);
		Assert.assertEquals(info.getRegInfo().get(0).getUserID(),"user id");
		Assert.assertEquals(res.getOut2(),"value");
		
	}

	@Test
	public void getEmptyAbsPolyTypeTestForProtobuf() throws DatatypeConfigurationException{
		
		GregorianCalendar greCal = new GregorianCalendar();
        greCal.setTimeInMillis(10000);
        
		GetEmptyAbsPolyType empty = new GetEmptyAbsPolyType();
		EmptyType type = new EmptyType();
		
		byte [] bytes ={10,0,11,0};
		type.setB64Bi(bytes);
		type.setBool(true);
		type.setDa(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		type.setDay(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		type.setDec(new BigDecimal("45"));
		type.setDtime(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		type.setDur(DatatypeFactory.newInstance().newDuration(1000));
		type.setGmday(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		type.setGmth(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		type.setGyear(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		type.setGymth(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		type.setHexbi(bytes);
		type.setTi(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		type.setValue("value");
		empty.setIn1(type);
		Pamphlet pam = new Pamphlet();
		pam.setISBN("ISBN");
		pam.setTitle("title");
		
		empty.setIn2(pam);
		empty.setIn3("plane");
		Plane pl = new Plane();
		pl.setInfo("info");
		empty.setIn4(pl);
		
		//retrieve response
		GetEmptyAbsPolyTypeResponse res = testClient.getEmptyAbsPolyType(empty);
		EmptyType emp  =res.getOut1();
		Assert.assertEquals(emp.getB64Bi()[0],bytes[0]);
		Assert.assertEquals(emp.getValue(),"value");
		Assert.assertEquals(emp.getDa(),DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		Assert.assertEquals(emp.getDay(),DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		Assert.assertEquals(emp.getDec(),new BigDecimal("45"));
		Assert.assertEquals(emp.getDtime(),DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		Assert.assertEquals(emp.getDur(),DatatypeFactory.newInstance().newDuration(1000));
		Assert.assertEquals(emp.getGmday(),DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		Assert.assertEquals(emp.getGmth(),DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		Assert.assertEquals(emp.getGyear(),DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		Assert.assertEquals(emp.getGymth(),DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		Assert.assertEquals(emp.getHexbi()[0],bytes[0]);
		Assert.assertEquals(emp.getTi(),DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		Assert.assertEquals(res.getOut3(),"value");
		Assert.assertEquals(res.getOut2().getISBN(),"ISBN");
		Assert.assertEquals(res.getOut2().getTitle(),"title");
		Assert.assertEquals(res.getOut4().getInfo(),"info");
		
	}
	
	@Test
	public void testGetResrvedTypeOperationForProtobuf(){
		
		//set request
		GetReservedType resType = new GetReservedType();
		resType.setIn1("value");
		resType.setIn2("value");
		resType.setIn3("value");
		resType.setIn4("value");
		
		GetReservedTypeResponse res = testClient.getReservedType(resType);
		
		//retrieve response
		Assert.assertEquals(res.getOut1(),"value");
		Assert.assertEquals(res.getOut2(),"value");
		Assert.assertEquals(res.getOut3(),"value");
		Assert.assertEquals(res.getOut4(),"value");
		
	}
	
	
	@Test
	public void testRecursionTypeForProtobuf() throws DatatypeConfigurationException{
		
		//set request
		RecursionType recType = new RecursionType();
		RecursionType recType2 = new RecursionType();
		recType2.setIn("value");
		
		SampleComplexType samType1 = new SampleComplexType();
		samType1.getInt().add(10);
		samType1.setValue1("value");
		samType1.setValue2("value");
		samType1.getValue3().add("value");
		samType1.setValue4("value");
		samType1.getValue5().add("value");
		samType1.getValue6().add("value");
		samType1.getValue7().add("value");
		samType1.setValue8("value");
		samType1.setValue9("value");
		samType1.setValue10("value");
		recType2.setInt2(samType1);
		
		recType.setGetRecursionType(recType2);
		recType.setIn("value");
		
		SampleComplexType samType = new SampleComplexType();
		samType.getInt().add(10);
		samType.setValue1("value");
		samType.setValue2("value");
		samType.getValue3().add("value");
		samType.setValue4("value");
		samType.getValue5().add("value");
		samType.getValue6().add("value");
		samType.getValue7().add("value");
		samType.setValue8("value");
		samType.setValue9("value");
		samType.setValue10("value");
		
		recType.setInt2(samType);
		
		GregorianCalendar greCal = new GregorianCalendar();
        greCal.setTimeInMillis(10000);
		
		GetRecursionTypeResponse res = testClient.getRecursionType(recType);
		Assert.assertEquals(res.getOut1().getValue(),23.4f);
		Assert.assertEquals(res.getOut1().isArgB(),true);
		Assert.assertEquals(res.getOut1().getArgA(),DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		Assert.assertEquals(res.getOut2().isArgB(),true);
		Assert.assertEquals(res.getOut2().getValue(),RestrictedType.ONE);
		
		Assert.assertEquals(res.getOut(),"value");
	}
	
	@Test
	public void testGetSecurityCredentialsOperationForProtobuf(){
		//set request
		
		GetSecurityCredentials sec = new GetSecurityCredentials();
		SecurityCredentials cre = new SecurityCredentials();
		cre.setAppName("value");
		cre.setPwd("value");
		cre.setUserName("value");
		sec.setIn1(cre);
		sec.setIn2("value");
	
		//get response
		
		GetSecurityCredentialsResponse res = testClient.getSecurityCredentials(sec);
		Assert.assertEquals(res.getOut1().getAppName(),"value");
		Assert.assertEquals(res.getOut1().getPwd(),"value");
		Assert.assertEquals(res.getOut1().getUserName(),"value");
		
		Assert.assertEquals(res.getOut2(),"value");
		
	
	}
	
	@Test
	public void testGetChoiceTypeForProtobuf() throws DatatypeConfigurationException{
		
		GregorianCalendar greCal = new GregorianCalendar();
        greCal.setTimeInMillis(10000);
        
		GetChoiceType choice = new GetChoiceType();
		PrimitiveTypes type = new PrimitiveTypes();
		byte [] bytes = new byte[10];
		type.setB64Bi(bytes);
		type.setBool(true);
		type.setDa(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		type.setDay(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		type.setDec(new BigDecimal("45"));
		type.setDtime(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		type.setDur(DatatypeFactory.newInstance().newDuration(1000));
		type.setGmday(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		type.setGmth(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		type.setGyear(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		type.setGymth(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		type.setHexbi(bytes);
		type.setTi(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		type.setId("value");
		type.setLan("vallue");
		type.setName("value");
		type.setNcname("value");
		type.setEnt("value");
		type.setNegInt(new BigInteger("23"));
		type.setNonNegInt(new BigInteger("23"));
		type.setNonPosInt(new BigInteger("23"));
		type.setNorstr("value");
		type.setQname(new QName("qname"));
		type.setToken("value");
		type.setUnbyte(new Short("23"));
		type.setUnint(222222l);
		type.getTokens().add("value");
		type.setUnlong(new BigInteger("23"));
		type.setUnshort( new Integer("23"));
		
		choice.setIn1(type);
		choice.setIn2("value");
		MultipleNS mul = new MultipleNS();
		mul.setCount(10);
		mul.setName("value");
		choice.setIn3(mul);
		PolymorphismTest test = new PolymorphismTest();
		MyComplexType ctype = new MyComplexType();
		ctype.setElemA(23.4f);
		ctype.setElemB("value");
		test.setElemD(ctype);
		choice.setInt4(test);
		
		
		GetChoiceTypeResponse res = testClient.getChoiceType(choice);
		
		Assert.assertEquals(res.getOut1().getB64Bi().length,bytes.length);
		Assert.assertEquals(res.getOut1().getDa(),DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		Assert.assertEquals(res.getOut1().getDay(),DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		Assert.assertEquals(res.getOut1().getDec(),new BigDecimal("45"));
		Assert.assertEquals(res.getOut1().getDtime(),DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		Assert.assertEquals(res.getOut1().getDur(),DatatypeFactory.newInstance().newDuration(1000));
		Assert.assertEquals(res.getOut1().getGmday(),DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		Assert.assertEquals(res.getOut1().getGmth(),DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		Assert.assertEquals(res.getOut1().getGyear(),DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		Assert.assertEquals(res.getOut1().getGymth(),DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		Assert.assertEquals(res.getOut1().getHexbi().length,bytes.length);
		Assert.assertEquals(res.getOut1().getTi(),DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		
		Assert.assertEquals(res.getOut1().getEnt(),"value");
		Assert.assertEquals(res.getOut1().getId(),"value");
		Assert.assertEquals(res.getOut1().getLan(),"value");
		Assert.assertEquals(res.getOut1().getName(),"value");
		Assert.assertEquals(res.getOut1().getNcname(),"value");
		Assert.assertEquals(res.getOut1().getNorstr(),"value");
		Assert.assertEquals(res.getOut1().getToken(),"value");
		Assert.assertEquals(res.getOut1().getNegInt(),new BigInteger("23"));
		Assert.assertEquals(res.getOut1().getNonNegInt(),new BigInteger("23"));
		Assert.assertEquals(res.getOut1().getNonPosInt(),new BigInteger("23"));
		Assert.assertEquals(res.getOut1().getPosInt(),new BigInteger("23"));
		Assert.assertEquals(res.getOut1().getQname(), new QName("qname"));
		Assert.assertEquals(res.getOut1().getTokens().get(0),"value");
		Assert.assertEquals(res.getOut1().getUnbyte(), new Short("23"));
		Assert.assertEquals(res.getOut1().getUnint(),new Long(222222l));
		Assert.assertEquals(res.getOut1().getUnlong(), new BigInteger("23"));
		Assert.assertEquals(res.getOut1().getUnshort(), new Integer("23"));
		
		Assert.assertEquals(res.getOut2(),"value");
		Assert.assertEquals(res.getOut3().getCount(),10);
		Assert.assertEquals(res.getOut3().getName(),"value");
		Assert.assertEquals(res.getOut4().getElemB(),"value");
		Assert.assertEquals(res.getOut4().getElemA(),23.4f);
		Assert.assertEquals(res.getOut5().getElemB(),"value");
		Assert.assertEquals(res.getOut5().getElemC(),"value");
		Assert.assertEquals(res.getOut5().getElemA(),23.4f);
		Assert.assertEquals(res.getOut6().getElemB(),"value");
		Assert.assertEquals(res.getOut6().getElemC(),"value");
		Assert.assertEquals(res.getOut6().getElemA(),23.4f);
		
		
		
	}
	
	@Test
	public void testTransportHTT10() throws ServiceInvocationException, DatatypeConfigurationException{
		
		GetUserInfoResponse response1 = (GetUserInfoResponse) invoke("normal",BindingConstants.PAYLOAD_PROTOBUF,BindingConstants.PAYLOAD_PROTOBUF,"getUserInfo",SOAConstants.TRANSPORT_HTTP_10,null);
	    Assert.assertEquals(response1.getOut2(),"value");
	    UserInfoType info = response1.getOut1();
		Assert.assertEquals(info.getCountry().get(0),"US");
		Assert.assertEquals(info.getRegInfo().get(0).getEmail(),"email");
		Assert.assertEquals(info.getRegInfo().get(0).getFeedback(),34.5f);
		Assert.assertEquals(info.getRegInfo().get(0).getSellerType(),"star");
		Assert.assertEquals(info.getRegInfo().get(0).getSite(),10);
		Assert.assertEquals(info.getRegInfo().get(0).getUserID(),"user id");

	}
	
	
	@Test
	public void testDiffFormatsXMLPROTOBUF() throws ServiceInvocationException, DatatypeConfigurationException{
		
		GetUserInfoResponse response1 = (GetUserInfoResponse) invoke("normal",BindingConstants.PAYLOAD_XML,BindingConstants.PAYLOAD_PROTOBUF,"getUserInfo",SOAConstants.TRANSPORT_HTTP_11,null);
	    Assert.assertEquals(response1.getOut2(),"value");
	    UserInfoType info = response1.getOut1();
		Assert.assertEquals(info.getCountry().get(0),"US");
		Assert.assertEquals(info.getRegInfo().get(0).getEmail(),"email");
		Assert.assertEquals(info.getRegInfo().get(0).getFeedback(),34.5f);
		Assert.assertEquals(info.getRegInfo().get(0).getSellerType(),"star");
		Assert.assertEquals(info.getRegInfo().get(0).getSite(),10);
		Assert.assertEquals(info.getRegInfo().get(0).getUserID(),"user id");

	}
	@Test
	public void testGetRegistrationInfoForProtobuf() throws DatatypeConfigurationException{
		
		GregorianCalendar greCal = new GregorianCalendar();
        greCal.setTimeInMillis(10000);	
		
	GetRegistrationInfo info = new GetRegistrationInfo();
	RegistrationInfoType infoType = new RegistrationInfoType();
	infoType.setEmail("value");
	infoType.setFeedback(23.4f);
	infoType.setSellerType("value");
	infoType.setSite(10);
	infoType.setUserID("value");
	info.setIn1(infoType);
	info.setIn2("value");
	ComplexTypeSimpleContentWithAttrGp grp = new ComplexTypeSimpleContentWithAttrGp();
	grp.setAttrA(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
	grp.setAttrB(new Integer(23));
	grp.setValue("value");

	info.setInt3(grp);
	AttrTypeEnum att  = new AttrTypeEnum();
	att.setArgE("value");
	att.setArgF("value");
	att.setAttrA(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
	att.setAttrB(TestEnum.ONE);
	info.setInt4(att);
	
	GetRegistrationInfoResponse res = testClient.getRegistrationInfo(info);
	
	
	//retrieve response
	
	Assert.assertEquals(res.getOut1().getEmail(),"value");
	Assert.assertEquals(res.getOut1().getSellerType(),"value");
	Assert.assertEquals(res.getOut1().getSite(),10);
	Assert.assertEquals(res.getOut1().getUserID(),"value");
	Assert.assertEquals(res.getOut1().getFeedback(),23.4f);
	
	Assert.assertEquals(res.getOut2(),"value");
	Assert.assertEquals(res.getOut3().getArgE(),"value");
	Assert.assertEquals(res.getOut3().getArgF(),"value");
	Assert.assertEquals(res.getOut3().getAttrA(),DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
	Assert.assertEquals(res.getOut3().getAttrB(),com.ebay.marketplace.services.TestEnum.ONE);
	
	Assert.assertEquals(res.getOut4().getParam(),"value");
	Assert.assertEquals(res.getOut4().getParam1(),"value");
	Assert.assertEquals(res.getOut4().getValue(),23.4f);
	Assert.assertEquals(res.getOut4().getArgA(),DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));

	Assert.assertEquals(res.getOut5().getValue(),"value");
			
	}
	
	@Test
	public void testDiffFormatsPROTOBUFXML() throws ServiceInvocationException, DatatypeConfigurationException{
		
		GetUserInfoResponse response1 = (GetUserInfoResponse) invoke("normal",BindingConstants.PAYLOAD_PROTOBUF,BindingConstants.PAYLOAD_XML,"getUserInfo",SOAConstants.TRANSPORT_HTTP_11,null);
	    Assert.assertEquals(response1.getOut2(),"value");
	    UserInfoType info = response1.getOut1();
		Assert.assertEquals(info.getCountry().get(0),"US");
		Assert.assertEquals(info.getRegInfo().get(0).getEmail(),"email");
		Assert.assertEquals(info.getRegInfo().get(0).getFeedback(),34.5f);
		Assert.assertEquals(info.getRegInfo().get(0).getSellerType(),"star");
		Assert.assertEquals(info.getRegInfo().get(0).getSite(),10);
		Assert.assertEquals(info.getRegInfo().get(0).getUserID(),"user id");

	}
	
	
	
	
	@Test
	public void testDiffFormatsPROTOBUFJSON() throws ServiceInvocationException, DatatypeConfigurationException{
		
		GetUserInfoResponse response1 = (GetUserInfoResponse) invoke("normal",BindingConstants.PAYLOAD_PROTOBUF,BindingConstants.PAYLOAD_JSON,"getUserInfo",SOAConstants.TRANSPORT_HTTP_11,null);
	    Assert.assertEquals(response1.getOut2(),"value");
	    UserInfoType info = response1.getOut1();
		Assert.assertEquals(info.getCountry().get(0),"US");
		Assert.assertEquals(info.getRegInfo().get(0).getEmail(),"email");
		Assert.assertEquals(info.getRegInfo().get(0).getFeedback(),34.5f);
		Assert.assertEquals(info.getRegInfo().get(0).getSellerType(),"star");
		Assert.assertEquals(info.getRegInfo().get(0).getSite(),10);
		Assert.assertEquals(info.getRegInfo().get(0).getUserID(),"user id");

	}
	
	
	@Test
	public void testDiffFormatsJSONPROTOBUF() throws ServiceInvocationException, DatatypeConfigurationException{
		
		GetUserInfoResponse response1 = (GetUserInfoResponse) invoke("normal",BindingConstants.PAYLOAD_JSON,BindingConstants.PAYLOAD_PROTOBUF,"getUserInfo",SOAConstants.TRANSPORT_HTTP_11,null);
	    Assert.assertEquals(response1.getOut2(),"value");
	    UserInfoType info = response1.getOut1();
		Assert.assertEquals(info.getCountry().get(0),"US");
		Assert.assertEquals(info.getRegInfo().get(0).getEmail(),"email");
		Assert.assertEquals(info.getRegInfo().get(0).getFeedback(),34.5f);
		Assert.assertEquals(info.getRegInfo().get(0).getSellerType(),"star");
		Assert.assertEquals(info.getRegInfo().get(0).getSite(),10);
		Assert.assertEquals(info.getRegInfo().get(0).getUserID(),"user id");

	}
	
	
	@Test
	public void testDiffFormatsPROTOBUFNV() throws ServiceInvocationException, DatatypeConfigurationException{
		
		GetUserInfoResponse response1 = (GetUserInfoResponse) invoke("normal",BindingConstants.PAYLOAD_PROTOBUF,BindingConstants.PAYLOAD_NV,"getUserInfo",SOAConstants.TRANSPORT_HTTP_11,null);
	    Assert.assertEquals(response1.getOut2(),"value");
	    UserInfoType info = response1.getOut1();
		Assert.assertEquals(info.getCountry().get(0),"US");
		Assert.assertEquals(info.getRegInfo().get(0).getEmail(),"email");
		Assert.assertEquals(info.getRegInfo().get(0).getFeedback(),34.5f);
		Assert.assertEquals(info.getRegInfo().get(0).getSellerType(),"star");
		Assert.assertEquals(info.getRegInfo().get(0).getSite(),10);
		Assert.assertEquals(info.getRegInfo().get(0).getUserID(),"user id");

	}
	
	
	@Test
	public void testDiffFormatsNVPROTOBUF() throws ServiceInvocationException, DatatypeConfigurationException{
		
		GetUserInfoResponse response1 = (GetUserInfoResponse) invoke("normal",BindingConstants.PAYLOAD_NV,BindingConstants.PAYLOAD_PROTOBUF,"getUserInfo",SOAConstants.TRANSPORT_HTTP_11,null);
	    Assert.assertEquals(response1.getOut2(),"value");
	    UserInfoType info = response1.getOut1();
		Assert.assertEquals(info.getCountry().get(0),"US");
		Assert.assertEquals(info.getRegInfo().get(0).getEmail(),"email");
		Assert.assertEquals(info.getRegInfo().get(0).getFeedback(),34.5f);
		Assert.assertEquals(info.getRegInfo().get(0).getSellerType(),"star");
		Assert.assertEquals(info.getRegInfo().get(0).getSite(),10);
		Assert.assertEquals(info.getRegInfo().get(0).getUserID(),"user id");

	}
	
	
	@Test
	public void testDiffFormatsFASTINFOSETPROTOBUF() throws ServiceInvocationException, DatatypeConfigurationException{
		
		GetUserInfoResponse response1 = (GetUserInfoResponse) invoke("normal",BindingConstants.PAYLOAD_FAST_INFOSET,BindingConstants.PAYLOAD_PROTOBUF,"getUserInfo",SOAConstants.TRANSPORT_HTTP_11,null);
	    Assert.assertEquals(response1.getOut2(),"value");
	    UserInfoType info = response1.getOut1();
		Assert.assertEquals(info.getCountry().get(0),"US");
		Assert.assertEquals(info.getRegInfo().get(0).getEmail(),"email");
		Assert.assertEquals(info.getRegInfo().get(0).getFeedback(),34.5f);
		Assert.assertEquals(info.getRegInfo().get(0).getSellerType(),"star");
		Assert.assertEquals(info.getRegInfo().get(0).getSite(),10);
		Assert.assertEquals(info.getRegInfo().get(0).getUserID(),"user id");

	}
	
	@Test
	public void testDiffFormatsPROTOBUFFASTINFOSET() throws ServiceInvocationException, DatatypeConfigurationException{
		
		GetUserInfoResponse response1 = (GetUserInfoResponse) invoke("normal",BindingConstants.PAYLOAD_PROTOBUF,BindingConstants.PAYLOAD_FAST_INFOSET,"getUserInfo",SOAConstants.TRANSPORT_HTTP_11,null);
	    Assert.assertEquals(response1.getOut2(),"value");
	    UserInfoType info = response1.getOut1();
		Assert.assertEquals(info.getCountry().get(0),"US");
		Assert.assertEquals(info.getRegInfo().get(0).getEmail(),"email");
		Assert.assertEquals(info.getRegInfo().get(0).getFeedback(),34.5f);
		Assert.assertEquals(info.getRegInfo().get(0).getSellerType(),"star");
		Assert.assertEquals(info.getRegInfo().get(0).getSite(),10);
		Assert.assertEquals(info.getRegInfo().get(0).getUserID(),"user id");

	}
	

   public Object invoke(String mode,String reqBinding,String resBinding,String opsName,String transport,String msgProtocol) throws DatatypeConfigurationException, ServiceInvocationException{
	   ByteBufferWrapper outParam = new ByteBufferWrapper();
		Object[] inParam = new Object[1];
		GetUserInfo userInfo = new GetUserInfo();
		UserInfoType type = new UserInfoType();
		type.setUserName("po");
		type.getCountry().add("US");
		RegistrationInfoType  regInfo = new RegistrationInfoType();
		regInfo.setEmail("email");
		regInfo.setFeedback(34.5f);
		regInfo.setSellerType("star");
		regInfo.setSite(10);
		regInfo.setUserID("user id");
		type.getRegInfo().add(regInfo);
		userInfo.setIn1(type);
		
		userInfo.setIn2("value");
		inParam[0] = userInfo;
		
		ServiceInvokerOptions options = svc.getInvokerOptions();
		options.setTransportName(transport);
		if(msgProtocol != null)
		options.setMessageProtocolName(msgProtocol);
		options.setRequestBinding(reqBinding);
		options.setResponseBinding(resBinding);
		DataBindingConfig config = new DataBindingConfig();
		SerializerConfig serConfig = new SerializerConfig();
		serConfig.setDeserializerFactoryClassName("com.ebay.soaframework.common.impl.binding.protobuf.ProtobufDeserializerFactory");
		serConfig.setSerializerFactoryClassName("com.ebay.soaframework.common.impl.binding.protobuf.ProtobufSerializerFactory");
		serConfig.setMimeType("application/plain");
		config.getDataBinding().add(serConfig);
		
		
		if (mode.contentEquals("raw")) {
			svc.invoke(opsName, inParam, outParam);
			String response = new String(outParam.getByteBuffer().array());
			return response;
		} else {
			List<Object> outParam1 = new ArrayList<Object>();
			svc.invoke(opsName, inParam, outParam1);
			GetUserInfoResponse response1 = (GetUserInfoResponse)outParam1.get(0);
			
			return response1;
		}
		
		
		
   }
	
}


package com.ebay.marketplace.services.interopmodified.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;

import com.ebay.marketplace.services.ComplexTypeSimpleContentWithAttrGp;
import com.ebay.marketplace.services.EmptyType;
import com.ebay.marketplace.services.ExtendMyComplexType1;
import com.ebay.marketplace.services.ExtendMyComplexType2;
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
import com.ebay.marketplace.services.GetVersion;
import com.ebay.marketplace.services.GetVersionResponse;
import com.ebay.marketplace.services.GlobalType;
import com.ebay.marketplace.services.Items;
import com.ebay.marketplace.services.MultiAttrSimpleType;
import com.ebay.marketplace.services.MultilevelExtension;
import com.ebay.marketplace.services.MultipleNS;
import com.ebay.marketplace.services.MyComplexSimpleContentType1;
import com.ebay.marketplace.services.MyComplexSimpleContentType2;
import com.ebay.marketplace.services.MyComplexType;
import com.ebay.marketplace.services.Pamphlet;
import com.ebay.marketplace.services.Plane;
import com.ebay.marketplace.services.PolymorphismTest;
import com.ebay.marketplace.services.PrimitiveTypes;
import com.ebay.marketplace.services.RecursionType;
import com.ebay.marketplace.services.RegistrationInfoType;
import com.ebay.marketplace.services.RestrictedType;
import com.ebay.marketplace.services.SecurityCredentials;
import com.ebay.marketplace.services.Test1;
import com.ebay.marketplace.services.UserInfoType;
import com.ebay.marketplace.services.interopmodified.BlogsInterOpModifiedV1;
import com.ebay.marketplace.services.interopmodified.GetUserInfo_faultMsg;


public class BlogsInterOpModifiedV1Impl
    implements BlogsInterOpModifiedV1
{


    public GetSecurityCredentialsResponse getSecurityCredentials(GetSecurityCredentials param0) {
    	
    	param0.getIn1().getAppName();
    	param0.getIn1().getPwd();
    	param0.getIn1().getUserName();
    	
    	param0.getIn2();
    	
    	
    	GetSecurityCredentialsResponse res = new GetSecurityCredentialsResponse();
    	SecurityCredentials cre = new SecurityCredentials();
    	cre.setAppName("value");
    	cre.setPwd("value");
    	cre.setUserName("value");
    	res.setOut1(cre);
    	res.setOut2("value");
    	
        return res;
    }

    public GetChoiceTypeResponse getChoiceType(GetChoiceType param0) {
    	param0.getIn1().getB64Bi();
    	param0.getIn1().getDa();
    	param0.getIn1().getDay();
    	param0.getIn1().getDec();
    	param0.getIn1().getDtime();
    	param0.getIn1().getDur();
    	param0.getIn1().getEnt();
    	param0.getIn1().getGmday();
    	param0.getIn1().getId();
    	param0.getIn1().getLan();
    	param0.getIn1().getName();
    	param0.getIn1().getNcname();
    	param0.getIn1().getNegInt();
    	param0.getIn1().getNonNegInt();
    	param0.getIn1().getNonPosInt();
    	param0.getIn1().getNorstr();
    	param0.getIn1().getPosInt();
    	param0.getIn1().getQname();
    	param0.getIn1().getToken();
    	param0.getIn1().getTokens();
    	param0.getIn1().getUnbyte();
    	param0.getIn1().getUnint();
    	param0.getIn1().getUnlong();
    	param0.getIn1().getUnshort();
    	
    	GetChoiceTypeResponse res = new GetChoiceTypeResponse();
    	GregorianCalendar greCal = new GregorianCalendar();
        greCal.setTimeInMillis(10000);
        
		PrimitiveTypes type = new PrimitiveTypes();
		byte [] bytes = new byte[10];
		type.setB64Bi(bytes);
		type.setBool(true);
		try {
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
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		type.setId("value");
		type.setLan("value");
		type.setName("value");
		type.setNcname("value");
		type.setEnt("value");
		type.setNegInt(new BigInteger("23"));
		type.setNonNegInt(new BigInteger("23"));
		type.setNonPosInt(new BigInteger("23"));
		type.setPosInt(new BigInteger("23"));
		type.setNorstr("value");
		type.setQname(new QName("qname"));
		type.setToken("value");
		type.setUnbyte(new Short("23"));
		type.setUnint(222222l);
		type.getTokens().add("value");
		type.setUnlong(new BigInteger("23"));
		type.setUnshort( new Integer("23"));
		
	
		MultipleNS mul = new MultipleNS();
		mul.setCount(10);
		mul.setName("value");
	
		PolymorphismTest test = new PolymorphismTest();
		MyComplexType ctype = new MyComplexType();
		ctype.setElemA(23.4f);
		ctype.setElemB("value");
		test.setElemD(ctype);
		
		res.setOut1(type);
		res.setOut2("value");
		res.setOut3(mul);
		res.setOut4(ctype);
		
		ExtendMyComplexType1 mytype1 = new ExtendMyComplexType1();
		mytype1.setElemA(23.4f);
		mytype1.setElemB("value");
		mytype1.setElemC("value");
		
		res.setOut5(mytype1);
		
		ExtendMyComplexType2 mytype2 = new ExtendMyComplexType2();
		mytype2.setElemA(23.4f);
		mytype2.setElemB("value");
		mytype2.setElemC("value");
		res.setOut6(mytype2);
		
    	
        return res;
    }

    public GetReservedTypeResponse getReservedType(GetReservedType param0) {
    	param0.getIn1();
    	param0.getIn2();
    	param0.getIn3();
    	param0.getIn4();
    	
    	GetReservedTypeResponse res = new GetReservedTypeResponse();
    	res.setOut1("value");
    	res.setOut2("value");
    	res.setOut3("value");
    	res.setOut4("value");
        return res;
    }

    public GetAnonTypeResponse getAnonType(GetAnonType param0) {
    	Items it = param0.getIn1();
    	System.out.println(it.getItem());
    	GlobalType gtype = param0.getIn2();
    	System.out.println(gtype.getGlobalName());
    	System.out.println(param0.getIn3());
    
    	GetAnonTypeResponse res = new GetAnonTypeResponse();
    	it = new Items();
    	
    	Test1 tst1 = new Test1();
    	tst1.setProductName("SOA");
    	tst1.setQuantity(100);
    	it.getItem().add(tst1);
    	res.setOut1(it);
    	GlobalType gt = new GlobalType();
    	gt.setGlobalName("value");
    	res.setOut2(gt);
    	res.setOut3("value");
        return res;
    }

    public GetRecursionTypeResponse getRecursionType(RecursionType param0) {
    	//get request
    	param0.getGetRecursionType().getIn();
    	param0.getGetRecursionType().getInt2().getValue1();
    	param0.getGetRecursionType().getInt2().getValue2();
    	param0.getGetRecursionType().getInt2().getValue3();
    	param0.getGetRecursionType().getInt2().getValue4();
    	param0.getGetRecursionType().getInt2().getValue5();
    	param0.getGetRecursionType().getInt2().getValue6();
    	param0.getGetRecursionType().getInt2().getValue7();
    	param0.getGetRecursionType().getInt2().getValue8();
    	param0.getGetRecursionType().getInt2().getValue9();
    	param0.getGetRecursionType().getInt2().getValue10();
    	
    	param0.getInt2().getInt().get(0);
    	param0.getInt2().getValue1();
    	param0.getInt2().getValue10();
    	param0.getInt2().getValue2();
    	param0.getInt2().getValue3().get(0);
    	param0.getInt2().getValue4();
    	param0.getInt2().getValue5().get(0);
    	param0.getInt2().getValue6().get(0);
    	param0.getInt2().getValue7().get(0);
    	param0.getInt2().getValue8();
    	param0.getInt2().getValue9();
    	
    	//set response
    	GregorianCalendar greCal = new GregorianCalendar();
        greCal.setTimeInMillis(10000);
        
    	GetRecursionTypeResponse res = new GetRecursionTypeResponse();
    	res.setOut("value");
    	MyComplexSimpleContentType1 cc = new MyComplexSimpleContentType1();
    	try {
			cc.setArgA(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	cc.setArgB(true);
    	cc.setValue(23.4f);
    	res.setOut1(cc);
    	
    	MyComplexSimpleContentType2 cc2 = new MyComplexSimpleContentType2();
    	cc2.setArgB(true);
    	cc2.setValue(RestrictedType.ONE);
    	res.setOut2(cc2);
    	
        return res;
    }
   public GetVersionResponse getVersion(GetVersion param0) {
        return null;
    }

    public GetRegistrationInfoResponse getRegistrationInfo(GetRegistrationInfo param0) {
    	
    	param0.getIn1().getEmail();
    	param0.getIn1().getFeedback();
    	param0.getIn1().getSellerType();
    	param0.getIn1().getSite();
    	param0.getIn1().getUserID();
    	param0.getIn2();
    	param0.getInt3().getAttrA();
    	param0.getInt3().getAttrB();
    	param0.getInt3().getValue();
    
    	//set response
    	
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
			GetRegistrationInfoResponse res = new GetRegistrationInfoResponse();
			ComplexTypeSimpleContentWithAttrGp grp = new ComplexTypeSimpleContentWithAttrGp();
			try {
				grp.setAttrA(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
			
			grp.setAttrB(new Integer(23));
			grp.setValue("value");
			
			
			res.setOut1(infoType);
			res.setOut2("value");
			MultiAttrSimpleType type = new MultiAttrSimpleType();
			type.setArgE("value");
			type.setArgF("value");
			type.setAttrA(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
			type.setAttrB(com.ebay.marketplace.services.TestEnum.ONE);
			res.setOut3(type);
			
			MultilevelExtension me = new MultilevelExtension();
			me.setArgA(DatatypeFactory.newInstance().newXMLGregorianCalendar(greCal));
			
			me.setArgB(true);
			me.setParam("value");
			me.setParam1("value");
			me.setValue(23.4f);
			res.setOut4(me);
			com.ebay.marketplace.services.SCMultiLevel scm = new com.ebay.marketplace.services.SCMultiLevel();
			scm.setValue("value");
			res.setOut5(scm);
			
			
			
			} catch (DatatypeConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        return res;
    }

    public GetEmptyAbsPolyTypeResponse getEmptyAbsPolyType(GetEmptyAbsPolyType param0) {
    	GregorianCalendar greCal = new GregorianCalendar();
        greCal.setTimeInMillis(10000);
        
    	param0.getIn1().getB64Bi();
    	param0.getIn1().getDa();
    	param0.getIn1().getDay();
    	param0.getIn1().getDec();
    	param0.getIn1().getDtime();
    	param0.getIn1().getDur();
    	param0.getIn1().getGmday();
    	param0.getIn1().getGmth();
    	param0.getIn1().getGyear();
    	param0.getIn1().getGymth();
    	param0.getIn1().getHexbi();
    	param0.getIn1().getTi();
    	param0.getIn1().getValue();
    	param0.getIn2().getISBN();
    	param0.getIn2().getTitle();
    	param0.getIn3();
    	param0.getIn4().getInfo();
    	
    	GetEmptyAbsPolyTypeResponse res = new GetEmptyAbsPolyTypeResponse();
    	
    	GetEmptyAbsPolyType empty = new GetEmptyAbsPolyType();
		EmptyType type = new EmptyType();
		
		byte [] bytes = {10,0,11,0};
		type.setB64Bi(bytes);
		type.setBool(true);
		try {
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
		
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

    	
    	res.setOut1(type);
    	res.setOut2(pam);
    	res.setOut3("value");
    	res.setOut4(pl);
        return res;
    }

    public GetUserInfoResponse getUserInfo(GetUserInfo param0)
        throws GetUserInfo_faultMsg
    {
    	//get the request
    	UserInfoType type = param0.getIn1();
        type.getCountry();
        type.getRegInfo();
        type.getUserName();
        
        
        // set response
        GetUserInfoResponse res = new GetUserInfoResponse();
        UserInfoType userType = new UserInfoType();
    	userType.setUserName("user name");
    	userType.getCountry().add("US");
    	
    	RegistrationInfoType  regInfo = new RegistrationInfoType();
		regInfo.setEmail("email");
		regInfo.setFeedback(34.5f);
		regInfo.setSellerType("star");
		regInfo.setSite(10);
		regInfo.setUserID("user id");
		userType.getRegInfo().add(regInfo);
    
        res.setOut1(userType);
    	res.setOut2("value");
        return res;
    }

}

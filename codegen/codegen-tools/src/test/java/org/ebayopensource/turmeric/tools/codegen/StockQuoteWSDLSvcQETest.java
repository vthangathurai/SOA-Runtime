package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.*;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


/**
 * @author shrao
 *
 */
public class StockQuoteWSDLSvcQETest extends AbstractServiceGeneratorTestCase{
	/**
	 * @param name
	 */
	
	
	File destDir = null;
	File prDir = null;
	File binDir = null;

	@Before
	public void init() throws Exception{
	
		mavenTestingRules.setFailOnViolation(false);
		testingdir.ensureEmpty();
		destDir = testingdir.getDir();
		binDir = testingdir.getFile("bin");
		prDir = testingdir.getDir();
		
		
		}
	public StockQuoteWSDLSvcQETest(){}


	/**
	 * @throws Exception 
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void interfaceStockQuoteSvc() throws Exception {
		
		
		String testArgs[] =  new String[] {
		
		"-genType","Interface",
		"-gip","org.ebayopensource.qaservices.stockquote.intf",
		"-namespace","http://www.ebayopensource.com/soaframework/service/StockQuote",
		"-serviceName","StockQuote",
		"-wsdl","http://www.webservicex.net/stockquote.asmx?WSDL",
		"-scv","1.0.0",
		"-dest",destDir.getAbsolutePath(),
		"-src",destDir.getAbsolutePath(),
		"-bin",binDir.getAbsolutePath(),

		};
		 
		try{
		performDirectCodeGen(testArgs, binDir);}
		catch(Exception e ){
			Assert.fail("remote wsdl test failed with exception");
		}
		
		
	}

	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allStockQuoteSvc() {
		
		
		String testArgs[] =  new String[] {
				
				"-genType","All",
				"-gip","org.ebayopensource.qaservices.stockquote.intf",
				"-namespace","http://www.ebayopensource.com/soaframework/service/StockQuote",
				"-serviceName","StockQuote",
				"-wsdl","http://www.webservicex.net/stockquote.asmx?WSDL",
				"-scv","1.0.0",
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath()};
		
		
		try{
			performDirectCodeGen(testArgs, binDir);}
			catch(Exception e ){
				Assert.fail("remote wsdl test failed with exception");
			}
	
	}


	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allStockQuoteSvc2() {
		
		String testArgs[] =  new String[] {
				
				"-genType","All",
				"-gip","org.ebayopensource.qaservices.stockquote.intf",
				"-namespace","http://www.ebayopensource.com/soaframework/service/StockQuote",
				"-serviceName","StockQuote",
				"-wsdl","http://www.webservicex.net/stockquote.asmx?WSDL",
				"-scv","1.0.0",
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				//"-http-proxy-host","skyline.qa.ebay.com",
				//"-http-proxy-port","80"
				};
		try{
			performDirectCodeGen(testArgs, binDir);}
			catch(Exception e ){
				Assert.fail("remote wsdl test failed with exception " + e.getMessage() + "caused by" + e.getCause());
			}
		
		
	}


	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allStockQuoteSvc3() {
		File sl = getCodegenQEDataFileInput("service_layers.txt");
		
		String testArgs[] =  new String[] {
				
				"-genType","All",
				"-gip","org.ebayopensource.qaservices.stockquote.intf",
				"-namespace","http://www.ebayopensource.com/soaframework/service/StockQuote",
				"-serviceName","StockQuote",
				"-wsdl","http://www.webservicex.net/stockquote.asmx?WSDL",
				"-scv","1.0.0",
				"-asl",sl.getAbsolutePath(),
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				//"-http-proxy-host","skyline.qa.ebay.com",
				//"-http-proxy-port","80"
				};
		try{
			performDirectCodeGen(testArgs, binDir);}
			catch(Exception e ){
				Assert.fail("remote wsdl test failed with exception" + e.getMessage() + "caused by" + e.getCause());
			}
		
		
	}


	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allStockQuoteSvc4() {
		
File sl = getCodegenQEDataFileInput("service_layers2.txt");
		
		String testArgs[] =  new String[] {
				
				"-genType","All",
				"-gip","org.ebayopensource.qaservices.stockquote.intf",
				"-namespace","http://www.ebayopensource.com/soaframework/service/StockQuote",
				"-serviceName","StockQuote",
				"-wsdl","http://www.webservicex.net/stockquote.asmx?WSDL",
				"-scv","1.0.0",
				"-asl",sl.getAbsolutePath(),
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				//"-http-proxy-host","skyline.qa.ebay.com",
				//"-http-proxy-port","80"
				};
		try{
			performDirectCodeGen(testArgs, binDir);}
			catch(Exception e ){
				Assert.fail("remote wsdl test failed with exception" + e.getMessage() + "caused by" + e.getCause());
			}
		
	}


	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allStockQuoteSvc5() {
		
File sl = getCodegenQEDataFileInput("service_layers.txt");
		
		String testArgs[] =  new String[] {
				
				"-genType","All",
				"-gip","org.ebayopensource.qaservices.stockquote.intf",
				"-namespace","http://www.ebayopensource.com/soaframework/service/StockQuote",
				"-serviceName","StockQuote",
				"-wsdl","http://www.webservicex.net/stockquote.asmx?WSDL",
				"-scv","1.0.0",
				"-slayer","COMMON",
				"-asl",sl.getAbsolutePath(),
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				//"-http-proxy-host","skyline.qa.ebay.com",
				//"-http-proxy-port","80"
				};
		try{
			performDirectCodeGen(testArgs, binDir);}
			catch(Exception e ){
				Assert.fail("remote wsdl test failed with exception" + e.getMessage() + "caused by" + e.getCause());
			}
		
		
	}


	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allStockQuoteSvc6() {
File sl = getCodegenQEDataFileInput("service_layers.txt");
		
		String testArgs[] =  new String[] {
				
				"-genType","All",
				"-gip","org.ebayopensource.qaservices.stockquote.intf",
				"-namespace","http://www.ebayopensource.com/soaframework/service/StockQuote",
				"-serviceName","StockQuote",
				"-wsdl","http://www.webservicex.net/stockquote.asmx?WSDL",
				"-scv","1.0.0",
				"-slayer","UD_INTERMEDIATE",
				"-asl",sl.getAbsolutePath(),
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				//"-http-proxy-host","skyline.qa.ebay.com",
				//"-http-proxy-port","80"
				};
		try{
			performDirectCodeGen(testArgs, binDir);
			Assert.fail("remote wsdl test without exception");	
		}
			catch(Exception e ){
				Assert.assertTrue(e.getMessage().contains("Invalid service layer specified : UD_INTERMEDIATE"));
			}
		
		
		
		
	}


	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void allStockQuoteSvc7() {
		
File sl = getCodegenQEDataFileInput("service_layers2.txt");
		
		String testArgs[] =  new String[] {
				
				"-genType","All",
				"-gip","org.ebayopensource.qaservices.stockquote.intf",
				"-namespace","http://www.ebayopensource.com/soaframework/service/StockQuote",
				"-serviceName","StockQuote",
				"-wsdl","http://www.webservicex.net/stockquote.asmx?WSDL",
				"-scv","1.0.0",
				"-slayer","UD_INTERMEDIATE",
				"-asl",sl.getAbsolutePath(),
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				//"-http-proxy-host","skyline.qa.ebay.com",
				//"-http-proxy-port","80"
				};
		try{
			performDirectCodeGen(testArgs, binDir);
			
			
		}
			catch(Exception e ){
				Assert.fail("remote wsdl test failed with exception" + e.getMessage() + "caused by" + e.getCause());
			}
			
	}


	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void interfaceStockQuoteWSDLNonExist() {
		
		String testArgs[] =  new String[] {
				
				"-genType","Interface",
				"-gip","org.ebayopensource.qaservices.stockquote.intf",
				"-namespace","http://www.ebayopensource.com/soaframework/service/StockQuote",
				"-serviceName","StockQuote",
				"-wsdl","http://www.webservicex.net/stockquoteNot.asmx?WSDL",
				"-scv","1.0.0",
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				//"-http-proxy-host","skyline.qa.ebay.com",
				//"-http-proxy-port","80"
				};
				 
		try{
			performDirectCodeGen(testArgs, binDir);
			Assert.fail("remote wsdl test without exception");
			
		}
			catch(Exception e ){
				e.printStackTrace();
			}
		
	}


	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void clientStockQuoteSvc() {
		
		String testArgs1[] =  new String[] {
				
				"-genType","Interface",
				"-gip","org.ebayopensource.qaservices.stockquote.intf",
				"-namespace","http://www.ebayopensource.com/soaframework/service/StockQuote",
				"-serviceName","StockQuote",
				"-wsdl","http://www.webservicex.net/stockquote.asmx?WSDL",
				"-scv","1.0.0",
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				//"-http-proxy-host","skyline.qa.ebay.com",
				//"-http-proxy-port","80"
				};
				 
				try{
				performDirectCodeGen(testArgs1, binDir);}
				catch(Exception e ){
					Assert.fail("remote wsdl test failed with exception" + e.getMessage() + "caused by" + e.getCause());
				}
		
		String testArgs[] =  new String[] {
				
				"-genType","Client",
				"-interface","org/ebayopensource/qaservices/stockquote/intf/StockQuote.java",
				"-serviceName","StockQuote",
				"-scv","1.0.0",
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				};
				 
				try{
				performDirectCodeGen(testArgs, binDir);}
				catch(Exception e ){
					Assert.fail("remote wsdl test failed with exception" + e.getMessage() + "caused by" + e.getCause());
				}
	
	}


	/**
	 * @check  Exceptions need to be handled
	 */
	@Test
	public  void clientNoConfigStockQuoteSvc() {
		
String testArgs1[] =  new String[] {
				
				"-gentype","All",
				"-gip","org.ebayopensource.qaservices.stockquote.intf",
				"-namespace","http://www.ebayopensource.com/soaframework/service/StockQuote",
				"-serviceName","StockQuote",
				"-wsdl","http://www.webservicex.net/stockquote.asmx?WSDL",
				"-scv","1.0.0",
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				//"-http-proxy-host","skyline.qa.ebay.com",
				//"-http-proxy-port","80"
				};
				 
				try{
				performDirectCodeGen(testArgs1, binDir);}
				catch(Exception e ){
					Assert.fail("remote wsdl test failed with exception" + e.getMessage() + "caused by" + e.getCause());
				}
		
		String testArgs[] =  new String[] {
				
				"-genType","ClientNoConfig",
				"-gip","org.ebayopensource.qaservices.stockquote.intf",
				"-namespace","http://www.ebayopensource.com/soaframework/service/StockQuote",
				"-interface","org/ebayopensource/qaservices/stockquote/intf/StockQuote.java",
				"-serviceName","StockQuote",
				"-scv","1.0.0",
				"-dest",destDir.getAbsolutePath(),
				"-src",destDir.getAbsolutePath(),
				"-bin",binDir.getAbsolutePath(),
				};
				 
				try{
				performDirectCodeGen(testArgs, binDir);}
				catch(Exception e ){
					Assert.fail("remote wsdl test failed with exception" + e.getMessage() + "caused by" + e.getCause());
				}
		
		
	}
}

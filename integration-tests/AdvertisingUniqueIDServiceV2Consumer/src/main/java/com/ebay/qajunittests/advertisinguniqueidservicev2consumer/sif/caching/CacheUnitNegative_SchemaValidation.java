package com.ebay.qajunittests.advertisinguniqueidservicev2consumer.sif.caching;

import org.junit.Assert;
import org.junit.Test;

import com.ebay.marketplace.v1.services.GetEmployeeDetailsRequest;
import com.ebay.soaframework.common.cachepolicy.CacheContext;
import com.ebay.soaframework.common.cachepolicy.CacheKey;
import com.ebay.soaframework.common.cachepolicy.CachePolicyDesc;
import com.ebay.soaframework.common.exceptions.ServiceCreationException;
import com.ebay.soaframework.common.exceptions.ServiceException;
import com.ebay.soaframework.spf.impl.internal.config.ServiceConfigManager;
import com.ebay.soaframework.spf.impl.internal.pipeline.ServerMessageProcessor;
import com.ebay.soaframework.spf.impl.internal.service.ServerServiceDesc;
import com.ebay.soaframework.spf.impl.internal.service.ServerServiceDescFactory;

public class CacheUnitNegative_SchemaValidation  {

	/*static{
		System.setProperty(ParseUtils.SYS_PROP_CONFIG_SCHEMA_CHECK, "ERROR");
	}*/
	
	@Test
	public void testInvalidSchemaDef_MultipleTTLEntries() throws ServiceException{
		System.out.println("Start testInvalidSchemaDef_MultipleTTLEntries");
		GetEmployeeDetailsRequest req = new GetEmployeeDetailsRequest();
    	req.setId2(1);
    	req.setSsn2(11111111);
    	req.setUserName2("Surya");

		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
	
		try {
			
			configManager.setConfigTestCase("UnitCacheNeg_SchemaMultipleTTL");
			ServerMessageProcessor.getInstance();
			ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc("SOATestAP1Caching");
			CachePolicyDesc desc = serviceDesc.getCachePolicyDesc();
			CacheContext context = new CacheContext().setOpName("getEmployeeDetails").setRequest(req);
			CacheKey cacheKey = desc.generateCacheKey(context);
			
		    Assert.assertTrue(false);
		}
		catch(ServiceCreationException e) {
			Assert.assertTrue(e.getMessage().contains("org.xml.sax.SAXParseException"));
		}catch(Exception e){
			 Assert.assertTrue(false);
		}
		finally {
			configManager.setConfigTestCase("config");
		}
	}
	
	@Test
	public void testInvalidSchemaDef_MultipleKeyExpressionEntries() throws ServiceException{
		System.out.println("Start testInvalidSchemaDef_MultipleKeyExpressionEntries");
		GetEmployeeDetailsRequest req = new GetEmployeeDetailsRequest();
    	req.setId2(1);
    	req.setSsn2(11111111);
    	req.setUserName2("Surya");

		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
	
		try {
			
			configManager.setConfigTestCase("UnitCacheNeg_SchemaMultipleKeyExpression");
			ServerMessageProcessor.getInstance();
			ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc("SOATestAP1Caching");
			CachePolicyDesc desc = serviceDesc.getCachePolicyDesc();
			CacheContext context = new CacheContext().setOpName("getEmployeeDetails").setRequest(req);
			CacheKey cacheKey = desc.generateCacheKey(context);
			
		    Assert.assertTrue(false);
		}
		catch(ServiceCreationException e) {
			Assert.assertTrue(e.getMessage().contains("org.xml.sax.SAXParseException"));
		}catch(Exception e){
			 Assert.assertTrue(false);
		}
		finally {
			configManager.setConfigTestCase("config");
		}
	}
	
	@Test
	public void testInvalidSchemaDef_NoEntryForTTL() throws ServiceException{
		System.out.println("Start testInvalidSchemaDef_NoEntryForTTL");
		GetEmployeeDetailsRequest req = new GetEmployeeDetailsRequest();
    	req.setId2(1);
    	req.setSsn2(11111111);
    	req.setUserName2("Surya");

		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
	
		try {
			
			configManager.setConfigTestCase("UnitCacheNeg_SchemaEmptyTTL");
			ServerMessageProcessor.getInstance();
			ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc("SOATestAP1Caching");
			CachePolicyDesc desc = serviceDesc.getCachePolicyDesc();
			CacheContext context = new CacheContext().setOpName("getEmployeeDetails").setRequest(req);
			CacheKey cacheKey = desc.generateCacheKey(context);
			
		    Assert.assertTrue(false);
		}
		catch(ServiceCreationException e) {
			Assert.assertTrue(e.getMessage().contains("org.xml.sax.SAXParseException"));
		}catch(Exception e){
			 Assert.assertTrue(false);
		}
		finally {
			configManager.setConfigTestCase("config");
		}
	}
	
	@Test
	public void testInvalidSchemaDef_NoEntryForKeyExpressionSet() throws ServiceException{
		System.out.println("Start testInvalidSchemaDef_NoEntryForKeyExpressionSet");
		GetEmployeeDetailsRequest req = new GetEmployeeDetailsRequest();
    	req.setId2(1);
    	req.setSsn2(11111111);
    	req.setUserName2("Surya");

		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
	
		try {
			
			configManager.setConfigTestCase("UnitCacheNeg_SchemaNoKeyExpression");
			ServerMessageProcessor.getInstance();
			ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc("SOATestAP1Caching");
			CachePolicyDesc desc = serviceDesc.getCachePolicyDesc();
			CacheContext context = new CacheContext().setOpName("getEmployeeDetails").setRequest(req);
			CacheKey cacheKey = desc.generateCacheKey(context);
			
		    Assert.assertTrue(false);
		}
		catch(ServiceCreationException e) {
			Assert.assertTrue(e.getMessage().contains("org.xml.sax.SAXParseException"));
		}catch(Exception e){
			 Assert.assertTrue(false);
		}
		finally {
			configManager.setConfigTestCase("config");
		}
	}
	
	@Test
	public void testInvalidSchemaDef_MissingKeyExpressionInKeyExpressionSet() throws ServiceException{
		System.out.println("Start testInvalidSchemaDef_MissingKeyExpressionInKeyExpressionSet");
		GetEmployeeDetailsRequest req = new GetEmployeeDetailsRequest();
    	req.setId2(1);
    	req.setSsn2(11111111);
    	req.setUserName2("Surya");

		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
	
		try {
			
			configManager.setConfigTestCase("UnitCacheNeg_SchemaMissingKeyInKeyExpression");
			ServerMessageProcessor.getInstance();
			ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc("SOATestAP1Caching");
			
		    Assert.assertTrue(false);
		}
		catch(ServiceCreationException e) {
			Assert.assertTrue(e.getMessage().contains("org.xml.sax.SAXParseException"));
		}catch(Exception e){
			 Assert.assertTrue(false);
		}
		finally {
			configManager.setConfigTestCase("config");
		}
	}
	
	@Test
	public void testInvalidSchemaDef_MissingOperationCachePolicy() throws ServiceException{
		System.out.println("Start testInvalidSchemaDef_MissingOperationCachePolicy");
		GetEmployeeDetailsRequest req = new GetEmployeeDetailsRequest();
    	req.setId2(1);
    	req.setSsn2(11111111);
    	req.setUserName2("Surya");

		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
	
		try {
			
			configManager.setConfigTestCase("UnitCacheNeg_SchemaMissingOperationCachePolicy");
			ServerMessageProcessor.getInstance();
			ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc("SOATestAP1Caching");
			CachePolicyDesc desc = serviceDesc.getCachePolicyDesc();
			Assert.assertNotNull(desc);
	
		}
		catch(ServiceCreationException e) {
			Assert.assertTrue(false);
		}catch(Exception e){
			 Assert.assertTrue(false);
		}
		finally {
			configManager.setConfigTestCase("config");
		}
	}
	
	@Test
	public void testInvalidSchemaDef_MissingSchemaCachePolicy() throws ServiceException{
		System.out.println("Start testInvalidSchemaDef_MissingSchemaCachePolicy");
		GetEmployeeDetailsRequest req = new GetEmployeeDetailsRequest();
    	req.setId2(1);
    	req.setSsn2(11111111);
    	req.setUserName2("Surya");

		ServiceConfigManager configManager = ServiceConfigManager.getInstance();
	
		try {
			
			configManager.setConfigTestCase("UnitCacheNeg_SchemaMissingSchemaCachePolicy");
			ServerMessageProcessor.getInstance();
			ServerServiceDesc serviceDesc = ServerServiceDescFactory.getInstance().getServiceDesc("SOATestAP1Caching");
			
		    Assert.assertTrue(false);
		}
		catch(ServiceCreationException e) {
			Assert.assertTrue(e.getMessage().contains("org.xml.sax.SAXParseException"));
		}catch(Exception e){
			 Assert.assertTrue(false);
		}
		finally {
			configManager.setConfigTestCase("config");
		}
	}
	
}

package org.ebayopensource.turmeric.qajunittests.advertisinguniqueidservicev1.sif;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import junit.framework.Assert;

import org.ebayopensource.turmeric.advertising.v1.services.FileAttachmentType;
import org.ebayopensource.turmeric.advertising.v1.services.TestAttachment;
import org.ebayopensource.turmeric.advertisinguniqueidservicev1.gen.SharedAdvertisingUniqueIDServiceV1Consumer;
import org.ebayopensource.turmeric.runtime.tests.common.util.QEFileUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class AttachmentCacheTests {
	File f1, fClient, fServer;
	static String currentDir;
	long MAX_SIZE;
	FileAttachmentType response = null;
	FileOutputStream out = null;
	static File f;

	@BeforeClass
	public static void setup() throws IOException {
		currentDir = System.getProperty("user.dir");
		 f = new File(currentDir + "\\attachmentcache");
		 if (QEFileUtils.deleteDir(f)) System.out.println("done");
	}
	
	/*
	 * Existing usecase
	 * default size = 2kb
	 */
	@Test
	public void testCacheONDefaultLimit1KbFile() throws Exception {
		System.out.println(" ** testCacheONDefaultLimit1KbFile ** ");
		f1 = new File(currentDir + "\\1kbAttachment.txt");
		fClient = new File(currentDir + "\\Client1kbAttachment.txt");
		fServer = new File(currentDir + "\\Server1kbAttachment.txt");
		if (!f1.exists()) QEFileUtils.createFileForTest(Integer.valueOf(1024), f1);
		MAX_SIZE = f1.length();
		SharedAdvertisingUniqueIDServiceV1Consumer client = new 
		SharedAdvertisingUniqueIDServiceV1Consumer(
				"AdvertisingUniqueIDServiceV1Consumer", "attachmentcache4");
		DataHandler dh = new DataHandler(new FileDataSource(f1));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(dh);
		value.setFilePath(currentDir + "\\");
		value.setFileName("1kbAttachment.txt");
		value.setSize(MAX_SIZE);
		param0.setIn(value);
		response = client.testAttachment(param0).getOut();
		assertOnResponse(response, 0, MAX_SIZE);
		
		System.out.println("-- testCacheONDefaultLimit1KbFile --");
	}
	
	@After
	public void cleanUp() {
		if (QEFileUtils.deleteDir(f)) System.out.println("done");
		else System.out.println("not done");
		fClient.delete();
		fServer.delete();
	}

	@Test
	public void testCacheONDefaultLimit2KbFile() throws Exception {
		System.out.println("-- testCacheONDefaultLimit2KbFile --");
		f1 = new File(currentDir + "\\2kbAttachment.txt");
		fClient = new File(currentDir + "\\Client2kbAttachment.txt");
		fServer = new File(currentDir + "\\Server2kbAttachment.txt");
		if (!f1.exists()) QEFileUtils.createFileForTest(Integer.valueOf(2048), f1);
		
		MAX_SIZE = f1.length();
		
		SharedAdvertisingUniqueIDServiceV1Consumer client = new 
		SharedAdvertisingUniqueIDServiceV1Consumer(
				"AdvertisingUniqueIDServiceV1Consumer", "attachmentcache4");
		DataHandler dh = new DataHandler(new FileDataSource(f1));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(dh);
		value.setFilePath(currentDir + "\\");
		value.setFileName("2kbAttachment.txt");
		value.setSize(MAX_SIZE);
		param0.setIn(value);
		response = client.testAttachment(param0).getOut();
		assertOnResponse(response, 1, MAX_SIZE);
		System.out.println("-- testCacheONDefaultLimit2KbFile --");
	}

	@Test
	public void testCacheONDefaultLimit3KbFile() throws Exception {
		System.out.println("-- testCacheONDefaultLimit3KbFile --");
		f1 = new File(currentDir + "\\3kbAttachment.txt");
		fClient = new File(currentDir + "\\Client3kbAttachment.txt");
		fServer = new File(currentDir + "\\Server3kbAttachment.txt");
		
		if (!f1.exists()) QEFileUtils.createFileForTest(Integer.valueOf(3072), f1);
		MAX_SIZE = f1.length();
		SharedAdvertisingUniqueIDServiceV1Consumer client = new 
		SharedAdvertisingUniqueIDServiceV1Consumer(
				"AdvertisingUniqueIDServiceV1Consumer", "attachmentcache4");
		DataHandler dh = new DataHandler(new FileDataSource(f1));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(dh);
		value.setFilePath(currentDir + "\\");
		value.setFileName("3kbAttachment.txt");
		value.setSize(MAX_SIZE);
		param0.setIn(value);
		response = client.testAttachment(param0).getOut();
		assertOnResponse(response, 1, MAX_SIZE);
		//		Assert on the temp location
		System.out.println("-- testCacheONDefaultLimit3KbFile --");
	}

	@Test
	public void testCacheON100bLimit1kbFile() throws Exception {
		System.out.println("-- testCacheON4KbLimit1KbFile --");
		
		f1 = new File(currentDir + "\\1kbAttachment.txt");
		fClient = new File(currentDir + "\\Client1kbAttachment.txt");
		fServer = new File(currentDir + "\\Server1kbAttachment.txt");
		if (!f1.exists()) QEFileUtils.createFileForTest(Integer.valueOf(1024), f1);
		MAX_SIZE = f1.length();
		SharedAdvertisingUniqueIDServiceV1Consumer client = new 
		SharedAdvertisingUniqueIDServiceV1Consumer(
				"AdvertisingUniqueIDServiceV1Consumer", "attachmentcache1");
		DataHandler dh = new DataHandler(new FileDataSource(f1));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(dh);
		value.setFilePath(currentDir + "\\");
		value.setFileName("1kbAttachment.txt");
		value.setSize(MAX_SIZE);
		param0.setIn(value);
		response = client.testAttachment(param0).getOut();
		assertOnResponse(response, 1, MAX_SIZE);
		
		System.out.println("-- testCacheON4KbLimit1KbFile --");
	}

	@Test
	public void testCacheON0kbLimit1KbFile() throws Exception {
		System.out.println(" ** testCacheON0kbLimit1KbFile ** ");
		f1 = new File(currentDir + "\\1kbAttachment.txt");
		fClient = new File(currentDir + "\\Client1kbAttachment.txt");
		fServer = new File(currentDir + "\\Server1kbAttachment.txt");
		if (!f1.exists()) QEFileUtils.createFileForTest(Integer.valueOf(1024), f1);
		MAX_SIZE = f1.length();
		SharedAdvertisingUniqueIDServiceV1Consumer client = new 
		SharedAdvertisingUniqueIDServiceV1Consumer(
				"AdvertisingUniqueIDServiceV1Consumer", "attachmentcache3");
		DataHandler dh = new DataHandler(new FileDataSource(f1));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(dh);
		value.setFilePath(currentDir + "\\");
		value.setFileName("1kbAttachment.txt");
		value.setSize(MAX_SIZE);
		param0.setIn(value);
		response = client.testAttachment(param0).getOut();
		Assert.assertFalse(f.exists());
		System.out.println(" ** testCacheON0kbLimit1KbFile ** ");
	}

	@Test
	public void testCacheOFFDefaultLimit1KbFile() throws Exception {
		System.out.println(" ** testCacheOFFDefaultLimit1KbFile ** ");
		f1 = new File(currentDir + "\\1kbAttachment.txt");
		fClient = new File(currentDir + "\\Client1kbAttachment.txt");
		fServer = new File(currentDir + "\\Server1kbAttachment.txt");
		
		if (f1.exists()) QEFileUtils.createFileForTest(Integer.valueOf(1024), f1);
		MAX_SIZE = f1.length();
		SharedAdvertisingUniqueIDServiceV1Consumer client = new 
		SharedAdvertisingUniqueIDServiceV1Consumer(
				"AdvertisingUniqueIDServiceV1Consumer", "attachmentcache2");
		DataHandler dh = new DataHandler(new FileDataSource(f1));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(dh);
		value.setFilePath(currentDir + "\\");
		value.setFileName("1kbAttachment.txt");
		value.setSize(MAX_SIZE);
		param0.setIn(value);
		response = client.testAttachment(param0).getOut();
		assertOnResponse(response, 0, MAX_SIZE);
		System.out.println(" ** testCacheOFFDefaultLimit1KbFile ** ");
	}

	@Test
	public void testCacheOFFDefaultLimit2KbFile() throws Exception {
		System.out.println(" ** testCacheOFFDefaultLimit2KbFile ** ");
		f1 = new File(currentDir + "\\2kbAttachment.txt");
		fClient = new File(currentDir + "\\Client2kbAttachment.txt");
		fServer = new File(currentDir + "\\Server2kbAttachment.txt");
		if (!f1.exists()) QEFileUtils.createFileForTest(Integer.valueOf(2*1024), f1);
		MAX_SIZE = f1.length();
		SharedAdvertisingUniqueIDServiceV1Consumer client = new 
		SharedAdvertisingUniqueIDServiceV1Consumer(
				"AdvertisingUniqueIDServiceV1Consumer", "attachmentcache2");
		DataHandler dh = new DataHandler(new FileDataSource(f1));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(dh);
		value.setFilePath(currentDir + "\\");
		value.setFileName("2kbAttachment.txt");
		value.setSize(MAX_SIZE);
		param0.setIn(value);
		response = client.testAttachment(param0).getOut();
		assertOnResponse(response, 0, MAX_SIZE);
		System.out.println(" ** testCacheOFFDefaultLimit2KbFile ** ");
	}
	

	@Test
	public void testCacheOFFDefaultLimit3KbFile() throws Exception {
		System.out.println(" ** testCacheOFFDefaultLimit3KbFile ** ");
		f1 = new File(currentDir + "\\3kbAttachment.txt");
		fClient = new File(currentDir + "\\Client3kbAttachment.txt");
		fServer = new File(currentDir + "\\Server3kbAttachment.txt");
		if (!f1.exists()) QEFileUtils.createFileForTest(Integer.valueOf(3*1024), f1);
		MAX_SIZE = f1.length();
		SharedAdvertisingUniqueIDServiceV1Consumer client = new 
		SharedAdvertisingUniqueIDServiceV1Consumer(
				"AdvertisingUniqueIDServiceV1Consumer", "attachmentcache2");
		DataHandler dh = new DataHandler(new FileDataSource(f1));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(dh);
		value.setFilePath(currentDir + "\\");
		value.setFileName("3kbAttachment.txt");
		value.setSize(MAX_SIZE);
		param0.setIn(value);
		response = client.testAttachment(param0).getOut();
		assertOnResponse(response, 0, MAX_SIZE);
		System.out.println(" ** testCacheOFFDefaultLimit3KbFile ** ");
	}
	
	@Test
	public void testCacheOFF100bLimit1KbFile() throws Exception {
		System.out.println(" ** testCacheOFFDefaultLimit1KbFile ** ");
		f1 = new File(currentDir + "\\1kbAttachment.txt");
		fClient = new File(currentDir + "\\Client1kbAttachment.txt");
		fServer = new File(currentDir + "\\Server1kbAttachment.txt");
		
		if (f1.exists()) QEFileUtils.createFileForTest(Integer.valueOf(1024), f1);
		MAX_SIZE = f1.length();
		SharedAdvertisingUniqueIDServiceV1Consumer client = new 
		SharedAdvertisingUniqueIDServiceV1Consumer(
				"AdvertisingUniqueIDServiceV1Consumer", "attachmentcache5");
		DataHandler dh = new DataHandler(new FileDataSource(f1));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(dh);
		value.setFilePath(currentDir + "\\");
		value.setFileName("1kbAttachment.txt");
		value.setSize(MAX_SIZE);
		param0.setIn(value);
		response = client.testAttachment(param0).getOut();
		assertOnResponse(response, 0, MAX_SIZE);
		System.out.println(" ** testCacheOFFDefaultLimit1KbFile ** ");
	}

	@Test
	public void testCacheOFF100bLimit2KbFile() throws Exception {
		System.out.println(" ** testCacheOFFDefaultLimit2KbFile ** ");
		f1 = new File(currentDir + "\\2kbAttachment.txt");
		fClient = new File(currentDir + "\\Client2kbAttachment.txt");
		fServer = new File(currentDir + "\\Server2kbAttachment.txt");
		if (!f1.exists()) QEFileUtils.createFileForTest(Integer.valueOf(2*1024), f1);
		MAX_SIZE = f1.length();
		SharedAdvertisingUniqueIDServiceV1Consumer client = new 
		SharedAdvertisingUniqueIDServiceV1Consumer(
				"AdvertisingUniqueIDServiceV1Consumer", "attachmentcache5");
		DataHandler dh = new DataHandler(new FileDataSource(f1));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(dh);
		value.setFilePath(currentDir + "\\");
		value.setFileName("2kbAttachment.txt");
		value.setSize(MAX_SIZE);
		param0.setIn(value);
		response = client.testAttachment(param0).getOut();
		assertOnResponse(response, 0, MAX_SIZE);
		System.out.println(" ** testCacheOFFDefaultLimit2KbFile ** ");
	}
	
	private void assertOnResponse(
			FileAttachmentType response, int cacheSize, long size)
	throws FileNotFoundException, IOException {
		// assert on server file
		String fileName = response.getFileName();
		String filePath = response.getFilePath();
		long fileSize = response.getSize().longValue();
		out = new FileOutputStream(new File(filePath + fileName));
		DataHandler dh = response.getData();
		if (!QEFileUtils.writeData(dh, MAX_SIZE, out)) 
			Assert.fail("File not written"); 
		//		Assert on the temp location
		if (cacheSize == 1) Assert.assertEquals(cacheSize, f.list().length);
		else Assert.assertFalse(f.exists());
	}
	

}

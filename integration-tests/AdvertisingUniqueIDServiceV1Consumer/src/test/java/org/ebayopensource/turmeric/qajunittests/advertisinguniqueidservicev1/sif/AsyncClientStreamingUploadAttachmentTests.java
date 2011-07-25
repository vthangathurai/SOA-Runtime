package org.ebayopensource.turmeric.qajunittests.advertisinguniqueidservicev1.sif;

import static org.junit.Assert.assertNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

import junit.framework.Assert;

import org.ebayopensource.turmeric.advertising.v1.services.FileAttachmentType;
import org.ebayopensource.turmeric.advertising.v1.services.TestAttachment;
import org.ebayopensource.turmeric.advertising.v1.services.TestAttachmentResponse;
import org.ebayopensource.turmeric.advertisinguniqueidservicev1.gen.SharedAdvertisingUniqueIDServiceV1Consumer;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;
import org.ebayopensource.turmeric.runtime.tests.common.util.QEFileUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class AsyncClientStreamingUploadAttachmentTests {
	
	public static final long DEFAULT_SLEEP_TIME = 100;
	FileAttachmentType response = null;
	static File f1, f2, f3mbServer,f3gbServer;
	static long MAX_SIZE1, MAX_SIZE2;
	int CHUNK_SIZE = 4096;
	long sizeCounter = 0;
	BufferedInputStream br = null;
	FileOutputStream out = null;
	static String currentDir;

	@BeforeClass
	public static void setUp() throws Exception {
		currentDir = System.getProperty("user.dir");
		System.out.println("user.dir=" + currentDir);
		f3mbServer = new File(currentDir + "\\Server3mbAttachment.txt");
		f3gbServer = new File(currentDir + "\\Server3gbAttachment.txt");
		f1 = new File(currentDir + "\\3mbAttachment.txt");
//		f2 = new File(currentDir + "\\3gbAttachment.txt");
		if (!f1.exists()) QEFileUtils.createFileForTest(Integer.valueOf(3145728), f1);
//		if (!f2.exists()) QEFileUtils.createFileForTest(Integer.valueOf(1024 * 1024 * 1024), f2);
		MAX_SIZE1 = f1.length();
//		MAX_SIZE2 = f2.length();
	}

	@After
	public void cleanUp() {
		if (f3mbServer.exists()) f3mbServer.delete();
		if (f3gbServer.exists()) f3gbServer.delete();
	}

	@Ignore
	public void testAsyncPullClientStreamingTrueUploadWith3GBAttachment() throws Exception {
		System.out.println("-- testAsyncPullClientStreamingTrueUploadWith3GBAttachment --");

		SharedAdvertisingUniqueIDServiceV1Consumer client = 
			new SharedAdvertisingUniqueIDServiceV1Consumer(
					"AdvertisingUniqueIDServiceV1Consumer", "ClientStreaming");
		DataHandler dh = new DataHandler(new FileDataSource(f2));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(dh);
		value.setType(true);
		value.setFilePath(currentDir + "\\");
		value.setFileName("3gbAttachment.txt");
		value.setSize(MAX_SIZE2);
		param0.setIn(value);
		Response<TestAttachmentResponse> resp = client.testAttachmentAsync(param0);
		while (!resp.isDone()) { // take a nap
			Thread.sleep(1000L);
		}
		System.out.println("isDone is true, now process response.");

		response = resp.get().getOut();
//		assertOnResponseAttachment(f3gbServer, MAX_SIZE2);

		System.out.println("-- testAsyncPullClientStreamingTrueUploadWith3GBAttachment --");
	}

	@Test
	public void testAsyncPullClientStreamingTrueUploadWith3MBAttachment() throws Exception {
		System.out.println("-- testAsyncPullClientStreamingTrueWith3MBAttachment --");

		SharedAdvertisingUniqueIDServiceV1Consumer client = 
			new SharedAdvertisingUniqueIDServiceV1Consumer(
					"AdvertisingUniqueIDServiceV1Consumer", "ClientStreaming");
		DataHandler dh = new DataHandler(new FileDataSource(f1));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(dh);
		value.setType(true);
		value.setFilePath(currentDir + "\\");
		value.setFileName("3mbAttachment.txt");
		value.setSize(MAX_SIZE1);
		param0.setIn(value);
		Response<TestAttachmentResponse> resp = client.testAttachmentAsync(param0);
		while (!resp.isDone()) {
			Thread.sleep(5000);
		}
		System.out.println("isDone is true, now process response.");

		response = resp.get().getOut();
		assertOnResponseAttachment(f3mbServer, MAX_SIZE1);

		System.out.println("-- testAsyncPullClientStreamingTrueWith3MBAttachment --");
	}


	@Ignore
	public void testAsyncPushUploadClientStreamingTrueWith3GBAttachment() throws Exception {
		System.out.println("-- testAsyncPushClientStreamingTrueWith3GBAttachment --");
		SharedAdvertisingUniqueIDServiceV1Consumer client = 
			new SharedAdvertisingUniqueIDServiceV1Consumer(
					"AdvertisingUniqueIDServiceV1Consumer", "ClientStreaming");
		AttachmentAsyncHandler<TestAttachmentResponse> attHandler = new AttachmentAsyncHandler<TestAttachmentResponse>();
		Future<?> attFutureObj = null;
		DataHandler dh = new DataHandler(new FileDataSource(f2));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(dh);
		value.setType(true);
		value.setFilePath(currentDir + "\\");
		value.setFileName("3gbAttachment.txt");
		value.setSize(MAX_SIZE2);
		param0.setIn(value);
		attFutureObj = client.testAttachmentAsync(param0, attHandler);
		while (!attFutureObj.isDone()) {
			Thread.sleep(1000L);
		}
		response = attHandler.resp.get().getOut();
		
		assertOnResponseAttachment(f3gbServer, MAX_SIZE2);

		System.out.println("-- testAsyncPushClientStreamingTrueWith3GBAttachment --");
	}

	@Test
	public void testAsyncPushClientStreamingTrueWith3MBAttachment() throws ServiceException, InterruptedException, ExecutionException, IOException {
		System.out.println("-- testAsyncPushClientStreamingTrueWith3MBAttachment --");
		SharedAdvertisingUniqueIDServiceV1Consumer client = new SharedAdvertisingUniqueIDServiceV1Consumer("AdvertisingUniqueIDServiceV1Consumer", "ClientStreaming");
		AttachmentAsyncHandler<TestAttachmentResponse> attHandler = new AttachmentAsyncHandler<TestAttachmentResponse>();
		Future<?> attFutureObj = null;
		DataHandler dh = new DataHandler(new FileDataSource(f1));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(dh);
		value.setType(true);
		value.setFilePath(currentDir + "\\");
		value.setFileName("3mbAttachment.txt");
		value.setSize(MAX_SIZE1);
		param0.setIn(value);
		try {
			attFutureObj = client.testAttachmentAsync(param0, attHandler);
		} catch (WebServiceException e1) {
			e1.printStackTrace();
			assertNull(e1);
		}
		while (!attFutureObj.isDone()) {
			try {
				Thread.sleep(DEFAULT_SLEEP_TIME);
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		System.out.println(attHandler.resp.get().getOut().getFileName());
		response = attHandler.resp.get().getOut();
		out = new FileOutputStream(new File(response.getFilePath() + response.getFileName()));
		assertOnResponseAttachment(f3mbServer, MAX_SIZE1);

		System.out.println("-- testAsyncPushClientStreamingTrueWith3MBAttachment --");
	}


	@Test
	public void testSynchWith3MBAttachmentRemote() throws Exception {
		System.out.println("-- testSynchWith3MBAttachmentRemote --");

		SharedAdvertisingUniqueIDServiceV1Consumer client = 
			new SharedAdvertisingUniqueIDServiceV1Consumer(
					"AdvertisingUniqueIDServiceV1Consumer", "ClientStreaming");
		DataHandler dh = new DataHandler(new FileDataSource(f1));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(dh);value.setType(true);
		value.setFilePath(currentDir + "\\");
		value.setFileName("3mbAttachment.txt");
		value.setSize(f1.length());
		param0.setIn(value);
		response = client.testAttachment(param0).getOut();
		assertOnResponseAttachment(f3mbServer, MAX_SIZE1);

		System.out.println("-- testSynchWith3MBAttachmentRemote --");

	}
	@Test
	public void testSynchWith3MBAttachmentLocal() throws Exception {
		System.out.println("-- testSynchWith3MBAttachmentLocal --");
		SharedAdvertisingUniqueIDServiceV1Consumer client = 
			new SharedAdvertisingUniqueIDServiceV1Consumer(
					"AdvertisingUniqueIDServiceV1Consumer", "local");
		DataHandler dh = new DataHandler(new FileDataSource(f1));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(dh);
		value.setType(true);
		value.setFilePath(currentDir + "\\");
		value.setFileName("3mbAttachment.txt");
		value.setSize(f1.length());
		param0.setIn(value);
		response = client.testAttachment(param0).getOut();
		assertOnResponseAttachment(f3mbServer, MAX_SIZE1);

		System.out.println("-- testSynchWith3MBAttachmentLocal --");

	}
	@Ignore
	public void testSynchWith3GBAttachmentLocal() throws Exception {
		System.out.println("-- testSynchWith3GBAttachmentLocal --");

		SharedAdvertisingUniqueIDServiceV1Consumer client = 
			new SharedAdvertisingUniqueIDServiceV1Consumer(
					"AdvertisingUniqueIDServiceV1Consumer", "local");
		client.getServiceInvokerOptions().getTransportOptions().setSkipSerialization(Boolean.TRUE);
		DataHandler dh = new DataHandler(new FileDataSource(f2));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(dh);
		value.setType(true);
		value.setFilePath(currentDir + "\\");
		value.setFileName("3gbAttachment.txt");
		value.setSize(MAX_SIZE2);
		param0.setIn(value);
		response = client.testAttachment(param0).getOut();
		assertOnResponseAttachment(f3gbServer, MAX_SIZE2);

		System.out.println("-- testSynchWith3GBAttachmentLocal --");
	}

	@Ignore
	public void testSynchWith3GBAttachmentRemote() throws Exception {
		System.out.println("-- testSynchWith3GBAttachmentRemote --");
		SharedAdvertisingUniqueIDServiceV1Consumer client = 
			new SharedAdvertisingUniqueIDServiceV1Consumer(
					"AdvertisingUniqueIDServiceV1Consumer", "ClientStreaming");
		DataHandler dh = new DataHandler(new FileDataSource(f2));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(dh);
		value.setFilePath(currentDir + "\\");
		value.setFileName("3gbAttachment.txt");
		value.setType(true);
		value.setSize(MAX_SIZE2);
		param0.setIn(value);
		response = client.testAttachment(param0).getOut();
		out = new FileOutputStream(new File(response.getFilePath() + response.getFileName()));
		assertOnResponseAttachment(f3gbServer, MAX_SIZE2);

		System.out.println("-- testSynchWith3GBAttachmentRemote --");
	}

	private void assertOnResponseAttachment(
			File serverFile, long size)
	throws FileNotFoundException, IOException {
		// assert on server file
		System.out.println("Server file size = " + serverFile.length());
		Assert.assertEquals("Unexpected output file size", serverFile.length(), size);
		Assert.assertTrue("Output file " + serverFile.getAbsolutePath() + " doesn't exist", serverFile.exists());
		// assert on Response
		Assert.assertTrue("Unexpected file name: " + response.getFileName(), 
				response.getFileName().contains(serverFile.getName()));
	}




	protected class AttachmentAsyncHandler<T> implements AsyncHandler<T> {
		private Response<T> resp;
		private boolean isDone = false;
		public void handleResponse(Response<T> resp) {
			this.resp = resp;
			String currThreadNm = Thread.currentThread().getName();
			System.out.println("AttachmentAsyncHandler:handleResponse:Executing thread " + currThreadNm);
			try {
//				System.out.println("AttachmentAsyncHandler:handleResponse:");
				System.out.println(this.resp.get());
//				TestAttachmentResponse response = (TestAttachmentResponse)this.resp.get();
//				System.out.println(response.getOut().getFileName());
//				System.out.println(response.getOut().getSize());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} finally {
				isDone = true;
			}
		}
		public Response<T> getReturn() {
			return resp;
		}

		public boolean isDone() {
			return isDone;
		}

	}


}

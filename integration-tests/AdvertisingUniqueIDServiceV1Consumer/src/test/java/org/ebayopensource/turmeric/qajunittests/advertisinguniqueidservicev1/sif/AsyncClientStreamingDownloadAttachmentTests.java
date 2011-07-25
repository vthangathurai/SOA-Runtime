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

public class AsyncClientStreamingDownloadAttachmentTests {
	public static final long DEFAULT_SLEEP_TIME = 100;
	FileAttachmentType response = null;
	static File f1, f2, f3mbClient, f3gbClient;
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
		f3mbClient = new File(currentDir + "\\Client3mbAttachment.txt");
		f3gbClient = new File(currentDir + "\\Client3gbAttachment.txt");
		f1 = new File(currentDir + "\\3mbAttachment.txt");
		f2 = new File(currentDir + "\\3gbAttachment.txt");
		if (!f1.exists()) QEFileUtils.createFileForTest(Integer.valueOf(3145728), f1);
//		if (!f2.exists()) QEFileUtils.create3GBFile(f1, f2);
		MAX_SIZE1 = f1.length();
		MAX_SIZE2 = f2.length();
	}

	@After
	public void cleanUp() {
		if (f3mbClient.exists()) f3mbClient.delete();
		if (f3gbClient.exists()) f3gbClient.delete();
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
		value.setData(null);
		value.setType(false);
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
		assertOnResponseAttachment(f3gbClient, MAX_SIZE2, response);

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
		value.setData(null);
		value.setType(false);
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
		assertOnResponseAttachment(f3mbClient, MAX_SIZE1, response);

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
		DataHandler dh = new DataHandler(new FileDataSource(f1));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(null);
		value.setType(false);
		value.setFilePath(currentDir + "\\");
		value.setFileName("3gbAttachment.txt");
		value.setSize(MAX_SIZE2);
		param0.setIn(value);
		attFutureObj = client.testAttachmentAsync(param0, attHandler);
		while (!attFutureObj.isDone()) {
			Thread.sleep(1000L);
		}
		response = attHandler.resp.get().getOut();
		assertOnResponseAttachment(f3gbClient, MAX_SIZE2, response);

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
		value.setData(null);
		value.setType(false);
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
		out = new FileOutputStream(new File(response.getFileName()));
		assertOnResponseAttachment(f3mbClient, MAX_SIZE1, response);

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
		value.setData(null);
		value.setType(false);
		value.setFilePath(currentDir + "\\");
		value.setFileName("3mbAttachment.txt");
		value.setSize(f1.length());
		param0.setIn(value);
		response = client.testAttachment(param0).getOut();
		assertOnResponseAttachment(f3mbClient, MAX_SIZE1, response);

		System.out.println("-- testSynchWith3MBAttachmentRemote --");

	}
	@Test
	public void testSynchCaseWith3MBAttachmentLocal() throws Exception {
		System.out.println("-- testSynchCaseWith3MBAttachmentLocal --");
		SharedAdvertisingUniqueIDServiceV1Consumer client = 
			new SharedAdvertisingUniqueIDServiceV1Consumer(
					"AdvertisingUniqueIDServiceV1Consumer", "local");
		DataHandler dh = new DataHandler(new FileDataSource(f1));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(null);
		value.setType(false);
		value.setFilePath(currentDir + "\\");
		value.setFileName("3mbAttachment.txt");
		value.setSize(MAX_SIZE1);
		param0.setIn(value);
		response = client.testAttachment(param0).getOut();
		assertOnResponseAttachment(f3mbClient, MAX_SIZE1, response);

		System.out.println("-- testSynchCaseWith3MBAttachmentLocal --");

	}
	@Test
	public void testSynchWith3GBAttachmentLocal() throws Exception {
		System.out.println("-- testSynchWith3GBAttachmentLocal --");

		SharedAdvertisingUniqueIDServiceV1Consumer client = 
			new SharedAdvertisingUniqueIDServiceV1Consumer(
					"AdvertisingUniqueIDServiceV1Consumer", "local");
		client.getServiceInvokerOptions().getTransportOptions().setSkipSerialization(Boolean.TRUE);
		DataHandler dh = new DataHandler(new FileDataSource(f2));
		TestAttachment param0 = new TestAttachment();
		FileAttachmentType value = new FileAttachmentType();
		value.setData(null);
		value.setType(false);
		value.setFilePath(currentDir + "\\");
		value.setFileName("3gbAttachment.txt");
		value.setSize(MAX_SIZE2);
		param0.setIn(value);
		response = client.testAttachment(param0).getOut();
		assertOnResponseAttachment(f3gbClient, MAX_SIZE2, response);

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
		value.setData(null);
		value.setType(false);
		value.setFilePath(currentDir + "\\");
		value.setFileName("3gbAttachment.txt");
		value.setSize(MAX_SIZE2);
		param0.setIn(value);
		response = client.testAttachment(param0).getOut();
		assertOnResponseAttachment(f3gbClient, MAX_SIZE2, response);

		System.out.println("-- testSynchWith3GBAttachmentRemote --");
	}

	private void assertOnResponseAttachment(
			File file, long size, FileAttachmentType resp)
	throws FileNotFoundException, IOException {
		// assert on Response
		Assert.assertEquals(response.getFileName(), file.getName());
		Assert.assertEquals("Unexpected response attachment size", size, response.getSize().longValue());
		// write response attachment to output file
		File outFile = new File(resp.getFileName());
		out = new FileOutputStream(outFile);
		DataHandler dh = resp.getData();
		if (!QEFileUtils.writeData(dh, size, out)) Assert.fail("Did not write the received data");
		
		// assert on output file
		Assert.assertEquals("Unexpected size for output file " + outFile.getAbsolutePath(), 
				size, outFile.length());
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

/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
//B''H
package org.ebayopensource.turmeric.runtime.tests.common.sif.async;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.ebayopensource.turmeric.runtime.sif.service.Service;
import org.ebayopensource.turmeric.runtime.tests.common.jetty.AbstractWithProxyServerTest;
import org.junit.Assert;
import org.junit.Test;


public class ConnectionDropTest extends AbstractWithProxyServerTest {
	private final String ECHO_STRING = "BH Test String";
	
	private static Thread proxyThread;

	private static boolean s_stop = false;

	static {
		s_stop = false;
		proxyThread = new Thread(new RunPoxyServer());
		proxyThread.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	static class RunPoxyServer implements Runnable {

		public void run() {
			try {
				runServer("localhost", 8080, 9999);
			} catch (IOException e) {
				e.printStackTrace();
				System.out
						.println("Unable to Start proxy, test run unreliable");
			}
		}

	}

	/**
	 * runs a single-threaded proxy server on the specified local port. It never
	 * returns.
	 */
	public static void runServer(String host, int remoteport, int localport)
			throws IOException {
		// Create a ServerSocket to listen for connections with
		ServerSocket ss = new ServerSocket(localport);

		final byte[] request = new byte[1024];
		byte[] reply = new byte[4096];
		int dropCount = 0;

		while (!s_stop) {
			Socket client = null, server = null;
			try {
				// Wait for a connection on the local port
				client = ss.accept();

				System.out.println("Proxy Engaged ....");

				final InputStream streamFromClient = client.getInputStream();
				final OutputStream streamToClient = client.getOutputStream();

				// Make a connection to the real server.
				// If we cannot connect to the server, send an error to the
				// client, disconnect, and continue waiting for connections.
				try {
					server = new Socket(host, remoteport);
				} catch (IOException e) {
					PrintWriter out = new PrintWriter(streamToClient);
					out.print("Proxy server cannot connect to " + host + ":"
							+ remoteport + ":\n" + e + "\n");
					out.flush();
					client.close();
					continue;
				}

				// Get server streams.
				final InputStream streamFromServer = server.getInputStream();
				final OutputStream streamToServer = server.getOutputStream();

				// a thread to read the client's requests and pass them
				// to the server. A separate thread for asynchronous.
				Thread t = new Thread() {
					public void run() {
						int bytesRead;
						try {
							while ((bytesRead = streamFromClient.read(request)) != -1) {
								streamToServer.write(request, 0, bytesRead);
								streamToServer.flush();
							}
						} catch (IOException e) {
						}

						// the client closed the connection to us, so close our
						// connection to the server.
						try {
							streamToServer.close();
						} catch (IOException e) {
						}
					}
				};

				// Start the client-to-server request thread running
				t.start();

				// Read the server's responses
				// and pass them back to the client.
				int bytesRead;
				try {
					boolean inTheLoop = false;
					while ((bytesRead = streamFromServer.read(reply)) != -1) {
						if (!inTheLoop) {
							inTheLoop = true;
							if (dropCount < 2) {
								streamToClient.close();
								dropCount++;
							} else {
								dropCount = 0;
							}
						}

						streamToClient.write(reply, 0, bytesRead);
						streamToClient.flush();
					}
				} catch (IOException e) {
				}

				// The server closed its connection to us, so we close our
				// connection to our client.
				streamToClient.close();
			} catch (IOException e) {
				System.err.println(e);
			} finally {
				try {
					if (server != null)
						server.close();
					if (client != null)
						client.close();
				} catch (IOException e) {
				}
			}
		}
		try {
			ss.close();
		} catch (IOException e) {
		}

	}
	@Test
	@SuppressWarnings("unchecked")
	public void testDispatchRemoteSyncWithSyncAsyncTransport() throws Exception {
		Service service = createProxiedService("Test1Service", "flakyWithHttpSyncAsyncTransport");
		String outMessage = (String) service.createDispatch("echoString")
				.invoke(ECHO_STRING);
		Assert.assertEquals(ECHO_STRING, outMessage);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testDispatchRemoteWithSyncTransport() throws Exception {
		Service service = createProxiedService("Test1Service", "flakyWithHttpSyncTransport");
		String outMessage = (String) service.createDispatch("echoString")
				.invoke(ECHO_STRING);
		Assert.assertEquals(ECHO_STRING, outMessage);
	}
}

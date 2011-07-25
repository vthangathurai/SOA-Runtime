package org.ebayopensource.turmeric.runtime.tests.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import javax.activation.DataHandler;

import com.ebay.kernel.util.FileUtils;

public class QEFileUtils {

	public static void createFileForTest(Integer size, File file) throws IOException {
		RandomAccessFile out = new RandomAccessFile(file, "rw");
		FileChannel fc = out.getChannel();
		long length = size;
		MappedByteBuffer MappByteBuff = fc.map(FileChannel.MapMode.READ_WRITE, 0, length);
		for (int i = 0; i < length; i++)
		{
			MappByteBuff.put((byte) 'V');
		}
		fc.close();
		System.out.println("File successFully written");
	}

	public static boolean writeData(DataHandler dh, long size, FileOutputStream out) throws IOException{
		byte[] dataBuf = new byte[4096];
		long sizeCounter = 0;
		//		FileOutputStream out = null;
		BufferedInputStream br = null;
		InputStream in = dh.getInputStream();
		br = new BufferedInputStream(in);
		try {
			int numBytes;
			while ((numBytes = br.read(dataBuf)) != -1) {
				sizeCounter += numBytes;
				if (sizeCounter <= size) {
					out.write(dataBuf, 0, numBytes);
				}
			}
		} finally {
			br.close();
			out.close();
		}
		return true;
	}


	public static void create3GBFile(File f1, File f2) throws Exception {
		String threeMegaJunk = FileUtils.readFile(f1.getAbsolutePath(), "UTF-8");
		FileOutputStream fos = new FileOutputStream(f2);
		try {
			for (int i = 0; i < 1024; i++) {
				byte[] threeMegaBytes = threeMegaJunk.getBytes("UTF-8");
				fos.write(threeMegaBytes);
			}
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				new RuntimeException("Couldn't close output stream " + f2.getAbsolutePath(), e).printStackTrace();
			}
		}
	}
	public static FileOutputStream writeData(InputStream in, int size, 
			long attachmentSize, String fileName) throws IOException {
		FileOutputStream out = new FileOutputStream(new File(fileName));
		BufferedInputStream br = new BufferedInputStream(in);
		long sizeCounter = 0;
		byte[] dataBuf = new byte[size];
		try {
			int numBytes;
			while ((numBytes = br.read(dataBuf)) != -1) {
				sizeCounter += numBytes;
				if (sizeCounter <= attachmentSize) {
					out.write(dataBuf, 0, numBytes);
				}
			}
		} finally {
			try {
				br.close();
				out.close();
			} catch (IOException e) {
				System.err.println("Could not close one of the streams");
				e.printStackTrace();
			}
		}
		return out;
	}
	public static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }

	    // The directory is now empty so delete it
	    return dir.delete();
	}

}

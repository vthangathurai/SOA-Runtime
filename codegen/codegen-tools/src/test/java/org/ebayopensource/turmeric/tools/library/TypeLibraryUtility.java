package org.ebayopensource.turmeric.tools.library;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;

import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenClassLoader;

public class TypeLibraryUtility  extends AbstractServiceGeneratorTestCase{

	File prCategoryRoot = null;
	File prProductRoot = null;	
	private String GOLD_COPY_ROOT = "./src/test/resources/TypeLibraryCodegen";
	private String PROJECT_ROOT_CATEGORY = testingdir.getFile("CategoryTypeLibrary").getAbsolutePath();
	private String PROJECT_ROOT_PRODUCT = testingdir.getFile("ProductTypeLibrary").getAbsolutePath();


	

	/**
	 * Specify the project root to be deleted.
	 * @param projectRoot
	 * @return
	 */
	public boolean deleteTypeLibrary(String projectRoot){
		File projectDir = new File(projectRoot);
		if (projectDir.isDirectory()) {
	            String[] childFiles = projectDir.list();
	            for(int i=0; i<childFiles.length ; i++){
	            	deleteTypeLibrary(projectRoot+"/"+childFiles[i]);
	            }
		 }
		boolean flag = projectDir.delete();
		return flag;
	}

	/**
	 * Additional path is name of the folder under Vanilla copy specific for the testcase.
	 * @param projectRoot
	 * @param libraryName
	 * @param aditionalPath
	 * @return
	 */
	public String getTypeInformationXMLPath(String projectRoot, String libraryName, String aditionalPath){
		String path = null;
		if(aditionalPath != null && aditionalPath.trim().length() != 0){
			path = projectRoot+File.separator +"gen-meta-src"+File.separator+"META-INF"+File.separator+libraryName+File.separator+aditionalPath+File.separator + "TypeInformation.xml";
		}else{
			path = projectRoot+File.separator +"gen-meta-src"+File.separator + "META-INF"+File.separator+libraryName+File.separator+"TypeInformation.xml";
		}
		return path;
	}

	/**
	 * Updates the TypeInformation.xml from Vanilla copy to Codegen Copy.
	 * @param xmlPath
	 * @return
	 */
	public boolean updateSourceFile(String fromFilePath, String toFilePath){
		boolean flag = false;
		File fromFile = new File(fromFilePath);
		File toFile = new File(toFilePath);
		try {
			InputStream in = new FileInputStream(fromFile);
			OutputStream out = new FileOutputStream(toFile);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			flag = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			fromFile = null;
			toFile = null;
			System.gc();
		}

		return flag;
	}


	/**
	 * Specify the complete path for the Codegen copy and Vanila copy.
	 * @param codegenPath
	 * @param goldCopyPath
	 * @return
	 */
	public boolean compareFiles(String codegenPath, String goldCopyPath)throws Exception{
		boolean compareEqual = false;
		String codegenCopy = getFileContent(codegenPath);
		String vanillaCopy = getFileContent(goldCopyPath);

		boolean javaCheck = codegenCopy.contains("package");
		if(javaCheck){
			codegenCopy = "package"+codegenCopy.split("package")[1];
			vanillaCopy = "package"+vanillaCopy.split("package")[1];
		}


		codegenCopy = removeTimeStamp(codegenCopy);
		vanillaCopy = removeTimeStamp(vanillaCopy);

		if(codegenCopy.equals(vanillaCopy)){
			compareEqual = true;
		}
		if(codegenCopy.trim().length() == 0 || vanillaCopy.trim().length() == 0){
			compareEqual = false;
		}
		return compareEqual;
	}

	private String getFileContent(String filePath) throws Exception{
		File actualFile = new File(filePath);
		BufferedReader input;
		String fileContent = "";

		input = new BufferedReader(new FileReader(actualFile));
		String line = null;
		StringBuilder contents = new StringBuilder();
		while ((line = input.readLine()) != null) {
			contents.append(line);
			contents.append(System.getProperty("line.separator"));
		}
		input = null;
		actualFile = null;
		System.gc();
		fileContent = new String(contents);

		return fileContent;
	}


	private String removeTimeStamp(String fileContent){
		// <xml> <!-- test1  -->
		//    <xml1>
		//     <!-- test2 -->
		String content = "";
		String[] iterOne = fileContent.split("<!--");
		for(int i=0; i<iterOne.length; i++){
			if(i == 0){
				content = content+iterOne[i];
				content = content.trim();
			}else{
				String[] iterTwo = iterOne[i].split("-->");
				for(int j=0; j<iterTwo.length; j++){
					if(j%2 == 1){
						content = content+iterTwo[j];
						content = content.trim();
					}
				}
			}

		}

		return content.trim();
	}


	public boolean copyXSDFileToTypesFolder(String libraryName, String xsdName) {
		File libfolder = new File (getTestDestDir().getAbsolutePath()+ File.separator + libraryName+"\\meta-src\\types\\"+libraryName);
		libfolder.mkdirs();
		String toXsdFilePath = libfolder.toString()+"\\" + xsdName;
		String fromXsdFilePath = GOLD_COPY_ROOT + "\\" + libraryName
				+ "\\meta-src\\types\\" + xsdName;
		File toXsdFile = new File(toXsdFilePath);
		if(!toXsdFile.exists()){
			try {
				toXsdFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		File fromXsdFile = new File(fromXsdFilePath);
		
		fromXsdFile.getAbsolutePath();
		if(!fromXsdFile.exists()){
			try {
				fromXsdFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		boolean flag = false;
		try {
			InputStream in = new FileInputStream(fromXsdFile);
			OutputStream out = new FileOutputStream(toXsdFile);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			flag = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			toXsdFile = null;
			fromXsdFile = null;
			System.gc();
		}

		return flag;
	}

	//method added for temporary testing by Nitin
	
	
	public boolean copyXSDFileToTypesFolder1(String fromLibraryName, String toLibraryName, String xsdName) {
		
		String toXsdFilePath = "AntTests\\" + toLibraryName+"\\meta-src\\types\\" + xsdName;
		String fromXsdFilePath = GOLD_COPY_ROOT + "\\" + fromLibraryName
				+ "\\meta-src\\types\\" + xsdName;
		File toXsdFile = new File(toXsdFilePath);
		if(!toXsdFile.exists()){
			try {
				toXsdFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		File fromXsdFile = new File(fromXsdFilePath);
		if(!fromXsdFile.exists()){
			try {
				fromXsdFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		boolean flag = false;
		try {
			InputStream in = new FileInputStream(fromXsdFile);
			OutputStream out = new FileOutputStream(toXsdFile);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			flag = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			toXsdFile = null;
			fromXsdFile = null;
			System.gc();
		}

		return flag;
	}	

	
	public String getGeneratedJavaFilePath(String projectRoot, String libraryName, String javaClassName, String aditionalPath){
		String path = null;
		if(aditionalPath != null && aditionalPath.trim().length() != 0){
			path = projectRoot+"/gen-src/org/ebayopensource/soaframework/examples/config/"+aditionalPath+"/"+javaClassName;
		}else{
			path = projectRoot+"/gen-src/org/ebayopensource/soaframework/examples/config/"+javaClassName;
		}
		return path;
	}

	public String getEpisodeFilePath(String projectRoot, String libraryName, String episodeFileName, String extraParam){
		String path = null;
		if(extraParam != null && extraParam.trim().length() != 0){
			path = projectRoot+"/gen-meta-src/META-INF/"+libraryName+"/"+extraParam+"/"+episodeFileName;
		}else{
			path = projectRoot+"/gen-meta-src/META-INF/"+libraryName+"/"+episodeFileName;
		}

		return path;
	}

	public String getXsdFilePath(String projectRoot, String xsdFileName){
		String path = projectRoot+"/meta-src/types/"+xsdFileName;
		return path;
	}
	public String getXsdFilePath1(String projectRoot,String libName, String xsdFileName){
		String path = projectRoot+"/meta-src/types/"+libName+"/" +xsdFileName;
		return path;
	}

	public long getFileCreationTime(String filePath){
		File file = new File(filePath);
		long time = 0;
		if(file.exists()){
			time = file.lastModified();
		}
		file = null;
		System.gc();
		return time;
	}

	public boolean waitForExecution(int time)throws Exception{
		System.out.println("In Time "+System.currentTimeMillis());
		Thread.currentThread().sleep(time * 1000);
		System.out.println("Out Time "+System.currentTimeMillis());
		return true;
	}

	public boolean checkFileExistance(String filePath){
		File file = new File(filePath);
		boolean flag = file.exists();
		file = null;
		System.gc();
		return flag;
	}

	public boolean setClassPath(String PROJECT_ROOT){
		
		

		String gen_meta_src_ProductTypeLibrary = PROJECT_ROOT +"/ProductTypeLibrary/gen-meta-src/";
		String gen_src_ProductTypeLibrary = PROJECT_ROOT +"/ProductTypeLibrary/gen-src/";
		String meta_src_ProductTypeLibrary = PROJECT_ROOT +"/ProductTypeLibrary/meta-src/";

		String gen_meta_src_CategoryTypeLibrary = PROJECT_ROOT +"/CategoryTypeLibrary/gen-meta-src/";
		String gen_src_CategoryTypeLibrary= PROJECT_ROOT +"/CategoryTypeLibrary/gen-src/";
		String meta_src_CategoryTypeLibrary = PROJECT_ROOT +"/CategoryTypeLibrary/meta-src/";

		String gen_meta_src_TestLibrary = PROJECT_ROOT  +"/Test/gen-meta-src/";
		String gen_src_TestLibrary=  PROJECT_ROOT  +"/Test/gen-src/";
		String meta_src_TestLibrary =  PROJECT_ROOT  +"/Test/meta-src/";

		String gen_meta_src_LibraryTest = PROJECT_ROOT  +"/LibraryTest/gen-meta-src/";
		String gen_src_LibraryTest= PROJECT_ROOT  +"/LibraryTest/gen-src/";
		String meta_src_LibraryTest = PROJECT_ROOT  +"/LibraryTest/meta-src/";

		File file_gen_meta_src_ProductTypeLibrary = new File(gen_meta_src_ProductTypeLibrary);
		File file_gen_src_ProductTypeLibrary = new File(gen_src_ProductTypeLibrary);
		File file_meta_src_ProductTypeLibrary = new File(meta_src_ProductTypeLibrary);

		File file_gen_meta_src_CategoryTypeLibrary = new File(gen_meta_src_CategoryTypeLibrary);
		File file_gen_src_CategoryTypeLibrary = new File(gen_src_CategoryTypeLibrary);
		File file_meta_src_CategoryTypeLibrary = new File(meta_src_CategoryTypeLibrary);

		File file_gen_meta_src_TestLibrary = new File(gen_meta_src_TestLibrary);
		File file_gen_src_TestLibrary = new File(gen_src_TestLibrary);
		File file_meta_src_TestLibrary = new File(meta_src_TestLibrary);

		File file_gen_meta_src_LibraryTest = new File(gen_meta_src_LibraryTest);
		File file_gen_src_LibraryTest = new File(gen_src_LibraryTest);
		File file_meta_src_LibraryTest = new File(meta_src_LibraryTest);


		try {
			URL[] urls = { file_gen_meta_src_ProductTypeLibrary.toURI().toURL(),
					file_gen_src_ProductTypeLibrary.toURI().toURL(),
					file_meta_src_ProductTypeLibrary.toURI().toURL(),
					file_gen_meta_src_CategoryTypeLibrary.toURI().toURL(),
					file_gen_src_CategoryTypeLibrary.toURI().toURL(),
					file_meta_src_CategoryTypeLibrary.toURI().toURL(),
					file_gen_meta_src_TestLibrary.toURI().toURL(),
					file_gen_src_TestLibrary.toURI().toURL(),
					file_meta_src_TestLibrary.toURI().toURL(),
					file_gen_meta_src_LibraryTest.toURI().toURL(),
					file_gen_src_LibraryTest.toURI().toURL(),
					file_meta_src_LibraryTest.toURI().toURL()
			};
			URLClassLoader urlClassLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
			Thread.currentThread().setContextClassLoader(urlClassLoader);
			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return true;
	}

	/*public String getGeneratedJavaFilePath1(String projectRoot, String javaClassName, String aditionalPath) {
			String path = null;
			if(aditionalPath != null && aditionalPath.trim().length() != 0){
				path = projectRoot+"\\gen-src\\com\\ebay\\marketplace\\services\\"+aditionalPath+"\\"+javaClassName;
			}else{
				path = projectRoot+"\\gen-src\\com\\ebay\\marketplace\\services\\"+javaClassName;
			}
			return path;
	}*/

	public static void main(String[] args){
		TypeLibraryUtility utility = new TypeLibraryUtility();
		try{
			utility.waitForExecution(20);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
		
	

}

package org.ebayopensource.turmeric.tools.codegen.proto;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.ebayopensource.turmeric.tools.codegen.CodeGenContext;
import org.ebayopensource.turmeric.tools.codegen.ServiceGenerator;
import org.ebayopensource.turmeric.tools.codegen.builders.BaseCodeGenerator;
import org.ebayopensource.turmeric.tools.codegen.exception.CodeGenFailedException;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;

public class BaseEprotoGeneratorTest {


	protected void compileGeneratedEProtos(CodeGenContext context, String eprotoName) throws Exception{
		BaseCodeGenerator.compileJavaFile(CodeGenUtil.toJavaSrcFilePath(
				context.getJavaSrcDestLocation(), eprotoName), context.getBinLocation());
	}
	
	protected void compileMultipleGeneratedEProtos(CodeGenContext context) throws Exception{
		List<String> files = new ArrayList<String>();
		CodeGenUtil.addAllFiles(new File(context.getJavaSrcDestLocation()), files);
		for (String fileName : files) {

			BaseCodeGenerator.compileJavaFile(fileName, context.getBinLocation());
		}
	}

	protected CodeGenContext getCodeGenContext(String[] args) throws Exception{
		
		getToolsJar(this.getClass().getClassLoader());
		return ProtobufSchemaMapperTestUtils.getCodeGenContext(args);
	}

	/**
	 * Returns a class loader that can load classes from JDK tools.jar.
	 * 
	 * @param parentClassLoader
	 */
	protected static void getToolsJar(ClassLoader parentClassLoader)
			throws CodeGenFailedException {

		try {
			Class.forName("com.sun.tools.javac.Main", false, parentClassLoader);
			Class.forName("com.sun.tools.apt.Main", false, parentClassLoader);
			return;
		} catch (ClassNotFoundException e) {
			
		}

		File toolsJar = null;
		boolean toolsJarFound = false;
		String jdkHome = null;
		if (!CodeGenUtil.isEmptyString(ServiceGenerator.s_JdkHome)) {
			jdkHome = ServiceGenerator.s_JdkHome;
		} else
			jdkHome = System.getenv("JDK_HOME");
		
		
		if (jdkHome != null) {
			toolsJar = new File(jdkHome, "lib/tools.jar");
			if (toolsJar.exists())
				toolsJarFound = true;
		}

		if (toolsJarFound == false) {
			String javaHomeStr = System.getProperty("java.home");

			File jreHome = new File(javaHomeStr);
			toolsJar = new File(jreHome.getParent(), "lib/tools.jar");

			if (!toolsJar.exists()) {
				if (javaHomeStr.indexOf("jre") > 0
						|| javaHomeStr.indexOf("JRE") > 0) {
					if (javaHomeStr.endsWith("/")) {
						javaHomeStr = javaHomeStr + "../";
					} else {
						javaHomeStr = javaHomeStr + "/../";
					}
					jreHome = new File(javaHomeStr);
					toolsJar = new File(jreHome.getParent(), "lib/tools.jar");
				}

				if (!toolsJar.exists()) {
					String exceptionMsg = "JdkHome used for loading tools.jar is:"+ jdkHome 
					 + " \n JavaHome used is : "+ javaHomeStr;
					throw new CodeGenFailedException(exceptionMsg + " Failed to load tools.jar with these values.");
				}
			}
		}

		try {
			addURL(toolsJar.toURL());
		} catch (Exception e) {}
	}


	   protected static void addURL(URL u) throws IOException {
		  Class[] parameters = new Class[]{URL.class};

	      URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
	      Class sysclass = URLClassLoader.class;

	      try {
	         Method method = sysclass.getDeclaredMethod("addURL", parameters);
	         method.setAccessible(true);
	         method.invoke(sysloader, new Object[]{u});
	      } catch (Throwable t) {
	         t.printStackTrace();
	         throw new IOException("Error, could not add URL to system classloader");
	      }

	   }
	

}

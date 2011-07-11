package org.ebayopensource.turmeric.tools.codegen.protobuf;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.ebayopensource.turmeric.tools.codegen.ServiceGenerator;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.junit.Assert;
import org.junit.Test;

public class SequenceNumberConsistencyCheck extends AbstractServiceGeneratorTestCase {
	
  
    public static String getPackageFromNamespace(String namespace) {
    	
    	//Using the method used by JAXB directly to avoid potential conflicts with JAXB generated code
    	//Therefore commenting out the old code which is based on JAXB 2.0 spec
    	return com.sun.tools.xjc.api.XJC.getDefaultPackageName(namespace);
    }
	@Test
	public void testEproto() throws Exception{
	
		
		String serviceName = "CalculatorService";
		File resource = new File("wsdlorxsd/fieldInformation");
		File bin = new File("generated/bin/");
		File proto = new File("generated/");
		File gensrc = new File("generated/gen-src/");
		
		
		URL [] urls = {bin.toURI().toURL(),proto.toURI().toURL(),gensrc.toURI().toURL()};
		URLClassLoader loader = new URLClassLoader(urls,Thread.currentThread().getContextClassLoader());
		Thread.currentThread().setContextClassLoader(loader);
		File srcdir = new File("JUnitTests/src/proto/files");
		String destDir = new File("generated").getAbsolutePath();
		File wsdlpath = new File("wsdlorxsd/TestWsdlXsdTypes.wsdl");
		
		//compileJProto(srcdir.getAbsolutePath(),destDir,srcdir.getAbsolutePath()+"/ExtendComplexType.proto");
		String name = "CalculatorService";
		String wsdlNSToPkg = getPackageFromNamespace("http://codegen.tools.soaframework.test.ebay.com");
		//String wsdlNSToPkg = "com.ebay.marketplace.search.v1.services";
		wsdlNSToPkg = getPackageFromNamespace("http://codegen.tools.soaframework.test.ebay.com");
		//String relativePath ="generated/"+ packagetoDirPath(wsdlNSToPkg)+"/proto/" +name+".java";
		
		//compile(relativePath);
		File file = new File("generated/gen-meta-src/META-INF/soa/services/proto/CalculatorService/CalculatorService.proto");
		
		CodeGenUtil.deleteContentsOfDir(new File(file.getParent()));
		generateJaxbClasses(wsdlpath.getAbsolutePath(), destDir);
		Class<?> protoClass = Thread.currentThread().getContextClassLoader().loadClass(wsdlNSToPkg +".proto." +serviceName);
		ProtoFileParser parser = new ProtoFileParser(file);
		List<Message> msg = parser.parse();
	
	
		
		
		

		List<String> listofMessageName = new ArrayList<String>();
		Map<String,List<String>> msgMap2 = null;
		for(int i=0; i < 5;i++) { 
		
		
						 parser = new ProtoFileParser(file);
						 msg = parser.parse();
					
					
					
					Map<String,List<String>> msgMap1 = getMessageInfo(msg);
					CodeGenUtil.deleteContentsOfDir(new File(file.getParent()));
					generateJaxbClasses(wsdlpath.getAbsolutePath(), destDir);
					
					
					ProtoFileParser parser1 = new ProtoFileParser(file);
					List<Message> msg2 = parser1.parse();
					
					msgMap2 = getMessageInfo(msg);
					
					
					for(Message m1 :msg2) {
						listofMessageName.add(m1.getMessageName());
					List<String> fieldsInfo1 = msgMap1.get(m1.getMessageName());
					List<String> fieldsInfo2 = msgMap2.get(m1.getMessageName());
					
					Assert.assertTrue(fieldsInfo1.containsAll(fieldsInfo2));
					}
					//check pmd info is consistent:
					List<String> list1 = FileUtil.readFileAsLines(file);
					List<String> onlyPMD = new ArrayList<String>();
					for(String s : list1){
						if(s.trim().startsWith("//PMD")){
							onlyPMD.add(s);
						}
					}
					List<PMDInfo> list = parser.parsePMDData(onlyPMD);

					Map<String,List<String>> msgMap3 = new HashMap<String, List<String>>();
					for(String msgName:listofMessageName){
						List<String> pmd = new ArrayList<String>();
						for(PMDInfo info : list ){
							if(info.getMessageName().trim().equals(msgName.trim())){
								
								pmd.add(info.getFieldName()+info.getSequenceNumber());
							}
							
						}
						msgMap3.put(msgName,pmd);
					}
					
					for(String msgName : listofMessageName) {
					List<String> fieldsInfo2 = msgMap2.get(msgName);
					List<String> fieldsInfo3 = msgMap3.get(msgName);
					Assert.assertTrue(fieldsInfo2.containsAll(fieldsInfo3));
					}
		}
		
		
		
		
			
	
		
		/*MyComplexType complextype = (MyComplexType) jaxbClasses.get("MyComplexType").newInstance();
		complextype.setElemA(344.55f);
		SampleComplexType sample = (SampleComplexType) jaxbClasses.get("SampleComplexType").newInstance();
		sample.getEnt().add(23);
		sample.setValue1("test1");
		sample.setValue10("test10");
		sample.setValue2("test2");
		sample.setValue4("test3");
		sample.setValue8("test8");
		sample.setValue9("test9");
		complextype.setElemB(sample);
		ExtendMyComplexType extended = (ExtendMyComplexType) jaxbClasses.get("ExtendMyComplexType").newInstance();
		extended.setElemB(sample);
		extended.setElemA(344.33f);
		extended.setElemC("test");
		
		
	    invokeNewInstance(jaxbClasses.get("ExtendMyComplexType"),eprotoClasses.get("ExtendMyComplexType"), protoClass,"ExtendMyComplexType",extended);
  */		
		 
	}
	
	public Map<String,List<String>> getMessageInfo(List<Message> msg){
		Map<String,List<String>> msgFields = new HashMap<String,List<String>>();
		
		
		for(Message m :msg){
			String msName = m.getMessageName();
			List<String> list = new ArrayList<String>();
			List<Field> fields = m.getFields();
			for(Field f :fields){
				String fsn = f.getFieldName().trim()+f.getSequenceNumber().trim();
				list.add(fsn);
			}
			msgFields.put(msName,list);
		}
		return msgFields;
		
	}
	
	public Object makeProtoObject(List<MessageInformation> msgList,MessageInformation ms,String wsdlNSToPkg) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		
		Class<?> protoClass = Thread.currentThread().getContextClassLoader().loadClass(wsdlNSToPkg +".proto.CalculatorService");
		Object builderObj = null;

		Object builtObj = null;
		Class<?> [] innerClasses = protoClass.getDeclaredClasses();
		
		builderObj = createBuilder(ms, innerClasses, builderObj);
		Object descriptorObj = null;
		Class<?> bc = builderObj.getClass();
		Method [] mthd = bc.getDeclaredMethods();
		for(ElementInformation el :ms.getElementInfo()){
			
			/*Class<?> bc = builderObj.getClass();
			Method [] mthd = bc.getDeclaredMethods();
				for(Method m: mthd){
					if(m.getName().equals("getDescriptorForType")){
						Object builder  = m.invoke(builderObj);
						
								Class<?> bu = builder.getClass();
								Method [] buMthd = bu.getDeclaredMethods();
								
								 for(Method m1 : buMthd){
									 
									 if(m1.getName().equals("findFieldByName")){
										 
										 descriptorObj =  m1.invoke(builder,el.getElementName());
										 break;
									 }
								 }
								 break;
					}
					
					
				}*/
				settingFieldValue(msgList,ms,builderObj, el,wsdlNSToPkg);
				
				
				
			
		}
		for(Method m: mthd){
			if(m.getName().equals("build")){
				builtObj = m.invoke(builderObj);
				System.out.println("");
				break;
				
			}
		}
		return builtObj;
	
	}
	
	public void settingFieldValue(List<MessageInformation> msgList,MessageInformation ms,Object builderObj,ElementInformation el,String wsdlNSToPkg) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException{
		String methodPrefix ="set";
		if(el.isList()){
			methodPrefix ="add";
	 	}
		Method [] mtds =  builderObj.getClass().getDeclaredMethods();
		for(Method m : mtds){
			
			
			if(m.getName().equals(methodPrefix+ firstLetterUpperCase(el.getElementName()))){
				
				 if(el.getDataType().equals("string")){
					 
						
						m.invoke(builderObj,"test");
						break;
				 }else if(el.getDataType().equals("int")){
					 	
						m.invoke(builderObj,123);
						break;
				 }else if(el.getDataType().equals("float")){
						
						m.invoke(builderObj,12.4f);
						break;
				 }
				 else {
						for(MessageInformation mi :msgList){
							if(mi.getMessageName().equals(el.getDataType())){
								m.invoke(makeProtoObject(msgList,mi,wsdlNSToPkg));
								break;
							}
						}
						
						break;
				 }
			
			}
		}
	}
	
	public Object createBuilder(MessageInformation ms,Class<?> [] innerClasses,Object builderObj) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		
		for(Class<?> cls : innerClasses){
			if(cls.getName().contains(ms.getMessageName()+"OrBuilder")){
				continue;
			}
			if(cls.getName().contains(ms.getMessageName())){
				
				
							Method [] mths = cls.getDeclaredMethods();
							
							
								for(Method m : mths){
									System.out.println(m.getName());
									if(m.getName().equals("newBuilder")){
										
										builderObj  =	m.invoke(null);
										break;
									}
								}
						
					
				
			}
			
		}
		
		return builderObj;
	}
	
	public Object makeObject(MessageInformation ms,List<MessageInformation> listMsg,String wsdlNSToPkg) throws ClassNotFoundException, IllegalAccessException, InstantiationException{
		
		Class<?> jaxbClass = Thread.currentThread().getContextClassLoader().loadClass(wsdlNSToPkg +"." +ms.getMessageName());
		Object obj = jaxbClass.newInstance();
		for(ElementInformation el : ms.getElementInfo()){
			
			try {
				invokeMtd(ms,listMsg,el,obj,wsdlNSToPkg);
				
				
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return obj;
	}
	
	public String firstLetterUpperCase(String word){
		char [] ch = word.toCharArray();
		String str = Character.toString(ch[0]).toUpperCase() + word.substring(1);
		return str;
	}
	public void invokeMtd(MessageInformation ms,List<MessageInformation> listMsg,ElementInformation el,Object jaxbObj,String wsdlNSToPkg) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, InstantiationException{
		 Method []  mths = jaxbObj.getClass().getMethods();
		 String dataType = el.getDataType();
		 for(Method m : mths){
			 if(!dataType.equals("boolean")){
				 
				 if(("set" + firstLetterUpperCase(el.getJaxbName())).equals(m.getName())){
					 if(dataType.equals("string")){
					 m.invoke(jaxbObj,"test");
					 }
					 else if(dataType.equals("int")){
						 m.invoke(jaxbObj,12);
					 }
					 else if(dataType.equals("integer")){
						 m.invoke(jaxbObj,100);
					 }
					 else if(dataType.equals("float")){
							 m.invoke(jaxbObj,100.00f);
					 }else{
						 for(MessageInformation info : listMsg){
							 if(info.getMessageName().equals(dataType)){
								 m.invoke(jaxbObj,makeObject(info,listMsg,wsdlNSToPkg));
							 }
							 
						 }
						 
					 }
				 }
			 }
		 }
		
	}
	
	public void loadEproto(List<URI> paths){
		
	}
	
	public void compile(List<URI> sources){
		List<SimpleJavaFileObject> jfoList = new ArrayList<SimpleJavaFileObject>();
		for(URI uri : sources){
			SimpleJavaFileObject jfo = new ExtSimpleFileObject(uri,JavaFileObject.Kind.SOURCE);
			jfoList.add(jfo);
			}
		List<String> optionList = new ArrayList<String>();
		// set compiler's classpath to be same as the runtime's
		optionList.addAll(Arrays.asList("-classpath",System.getProperty("java.class.path")));
	
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		PrintWriter out = new PrintWriter(System.out);
		JavaCompiler.CompilationTask task = compiler.getTask(out,null,null,optionList,null,jfoList);
		task.call();
		
		
		
		
	}
	
	class ExtSimpleFileObject extends SimpleJavaFileObject{
		
		public ExtSimpleFileObject(URI uri,JavaFileObject.Kind knd) {
			super(uri,knd);
			
		}
	}
	
	public void generateJaxbClasses(String path,String destDir) throws Exception{
		String [] testArgs = {"-serviceName","CalculatorService",
				  "-genType","ServiceFromWSDLIntf",	
				  "-wsdl",path,
				  "-gip","com.ebay.test.soaframework.tools.codegen",
				  "-dest",destDir,
				  "-src",destDir,
				  "-bin",destDir+"/bin",
				  "-slayer","INTERMEDIATE",
				  "-fastserformat","protobuf",
				  "-enabledNamespaceFolding",
				  "-scv","1.0.0",
				  "-pr",destDir};
		
		ServiceGenerator sgen = new ServiceGenerator();
		sgen.startCodeGen(testArgs);
		
	}
	
	public List<String> getTypeInformation(File fieldInfo){
		List<String> info = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			FileReader in = new FileReader(fieldInfo);
			reader = new BufferedReader(in);
			String line = reader.readLine();
			while(line != null ){
				info.add(line);
				line = reader.readLine();
			}
			
		} catch (FileNotFoundException e) {
		
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}finally{
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return info;
		
	}
	
	public String invokeNewInstance(Class<?> jaxbClass,Class<?> eprotoClass,Class<?> protoClass,String typeName,Object jaxbObject){
		
		Class<?>	classForType = null;
		Object obj = null;
		try {
					Method method =eprotoClass.getMethod("newInstance",jaxbClass);
					Class<?> innerClasses [] =  protoClass.getDeclaredClasses();
					Constructor<?> []  constructor =  eprotoClass.getDeclaredConstructors();
					for(Class<?> cl : innerClasses){
						if(!cl.getName().contains(typeName + "OrBuilder"))
						if(cl.getName().contains(typeName)){
							classForType = cl;
						}
					
					}
					
					Constructor<?> [] constr = eprotoClass.getDeclaredConstructors();
					Constructor<?> builderCon = null;
					
					//FitmentFieldValue$Builder
					for(Constructor<?> ct : constr){
						if(ct.getParameterTypes().length == 1){
							
								builderCon = ct;
								ct.setAccessible(true);
						}	
					
					}
					
					Method m =  classForType.getMethod("newBuilder");
					Object ob = m.invoke(null);
				    
					Class<?> builders = ob.getClass();
					
					
					Method buildMethod = builders.getMethod("build");
					Constructor<?> [] builder = builders.getDeclaredConstructors();
					Constructor<?> cons = null;
					for(Constructor<?> ct : builder){
					 
						if(ct.getParameterTypes().length == 0){
							cons = ct;
							ct.setAccessible(true);
						}
						
					}
					
					Object builtObj = buildMethod.invoke(cons.newInstance());
					
					
					 Object eprotoObj = builderCon.newInstance(builtObj);
					
					  
			
					 obj  = method.invoke(eprotoObj,jaxbObject);
					 

	
		
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj.getClass().getName(); 

	}
	
	
public String invokeGetters(Class<?> jaxbClass,Class<?> eprotoClass,Class<?> protoClass,String methodName,String typeName,Object builder){
		
		Class<?>	classForType = null;
		Object obj = null;
		try {
					Method method =eprotoClass.getMethod(methodName);
					Class<?> innerClasses [] =  protoClass.getDeclaredClasses();
					Constructor<?> []  constructor =  eprotoClass.getDeclaredConstructors();
					for(Class<?> cl : innerClasses){
						if(!cl.getName().contains(typeName + "OrBuilder"))
						if(cl.getName().contains(typeName)){
							classForType = cl;
						}
					
					}
					
					Constructor<?> [] constr = eprotoClass.getDeclaredConstructors();
					Constructor<?> builderCon = null;
					
					//FitmentFieldValue$Builder
					for(Constructor<?> ct : constr){
						if(ct.getParameterTypes().length == 1){
							
								builderCon = ct;
								ct.setAccessible(true);
						}	
					
					}
					
					
					
					 Object eprotoObj = builderCon.newInstance(builder);
					
					  
			
					 obj  = method.invoke(eprotoObj);
					 

	
		
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj.getClass().getName(); 

	}
	
	
	
	public List<String> assertReturnTypes(Class<?> eprotoClass){
		List<String> returntype = new ArrayList<String>();
		
		
		Method[] methods = eprotoClass.getDeclaredMethods();
		for(Method m : methods){
			returntype.add(m.getReturnType().getName());
		}
		
		return returntype;
		
	}
	
	public void compile(String relativePath){
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		int result = compiler.run(null, null, null,relativePath);

		System.out.println("Compile result code = " + result);
	}
	
	
	public String packagetoDirPath(String pkg){
		
		return pkg.replace(".","/");
		
	}
	
	public void compileJProto(String src_dir,String dst_dir,String protoloc){
		String s = null;
		try {
            
		    // run the Unix "ps -ef" command
	            // using the Runtime exec method:
	            Process p = Runtime.getRuntime().exec("protoc.exe -I="+src_dir+" --java_out="+dst_dir+" " + protoloc);
	            
	            BufferedReader stdInput = new BufferedReader(new 
	                 InputStreamReader(p.getInputStream()));

	            BufferedReader stdError = new BufferedReader(new 
	                 InputStreamReader(p.getErrorStream()));

	            // read the output from the command
	            System.out.println("Here is the standard output of the command:\n");
	            while ((s = stdInput.readLine()) != null) {
	            	System.out.println(s);
	            }
	            
	            while ((s = stdError.readLine()) != null) {
	            	System.out.println(s);
	            }
		}catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }
		
	}

}

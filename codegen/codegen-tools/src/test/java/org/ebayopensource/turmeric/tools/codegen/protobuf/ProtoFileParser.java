package org.ebayopensource.turmeric.tools.codegen.protobuf;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProtoFileParser {
	
	static final String JAVA_PACKAGE ="java_package";
	static final String JAVA_OUTER_CLASS_NAME="java_outer_classname";
	
	 static int level =0;
	 static int enumchild =0;
	 static int msgchild =0;
	
	
	String packageName;
	File fileLocation;
	HashMap<String,String> options = new HashMap<String,String>();
	List<Message> messages = new ArrayList<Message>();
	List<String> enums = new ArrayList<String>();
	
	public ProtoFileParser(File location) {
		
		fileLocation = location;
	}
	
	
	
	public List<Message> read(String content){
		StringBuffer message = null;
		StringBuffer enums = null;
		
		List<String> list =FileUtil.readFileAsLines(fileLocation);
		List<String> anotherList = new ArrayList<String>(list);
		Iterator<String> itr = list.iterator();
		while(itr.hasNext()){
			
			String temp = itr.next().trim();
			if(temp.startsWith("//")){
				anotherList.remove(temp);
			}
		}
		StringBuffer act = new StringBuffer();
		for(String str :anotherList){
			act.append(str);
		}
		content = act.toString();
		List<StringBuffer> msg = new ArrayList<StringBuffer>();
		
		String [] statement = content.split(";");
		List<String> stmt = new ArrayList<String>();
		for(String s :statement){
		stmt.add(s);
		}
		Iterator<String> it = stmt.iterator();
		
		message = new StringBuffer();
		while(it.hasNext()){
			String s = it.next();
			if(s.contains("AckValue")){
				System.out.println("dsf");
			}
				if(s.trim().startsWith("//")){
					continue;
				}
		
				if(s.trim().startsWith("package")){
					
					String [] pack = s.trim().split(" ");
					packageName = pack[1];
					continue;
				}
		
				if(s.trim().startsWith("option") && !s.trim().startsWith("optional") ){
					
					String [] pack = s.trim().split("=");
					String [] opt = pack[0].split(" ");
					String optioName = opt[1].trim();
					String trimmedPack = pack[1].trim();
					if(optioName.equals(ProtoFileParser.JAVA_PACKAGE)){
						
						options.put(ProtoFileParser.JAVA_OUTER_CLASS_NAME,trimmedPack.substring(1,trimmedPack.length()-1).trim());
				
					}else if(optioName.equals(ProtoFileParser.JAVA_OUTER_CLASS_NAME)){
						
						options.put(ProtoFileParser.JAVA_OUTER_CLASS_NAME,trimmedPack.substring(1,trimmedPack.length()-1).trim());
			
					}
					continue;
				}
		
		
				if(s.trim().startsWith("message") && s.contains("enum")){
					message.append(s);
					message.append(";");
					continue;
				}
				
				if(s.trim().startsWith("message") && !s.contains("enum")){
					message.append(s);
					message.append(";");
					continue;
				}
				if(s.trim().startsWith("}}")){
					if(s.trim().equals("}}")){
						message.append("}}");
						msg.add(message);
						break;
					}
					String [] ar = s.split("}}");
					message.append("}}");
					msg.add(message);
					message = new StringBuffer();
					message.append(ar[1]);
					message.append(";");
					continue;
				}
				
				if(s.trim().startsWith("}")){
					if(s.trim().equals("}")){
						message.append("}");
						msg.add(message);
						break;
					}
					String [] ar = s.split("}");
					
					message.append("}");
					msg.add(message);
					message = new StringBuffer();
					message.append(ar[1]);
					message.append(";");
					continue;
				}
				
				message.append(s);
				message.append(";");
		
				/*if(s.trim().startsWith("message") && !s.contains("enum")){
					message =new StringBuffer();
					message.append(s);
					message.append(";");
					
					while(it.hasNext()){
						String nl = it.next();
						if(nl.contains("AckValue")){
							System.out.println("dsf");
						}
						if(nl.startsWith("//")){
							continue;
						}

						if(nl.contains("}")){
							if(nl.trim().equals("}")){
								message.append("}");
								msg.add(message);
								continue;
							}if(nl.trim().equals("}}")){
								message.append("}}");
								msg.add(message);
								continue;
							} 
							else{
							String [] ar = nl.trim().split("}");
							message.append("}");
							msg.add(message);
							if(ar[1].trim().startsWith("message") && ar[1].contains("enum")){
								if(ar[1].contains("AckValue")){
									System.out.println("dsf");
								}
								message =new StringBuffer();
								message.append(ar[1]);
								message.append(";");
								String n12 = null;
								while(it.hasNext()){
									 n12 = it.next();
									 if(n12.contains("AckValue")){
											System.out.println("dsf");
										}
									 if(n12.contains("//")){
										 continue;
									 }

									if(n12.contains("}")){
										n12.trim().replace(" ","");
										if(n12.equals("}}")){
											message.append("}}");
											msg.add(message);
											break;
										}
										String [] ar1 = n12.trim().split("}");
										message.append("}}");
										msg.add(message);
										message = new StringBuffer();
										for(String a: ar1) {
											if(a!="")
										message.append(a);
										}
										message.append(";");
										break;
									}
									message.append(n12);
									message.append(";");
									}
								continue;
							}
							message = new StringBuffer();
							message.append(ar[1]);
							message.append(";");
							continue;
							}
						}
						message.append(nl);
						message.append(";");
						
					}
					
				}
				if(s.trim().equals("}")){
					message.append("}");
					msg.add(message);
				}
				
				if(s.trim().startsWith("message") && s.contains("enum")){
					message =new StringBuffer();
					message.append(s);
					message.append(";");
					String n12 = null;
					while(it.hasNext()){
						 n12 = it.next();
						 if(n12.contains("//")){
							 continue;
						 }

						if(n12.contains("}")){
							n12.trim().replace(" ","");
							if(n12.equals("}}")){
								message.append("}}");
								msg.add(message);
								break;
							}
							String [] ar = n12.trim().split("}");
							message.append("}}");
							msg.add(message);
							message = new StringBuffer();
							for(String a: ar) {
								if(a!="")
							message.append(a);
							}
							message.append(";");
							break;
						}
						message.append(n12);
						message.append(";");
						}
					
						
				}*/

					
				} 
		
				
		
		/*if(!s.contains("}") && message != null){
			message.append(s);
			message.append(";");
		}
			if(s.contains("}")){
				
				if(s.trim().equals("}")){
					message.append("}");
					msg.add(message);
					continue;
				}
				String [] ar = s.split("}");
				message.append("}");
				msg.add(message);
				
				if(ar[1].trim().equals("")){
					continue;
				}
				else{
				message = new  StringBuffer();
				message.append(ar[1]);
				}
				
			}*/
	
		List<Message> ms = new ArrayList<Message>();
		for(StringBuffer buf: msg){
		System.out.println(buf);
		ms.add(getMsg(buf));
		
		}
		
		for(Message m :ms){
			System.out.println(m.getMessageName());
			if(m.getFields()!=null)
			for(Field f :m.getFields()){
				System.out.println(f.getFieldName());
			}
		}
		
		return ms;
		
	}
	
	
	public List<PMDInfo> parsePMDData(List<String> list){
		boolean canAdd = false;
		List<String> pmdData = new ArrayList<String>();
		for(String s : list){
			if(s.trim().equals("//PMD-PMD_DATA_END")){
				break;
			}
			if(canAdd){
				pmdData.add(s);
			}
			if(s.trim().equals("//PMD-PMD_DATA_START")){
				canAdd = true;
				
			}
			
			
		}
		
		List<PMDInfo> pmdList = new ArrayList<PMDInfo>();
		
		for(String str : pmdData){
			if(str.contains(("LAST_SINGLE"))){
				continue;
			}
			if(str.contains(("LAST_REPEATED"))){
				continue;
			}
			String [] pm = str.split("-");
			String [] pm1 =  pm[1].split("}");
			String [] info =  pm1[1].split("/");
			PMDInfo pmd = new PMDInfo();
			pmd.setMessageName(info[0]);
			pmd.setFieldName(info[1]);
			pmd.setProtoType(info[2].split("=")[0]);
			pmd.setSequenceNumber(info[2].split("=")[1]);
			pmdList.add(pmd);
		}
		
		return pmdList;
	}
	
	public Message getMsg(StringBuffer buf ){
		boolean isEnum = false;
		List<Field> flds = new ArrayList<Field>();
		Map<String,Integer> values = new HashMap<String,Integer>();
		Message msg = new Message();
		EnumMessage enumMsg = new EnumMessage();
		String message = buf.toString();
		String [] fields = message.split(";");
		for(String s :fields){
			
			if(s.contains("}")){
				continue;
			}
			if(s.startsWith("message") && !s.contains("enum")){
				System.out.println(s.indexOf("{"));
				String field = s.substring(0,s.indexOf("{"));
				String rest = s.substring(s.indexOf("{")+1);
				 msg.setMessageName(field.split(" ")[1]);
				
				 String [] foptions = rest.split("=");
				 String [] options = foptions[0].split(" ");
				 Field f = new Field();
				 f.setSequenceNumber(foptions[1].trim().substring(0));
				 f.setFieldName(options[2].trim());
				 f.setFieldRestriction(options[0].trim());
				 if(options[1].trim().contains("Enum.")){
					 f.setEnums(true);
				 }
				 f.setFieldType(options[1].trim());
				 flds.add(f);
				 continue;
				 
			}if(s.contains("message") && s.contains("enum")){
				isEnum = true;
				
				String field = s.substring(0,s.indexOf("{"));
				String field2 = s.substring(s.indexOf("{")+1,s.length());
				String field3 = field2.substring(0,field2.indexOf("{"));
				String field4 = field2.substring(field2.indexOf("{")+1,field2.length());
				enumMsg.setMessageName(field.split(" ")[1]);
				enumMsg.setEnumName(field3.split(" ")[1]);
				values.put(field4.split("=")[0].trim(),Integer.valueOf(field4.split("=")[1].trim()));

				continue;
				
			}
			else{
			
			 String [] foptions = s.split("=");
			 if(foptions[0].split(" ").length == 1){
				 
				 values.put(foptions[0].trim(),Integer.valueOf(foptions[1].trim()));
				 continue;
			 }
			 String [] options = foptions[0].split(" ");
			 Field f = new Field();
			 f.setSequenceNumber(foptions[1]);
			 f.setFieldName(options[2].trim());
			 f.setFieldRestriction(options[0].trim());
			 f.setFieldType(options[1].trim());
			 flds.add(f);
			}
		}
		msg.setFields(flds);
		enumMsg.setValues(values);
		if(isEnum)
		return enumMsg;
		return msg;
		
	}
	
	public void getMessage(String nl,Iterator<String> it,StringBuffer message,List<StringBuffer> msg){
		if(nl.contains("{")){
			level++;
			if(nl.contains("message") || nl.contains("enum")){
				message.append(nl);
				while(it.hasNext()){
					String n = it.next();
					if(n.contains("}")){
						if(level >= 1)
						message.append(n);
						if(level == 0) {
							if(n.trim().equals("}")){
								message.append("}");
								msg.add(message);
							}
							 String [] ar = n.split("}");
							 message.append("}");
							 msg.add(message);
							 message = new StringBuffer();
							 message.append(ar[1].trim());
							 break;
						}
							
						level--;

					}else
					getMessage(n, it, message,msg);
				}
			}
			
		} else{
			message.append(nl);
		}
	}
	
	public List<Message> parse(){
		
		return read(FileUtil.readProtoFileToString(fileLocation));
	}

}

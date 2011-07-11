package org.ebayopensource.turmeric.tools.codegen.protobuf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	
	
public static String readProtoFileToString(File fileLocation){
		
		StringBuffer sb = new StringBuffer();
		FileReader fr  = null;
		BufferedReader br =null;
		if(fileLocation != null){
			
			try {
				
				fr = new FileReader(fileLocation);
				br = new BufferedReader(fr);
				
				String line = null;
				do{
					line = br.readLine();
					if(line != null){
						
						sb.append(line.trim());
					
					}
						
				}while(line != null);
				
				
			} catch (FileNotFoundException e) {
			
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}finally{
				if(fr != null && br != null){
					
					try {
						fr.close();
						br.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
			
		}else{
			return null;
		}
		return sb.toString();
	}


public static List<String> readFileAsLines(File fileLocation){
	
	List<String> lines = new ArrayList<String>();
	FileReader fr  = null;
	BufferedReader br =null;
	if(fileLocation != null){
		
		try {
			
			fr = new FileReader(fileLocation);
			br = new BufferedReader(fr);
			
			String line = null;
			do{
				line = br.readLine();
				if(line != null){
					
					lines.add(line.trim());
				
				}
					
			}while(line != null);
			
			
		} catch (FileNotFoundException e) {
		
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}finally{
			if(fr != null && br != null){
				
				try {
					fr.close();
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		
	}else{
		return null;
	}
	return lines;
}

}

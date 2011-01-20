/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.tools.codegen.handler;

import java.util.Scanner;


public class ConsoleResponseHandler implements UserResponseHandler {
	
	private static final String INPUT_OPTION = "Enter [Y] to continue, [N] to exit : ";
	
	private static final String YES = "Y";
	private static final String NO = "N";
	

	public ConsoleResponseHandler() {}
	
	
	public boolean getBooleanResponse(String promptMsg) {
		
		String userInput = null;
		System.out.println(promptMsg); //KEEPME
		System.out.print(INPUT_OPTION); //KEEPME

		Scanner inputScan = new Scanner(System.in);
		while (inputScan.hasNext()) {
			userInput = inputScan.next();
			if (YES.equalsIgnoreCase(userInput) || 
				NO.equalsIgnoreCase(userInput)) {
				break;
			}
			System.out.println(INPUT_OPTION); //KEEPME
		}
		
		return YES.equalsIgnoreCase(userInput);
	}
	
}

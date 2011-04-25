/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.runtime.binding.impl.jaxb.json;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

/**
 * The JSON FilterInputStream check the input stream if the root XML element is present 
 * in the JSON payload. If not, it will add the root XML element at the begining and the 
 * the corresponding end before return EOS of the underlying stream.
 * 
 * @author asahni
 *
 */
public class JSONFilterInputStream extends FilterInputStream {
	
	private QName m_rootXMLName;
	private Charset m_charset;

	private static final int MAX_BUFFER_SIZE = 8192;
	
	// byte array
	private byte[] m_bites = new byte[INITIAL_BUFFER_SIZE*2];

	// the index from which the bytes are returned from the byte array
	private int m_readIndex = 0;

	// number of bytes in buffer that are present in the byte and ready to be read
	private int m_numBites = 0;

	// Whether the root has been looked for or not
	// Checking for the root is a one time activity, invoked at the very first 
	// of reads
	private boolean m_lookedForRoot = false;

	// Signifies whether the root XML element is already present in the input stream or not
	private boolean m_rootElementAdded = false;

	// If the root XML ending was added, whether it has been sent to the caller or not
	private boolean m_sentRootElementSuffix = false;

	private String m_rootElementName = null;
	private boolean m_endCurlyBraceNeeded = false;
	
	/**
	 * Constructor
	 * 
	 * @param in - the underlying input stream carrying the JSON payload
	 * @param rootXMLName - the root XML element to look for
	 * @param charset - character encoding
	 */
	public JSONFilterInputStream(InputStream in, QName rootXMLName, Charset charset) {
		super(in);
		this.m_rootXMLName = rootXMLName;
		this.m_charset = charset;
		
		m_rootElementName = "\"" + 
			(
					(m_rootXMLName.getPrefix() == null || m_rootXMLName.getPrefix().equals(""))
					?
					""
					:
					(m_rootXMLName.getPrefix() + ".")
			) +
			m_rootXMLName.getLocalPart() + "\"" + ":";

	}

	/**
	 * Ensures that there are atleast the specified number of 'len' bytes available
	 * in the byte array.
	 * 
	 * @param len
	 */
	private void ensureCapacity(int len) {
		if( m_numBites + len > m_bites.length ) {
			byte[] newBites = new byte[(m_bites.length + len)*2];
			System.arraycopy(m_bites, 0, newBites, 0, m_numBites);
			m_bites = newBites;
		}
	}

	/**
	 * This method first checks whether the root XML element has been looked for.
	 * If not, the method to look for the root XML element is called, else
	 * super.read() is call and the returned values is stored in the byte array and
	 * returned from it.
	 */
	public int read() throws IOException {

		// Check if the root has been looked for
		if( ! m_lookedForRoot ) {
			lookForRootUsingRegex();
		} 

		// Read from either the buffer or from stream
		if( m_readIndex < m_numBites ) {
			// the buffer has some unread chars
			return m_bites[m_readIndex++];
		} else {
			// either the buffer is empty or end of stream has been reached
			int retval = super.read();
			if( retval < 0 ) {
				// check if corresponding end of XML root element needs to be added
				if( m_rootElementAdded && m_endCurlyBraceNeeded && ! m_sentRootElementSuffix ) {
					m_sentRootElementSuffix = true;
					return '}';
				} else {
					return retval;
				}
			} else {
				return retval;
			}

		}
	}

	/**
	 * This method first looks for the root XML element. If the root XML element
	 * has been not been looked for, it calls the method to look for the root XML
	 * element, else super.read
	 */
	public int read(byte[] b,  int off, int len) throws IOException {

		if( len == 0 ) {
			return 0;
		}

		// look for presence or absence of root XML element 
		if( ! m_lookedForRoot ) {
			lookForRootUsingRegex();
		}

		// check if unread data is present in the byte array buffer
		if( m_readIndex < m_numBites ) {
			// unread data in the buffer - read from it
			int xferCount = Math.min(len, (m_numBites-m_readIndex));
			System.arraycopy(m_bites, m_readIndex, b, off, xferCount);
			
//			System.out.println("ashish debug: " + new String(b, off, xferCount));
			
			m_readIndex += xferCount;
			return xferCount;
		}

		// buffer is empty. go ahead and read from stream
		int numRead2 = super.read(b, off, len);
		if( numRead2 < 0 ) {

			// check if corresponding end of XML root element needs to be added
			if( m_rootElementAdded && m_endCurlyBraceNeeded && ! m_sentRootElementSuffix ) {
				m_sentRootElementSuffix = true;
				b[off] = '}';

				//System.out.println("ashish debug: " + new String(b, off, 1));
				
				return 1;
			} else {
				// EOS
				return numRead2;
			}

		} else {

			//System.out.println("ashish debug: " + new String(b, off, numRead2));
			
			// return the number of bytes read
			return numRead2;
		}

	}

	/**
	 * If the root XML element has already been looked for, 0 is returned,
	 * else inputstream is read and the number of bytes read from the input stream are returned
	 * 
	 * This method needs to be called once only, for the first time when read() or read(byte[], int, int)
	 * is called. Its look for the first two curly braces ('{'). If the char sequence between the first two
	 * curly braces matches the root XML element name, nothing special is done, else the root XML element
	 * name is prefixed to the byte array.
	 * 
	 * @return total number of byte added to the byte array during this call
	 * @throws IOException
	 */
	// TODO - For the logic here pushback inputstream might work better
	//    read till the second curly brace
	//   if( rootXMLelement NOT present ) {
	//    add rootXMLelement to byte array buffer
	//   }
	//    pushback all the bytes read from the input stream
	private int lookForRoot() throws IOException {

		if( m_lookedForRoot ) {
			return 0; 
		}

		m_lookedForRoot = true;

		int totalBitesRead = 0;
		int firstIndex = 0;
		int secondIndex = 0;
		
		int firstOpenParanIndex = -1;
		int firstCloseParanIndex = -1;
		boolean noBraces = false;

		// look for the first '{'
		while(true) {

			ensureCapacity(1);

			int biteRead = super.read();
			if( biteRead < 0 ) {
				return totalBitesRead;
			}
			
			m_bites[m_numBites++] = (byte)biteRead;
			totalBitesRead++;

			if( biteRead == '{' ) {
				firstOpenParanIndex = totalBitesRead - 1;
			}
			
			if(biteRead == '"' ) {
				break;
			}

			firstIndex++;
			
			
			if( totalBitesRead > MAX_BUFFER_SIZE ) {
				throw new IllegalStateException("Maximum buffer size exceeded");
			}

		}

		// signifies whether to break from the while loop of keep looking for '{ or '}'
		boolean loopForBraceOnly = false;
		
		secondIndex = firstIndex + 1;
		// look for the second '{' or ':'
		while(true) {

			ensureCapacity(1);

			int biteRead = super.read();
			if( biteRead < 0 ) {
				return totalBitesRead;
			}
			
			m_bites[m_numBites++] = (byte)biteRead;
			totalBitesRead++;

			if(biteRead == '"' ) {
				loopForBraceOnly = true;
			}
			
			if(biteRead == '{' || biteRead == ':' ) {
				break;
			}
		
			// its a single level like {"this is my message"}
			// the output should look 
			//		something like {"XMLRootElementName":"this is my message"}
			//		and not        {"XMLRootElementName":{"this is my message"}} 
			if( biteRead == '}' ) {
				noBraces = true;
				firstCloseParanIndex = totalBitesRead - 1;
				break;
			}
			
			if( loopForBraceOnly ) {
				continue;
			}

			secondIndex++;

			if( totalBitesRead > MAX_BUFFER_SIZE ) {
				throw new IllegalStateException("Maximum buffer size exceeded");
			}

		}

		// Look if the root element if already present between the first and the second parantheses
		//String s = new String(m_bites, firstParanIndex, (secondParanIndex-firstParanIndex), m_charset.name());
		String s = new String(m_bites, firstIndex+1, (secondIndex-firstIndex-1), m_charset.name());
		boolean addRootElement = false;
		if(s.equals(m_rootXMLName.getLocalPart())) {
			// Yes, root element is present in the payload
			addRootElement = false;
		} else {
			// Check is the root element is prefix qualified
			if(s.indexOf('.') != -1) {
				// Yes, prefix qualified. Check local part only 
				String s1 = s.substring(s.indexOf('.')+1);
				if(s1.equals(m_rootXMLName.getLocalPart())) {
					// Local part matches root element
					addRootElement = false;
				} else {
					// No root element present, must be added
					addRootElement = true;
				}
			} else {
				// no, not prefix  qualified and therefore add root element 
				addRootElement = true;
			}
		}

		if( addRootElement ) {
			
			if( noBraces ) {
				// get rid of the '{' '}' in the byte array read so far
				
				// create a tmp byte array
				byte[] tmpBites = new byte[m_bites.length];
				// copy from 0 to firstParanIndex(exclusive)
				System.arraycopy(m_bites, 0, tmpBites, 0, firstOpenParanIndex);
				// copy from firstParanIndex+1, to secondParanIndex(exclusive)
				System.arraycopy(m_bites, firstOpenParanIndex+1, 
						tmpBites, firstOpenParanIndex, (firstCloseParanIndex-firstOpenParanIndex-1));
				// reduce the length by 2
				m_numBites -= 2;
				// reassign the new to old
				m_bites = tmpBites;
				
			}

			// root XML element is not present - add it now to the buffer bytes

			String rootPrefix = "{" + "\"" + 
				(
					(m_rootXMLName.getPrefix() == null || m_rootXMLName.getPrefix().equals(""))
					?
					""
					:
					(m_rootXMLName.getPrefix() + ".")
				) +
//				(m_rootXMLName.getPrefix() == "" ? 
//						(m_rootXMLName.getLocalPart().equals("MyMessage") ? "ns2." : "") : m_rootXMLName.getPrefix()) + 
				m_rootXMLName.getLocalPart() + "\"" + ":";

			ensureCapacity(rootPrefix.length());

			byte[] tmpBites = this.m_bites.clone();
			System.arraycopy(rootPrefix.getBytes(), 0, m_bites, 0, rootPrefix.length());
			System.arraycopy(tmpBites, 0, m_bites, rootPrefix.length(), totalBitesRead);

			m_numBites += rootPrefix.length();
			m_rootElementAdded = true;
			
			return totalBitesRead + rootPrefix.length();

		} else {

			m_rootElementAdded = false;
			return totalBitesRead;

		}

	}
	
	/**
	 * "{\"jsonns.xsi\":\"http://www.w3.org/2001/XMLSchema-instance\",\"jsonns.xs\":\"http://www.w3.org/2001/XMLSchema\",\"jsonns.ms\":\"http://www.ebayopensource.org/turmeric/common/v1/types\",\"jsonns.ns2\":\"http://www.ebayopensource.org/test\",\"ms.getName\":[{\"ms.in\":[{\"ns2.name\":[\"whatever\"]}]}]}";
	 * {(("jsonns.[a-z]+":"[^"]*")?(,"jsonns.[a-z]+":"[^"]*")*("rootElementName":)?[?{?.*}
	 * JSON-PAYLOAD:
	 */
	
	private static final Pattern JSON_NS_PATTERN = Pattern.compile(
		"\\{\\s*(\\s*\"jsonns\\.[a-zA-Z0-9]+\"\\s*:\\s*\"[^\"]+\"\\s*,)*\\s*(\"[a-zA-Z0-9@.]+\"\\s*:\\s*)?(\\[?\\{?.*)", Pattern.MULTILINE | Pattern.DOTALL);
		
	
	private static final int INITIAL_BUFFER_SIZE = 1024;
	private void lookForRootUsingRegex() throws IOException {
		
		if( m_lookedForRoot ) {
			return; //return 0; 
		}

		m_lookedForRoot = true;

		int bitesRead = 0;
		
		while((bitesRead = super.read(this.m_bites, m_numBites, INITIAL_BUFFER_SIZE-m_numBites)) >= 0) {
			m_numBites += bitesRead;
			if(m_numBites == INITIAL_BUFFER_SIZE) {
				break;
			}
		}
		
		String sRead = new String(m_bites, 0, m_numBites, m_charset.name());
		
		//System.out.println("pattern: " + JSON_NS_PATTERN);
		Matcher matcher = JSON_NS_PATTERN.matcher(sRead);
//		System.out.println("matcher matches: " + matcher.matches());
//		System.out.println("group count: " + matcher.groupCount());
		
		if( matcher.matches() ) {
//			for(int i=0; i<=matcher.groupCount(); i++) {
//				System.out.println("    " + i + " : " + matcher.group(i));
//			}
			
			if( matcher.group(3) == null ) {
				return; // ?
			}
			
			String currentRootElement = matcher.group(2);
			if( currentRootElement == null ) {
				
				// its of the form { "abc" } to be transformed into { "root-element-name": "abc" }
				int startIndex = matcher.start(3);
//				System.out.print("part1=\"" + matcher.group(1) + "\" part2=\"" + matcher.group(2) + "\" part3=\"" + matcher.group(3) + "\"");
				byte[] tmpBites = m_bites.clone();
				System.arraycopy(m_bites, 0, tmpBites, 0, startIndex);
				int rootElementLen = m_rootElementName.length();
				System.arraycopy(m_rootElementName.getBytes(), 0, tmpBites, startIndex, rootElementLen);
				System.arraycopy(m_bites, startIndex, tmpBites, startIndex + rootElementLen, m_numBites-startIndex);
		
				m_bites = tmpBites;
				m_numBites +=  rootElementLen;
				
				this.m_rootElementAdded = true;
				this.m_endCurlyBraceNeeded = true;
				
			} else {
				
				currentRootElement = currentRootElement.substring(1, currentRootElement.length()-2);

				boolean addRootElement = false;
				if(currentRootElement.equals(m_rootXMLName.getLocalPart())) {
					// Yes, root element is present in the payload
					addRootElement = false;
				} else {
					// Check is the root element is prefix qualified
					if(currentRootElement.indexOf('.') != -1) {
						// Yes, prefix qualified. Check local part only 
						String s1 = currentRootElement.substring(currentRootElement.indexOf('.')+1);
						if(s1.equals(m_rootXMLName.getLocalPart())) {
							// Local part matches root element
							addRootElement = false;
						} else {
							// No root element present, must be added
							addRootElement = true;
						}
					} else {
						// no, not prefix  qualified and therefore add root element 
						addRootElement = true;
					}
				}
				
				if( addRootElement ) {
					
					String newRoot = m_rootElementName + "{";
					
					// its of the form { "abc" } to be transformed into { "root-element-name": "abc" }
					int startIndex = matcher.start(2);
					byte[] tmpBites = m_bites.clone();
					System.arraycopy(m_bites, 0, tmpBites, 0, startIndex);
					System.arraycopy(newRoot.getBytes(), 0, tmpBites, startIndex, newRoot.length());
					System.arraycopy(m_bites, startIndex, tmpBites, startIndex + newRoot.length(), m_numBites-startIndex);
					
					m_bites = tmpBites;
					m_numBites +=  newRoot.length();
					
					this.m_rootElementAdded = true;
					this.m_endCurlyBraceNeeded = true;
				}

			}
			
			
		}
	}

}

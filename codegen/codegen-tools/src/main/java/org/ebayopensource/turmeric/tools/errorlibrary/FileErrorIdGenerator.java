/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
/**
 * 
 */
package org.ebayopensource.turmeric.tools.errorlibrary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.ebayopensource.turmeric.runtime.common.impl.utils.CallTrackingLogger;
import org.ebayopensource.turmeric.runtime.common.impl.utils.LogManager;
import org.ebayopensource.turmeric.tools.codegen.util.CodeGenUtil;
import org.ebayopensource.turmeric.tools.errorlibrary.exception.ErrorIdGeneratorException;

import org.ebayopensource.turmeric.common.config.OrganizationErrorBlocks;
import org.ebayopensource.turmeric.common.config.ReservedErrorBlock;
import org.ebayopensource.turmeric.common.config.UsedErrorBlock;
import org.ebayopensource.turmeric.common.config.OrganizationErrorBlocks.UsedErrorBlockList;
import org.ebayopensource.turmeric.common.config.OrganizationErrorBlocks.ReservedErrorBlockList;



/**
 * @author arajmony,stecheng
 *
 */
class FileErrorIdGenerator implements ErrorIdGenerator {
	private JAXBContext jc;
	private String m_fileName;
	private Unmarshaller u;
	private Marshaller m;
	private OrganizationErrorBlocks errorBlocks;
	private static final String s_lockFileExtension = ".lck";
	
	public static int MINIMUM_BLOCKSIZE = 100;
	public static int DEFAULT_BLOCKSIZE = 1000;
	private int blocksize = DEFAULT_BLOCKSIZE;
	private int SOA_RESERVED_BLOCK = 100000;
	
	private Map<String,List<UsedErrorBlock>> usedErrorBlockMap;
	private Set<Range> allocatedRanges = new TreeSet<Range>();
	
	private static CallTrackingLogger s_Logger = LogManager.getInstance(FileErrorIdGenerator.class);
	
	
	/**
	 * Builder class helps solve the telescoping constructor problem
	 */
	static class Builder implements ErrorIdGenerator.Builder {
		private String storeLocation;
		private String organizationName;
		private int blocksize = DEFAULT_BLOCKSIZE;
		
		private static final String SUFFIX_FOR_FILE_NAME = "ErrorIDs.xml";
		
		/**
		 * This is the equivalent to the filename where error ids are maintained
		 * @param storeLocation 
		 * @return this
		 */
		public Builder storeLocation( String storeLocation ) {
			this.storeLocation = storeLocation; 
			return this;
		}
		public Builder organizationName( String organizationName ) {
			this.organizationName = organizationName;
			return this;
		}
		public Builder blocksize( int blocksize ) {
			this.blocksize = blocksize; return this;
		}
		/**
		 * This implementation does not use authentication to gain access to the file.
		 * It is assumed that all users have the same access rights to the file.  
		 * @throws UnsupportedOperationException
		 */
		public ErrorIdGenerator.Builder credentials( String username, String password ) {
			throw new UnsupportedOperationException();
		}
		/**
    	 * @throws IllegalArgumentException
	     * if fileName is not specified or
	     * if organization is not specified or does NOT match the organization contained within fileName
     	 * @throws IllegalStateException if there were problems loading/verifying fileName  
		 */
		public ErrorIdGenerator build() {
			
			validateArguments();
			String fileName =  CodeGenUtil.toOSFilePath(storeLocation) + organizationName + SUFFIX_FOR_FILE_NAME;
			return new FileErrorIdGenerator( fileName, organizationName, blocksize );
		}

		
		private void validateArguments() {
			
			if ( CodeGenUtil.isEmptyString(storeLocation))
				throw new IllegalArgumentException( "No filename(store Location) specified!" );
			if ( CodeGenUtil.isEmptyString(organizationName) )
				throw new IllegalArgumentException( "No organizationName specified!" );
			
			File storeLocationFile = new File(storeLocation);
			if(!storeLocationFile.exists())
				throw new IllegalArgumentException( "The specified location for storeLocation does not exist : "+ storeLocation);
			
			if(!storeLocationFile.isDirectory())
				throw new IllegalArgumentException( "The specified location for storeLocation is not a directory : "+ storeLocation);

			if(!storeLocationFile.canWrite())
				throw new IllegalArgumentException( "The specified location for storeLocation is not writable : "+ storeLocation);

			if(blocksize < MINIMUM_BLOCKSIZE)
				throw new IllegalArgumentException( "The block specified is less then the minimum block size of "+ MINIMUM_BLOCKSIZE);
				
		}
	}
	
	/**
	 * 
	 * @param fileNameParam
	 * @param organization
	 * @param blocksize
	 * @throws IllegalStateException if there were problems loading/verifying fileName 
	 */
	private FileErrorIdGenerator( String fileNameParam, String organization, int blocksize ) {
		
		this.blocksize = blocksize;
		
		try {
			jc = JAXBContext.newInstance( OrganizationErrorBlocks.class );
			u = jc.createUnmarshaller();
			m = jc.createMarshaller();
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			this.m_fileName = fileNameParam;
			File xmlFile = new File( fileNameParam );
			if ( xmlFile.exists() )
				errorBlocks = (OrganizationErrorBlocks) u.unmarshal( xmlFile );
			else
				errorBlocks = buildDefaultErrorBlock( organization );
			
			if ( !errorBlocks.getOrganization().equals( organization ) )
				throw new IllegalArgumentException( "The specified organization name is inconsistent with the name stored in the file: " + errorBlocks.getOrganization() );
		} catch ( JAXBException e ) {
			/**
			 * Don't let the JAXBException leak.  This allows our choice of XML bindings to change in the future
			 */
			throw new IllegalStateException( "Failed to initialize the environment or load the file: " + e.getMessage() );
		}
		
		buildUsedErrorBlockMap();
		buildAllocatedRanges();
		try {
			persist();
		} catch (ErrorIdGeneratorException e) {
		    s_Logger.log(Level.WARNING, "Unable to persist", e);
		}
		
	}
	
	private void buildUsedErrorBlockMap() {
		usedErrorBlockMap = new HashMap<String,List<UsedErrorBlock>>();
		for ( UsedErrorBlock usedErrorBlock : errorBlocks.getUsedErrorBlockList().getUsedErrorBlock() ) {
			List<UsedErrorBlock> domainErrorBlockList = usedErrorBlockMap.get( usedErrorBlock.getDomain() );
			if ( domainErrorBlockList == null ) {
				domainErrorBlockList = new ArrayList<UsedErrorBlock>();
				usedErrorBlockMap.put( usedErrorBlock.getDomain(), domainErrorBlockList );
			}
			domainErrorBlockList.add( usedErrorBlock );
		}
	}
	
	private void buildAllocatedRanges() {
		allocatedRanges = new TreeSet<Range>();
		// TODO: Verify no overlapping ranges
		// TODO: Verify that start < end
		for ( ReservedErrorBlock reservedErrorBlock : errorBlocks.getReservedErrorBlockList().getReservedErrorBlock() ) {
			allocatedRanges.add( new Range( reservedErrorBlock.getStart(), reservedErrorBlock.getEnd() ) );
		}
		for ( UsedErrorBlock usedErrorBlock : errorBlocks.getUsedErrorBlockList().getUsedErrorBlock() ) {
			allocatedRanges.add( new Range( usedErrorBlock.getStart(), usedErrorBlock.getEnd() ) );
		}
	}
	
//	private UsedErrorBlock findLastUsedErrorBlock() {
//		List<UsedErrorBlock> usedErrorBlockList = errorBlocks.getUsedErrorBlockList().getUsedErrorBlock();
//		int indexOfLastUsedBlock = usedErrorBlockList.size() - 1;
//		return usedErrorBlockList.get( indexOfLastUsedBlock );
//	}
	
	private Range findNextUnallocatedRange() {
		Range unallocatedRange = new Range( 1, blocksize - 1 );
		for ( Range allocatedRange : allocatedRanges ) {
			if ( !allocatedRange.intersects( unallocatedRange ) )
				break;
			unallocatedRange = new Range( allocatedRange.getEnd() + 1, allocatedRange.getEnd() + blocksize );
		}
		return unallocatedRange;
	}
	
	private OrganizationErrorBlocks buildDefaultErrorBlock( String organization ) {
		errorBlocks = new OrganizationErrorBlocks();
		errorBlocks.setDefaultBlockSize( DEFAULT_BLOCKSIZE );
		errorBlocks.setOrganization( organization );
		errorBlocks.setReservedErrorBlockList(getDefaultReservedBlockList());// new ReservedErrorBlockList() );
		errorBlocks.setUsedErrorBlockList( new UsedErrorBlockList() );
		return errorBlocks;
	}

//	private UsedErrorBlock buildUsedErrorBlock( String domain, long start, long end ) {
//		UsedErrorBlock usedErrorBlock = new UsedErrorBlock();
//		usedErrorBlock.setDomain( domain );
//		usedErrorBlock.setStart( start );
//		usedErrorBlock.setEnd( start + blocksize );
//		usedErrorBlock.setLast( start );
//		return usedErrorBlock;
//	}
	
	private ReservedErrorBlockList getDefaultReservedBlockList() {
		ReservedErrorBlockList reservedErrorBlockList = new ReservedErrorBlockList();
		
		long reservedBlockStart = 1;
		
		ReservedErrorBlock soaReservedErrorBlock = new ReservedErrorBlock();
		soaReservedErrorBlock.setStart(reservedBlockStart);
		soaReservedErrorBlock.setEnd(reservedBlockStart +  SOA_RESERVED_BLOCK - 1);
		
		
		
		reservedErrorBlockList.getReservedErrorBlock().add(soaReservedErrorBlock);
		
		return reservedErrorBlockList;
	}

	private boolean isDomainCreated( String domain ) {
		return usedErrorBlockMap.containsKey( domain );
	}
	
	/**
	 * 
	 * @param domain
	 * @throws IllegalArgumentException if the blocksize is not valid
	 */
	private void createDomain( String domain ) {
		if ( blocksize < MINIMUM_BLOCKSIZE )
			throw new IllegalArgumentException( "Block size must be at least " + MINIMUM_BLOCKSIZE );
		Range range = findNextUnallocatedRange();
		allocatedRanges.add( range );
		UsedErrorBlock usedErrorBlock = new UsedErrorBlock();
		usedErrorBlock.setDomain( domain );
		usedErrorBlock.setStart( range.getStart() );
		usedErrorBlock.setEnd( range.getEnd() );
		usedErrorBlock.setLast( range.getStart() - 1 );
		List<UsedErrorBlock> usedErrorBlockList = errorBlocks.getUsedErrorBlockList().getUsedErrorBlock();
		usedErrorBlockList.add( usedErrorBlock );
		
		List<UsedErrorBlock> domainErrorBlockList = usedErrorBlockMap.get( usedErrorBlock.getDomain() );
		if ( domainErrorBlockList == null ) {
			domainErrorBlockList = new ArrayList<UsedErrorBlock>();
			usedErrorBlockMap.put( usedErrorBlock.getDomain(), domainErrorBlockList );
		}
		domainErrorBlockList.add( usedErrorBlock );
	}
	
	private UsedErrorBlock getUsedErrorBlock( String domain ) {
		List<UsedErrorBlock> domainErrorBlockList = usedErrorBlockMap.get( domain );
		for ( UsedErrorBlock usedErrorBlock : domainErrorBlockList )
			if ( usedErrorBlock.getLast() < usedErrorBlock.getEnd() )
				return usedErrorBlock;

		/**
		 * Otherwise the domain ran out of error id's in which case, we create a new block of them
		 */
		createDomain( domain );
		domainErrorBlockList = usedErrorBlockMap.get( domain );
		assert domainErrorBlockList != null;
		assert domainErrorBlockList.size() > 1;
		return domainErrorBlockList.get( domainErrorBlockList.size() - 1 );
	}
	
	
	/**
	 * 
	 */
	public long getNextId(String domain) throws IllegalArgumentException, IllegalStateException, ErrorIdGeneratorException {
		long nextId;
		synchronized (this) {
			
			File xmlFile = new File( m_fileName );
			if ( xmlFile.exists() ){
				errorBlocks = JAXB.unmarshal(xmlFile, OrganizationErrorBlocks.class);
				buildUsedErrorBlockMap();
				buildAllocatedRanges();
			}

			
			boolean isFileLock = tryGetFileLock();
			if(!isFileLock)
				throw new ErrorIdGeneratorException("Could not get the lock for the file : " + m_fileName);

			
			if ( !isDomainCreated( domain ) )
				createDomain( domain );
			
			nextId = findAndUpdateNextId( domain );

			persist();
			
		}
		return nextId;
	}
	
	
//	private void updateLastId( long nextId, String domain ) {
//		UsedErrorBlock usedErrorBlock = getUsedErrorBlock( domain );
//		usedErrorBlock.setLast( nextId );
//	}

	private boolean tryGetFileLock() throws ErrorIdGeneratorException{
		
		
		/*
		 * a. create the lock file it it does not exist, it it exists return
		 * b. create a copy of the exisiting file 
		 * c. delete the exisisting file
		 * d. rename the new file
		 * e. take the lock on the new file
		 */

		
		File file = new File(m_fileName);
		
		String lockFilePath = getLockFilesPath();
		File lockFile = new File(lockFilePath);
		if(lockFile.exists()){
			s_Logger.log(Level.SEVERE, "#1 Could not get lock for the file : " + m_fileName ); 
			throw new ErrorIdGeneratorException("#1 Could not get lock for the file : " + m_fileName  );
		}else{
			try {
					if(!lockFile.createNewFile()){
						String errMsg = "#1 Could not create the lock file : " + m_fileName ;
						s_Logger.log(Level.SEVERE,errMsg );
						throw new ErrorIdGeneratorException(errMsg );

					}
			} catch (IOException e) {
				String errMsg = e.getMessage();
				s_Logger.log(Level.SEVERE,errMsg );
				throw new ErrorIdGeneratorException(errMsg );
			}
		}
		
		FileOutputStream tempStream = null;
		try {
			File tempFile = new File(m_fileName  + ".copy");
			
			if(!copyFile(file,tempFile)){
				String errMsg = "#1 Could not copy the original file to temp file: " + m_fileName ;
				s_Logger.log(Level.SEVERE,errMsg );
				throw new ErrorIdGeneratorException(errMsg );
			}
			
			
			if(!file.delete()){
				String errMsg = "#1 Could not delete the file : " + m_fileName;
				s_Logger.log(Level.SEVERE, errMsg );
				throw new ErrorIdGeneratorException(errMsg);
			}
			
			
			file = new File(m_fileName);
			if(!tempFile.renameTo(file)){
				String errMsg = "#1 Could not rename the file : " + m_fileName;
				s_Logger.log(Level.SEVERE, errMsg );
				throw new ErrorIdGeneratorException(errMsg);
			}
				
			
			tempStream = new FileOutputStream(file);
			
			FileChannel fileChannel = tempStream.getChannel();
			FileLock fileLock = fileChannel.tryLock();
			if(fileLock == null){
				throw new ErrorIdGeneratorException("#2 Could not get lock for the file : " + m_fileName  );
			}
			
		} catch ( FileNotFoundException e) {
			throw new ErrorIdGeneratorException("Could not make the file writable : " + e.getMessage());
		} catch (IOException e) {
			throw new ErrorIdGeneratorException("Could not make the file writable " + e.getMessage());		
		} finally {
			CodeGenUtil.closeQuietly(tempStream);
		}
		
		
		return true;
	}
	
	

	private boolean copyFile(File sourceFile, File tempFile) {
		
		
		InputStream sourceInputStream = null;
		OutputStream targetOutputStream = null;
		try {
			sourceInputStream = new FileInputStream(sourceFile);
			targetOutputStream = new FileOutputStream(tempFile);
			
			byte[] bytes = new byte[10000];
			
			
			int readCount = 0;
			while ( (readCount = sourceInputStream.read(bytes)) > 0 ){
				targetOutputStream.write(bytes,0,readCount);
	       }
			
		} catch (FileNotFoundException e) {
			s_Logger.log(Level.INFO, e.getMessage());
			return false;
		} catch (IOException e) {
			s_Logger.log(Level.INFO, e.getMessage());
			return false;
		}
		finally{
			CodeGenUtil.closeQuietly(targetOutputStream);
			CodeGenUtil.closeQuietly(sourceInputStream);
		}
		
		return true;
	}

	private String getLockFilesPath() {
		
		return m_fileName + s_lockFileExtension;
	}

	/**
	 * 
	 * @param domain
	 * @return
	 */
	private long findAndUpdateNextId( String domain ) {
		
		UsedErrorBlock usedErrorBlock = getUsedErrorBlock( domain );
		// TODO: Create a new block 
		long lastId = usedErrorBlock.getLast() + 1;
		usedErrorBlock.setLast( lastId );
		return lastId;
	}
	
	
	public void persist() throws ErrorIdGeneratorException {
		try {
			File file = new File(m_fileName);
			m.marshal( errorBlocks, file );
			
			file.setReadOnly();
			
			File lockFile = new File(getLockFilesPath());
			if(lockFile.exists()){
				if(!lockFile.delete()){
					throw new ErrorIdGeneratorException("The lock file could not be deleted");
				}
			}
		} catch ( JAXBException e ) {
			throw new ErrorIdGeneratorException( "Failed to save: " + e.getMessage() );
		} 
	}

	
	/*
	private void makeTheFileReadOnly() throws ErrorIdGeneratorException{
		
		File file = new File(fileName);
		file.setReadOnly();
		
		try {
			File file = new File(fileName);
			String filePath = file.getAbsolutePath();
			Runtime.getRuntime().exec("chmod 111 " +  filePath);
		} catch (IOException e) {
			throw new ErrorIdGeneratorException("Could not make the file read only after updating the file : "+ fileName);
		}			
		
	}
	*/

	

}

class Range implements Comparable<Range> {
	private long start;
	private long end;
	
	public long getStart() { return start; }
	public long getEnd() { return end; }
	
	public Range( long start, long end ) {
		if ( start > end ) throw new IllegalArgumentException();
		
		this.start = start;
		this.end = end;
	}
	
	public boolean isContained( long n ) {
		return start <= n && n < end; 
	}

	public boolean intersects( Range range ) {
		return
		( this.start < range.start && range.start < this.end ) || // right intersection
		( this.start < range.end && range.end < this.end ) || // left intersection
		( this.start <= range.start && this.end >= range.end );  // all enveloping
	}
	
	public int compareTo( Range rhs ) {
		return this.end < rhs.end ? -1 : this.end == rhs.end ? 0 : 1;
	}
	
	public String toString() {
		return "start="+start + ", end="+end;
	}
}
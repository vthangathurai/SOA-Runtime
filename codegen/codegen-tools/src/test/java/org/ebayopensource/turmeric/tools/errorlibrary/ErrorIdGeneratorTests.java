package org.ebayopensource.turmeric.tools.errorlibrary;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;

import org.ebayopensource.turmeric.tools.codegen.AbstractServiceGeneratorTestCase;
import org.ebayopensource.turmeric.tools.errorlibrary.ErrorIdGenerator;
import org.ebayopensource.turmeric.tools.errorlibrary.ErrorIdGeneratorFactory;
import org.ebayopensource.turmeric.tools.errorlibrary.ErrorIdGeneratorTest;
import org.ebayopensource.turmeric.tools.errorlibrary.ErrorIdGenerator.Builder;
import org.ebayopensource.turmeric.tools.errorlibrary.exception.ErrorIdGeneratorException;
import org.junit.Before;
import org.junit.Test;

public class ErrorIdGeneratorTests extends AbstractServiceGeneratorTestCase {

	private static final String MARKET_PLACES_ORG_NAME = "MarketPlaces";
	private static String MARKET_PLACES_STORE_LOCATION = null;
	private static final int MIN_BLK_SIZE = 1000;
	private static final String PAY_ORG_NAME = "Play";
	
	private static final String DOMAIN_ONE = "DOMAIN_ONE";
	private static final String DOMAIN_TWO = "DOMAIN_TWO";
	private static final String DOMAIN_THREE = "DOMAIN_THREE";
	private static final String MARKET_PLACES_ORG_NAME1 = "MarketPlaces_NoUserBlocks";
	

	private ErrorIdGeneratorTest  errorIdGenerator2;
	
	
	public ErrorIdGeneratorTests() {
		super();
	}
	@Before
	public void init(){
		
		mavenTestingRules.setFailOnViolation(false);
		
		MARKET_PLACES_STORE_LOCATION =   testingdir.getDir().getAbsolutePath();
		
		
	}
	
	/* Positive test case for generating ID
	*/
	@Test
	public void testErrorIDGenerator() throws ErrorIdGeneratorException{
		
		boolean isillegalArgumentException = false;
		long id =0;
		boolean deleteLibraryFlag = deleteTypeLibrary(MARKET_PLACES_STORE_LOCATION, MARKET_PLACES_ORG_NAME);
		ErrorIdGenerator  errorIdGenerator =ErrorIdGeneratorFactory.getErrorIdGenerator(
				MARKET_PLACES_STORE_LOCATION,
				 MARKET_PLACES_ORG_NAME);
		id = errorIdGenerator.getNextId(DOMAIN_ONE);
		if(id == 0)
		{
			isillegalArgumentException = true;
			assertTrue("ID is not generated", isillegalArgumentException);
		}
		/* ***************  TO DO **************** 
		For positive test case #4 add validation for checking reserved IDs range
		validate on default block size
		Validate existance of file at specified location
		Validate locking mechanism */
	}
	
	/* Positive test case for generating ID with block size as one of the input param 
	*/
	@Test
	public void testErrorIDGenerator_blocksize() throws IllegalStateException, ErrorIdGeneratorException{
		
		boolean deleteLibraryFlag = deleteTypeLibrary(MARKET_PLACES_STORE_LOCATION, MARKET_PLACES_ORG_NAME + 1);
		boolean isillegalArgumentException = false;
		long id =0;
		try{
			 ErrorIdGenerator  errorIdGenerator =ErrorIdGeneratorFactory.getErrorIdGenerator(
					 MARKET_PLACES_STORE_LOCATION,
					 MARKET_PLACES_ORG_NAME +1,
					 MIN_BLK_SIZE);
			  id = errorIdGenerator.getNextId(DOMAIN_TWO);
			}catch(IllegalArgumentException illegalArgumentException){
				isillegalArgumentException = true;
			}
		assertEquals(isillegalArgumentException,false);			 
		
	}
	
	/* Negative test case with block size less than minimum
	*/
	@Test
	public void testErrorIDGenerator_blck_size_lessthan_min() throws Exception {
		
		boolean isException = false;
		boolean deleteLibraryFlag = deleteTypeLibrary(MARKET_PLACES_STORE_LOCATION, MARKET_PLACES_ORG_NAME + 2);
		try{
			ErrorIdGenerator  errorIdGenerator = ErrorIdGeneratorFactory.getErrorIdGenerator(
        		MARKET_PLACES_STORE_LOCATION,
        		MARKET_PLACES_ORG_NAME + 2,
        		99);
		}catch(Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();//KEEPME
			isException = true;
		}
		assertEquals(isException,true);
		
	}
	
	/*
	 * Positive test case for two domains
	 */
	@Test
	public void testErrorID_Diff_Domains_diff_start_points() throws Exception {
		
		boolean deleteLibraryFlag = deleteTypeLibrary(MARKET_PLACES_STORE_LOCATION, MARKET_PLACES_ORG_NAME + 3);
	    ErrorIdGenerator  errorIdGenerator = ErrorIdGeneratorFactory.getErrorIdGenerator(
        		MARKET_PLACES_STORE_LOCATION,
        		MARKET_PLACES_ORG_NAME + 3);
        long id = errorIdGenerator.getNextId(DOMAIN_ONE);
        long id2 = errorIdGenerator.getNextId(DOMAIN_TWO);
        System.out.println("id  : " + id);
        System.out.println("id2 : " + id2);
        assertTrue((id2-id) == MIN_BLK_SIZE);
 		
	}
	
	/*
	 * Positive test case for two domains 
	 * Scenario: when last ID of first domain is end of the range for that ID
	 */
	@Test
	public void testErrorID_Diff_Domains() throws Exception {
		
		// Delete existing IDs.xml file
		boolean deleteLibraryFlag = deleteTypeLibrary(MARKET_PLACES_STORE_LOCATION, MARKET_PLACES_ORG_NAME + 4);
		System.out.println("Library Deletion status: " + deleteLibraryFlag);
		ErrorIdGenerator  errorIdGenerator = ErrorIdGeneratorFactory.getErrorIdGenerator(
        		MARKET_PLACES_STORE_LOCATION,
        		MARKET_PLACES_ORG_NAME + 4);

        long id = errorIdGenerator.getNextId(DOMAIN_ONE);
        long id2 = errorIdGenerator.getNextId(DOMAIN_TWO);

        for(int i=1 ;i<= 999; i++){
		errorIdGenerator.getNextId(DOMAIN_ONE);
		}
        long id3 = errorIdGenerator.getNextId(DOMAIN_ONE);
        assertFalse((id3-id)==(MIN_BLK_SIZE + 1));
        String newIdsxmlFile = getIDsxmlFilePath(MARKET_PLACES_STORE_LOCATION,
				"MarketPlaces4ErrorIDs.xml", null);
				
		String vanillaIdsxmlFile = getIDsxmlFilePath(getTestResrcDir().getAbsolutePath()+"/ErrorLibraryCodgen",
				"MarketPlaces4ErrorIDs.xml",
				null);
		boolean IdsXMLCompare = compareFiles(newIdsxmlFile, vanillaIdsxmlFile);
		assertTrue("IDs range for DOMAIN_ONE is incorrect!Check generated IDs.xml file. ", IdsXMLCompare);

 		
	}
	/*
	 * Positive test case: Generating ID in sequence
	 */
	@Test
	public void testBuildErrorIDGenerator_forSequenceGeneration() throws Exception {
		
		boolean deleteLibraryFlag = deleteTypeLibrary(MARKET_PLACES_STORE_LOCATION, MARKET_PLACES_ORG_NAME + 5);
		ErrorIdGenerator  errorIdGenerator = ErrorIdGeneratorFactory.getErrorIdGenerator(
        		MARKET_PLACES_STORE_LOCATION,
        		MARKET_PLACES_ORG_NAME +5);
        long id = errorIdGenerator.getNextId(DOMAIN_ONE);
        long id2 = errorIdGenerator.getNextId(DOMAIN_ONE);
        assertTrue((id2-id) == 1);
		
	}
	
	/*
	 * Positive test case for subsequent calls with same parameter 
	 */
	@Test
	public void testErrorIDGenerator_subsequent_calls_with_same_param(){
	
	boolean isArgException = false;
	try{
			String orgName = "testErrorIDGenerator_subsequent";
			Long l,m;
			boolean deleteLibraryFlag = deleteTypeLibrary(MARKET_PLACES_STORE_LOCATION, MARKET_PLACES_ORG_NAME + 6);
			ErrorIdGenerator  errorIdGenerator = ErrorIdGeneratorFactory.getErrorIdGenerator(
	        		MARKET_PLACES_STORE_LOCATION,
	        		MARKET_PLACES_ORG_NAME + 6);
			ErrorIdGenerator  errorIdGenerator2 = ErrorIdGeneratorFactory.getErrorIdGenerator(
	        		MARKET_PLACES_STORE_LOCATION,
	        		MARKET_PLACES_ORG_NAME + 6);
			l = errorIdGenerator.getNextId(DOMAIN_ONE);
			m = errorIdGenerator2.getNextId(DOMAIN_ONE);
		}catch(Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
			isArgException = true;
		}
		assertFalse(isArgException);
	}
	/*
	 * Negative test cases
	 * Test case where organization name is provided as null
	 */
	@Test
	public void testErrorIDGenerator_null_org_name(){
	
		boolean isArgException = false;
		try{
		ErrorIdGeneratorFactory.getErrorIdGenerator(
	        		MARKET_PLACES_STORE_LOCATION,
	        		null);
		}catch(Exception e){
			System.out.println("Test case failed due to value of organization name: null \n" + e.getMessage());
			e.printStackTrace();//KEEPME
			isArgException = true;
		}
		assertTrue(isArgException);
	
	}
	
	/*
	 * Negative test cases
	 * Test case where storage location value is provided as null
	 */
	@Test
	public void testErrorIDGenerator_null_location(){
		
		boolean isArgException = false;
		try{
			ErrorIdGeneratorFactory.getErrorIdGenerator(
	        		null,
	        		PAY_ORG_NAME);
		}catch(Exception e){
			System.out.println("Test case failed due to value of storage location: null \n" + e.getMessage());
			e.printStackTrace();
			isArgException = true;
		}
		assertTrue(isArgException);
		
	}
	
	/*
	 * Negative test cases
	 * Test case where value of storagre location is provided such that it does not exists
	 */
	@Test
	public void testErrorIDGenerator_location_ntExists(){
		
		boolean isArgException = false;
		try{
			ErrorIdGeneratorFactory.getErrorIdGenerator(
	        		"C:\\SOAtemp\\",
	        		PAY_ORG_NAME +1 );
		}catch(Exception e){
			System.out.println("TEST CASE FAILED!" + e.getMessage());
			e.printStackTrace();
			isArgException = true;
		}
		assertTrue(isArgException);
		
	}
	
	
	
	/*  TO DO 
	 * 
	 */
	@Test
	public void testErrorIDGenerator_id_overlap() throws Exception {
		
		
		Date date = new Date();
		long secs = date.getTime();
		boolean deleteLibraryFlag = deleteTypeLibrary(MARKET_PLACES_STORE_LOCATION, PAY_ORG_NAME + 2);
		System.out.println("Library Deletion status for :" + deleteLibraryFlag);
		ErrorIdGenerator  errorIdGenerator = ErrorIdGeneratorFactory.getErrorIdGenerator(
    		MARKET_PLACES_STORE_LOCATION,
    		PAY_ORG_NAME + 2,
    		100);
		String domainOne = "testBuildErrorIDGenerator_does_id_overlap_domain_one_"+ secs;
		String domainTwo = "testBuildErrorIDGenerator_does_id_overlap_domain_two_" + secs;
		long id1 = errorIdGenerator.getNextId(domainOne);
		long id2 = errorIdGenerator.getNextId(domainTwo);
		assertTrue((id2-id1) == 100);
		for(int i=1 ;i<= 99; i++){
			errorIdGenerator.getNextId(domainOne);
		}
		long id3 = errorIdGenerator.getNextId(domainOne);
		//System.out.println("Value of id 3 "+ id3);
		assertTrue((id3-id2) == 100);
	
	}
	
	/*
	 *  Testcase which calls build() where <UsedErrorBlocks> are nto present in 
	 *  the OrganizationalErrorIds.xml file
	 */
	@Test
	public void testErrorIDgen_NoUsedErrorBlockList() throws Exception {
		System.out.println("******testErrorIDgen_NoUsedErrorBlockList****");
		boolean deleteLibraryFlag = deleteTypeLibrary(MARKET_PLACES_STORE_LOCATION, MARKET_PLACES_ORG_NAME1);
		ErrorIdGenerator  errorIdGenerator = ErrorIdGeneratorFactory.getErrorIdGenerator(
				MARKET_PLACES_STORE_LOCATION,
        	MARKET_PLACES_ORG_NAME1,
        	1234);
		build();
		/* Validate if IDs.xml file is genrated at given location, 
		compare the files, see that it does not conatin <UsedErrorIdBlocks>
		*/
        String newIdsxmlFile = getIDsxmlFilePath(MARKET_PLACES_STORE_LOCATION,
				"MarketPlaces_NoUserBlocksErrorIDs.xml", null);
				
		String vanillaIdsxmlFile = getIDsxmlFilePath(getTestResrcDir().getAbsolutePath()+"/ErrorLibraryCodgen",
				"MarketPlaces_NoUserBlocksErrorIDs.xml",
				null);
		boolean IdsXMLCompare = compareFiles(newIdsxmlFile, vanillaIdsxmlFile);
		assertTrue("Generated xml may contain <UsedErrorBlocks>!Check generated IDs.xml file. ", IdsXMLCompare);
		System.out.println("**** End testErrorIDgen_NoUsedErrorBlockList ****");
	}
	
	/**
	 * @param args
	 * @throws ErrorIdGeneratorException 
	 * @throws IllegalStateException 
	 */
	public static void main(String[] args) throws IllegalStateException, ErrorIdGeneratorException {
		// TODO Auto-generated method stub
		ErrorIdGeneratorTests objErrorIdGeneratorTests = new ErrorIdGeneratorTests();
		try {
			objErrorIdGeneratorTests.testErrorIDGenerator_id_overlap();
			} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean compareFiles(String codegenPath, String goldCopyPath)throws Exception{
		boolean compareEqual = false;
		String codegenCopy = getFileContent(codegenPath);
		String vanillaCopy = getFileContent(goldCopyPath);

		boolean javaCheck = codegenCopy.contains("ReservedErrorBlockList");
		if(javaCheck){
			codegenCopy = "ReservedErrorBlockList "+codegenCopy.split("<ReservedErrorBlockList>")[1];
			vanillaCopy = "ReservedErrorBlockList "+vanillaCopy.split("<ReservedErrorBlockList>")[1];
		}
		if(codegenCopy.equals(vanillaCopy)){
			compareEqual = true;
		}
		if(codegenCopy.trim().length() == 0 || vanillaCopy.trim().length() == 0){
			compareEqual = false;
		}
		return compareEqual;
		
	}
	/*
	 * Get the file content to compare the generated and vanilla copy
	 */
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
	
	

	public ErrorIdGenerator build() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	/**
	 * Get Idsxml file path 
	 * @param projectRoot
	 * @param FileName
	 * @param extraParam
	 * @return
	 */
	public String getIDsxmlFilePath(String projectRoot, String FileName, String extraParam){
		String path = null;
		if(extraParam != null && extraParam.trim().length() != 0){
			path = projectRoot+ "/"+ FileName+"/extraParam";
		}else{
			path = projectRoot+ "/"+ FileName;
		}

		return path;
	}
	
	/**
	 * Specify the IDs.xml file to be deleted.
	 * @param IDxmlFile
	 * @return
	 */
	public boolean deleteTypeLibrary(String projectRoot, String FileName){
		File IdsxmlFile = new File (projectRoot+"/"+FileName + "ErrorIDs.xml");
		//System.out.println("Name of file" + IdsxmlFile);
		boolean flag = false;
		if(IdsxmlFile.exists())
		{
			flag  = IdsxmlFile.delete();
			
		}
		return flag;
	}
	
}

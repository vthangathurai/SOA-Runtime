package org.ebayopensource.turmeric.tools.codegen;

import static org.junit.Assert.*;
import junit.framework.TestCase;

/**
 * @author svaddi
 *
 */
public class InterfaceTest1 extends TestCase {
	/**
	 * @param name
	 */
	public InterfaceTest1(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*public void testInterface() {
		String antBinPath = System.getenv("ANT_HOME") + File.separator + "bin" + File.separator + "ant.bat";
		String output
			= Utils.runCommand(antBinPath + " -buildfile .//AntTests/build.xml testInterface");
		assertTrue(output.contains("BUILD SUCCESSFUL"));
		assertFalse(output.contains("Exception"));
		assertFalse(output.contains("class is not found"));
		assertFalse(output.contains("Could not find file"));
	}*/

	public static void testInterface() {
			/*try {
				AntRunnerTest ar = null;
				try {
					ar = new AntRunnerTest();
					System.out.println("**************************************************************************");
					System.out.println("testInterface");
					ar.init("./../../QAServices/SOAServicesTests/AntTests/build.xml","./../../QAServices/SOAServicesTests/AntTests");
					ar.runTarget("testInterface");
					ar = null;
				} 
				catch (Exception ex) {
					ar=null;
					fail("AntRunner ERROR: " + ex.getMessage());
					ex.printStackTrace();
				}
			}
			catch (Exception ioEx) {
				fail("ERROR: " + ioEx.getMessage());
				ioEx.printStackTrace();
			}*/
	}
}

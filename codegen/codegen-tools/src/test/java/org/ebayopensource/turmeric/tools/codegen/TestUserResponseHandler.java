/**
 * 
 */
package org.ebayopensource.turmeric.tools.codegen;

/**
 * @author rmohagaonkar
 *
 */
import org.ebayopensource.turmeric.tools.codegen.handler.UserResponseHandler;
/**
 * @author rmohagaonkar
 *
 */
public class TestUserResponseHandler  implements UserResponseHandler {
	

public TestUserResponseHandler() {}
		
		
		public boolean getBooleanResponse(String promptMsg) {
			return true;
		}
			

	}


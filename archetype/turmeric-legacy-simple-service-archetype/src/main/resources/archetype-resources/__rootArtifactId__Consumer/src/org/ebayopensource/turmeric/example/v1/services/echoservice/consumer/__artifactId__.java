#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package org.ebayopensource.turmeric.example.v1.services.echoservice.consumer;

import java.util.Date;

import org.ebayopensource.turmeric.example.v1.services.EchoRequest;
import org.ebayopensource.turmeric.example.v1.services.EchoResponse;
import org.ebayopensource.turmeric.example.v1.services.echoservice.gen.Shared${rootArtifactId}Consumer;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;

public class ${artifactId} extends Shared${rootArtifactId}Consumer{

	public ${artifactId}(String clientName)
			throws ServiceException {
		super(clientName);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws ServiceException{
		${artifactId} consumer = new ${artifactId}("${artifactId}");
		EchoRequest request = new EchoRequest();
		request.setEchoText("echo from consumer. Date = "+new Date());
		EchoResponse response = consumer.echo(request);
		System.out.println(response.getOutput());
	}
}

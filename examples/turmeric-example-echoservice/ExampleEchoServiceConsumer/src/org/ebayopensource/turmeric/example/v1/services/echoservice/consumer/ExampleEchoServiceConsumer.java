package org.ebayopensource.turmeric.example.v1.services.echoservice.consumer;

import java.util.Date;

import org.ebayopensource.turmeric.example.v1.services.EchoRequest;
import org.ebayopensource.turmeric.example.v1.services.EchoResponse;
import org.ebayopensource.turmeric.example.v1.services.echoservice.gen.SharedExampleEchoServiceV1Consumer;
import org.ebayopensource.turmeric.runtime.common.exceptions.ServiceException;

public class ExampleEchoServiceConsumer extends SharedExampleEchoServiceV1Consumer{

	public ExampleEchoServiceConsumer(String clientName)
			throws ServiceException {
		super(clientName);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws ServiceException{
		ExampleEchoServiceConsumer consumer = new ExampleEchoServiceConsumer("ExampleEchoServiceConsumer");
		EchoRequest request = new EchoRequest();
		request.setEchoText("echo from consumer. Date = "+new Date());
		EchoResponse response = consumer.echo(request);
		System.out.println(response.getOutput());
	}
}

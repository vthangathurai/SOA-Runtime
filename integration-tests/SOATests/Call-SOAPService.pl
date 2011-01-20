#*******************************************************************************
# Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#*******************************************************************************
use LWP::UserAgent;
use HTTP::Request;
use HTTP::Response;
use HTTP::Headers;
use Data::Dumper;

my $headerURL = "http://localhost:8080/ws/spf";

my $loop = 1;

my $header = new HTTP::Headers;
$header->push_header('X-TURMERIC-OPERATION-NAME' =>'test');
$header->push_header('X-TURMERIC-SERVICE-NAME' =>'TestService');
$header->push_header('X-TURMERIC-MESSAGE-PROTOCOL' =>'SOAP11');

my $reqxml = qq|<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
	<soapenv:Body>
		test=Sree
	</soapenv:Body>
</soapenv:Envelope> |;

my $stats = undef;
my $avgTime = 0;
my $totalTime = 0;

print "Sending to SOA service: " , $headerURL, "\n";
print "Loop: " , $loop, "\n";
for(my $i=0; $i<$loop; $i++) {
	my $req = new HTTP::Request "POST", $headerURL, $header;
	$req ->add_content($reqxml);
	print $reqxml;

	my $ua = LWP::UserAgent->new();
	$ua->agent("Mozilla/5.0 Firefox/2.0.0.4");
	my $response = $ua->request($req);
	my $content = undef;

	if (defined($response)) { 
	  $content = $response->content();
	}

	if ($response->is_error()) {
		print "%s\n", $response->status_line;
		my $responseheader = $response->header();
		my $content = $response->content();
		print $content;
	}
	else {
		print $content;
	}
}

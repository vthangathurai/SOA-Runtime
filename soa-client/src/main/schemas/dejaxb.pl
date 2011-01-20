#*******************************************************************************
# Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#*******************************************************************************
my $file = shift @ARGV;

open(FILE, $file) or die "Can't open file: $file\n";
my $skip = 0;
while (<FILE>) {
	if (/\@XmlType.*propOrder = {/) {
		$skip = 1;
		next;
	}
	if (/\@XmlElements.*{/) {
		$skip = 1;
		next;
	}
	if ($skip) {
		if (/}\)/) {
			$skip = 0;
		}
		next;
	
	}
	next if ($. <= 6);
	next if /\* <p>The following/;
	next if /<pre>/;
	next if /<\/pre>/;
	next if /import javax.xml.bind/;
	next if /\*\s+&lt;/;
	next if /^\s*@/;
	print;
}

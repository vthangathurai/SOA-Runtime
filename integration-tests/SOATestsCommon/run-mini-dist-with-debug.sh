#*******************************************************************************
# Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#*******************************************************************************
#!/bin/bash

java \
    -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005 \
   -jar target/SOATestsCommon-1.0.0.SNAPSHOT.jar


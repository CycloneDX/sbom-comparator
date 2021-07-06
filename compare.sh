#!/bin/bash
# Example will compare two Sboms, file 1 (-f1) OrgSbom.xml to file 2 (-f2) ModifiedSbom.xml and produce output file (-o) output in a test directory in the xml format (-f). Will additionally produce html output file (-t). 
# ./compare.sh -f1 ./test/OrgSbom.xml -f2 ./test/ModifiedSbom.xml -o ./test/output -f xml -t ./test/quickOutput

if [ -z "$JAVA_HOME" ]
then
	echo "\$JAVA_HOME is empty setting it now."
	export JAVA_HOME=./jdk-11
	export CLASSPATH=$JAVA_HOME
	export PATH=/bin:/usr/bin:$JAVA_HOME/bin
else
	echo "\$JAVA_HOME is already set."
fi

echo $JAVA_HOME


$JAVA_HOME/bin/java -Xms256m -Xmx2048m -Dlog4j.configuration=file:./logging/log4j.xml -jar ./target/sbomcomparator-[0-9].[0-9].[0-9].jar "$@"

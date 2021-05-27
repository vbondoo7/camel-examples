#!/bin/sh
CONTEXT=$1
CONFIG_ENV=$2
echo Context value: $CONTEXT
echo Config_env value: $CONFIG_ENV
exec java -Djavax.xml.transform.TransformerFactory=net.sf.saxon.TransformerFactoryImpl $CONTEXT $CONFIG_ENV -jar /deployments/${project.artifactId}-${project.version}.jar

echo %JAVA_HOME%\lib\security\cacerts

"%JAVA_HOME%\bin\keytool" -import -trustcacerts -keystore "%JAVA_HOME%\lib\security\cacerts" -storepass changeit -noprompt -alias officedepotroot -file OfficedepotRoot.cer
"%JAVA_HOME%\bin\keytool" -import -trustcacerts -keystore "%JAVA_HOME%\lib\security\cacerts" -storepass changeit -noprompt -alias officedepotintermediate -file OfficedepotIntermediate.cer
"%JAVA_HOME%\bin\keytool" -import -trustcacerts -keystore "%JAVA_HOME%\lib\security\cacerts" -storepass changeit -noprompt -alias qdrdevleaf -file qdrDevLeaf.cer

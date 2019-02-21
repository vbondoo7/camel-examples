buildDeployService {
	email = "eaiteam@officedepot.com"
	namespace = "eai"
	timeoutSeconds = "5"
	maxCpu = "500m"
	maxMemory = "1Gi"
	appPort="8085"
	deployLocations = "aws"
	buildCommands = [
		"mvn install --settings settings.xml -B",
		"cp ./target/extra-resources/Dockerfile .",
		"cp ./target/extra-resources/docker/entrypoint.sh ./docker"
	]
	metaData = [
    'clarityProjectId': 'PR003529',
    'teamName': 'Enterprise Application Integration',
    'SLA': 'Application',
    'serviceNowClass': 'Application',
    'serviceNowAppName': 'ctu-soap-service',
    'serviceOwnerEmail': 'eaiteam@officedepot.com', 
	'appSupportTeamEmail': 'eaiteam@officedepot.com', 
	'devManagerEmail': 'vip-boss@officedepot.com'
   ]
	envs = [  
		'{"secretEnv":{"name": "JWT_SECRET_KEY", "secretKey": "JWT_SECRET_KEY", "secretName": "jwt-secret"}}',
		'{"secretEnv":{"name": "ERRORANDAUDIT_MESSAGING_PROVIDER_USERNAME", "secretKey": "ERRORANDAUDIT_MESSAGING_PROVIDER_USERNAME", "secretName": "errorandaudit-messaging-provider"}}',
		'{"secretEnv":{"name": "ERRORANDAUDIT_MESSAGING_PROVIDER_PASSWORD", "secretKey": "ERRORANDAUDIT_MESSAGING_PROVIDER_PASSWORD", "secretName": "errorandaudit-messaging-provider"}}'
	]
    volumes = [
        '{"configPathVolume":{"mountPath": "/eai/security/basicauth", "configName": "basicauth"}}'
    ]
	skipPerformanceTest = "true"
	skipDeployDev = "false"
}

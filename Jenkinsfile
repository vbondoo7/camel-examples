buildDeployService {
	email = "eaiteam@officedepot.com"
	namespace = "eai"
	timeoutSeconds = "5"
	maxCpu = "500m"
	maxMemory = "1Gi"
	healthUrl = "/eaiapi/health"
	appPort="8085"
	deployLocations = "aws"
	buildCommands = [
		"mvn install --settings settings.xml -Dmaven.test.skip=true -B",
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
		'{"secretEnv":{"name": "ERRORANDAUDIT_MESSAGING_PROVIDER_PASSWORD", "secretKey": "ERRORANDAUDIT_MESSAGING_PROVIDER_PASSWORD", "secretName": "errorandaudit-messaging-provider"}}',
		'{"secretEnv":{"name": "EAI_SERVICE_USERNAME_INTERNAL", "secretKey": "EAI_SERVICE_USERNAME_INTERNAL", "secretName": "eai-services-credentials-internal-use-only"}}',
		'{"secretEnv":{"name": "EAI_SERVICE_PASSWORD_INTERNAL", "secretKey": "EAI_SERVICE_PASSWORD_INTERNAL", "secretName": "eai-services-credentials-internal-use-only"}}'
	]
    volumes = [
        '{"configPathVolume":{"mountPath": "/eai/security/basicauth", "configName": "basicauth"}}'
    ]
	skipPerformanceTest = "true"
	skipDeployDev = "false"
	livenessProbeType = "process-java"
	readinessProbeType = "process-java"
	initialDelaySeconds = "30" 
	skipPerformanceTest = "true"
}

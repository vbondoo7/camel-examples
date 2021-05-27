buildDeployService {
	email = "vsaxena.lko@gmail.com"
	namespace = "integration"
	timeoutSeconds = "5"
	maxCpu = "2000m"
	maxMemory = "6Gi"
	healthUrl = "/api/health"
	appPort="8085"
	deployLocations = "ocp"
	buildCommands = [
		"mvn package --settings settings.xml -B",
		"cp ./target/extra-resources/Dockerfile .",
		"cp ./target/extra-resources/docker/entrypoint.sh ./docker"
	]
	envs = [  
		'{"secretEnv":{"name": "JWT_SECRET_KEY", "secretKey": "JWT_SECRET_KEY", "secretName": "jwt-secret"}}',
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

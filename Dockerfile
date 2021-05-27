FROM openjdk:11
WORKDIR /
USER root
RUN rm -f /etc/localtime && ln -s /usr/share/zoneinfo/America/New_York /etc/localtime
ADD ca/* /etc/pki/ca-trust/source/anchors/
RUN update-ca-trust
RUN mkdir /deployments || true
ADD ./target/${project.artifactId}-${project.version}.jar /deployments
ENV JAVA_APP_DIR=/deployments
COPY docker/entrypoint.sh ./
USER root
RUN chmod +x *.sh
ADD ca/* /etc/pki/ca-trust/source/anchors/
RUN update-ca-trust
USER jboss
RUN ls
ENTRYPOINT ["/entrypoint.sh"]
===========================================

FROM maven:3-jdk-11 as builder
COPY src /usr/src/app/src  
COPY pom.xml /usr/src/app  
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:11 as runner
COPY --from=builder /usr/src/app/target/OdpGsdiGKEIngest-0.0.1-SNAPSHOT.jar service.jar
ENTRYPOINT ["java","-jar","/service.jar"]

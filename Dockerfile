FROM 729964090428.dkr.ecr.us-east-1.amazonaws.com/jboss-fuse-6/fis-java-openshift:latest
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
EXPOSE 8085
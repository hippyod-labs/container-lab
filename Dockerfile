# (a)
FROM docker.io/eclipse-temurin:17-jre-ubi9-minimal

# (b)
ARG SPRING_PROFILES_ACTIVE=prod
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}

# (c)
USER root

# (d)
RUN <<EOF
    set -ex
    microdnf upgrade -y --nodocs --refresh
    microdnf clean all
    mkdir -p /mnt/logs
    chmod 777 /mnt/logs
EOF

# (e)
ENV JAVA_APP_DIR=/app
COPY target/*.jar ${JAVA_APP_DIR}/app.jar

# (f)
EXPOSE 8081

# (g)
USER 1001

# (h)
WORKDIR ${JAVA_APP_DIR}

# (i)
ENTRYPOINT ["java", "-jar", "app.jar"]
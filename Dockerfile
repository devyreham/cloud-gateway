FROM docker.io/alpine/java:21-jdk

WORKDIR /app
ARG USERNAME=restricted
RUN addgroup --gid 988 $USERNAME \
    && adduser --u 999 -D -G $USERNAME $USERNAME
RUN mkdir ./logs
RUN mkdir ./config
ARG JAR_FILE=build/libs/*.war
COPY ${JAR_FILE} cloud-gateway.war

RUN chown -R $USERNAME:$USERNAME $(pwd)
USER $USERNAME

VOLUME config

ENTRYPOINT ["java","-Xmx128m","-Xms128m", "-Dfile.encoding=UTF-8","-jar","cloud-gateway.war"]

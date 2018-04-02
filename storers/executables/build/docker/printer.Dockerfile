FROM openjdk:8-jre-alpine

COPY *.jar /app.jar

ENTRYPOINT ["/usr/bin/java", "-cp", "/app.jar", "edu.jhu.hlt.concrete.storers.executables.PrintStore"]

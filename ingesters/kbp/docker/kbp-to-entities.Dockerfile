FROM openjdk:8-jre-alpine

COPY target/concrete*uberjar.jar /app.jar

ENTRYPOINT ["/usr/bin/java", "-cp", ".:/app.jar", "edu.jhu.hlt.concrete.ingesters.kbp2017.KBReader"]
CMD ["--help"]

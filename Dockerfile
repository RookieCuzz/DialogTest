FROM eclipse-temurin:21-jre-alpine
COPY target/DialogTest-1.0-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]

FROM openjdk:17-oracle
EXPOSE 8080
ARG JAR_FILE=WebApp/Backend/target/WebBackend-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ADD WebApp/Backend/src/main/java/com/example/Mock/StartingClasses/WebScraping/PlayerData.txt PlayerData.txt
ENTRYPOINT ["java","-jar","/app.jar"]
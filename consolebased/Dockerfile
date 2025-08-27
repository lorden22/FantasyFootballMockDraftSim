FROM openjdk:17-jdk-slim

WORKDIR /app

COPY consolebased/src/main/java/com/example /app/src/main/java/com/example
COPY shared/src/main/java/com/example/common /app/src/main/java/com/example/common
COPY shared/src/main/java/com/example/console /app/src/main/java/com/example/console

RUN apt-get update -y && \
    apt-get install -y mariadb-client wget && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Download MySQL JDBC driver
RUN wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar -O /app/mysql-connector-java.jar

RUN mkdir -p /app/bin
RUN mkdir -p /app/logs
RUN chmod 755 /app/logs

RUN find src/main/java -name "*.java" > sources.txt && \
    javac -cp /app/mysql-connector-java.jar -d bin @sources.txt

CMD ["java", "-cp", "bin:/app/mysql-connector-java.jar", "com.example.MockDraftDriver"]
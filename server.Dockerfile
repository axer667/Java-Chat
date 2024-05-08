FROM khipu/openjdk17-alpine

EXPOSE 5000
WORKDIR app

ADD ./server server
ADD ./postman postman
ADD ./settings settings

CMD ["java", "-jar", "server/target/server-1.0-SNAPSHOT.jar"]
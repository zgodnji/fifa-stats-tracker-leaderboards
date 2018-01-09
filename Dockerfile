FROM openjdk:8-jre-alpine

RUN mkdir /app

WORKDIR /app

ADD ./target/games-1.0.0.jar /app

EXPOSE 8082

CMD ["java", "-jar", "games-1.0.0.jar"]

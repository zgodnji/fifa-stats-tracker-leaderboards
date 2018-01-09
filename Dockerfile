FROM openjdk:8-jre-alpine

RUN mkdir /app

WORKDIR /app

ADD ./target/leaderboards-1.0.0.jar /app

EXPOSE 8084

CMD ["java", "-jar", "leaderboards-1.0.0.jar"]

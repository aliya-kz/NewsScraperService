FROM openjdk:17

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY target/NewsScraperService-0.0.1.jar /usr/src/app

ENTRYPOINT ["java","-jar","NewsScraperService-0.0.1.jar"]
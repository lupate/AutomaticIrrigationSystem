FROM  amazoncorretto:11-alpine-jdk as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN ./mvnw install -DskipTests

FROM  amazoncorretto:11-alpine-jdk
ARG JAR_FILE=workspace/app/target/*.jar
COPY --from=build ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]

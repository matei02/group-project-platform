FROM openjdk:17-jdk-slim

WORKDIR /groupproject_backend

COPY groupproject_backend/build/libs/groupproject_backend-0.0.1-SNAPSHOT.jar groupproject_backend.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "groupproject_backend.jar"]

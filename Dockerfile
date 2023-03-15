FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean assemble --no-daemon

FROM amazoncorretto:11
RUN yum install -y shadow-utils && yum clean all && useradd javauser
COPY --chown=javauser:javauser --from=build /home/gradle/src/build/libs/data-gpt-0.0.1-SNAPSHOT.jar app.jar
USER javauser
ENTRYPOINT ["java","-jar","/app.jar"]

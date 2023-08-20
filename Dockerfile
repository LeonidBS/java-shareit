FROM amazoncorretto:11-alpine-jdk
#ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9090
#COPY target/*.jar app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]
ENV TZ=Europe/Moscow
COPY /server/src/main/resources/schema.sql /docker-entrypoint-initdb.d/
EXPOSE 5432
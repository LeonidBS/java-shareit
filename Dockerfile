FROM amazoncorretto:11-alpine-jdk
#ENV TZ=Europe/Moscow
COPY /server/src/main/resources/schema.sql /docker-entrypoint-initdb.d/
EXPOSE 5432
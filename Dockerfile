FROM eclipse-temurin:17-jdk AS build

WORKDIR /workspace
COPY mytest.java .
RUN javac mytest.java

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /workspace/mytest.class .
USER 10001:10001
ENTRYPOINT ["java", "mytest"]

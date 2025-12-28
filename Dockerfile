FROM openjdk:17-jdk-slim

LABEL maintainer="shyamgupta2000"
LABEL application="payment-processing-system"

WORKDIR /app

COPY target/payment-processing-system-1.0.0.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms512m -Xmx1024m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
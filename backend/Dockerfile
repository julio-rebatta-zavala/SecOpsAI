FROM maven:3.9.12-eclipse-temurin-25-alpine AS build
WORKDIR /workspace
COPY pom.xml .
# RUN mvn -q -B dependency:go-offline
COPY src src
RUN mvn -q -B -DskipTests package

FROM eclipse-temurin:25-jre-alpine

# Instalar y actualizar certificados SSL/TLS
RUN apk add --no-cache ca-certificates java-cacerts openssl && update-ca-certificates

# Sincronizar los certificados del sistema con Java
RUN ln -sf /etc/ssl/certs/java/cacerts "$JAVA_HOME/lib/security/cacerts"

# Crear usuario no privilegiado
RUN addgroup -S secops && adduser -S -G secops -u 10001 secops

WORKDIR /app
COPY --from=build /workspace/target/secops-backend-*.jar app.jar
USER 10001
EXPOSE 8080

ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-Djava.security.egd=file:/dev/urandom","-jar","/app/app.jar"]

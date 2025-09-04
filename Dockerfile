# Base image (official, reliable)
FROM eclipse-temurin:17-jdk

# Set working directory inside container
WORKDIR /app

# Copy project files
COPY . .

# Give permission to mvnw
RUN chmod +x mvnw

# Build the jar (skip tests)
RUN ./mvnw clean package -DskipTests

# Specify jar file (replace with your actual jar name)
ARG JAR_FILE=target/hotel-0.0.1-SNAPSHOT.jar

# Expose default Spring Boot port
EXPOSE 8080

# Command to run Spring Boot app
ENTRYPOINT ["java","-jar","/app/target/hotel-0.0.1-SNAPSHOT.jar"]

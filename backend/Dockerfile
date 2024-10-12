# Use Gradle JDK 21 image for both building and running
FROM gradle:8.10.2-jdk21 AS builder

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy the backend project files into the container
COPY . .

# Step 4: Run the Gradle build using the bootJar task
RUN gradle bootJar

# Step 5: Use a lightweight JRE image to run the Spring Boot application
FROM eclipse-temurin:21-jre-alpine

# Step 6: Set the working directory for the runtime container
WORKDIR /app

# Step 7: Copy the built JAR from the builder stage to the runtime stage
COPY --from=builder /app/build/libs/*.jar ./app.jar

# Step 8: Expose the port the Spring Boot app will run on
EXPOSE 8080

# Step 9: Run the Spring Boot application
CMD ["java", "-jar", "/app/app.jar"]

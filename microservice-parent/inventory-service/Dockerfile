#-------------
#BUILD STAGE
#-------------

FROM gradle:8-jdk-21-and-22-alpine AS builder
#FROM gradle:8-jdk22-alpine AS builder  // teacher use this but I idt have that option

#Copy the application files from the host machine to the image filesystem
#we use the '--chown=gradle' flag to ensure proper file permisions for the Gradle user.
COPY --chown=gradle:gradle . /home/gradle/src

#Set the working directory inside the container to /home/gradle/src
#All future commands will be executed from this directory
WORKDIR /home/gradle/src


#Run the Gradle build inside the container, skipping the tests (-x test).
#This command compiles the code, resolves dependencies and packages the application as a .jar file
#Note: The command applies to the image only, not to the host machine.
RUN gradle build -x test

#-------------
#PACKAGE STAGE
#-------------

#Start from a lightweight OpenJDK 22 almine image. This will be our runtime image.
#Alpine images are much smaller, which helps keep the final image size down.
FROM openjdk:21-jdk


#create a directory inside the container where the application will be stored.
#This directory is where we will place the packaged .jar file built in the previous stage.
RUN mkdir /app


#copy the built .jar file from the build stage to the /app directory in the final image
# we use the '--from=builder' instruction to reference the "builder" stage.
COPY --from=builder /home/gradle/src/build/libs/*.jar /app/inventory-service.jar



# Expose port 8081 to allow communication with the containerized application
#Expose does not actually make the port accessible to the host machine; it's documentation for the image.
EXPOSE 8085


#The entrypoint instruction defines the command to run when the container starts
#IN this case, we are telling Docker to run the Java command with the packaged JAR file
ENTRYPOINT ["java", "-jar", "/app/inventory-service.jar"]

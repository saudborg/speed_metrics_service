# Java Spring Application for fetching asynchronously an API and provide one response

## Overview
The following repo contains a simple spring boot application which using libs, provide the client a merged object when a user is given.
The application will fetch the user data and the posts from this user and return in one endpoint

## Guidelines
Run the example of connecting to OpenFin and creating applications

1. Clone this repository
2. Make sure you have maven installed https://maven.apache.org/install.html
3. Go to the directory you cloned
4. ```mvn clean install```
5. ```cd target```
6. ```java -jar asyncFetchApi-1.0-SNAPSHOT.jar```
7. Access http://localhost:8080/swagger-ui/index.html for swagger documentation

## Usage

Source code for the example is located in /src/main/java/com/openfin/desktop/demo/OpenFinDesktopDemo.java.  The followings overview of how it communicates with OpenFin Runtime with API calls supported by the Java adapter:

```curl -X GET "http://localhost:8080/posts/{userId}" -H "accept: */*"```

Change the {userId} for the id you desire.

A few examples:

```curl -X GET "http://localhost:8080/posts/1" -H "accept: */*"```

```curl -X GET "http://localhost:8080/posts/55" -H "accept: */*"```

## Tech Stack

- Java 11
- Maven
- Spring boot
- Lombok
- Swagger
- Junit
- Mockito
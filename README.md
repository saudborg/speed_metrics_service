# Java Spring Application for simulating a Speed Metrics Service

## Overview
The following repo contains a simple spring boot application which using libs, provide the client a possibility to add 
new metrics and query from machines which are pre-loaded when the application starts.

The application will read the machines.csv file which contains information about all the machines, store in a memory db
and using endpoints, expose the function to see data from each machine

## Guidelines
MachineDataServiceTest
1. Clone this repository
2. Make sure you have maven installed https://maven.apache.org/install.html
3. Go to the directory you cloned
4. ```mvn clean install```
5. ```cd target```
6. ```java -jar metrics.service-1.0-SNAPSHOT.jar```
7. Access http://localhost:8080/swagger-ui/index.html for swagger documentation

## Usage

If a different dataset of machines is needed, please replace the file src/main/resources/machines.csv
Once you have started the application you can make use of the swagger or simply use curl functions e.g:

```curl -X GET "http://localhost:8080/machine/{machineKey}" -H "accept: */*"```
```curl -X GET "http://localhost:8080/machine/ajoparametrit/lastestParameters" -H "accept: */*"```
```curl -X GET "http://localhost:8080/machine/summary/1" -H "accept: */*"```

Change the {machinekey} for the id you desire.

A few examples:

```curl -X GET "http://localhost:8080/machine/ajoparametrit" -H "accept: */*"```

```curl -X GET "http://localhost:8080/machine/anyInvalidKey" -H "accept: */*"```

## Tech Stack

- Java
- Maven
- Spring boot
- Lombok
- Swagger
- Junit
- Mockito
- h2
- opencsv

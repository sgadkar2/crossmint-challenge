# Crossmint Megaverse Challenge

This is a simple Spring Boot application that creates a megaverse based on the goal provided by the Crossmint API.

## How to run the application

1. Clone the repository
2. Run the following command to build the docker container
    sudo docker build --tag=megaverse:latest .
3. Run the following command to run the docker container
    sudo docker run -p8080:8080 megaverse:latest
4. I have created REST API for creating the megaverse. You can call the API using Postman or by calling the following Curl command
    curl --location --request POST 'http://localhost:8080/create-megaverse'

Note: The application is configured by default to run on port 8080. You can change the port in the `application.properties` file.

## Important Information: The main logic is in the `MegaverseServiceImpl` and `MegaverserApiClientImpl` class in ServiceImpl package. The configuration for the retry mechanism is present in the `AppConfig` class in Configuration package


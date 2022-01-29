# SAGA Pattern Implementation for Microservices

Example of SAGA patter for microservice communication:
1. Booking
   1.1 `booking`: Accept booking request (`http post :8080/booking name=test price=3`)
   1.2 `booking`: Add booking into DB with PENDING status (local map for simplicity)
   1.3 `booking`: Send booking event via message queue (ActiveMQ)  
2. Payment
   2.1 `payment`: Receive payment message via message queue
   2.2 `payment`: Make payment
   2.3 `payment`: Send action status message via message queue
3. Complete booking
   3.1 `booking`: In case of failed payment delete booking from DB
   3.2 `booking`: In case of succeeded payment mark booking as completed

## Usage

1. Start Active MQ service (in project root dir):
    ```shell
    $ cd docker
    $ docker compose up -d
    ```
2. Build the project (in project root dir):
   ```shell
   $ mvn clean package
   ```
3. Start `booking` service (in project root dir):
   ```shell
   $ cd booking
   $ mvn spring-boot:run
   ```
4. Start `payment` service (in project root dir):
   ```shell
   $ cd payment
   $ mvn spring-boot:run
   ```
5. Send booking request:
   ```
   POST http://localhost:8080/booking {"name": "test", "price": 100500}
   ```
6. Watch logs
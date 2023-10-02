# Employee Service

### This is Employee Service app that was created based on following requirements:

1. Create an employee with the following properties
     - Uuid (generated automatically)
     - E-mail
     - Full name (first and last name)
     - Birthday (format YYYY-MM-DD)
     - List of hobbies (for example, "soccer", "music", etc)
2. Get a list of all employees
3. Get a specific employee by uuid
4. Update an employee
5. Delete an employee
6. Whenever an employee is created, updated or deleted, an event related to this
   action must be pushed in some message broker (i.e, Kafka, RabbitMq, etc)
7. Documentation is an important aspect of software development, please provide some
   sort of API documentation
8. Run the database you’ve chosen to store the employee's data in a Docker container
9. Include some unit and integration tests

### Following parts wasn't implemented:

1. Add authentication to access create, update and delete employee endpoints
2. A service publishes an event that affects the Employee service. Such an event is
   published in a queue/topic named employee-events with payload as example provided
   below. Please implement the code that consumes that event removing the affected
   employee record

### Implementation details

1. Application build with Java 17, SpringBoot, Docker and maven
2. Postgres database is used to store data and RabbitMQ is used as message broker, both are running in Docker containers
3. Testcontainers is used to run Postgres database for integration testing
4. I didn't come up with how to run RabbitMQ in testcontainers, so dockerized RabbitMQ 
   should be available during integration testing

## How to run

### Run application
`mvn springboot:run` - to run application
- First run could be slow because of downloading docker images
- Application is listening on port 8080
- API documentation is available on `http://localhost:8080/swagger-ui/index.html#/`

### Run tests
Run rabbitmq manually docker-compose before running tests

`docker compose -f docker-compose.yml -p employee-service up rabbitmq`

`mvn test` - to run tests

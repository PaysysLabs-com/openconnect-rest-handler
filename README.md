# OpenConnect System

OpenConnect is a modular transaction processing system designed for secure and validated communication between Digital Financial Service Providers (DFSPs). The system comprises three main components: **Web Service Interface**, **Queue Processor**, and **Rest Handler**, along with front-end and mobile interfaces for back-office and end-user interactions.

## Components

- **Rest Handler**  
   - Creates and sends API requests based on host API specifications.
   - Handles authentication if required and sends the response to the Queue Processor.
   - [Introduction to REST APIs](https://restfulapi.net/)  
   - [Unirest Documentation](http://kong.github.io/unirest-java/)

## Tech Stack & Tools



- **Language & Framework**: Java with [Spring Boot](https://spring.io/projects/spring-boot) (REST APIs, JPA, Hibernate)
   - [Install Maven](https://maven.apache.org/install.html) for building and running the project.
   - [Introduction to JPA and Hibernate](https://www.baeldung.com/hibernate-5-jpa)
- **Queue System**: [RabbitMQ](https://www.rabbitmq.com/) (supports SSL for production)  
   - Communicates between components through RPC and Web Service Interface.  
   - [Install RabbitMQ](https://www.rabbitmq.com/download.html)
   - [RabbitMQ RPC](https://www.rabbitmq.com/tutorials/tutorial-six-java.html)
- **Database**: [MySQL](https://www.mysql.com/)
   - [Download MySQL](https://dev.mysql.com/downloads/installer/) for local development.
   - Ensure database configurations are updated in `application.yaml`.
- **File Logging**: [Log4j](https://logging.apache.org/log4j/2.x/) for structured logging.
   - [Guide to Log4j setup](https://www.baeldung.com/log4j-2-configuration)
- **API Communication**:
   - [Unirest](http://kong.github.io/unirest-java/) for REST and SOAP APIs (configurable timeouts and concurrency).
   - Handles REST API and authentication requests in the Rest Handler.
- **Caching**: [Redis](https://redis.io/) for performance optimization.
   - [Installing Redis](https://redis.io/download)
   - [Guide to Redis Caching with Spring Boot](https://www.baeldung.com/spring-boot-redis-cache)
- **API Documentation**: [Swagger](https://swagger.io/) for API documentation and exploration.
   - [Set up Swagger in Spring Boot](https://www.baeldung.com/spring-rest-openapi-documentation)
- **Security & Authentication**:
   - IP Address Validation for trusted access.
   - One Token per Request Authentication to ensure secure transactions.

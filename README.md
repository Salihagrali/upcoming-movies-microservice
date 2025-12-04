# Upcoming Movies App (Microservices)

A backend system built to track upcoming movies and manage user favorites. This project serves as a playground for implementing event-driven microservices patterns using the Spring ecosystem.

## Tech Stack

* **Core:** Java, Spring Boot 3

* **Spring Cloud:** Gateway, Eureka (Discovery), Config Server, OpenFeign

* **Messaging:** Apache Kafka (Asynchronous communication between services)

* **Caching:** Redis

* **Containerization:** Docker & Docker Compose

* **Database:** PostgreSQL

## Architecture

The system is composed of the following microservices:

* **API Gateway:** Single entry point for routing and filtering requests.

* **Config Server:** Centralized configuration management for all services.

* **Eureka Server:** Service registry and discovery.

* **Movie Service:** Manages movie data. Publishes events to Kafka when users interact with movies.

* **Favorite Service:** Consumes Kafka events to manage user favorite lists asynchronously.

* **User Service:** Handles user registration and authentication details.

## Status

This project is currently under active development. Future updates will include advanced monitoring, frontend, and extended API features.

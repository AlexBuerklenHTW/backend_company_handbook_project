**1.) What is this Project about?**

This Project represents an MVP for a company handbook, where user's can view, create and edit articles. An admin role 
can review those articles and approve or deny them.

**2.) What requirements are needed to build the backend?**

- Java 17
- Docker 27.4.0
- Maven 3.9.6

**3.) How to start the backend?**

3.1.) First you need to run ```docker-compose.yml```, to create a docker image and start the docker container:
```shell
docker compose up
```

3.2.) Start Spring Boot backend:
```shell
mvn spring-boot:run
```
# spring-demos

## Purpose

- Experiment with Spring Boot, Hibernate, Solace, Google Protocol Buffers, JAXB and WebFlux

## Learning resources

- Solace HA docker-compose
  - https://github.com/SolaceLabs/solace-ha-docker-compose
- Maven Protocol Buffers Plugin
  - https://www.xolstice.org/protobuf-maven-plugin/index.html
- JUnit 5 docs
  - https://junit.org/junit5/docs/current/user-guide
- Mockito docs
  - https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
- Flyway docs
  - https://flywaydb.org/documentation

## Create an environment variable for Sonar Scanner & pom.xml usage 
 
- Windows configuration

```cmd
# Set a variable
setx SONAR_SPRING_DEMOS_TOKEN "<token>"

# View a variable
reg query HKEY_CURRENT_USER\Environment

# Unset a variable
reg delete HKEY_CURRENT_USER\Environment /v SONAR_SPRING_DEMOS_TOKEN /f

# Check a variable
mvn clean install verify
```

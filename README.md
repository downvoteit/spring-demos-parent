# spring-demos-parent

## Purpose

- Experiment with various tech

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

```
# Set a variable
setx SPRING_DEMOS_SONAR_URL <url>
setx SPRING_DEMOS_SONAR_TOKEN <token>

# View a variable
reg query HKEY_CURRENT_USER\Environment

# Unset a variable
reg delete HKEY_CURRENT_USER\Environment /v SPRING_DEMOS_SONAR_URL /f
reg delete HKEY_CURRENT_USER\Environment /v SPRING_DEMOS_SONAR_TOKEN /f

# Check a variable
mvn clean install
```

## Possible schema

### Ledger

- Server 7002 Angular
- Server 7003 WebFlux + WebClient
- Server 7004 WebFlux + Solace
- Server 7005 WebFlux + Solace
- Server 7006 Redis
- Server 7007 PostgreSQL 

### Analytics

- Server 8002 Angular
- Server 8003 WebFlux + Solace
- Server 8004 PostgreSQL

```
               Server 7002 
                 Angular
                    |
               Server 7003 
            WebFlux + WebClient
                    |
               Server 7004                                  Server 8002 
             WebFlux + Solace                                 Angular
                    |                                            |
                    ----------------------------------------------
                    |                                            |
               Server 7005                                   Server 8003
             WebFlux + Solace                              WebFlux + Solace 
                    |                                            |
         -----------------------                            Server 8004
         |                     |                             PostgreSQL
    Server 7006            Server 7007                       
       Redis                PostgreSQL  
```



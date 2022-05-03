# spring-demos-parent

## Purpose

- Experiment with various tech

## Learning resources

- Solace docs
  - https://docs.solace.com/Solace-PubSub-Platform.htm
- Solace core concepts
  - https://docs.solace.com/Basics/Core-Concepts.htm
- Solace endpoints
  - https://docs.solace.com/Basics/Endpoints.htm#Durable_Endpoint_Access_Types
- Solace JCSMP best practices
  - https://docs.solace.com/Solace-PubSub-Messaging-APIs/API-Developer-Guide/Java-API-Best-Practices.htm
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

## Project schema

### Primary

#### Ledger (OLTP)

- Server 7002 Angular
- Server 7003 WebFlux + WebClient
- Server 7004 WebFlux + Solace
- Server 7005 WebFlux + Solace
- Server 7006 Redis
- Server 7007 PostgreSQL 

### Secondary

#### Analytics (OLAP)

- Server 8002 Angular
- Server 8003 WebFlux + Solace
- Server 8004 PostgreSQL

### Tertiary 

#### Hibernate

- Server 9001 WebFlux
- Server 9002 PostgreSQL

#### SonarQube

- Server 20001/2 SonarQube
- Server 20003 PostgreSQL

#### Solace

- Server 8080 HAProxy
- Server 212 Solace Primary
- Server 312 Solace Backup
- Server 412 Solace Monitoring

```
               Server 7002 
                 Angular
                    |
               Server 7003 
       WebFlux + WebClient + Caffeine
                    |
               Server 7004                                  Server 8002 
            WebFlux + Solace                                  Angular
                    |                                            |
                    ----------------------------------------------
                    |                     |                      |
               Server 7005                |                 Server 8003
            WebFlux + Solace              |              WebFlux + Solace 
                    |                     |                      |
         -----------------------          |                 Server 8004
         |                     |          |                  PostgreSQL
    Server 7006           Server 7007     |                 
       Redis               PostgreSQL     |
                                          |
                                     Server 8080
                                       HAProxy
                                          |
                       -----------------------------------------
                       |                  |                    |
                   Server 212         Server 312           Server 412
                 Solace Primary      Solace Backup      Solace Monitoring


                  Server 20001/2                         Server 9001
                    SonarQube                              WebFlux  
                        |                                     |     
                   Server 20003                          Server 9002
                    PostgreSQL                            PostgreSQL
```

## Feature schema

### Create item

- Create an item on Server 7005 and add stats on Server 8003
- On duplicate error on Server 7005 send a compensatory operation to Server 8003
- Features: Async (non-blocking), Eventual consistency, Durable, Exclusive, Byte transfer (Google Protobuf)

```
                    ---- create item ----> Server 7005 --- duplicate error ----
                    |                                                         |
    Server 7004 --- |                                                         |
                    |                                                         |
                    ---- add stats ------> Server 8003 <--- substract stats ---
```

![create item ui](documents/create_item_ui.png)

### Get item

- Send a name to Server 7005
- Receive an item from Server 7004
- Features: Async (non-blocking), Non-durable (Direct), Exclusive, Byte transfer (Google Protobuf)

```
                    ------- send name ------> (request)
                    |                       |
    Server 7004 --- |                       | --- Server 7005
                    |                       |
            (reply) <------- get item -------           
```

![get item ui](documents/get_item_ui.png)

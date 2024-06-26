# Chat Server

This project builds the chat server with microservices with user authentication and message storage,
it also supports [STOMP](https://stomp.github.io/stomp-specification-1.2.html#Abstract) (Simple Text
Oriented Messaging Protocol) on websocket STOMP.

## Project structures

- `auth-service` (port 8080): supports user authentication using username/password.
- `chat-service` (port 8081): store messages. It also provides Restful API and websocket (STOMP
  protocol)
- `websocket-server` :  is the draft module building websocket service then remove the websocket in
  chat-service

## System requirements

- Java 17
- Docker engine
- Database using H2 for development with JPA auto migration update.

## IDE

- IntelliJ IDEA Community

## Build & Run services

Project use gradle build tool then you will:

- Build the project with `./gradlew {service}:build`
- Run test with `./gradlew {service}:test` then you can check report in `build > reports`.
- Run the project with `./gradlew {service}:bootRun`

You also build container images with command `./gradlew {service}:jibDockerBuild`

## API Documents

- `auth-service` : http://localhost:8080/api/auth/swagger-ui/index.html
- `chat-service` : http://localhost:8081/api/chat/swagger-ui/index.html

[Postman collections](https://raw.githubusercontent.com/truyet/chat-server/main/Chat.postman_collection.json)

## Websocket

`chat-service` is running then access websocket: `ws://localhost:8081/api/chat/ws`
Example STOMP message

```
CONNECT
accept-version:1.2,1.1,1.0
heart-beat:10000,10000
Authorization:eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNzE1NTkzNjQ5LCJ1c2VybmFtZSI6ImFkbWluIn0.2__iuNlEXqnM8FcGUOVE_8jVyXk7h2099Hm5r8JYQjY

{{U+0000}}

-----------------------------

SUBSCRIBE
id:sub-0
destination:/topic/default
Authorization:eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNzE1NTkzNjQ5LCJ1c2VybmFtZSI6ImFkbWluIn0.2__iuNlEXqnM8FcGUOVE_8jVyXk7h2099Hm5r8JYQjY

{{U+0000}}

-----------------------------

SEND
destination:/chat/default
content-length:33
Authorization:eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNzE1NTkzNjQ5LCJ1c2VybmFtZSI6ImFkbWluIn0.2__iuNlEXqnM8FcGUOVE_8jVyXk7h2099Hm5r8JYQjY

{"message":"Hello","type":"TEXT"}{{U+0000}}
```

with {{U+0000}} is NULL character in unicode

## Next Plan

- [ ] Enhance login method and token issue
- [ ] Separate `websocket-server` from `chat-service` to standalone service and communication with
  chat service use message queue  

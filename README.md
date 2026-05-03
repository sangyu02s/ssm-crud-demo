# ssm-crud-demo

Traditional Spring MVC + Spring JDBC + MyBatis WAR demo for Java 21 and external Tomcat.

## Build

```bash
mvn clean package
```

## Deploy

```bash
cp target/ssm-crud-demo.war /opt/homebrew/opt/tomcat/libexec/webapps/
brew services restart tomcat
```

## REST API

Base URL:

```text
http://localhost:8080/ssm-crud-demo/api/books
```

Examples:

```bash
curl http://localhost:8080/ssm-crud-demo/api/books

curl -X POST http://localhost:8080/ssm-crud-demo/api/books \
  -H 'Content-Type: application/json' \
  -d '{"title":"Java 21 Notes","author":"Sangyu","price":39.90}'

curl -X PUT http://localhost:8080/ssm-crud-demo/api/books/1 \
  -H 'Content-Type: application/json' \
  -d '{"title":"Spring MVC Updated","author":"Spring Team","price":69.90}'

curl -X DELETE http://localhost:8080/ssm-crud-demo/api/books/1
```

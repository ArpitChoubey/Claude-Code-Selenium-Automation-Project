# Java learning workspace

This workspace contains two separate Java projects:

- The root project is a standalone Java program, [mytest.java](./mytest.java), which prints `Hello Arpit`. It has no build tool or external dependencies.
- [SeleniumRepo](./SeleniumRepo/) is a Maven project using Java 17, Selenium WebDriver, and TestNG. It contains Selenium examples and a TestNG suite that visits live public websites. It is a browser-test runner, not a web service. It also contains a reusable Apache Kafka module (producer, consumer, topic management, JSON schema validation) with its own TestNG suite.

## Run the standalone program

With Java 11 or later:

```powershell
java mytest.java
```

Or compile and run:

```powershell
javac mytest.java
java mytest
```

## Run either project with Docker Compose

Docker Engine and the Docker Compose plugin are required. Run these commands from the workspace root.

Build and run the standalone program:

```powershell
docker compose --profile hello run --rm hello
```

Build and run the Selenium/TestNG suite in a headless Chrome container:

```powershell
docker compose --profile tests run --rm selenium-tests
```

The Selenium container needs internet access to download Maven dependencies and the matching browser driver through Selenium Manager, and to reach the test websites. The current TestNG suite is coupled to live third-party sites, so site changes, access restrictions, or network issues can cause test failures. TestNG reports and Maven build output are available under `SeleniumRepo/test-output` and `SeleniumRepo/target` on the host.

The root [Dockerfile](./Dockerfile) uses a multi-stage build: it compiles the program with a JDK, then runs only the class on a JRE as a non-root user. [SeleniumRepo/Dockerfile](./SeleniumRepo/Dockerfile) supplies Maven, Java 17, and Google Chrome for the browser-test suite. [docker-compose.yml](./docker-compose.yml) defines separately selectable `hello`, `tests`, `kafka` and `kafka-tests` profiles, so each part can be run on its own.

## Run Kafka and the Kafka tests

Start a single-node Kafka broker (KRaft mode, no ZooKeeper), reachable from your PC at `localhost:9092`:

```powershell
docker compose --profile kafka up -d
docker compose ps          # wait until kafka shows "healthy"
```

Run the Kafka TestNG suite from your PC (needs Maven):

```powershell
cd SeleniumRepo
mvn test "-Dtestng.suite=src/test/resources/TestRunner/KafkaTests.xml"
```

Or run the suite entirely in Docker. This starts the broker automatically and connects to it at `kafka:19092`:

```powershell
docker compose --profile kafka-tests run --rm kafka-tests
```

Stop the broker with `docker compose --profile kafka down`. Its data is not persisted, so every start is a clean broker.

The current applications do not expose an HTTP endpoint. Kubernetes Services, readiness/liveness probes, and rolling deployments are therefore not applicable to them as-is. The Selenium suite is naturally run as a CI task or Kubernetes Job; Kubernetes Deployment and Service examples would need a long-running application or Selenium Grid service as their target.

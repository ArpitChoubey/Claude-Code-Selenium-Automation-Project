# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

A Java learning workspace (the root is not a git repository) holding two separate projects:

- **Root:** a single standalone program, `mytest.java`, which prints `Hello Arpit`. No build tool or dependencies.
- **`SeleniumRepo/`:** a Maven project (Java 17) with Selenium WebDriver + TestNG examples **and** a reusable Apache Kafka module (producer, consumer, topic management, JSON schema validation) under `src/main/java/com/masterclass/kafka`. It has its own detailed `SeleniumRepo/CLAUDE.md`. Read that before working inside it.

`docker-compose.yml` at the root ties both together and runs a local Kafka broker.

## Commands

Standalone program (PowerShell, from the root):

```powershell
javac mytest.java              # compile; writes mytest.class next to the source
java mytest                    # run (class name, no extension)
javac mytest.java; if ($?) { java mytest }   # compile and run in one line
java mytest.java               # Java 11+: run without compiling first
```

Docker Compose (from the root). Each part has its own profile, so nothing starts unless you pick a profile:

```powershell
docker compose --profile hello run --rm hello                  # build + run mytest in a container
docker compose --profile tests run --rm selenium-tests         # Selenium suite in headless Chrome
docker compose --profile kafka up -d                           # Kafka broker on localhost:9092
docker compose --profile kafka-tests run --rm kafka-tests      # Kafka suite in Docker (broker at kafka:19092)
docker compose --profile kafka down                            # stop the broker (data is not persisted)
```

Kafka tests from the host, with the broker running:

```powershell
cd SeleniumRepo
mvn test "-Dtestng.suite=src/test/resources/TestRunner/KafkaTests.xml"
```

The root program has no tests or linter. For Maven, Selenium, Kubernetes and CI commands, see `SeleniumRepo/CLAUDE.md`.

## Docker and Kafka setup

- Root `Dockerfile`: multi-stage build. It compiles `mytest.java` on `eclipse-temurin:17-jdk`, then runs only the class on `eclipse-temurin:17-jre` as non-root user `10001`. `.dockerignore` excludes `SeleniumRepo`, `*.class`, `target` and `test-output` from the root build context.
- `selenium-tests` and `kafka-tests` both build from `SeleniumRepo/Dockerfile` and mount `SeleniumRepo/target` and `SeleniumRepo/test-output` into `/workspace`. `kafka-tests` only overrides the command (selecting `KafkaTests.xml`) and sets `KAFKA_BOOTSTRAP_SERVERS=kafka:19092`.
- `kafka` service: `apache/kafka:3.7.1`, a single node in KRaft mode (no ZooKeeper). Listeners: `EXTERNAL` `localhost:9092` for the host, `INTERNAL` `kafka:19092` for other compose services, and `CONTROLLER` on 9093. Internal topic replication factors are 1. `auto.create.topics.enable=false`, so topics must be created through `KafkaTopicManager`. A healthcheck gates `kafka-tests` via `depends_on: service_healthy`.
- Keep the Kafka image version in `docker-compose.yml` in step with `kafka.version` in `SeleniumRepo/pom.xml` (both 3.7.1).

## Conventions

- Each root `.java` file is a self-contained program in the default package (no `package` declaration). It needs a `public class` whose name exactly matches the filename (case-sensitive) and a `public static void main(String[] args)` entry point.
- `.class` files are build output. Regenerate them with `javac`; don't edit them.
- No application here exposes an HTTP endpoint. The Selenium and Kafka suites are run-to-completion jobs, not services.

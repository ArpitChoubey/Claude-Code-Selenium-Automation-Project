# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

A learning/reference repo of Selenium WebDriver 4.11.0 examples in Java, using TestNG 7.8.0 and Maven. It is not a page-object framework: most files are standalone demos of one Selenium concept each. It is also an Eclipse project (`.classpath`, `.project`, `.settings/`). Tests can run locally, in Docker, as a Kubernetes Job, and in Azure DevOps CI.

## Commands

```powershell
mvn clean test                                   # compile and run the TestNG suite (Test.xml)
mvn test -Dtest=AbzTest                          # run one test class (bypasses the suite file)
mvn test -Dtest=AbzTest#ABCTest                  # run one test method
mvn test -Dbrowser=chrome -Dheadless=true        # choose browser / headless (see DriverFactory)
mvn clean test-compile                           # just check everything compiles

docker build -t selenium-tests:local .           # build the test image
docker run --rm selenium-tests:local             # run the suite in headless Chrome
# from the parent folder (..), via its docker-compose.yml:
docker compose --profile tests run --rm selenium-tests

kubectl apply -f k8s/namespace.yaml -f k8s/deployment.yaml -f k8s/service.yaml   # namespace + Selenium Grid
kubectl apply -n selenium-tests -f k8s/selenium-tests-job.yaml   # after replacing IMAGE_PLACEHOLDER with an image
kubectl logs -f job/selenium-tests -n selenium-tests
kubectl delete job selenium-tests -n selenium-tests             # required before re-applying (Job spec is immutable)
```

- `pom.xml` configures `maven-surefire-plugin` to run `src/test/resources/TestRunner/Test.xml`, so `mvn test` only runs the classes listed there (currently `AbzTest` and `XYZTest`). Add new test classes to `Test.xml` if they should run in the suite, Docker, Kubernetes and CI.
- The suite runs `parallel="tests"` with `thread-count="10"`, so each `<test>` block gets its own thread. Keep `WebDriver` instances per-class or per-method, never static or shared.
- Compiles with `maven.compiler.release=17` (JDK 17+ required; matches CI and the Docker image).
- On this Windows machine Git is at `C:\Program Files\Git\cmd\git.exe` (installed via winget; may not be on PATH in an old shell). Maven may not be installed yet (`winget install --id Apache.Maven -e`).

## Structure

- `src/main/java/SeleniumSess/`: concept demos, each runnable on its own through a `main` method (alerts, windows, frames, dropdowns, waits, file upload, headless, relative locators, etc.). Run them from the IDE. Maven doesn't run them, but it compiles them, so a compile error here breaks `mvn test`.
  - `SeleniumUtil`: `initDriver(browserName)` plus URL/title/quit helpers, used by the demos. It lowercases its input, so `switch` case labels must be lowercase.
  - `ElementUtil`: instance wrapper around a `WebDriver` for element, dropdown, wait, alert and frame helpers (`getElement`, `getElements`, `waitFor…`, etc.).
- `src/test/java/TestNGClass/`: TestNG classes demonstrating annotations (`@BeforeMethod`/`@AfterMethod`, `priority`, `dependsOnMethods`, `expectedExceptions`, `@DataProvider`).
  - `DriverFactory.createDriver()` is how tests get a browser. Don't call `new XxxDriver()` in tests. It reads `-Dbrowser` / `BROWSER` (`firefox` default, `chrome`, `edge`) and `-Dheadless` / `HEADLESS` (`false` default). Chrome always gets `--no-sandbox --disable-dev-shm-usage` so it works as root in containers.
- `test-output/` and `target/` are generated and gitignored, as is `*.html`.

## Kafka module

Reusable Kafka code is in `src/main/java/com/masterclass/kafka` (kafka-clients 3.7.1, Jackson, networknt json-schema-validator 1.0.87, slf4j-simple). Tests are in `src/test/java/com/masterclass/kafka`.

```powershell
docker compose --profile kafka up -d     # run from the parent folder; broker on localhost:9092
mvn test "-Dtestng.suite=src/test/resources/TestRunner/KafkaTests.xml"
mvn test "-Dtest=JsonSchemaValidatorTest,KafkaClientsMockTest"     # broker-free unit tests
docker compose --profile kafka-tests run --rm kafka-tests          # whole suite in Docker (broker at kafka:19092)
```

- The `testng.suite` Maven property selects the surefire suite. It defaults to the Selenium `Test.xml`, so plain `mvn test`, the Dockerfile and the Azure pipeline still run only Selenium. Don't add Kafka classes to `Test.xml`: CI has no broker.
- All clients get their settings from `KafkaConfig` (`fromEnvironment()` reads `-Dkafka.bootstrap.servers` / `KAFKA_BOOTSTRAP_SERVERS`). Producer: `acks=all` + idempotence. Consumer: `earliest`, manual `commitSync`, `read_committed`. Keys and values are Strings; JSON conversion goes through the shared mapper in `json.JsonSupport`.
- `KafkaMessageProducer` validates **before** sending when given a `JsonSchemaValidator`, so invalid messages never reach a topic. Producer, consumer and topic manager each have a constructor that takes a `Producer`/`Consumer`/`Admin`, which is how `KafkaClientsMockTest` uses Kafka's `MockProducer`/`MockConsumer`.
- `KafkaMessageConsumer` is not thread-safe (one per test). `KafkaTopicManager` methods block until the topic is visible or gone.
- Message contract: `model.OrderEvent` (Java record) ↔ `src/main/resources/schemas/order-event.schema.json` (`additionalProperties: false`). Change both together.
- Integration tests extend `support.KafkaTestBase`, which fails fast if no broker is reachable and isolates runs with unique topic/group names (`uniqueName(...)`). The broker has `auto.create.topics.enable=false`, so tests must create topics with `KafkaTopicManager`.
- Kafka client logging is set to WARN in `src/main/resources/simplelogger.properties`.

## Docker and Kubernetes

- `Dockerfile`: `maven:3.9.6-eclipse-temurin-17` with Google Chrome installed. It pre-fetches dependencies, then runs `test-compile`. It sets `BROWSER=chrome HEADLESS=true` and `CMD mvn -B test`. WORKDIR is `/workspace` because `../docker-compose.yml` (service `selenium-tests`, profile `tests`) mounts `target/` and `test-output/` there. Keep those in sync.
- Only Chrome is installed in the image. Firefox tests need `BROWSER=chrome` there (already the default via `ENV`).
- `k8s/` (see `k8s/README.md` for full run steps): `namespace.yaml` (`selenium-tests`), `deployment.yaml` + `service.yaml` (Selenium Grid `selenium/standalone-chrome:4.11.0-20230801` at `selenium-grid:4444`, noVNC on 7900, max 4 sessions). Deployment and Service hard-code `namespace: selenium-tests`; the Job does not, so pass `-n`.
- `DriverFactory` uses `RemoteWebDriver` when `SELENIUM_REMOTE_URL` / `-Dselenium.remote.url` is set (e.g. `http://selenium-grid:4444`, or `http://localhost:4444` after `kubectl port-forward`). The Grid only has Chrome.
- `k8s/selenium-tests-job.yaml`: a one-off `Job` with `backoffLimit: 0`, a 30-minute `activeDeadlineSeconds`, a 1-day TTL, a memory-backed `/dev/shm` volume for Chrome, and 1-2 CPU / 2-4Gi. Its `image:` is the literal `IMAGE_PLACEHOLDER`; the pipeline substitutes it with `sed`.

## CI (`azure-pipelines.yml`)

Two stages run on pushes to `master`, on `ubuntu-latest`:
1. **Test**: `Maven@4` runs `clean test` with JDK 17 and `BROWSER=chrome HEADLESS=true` (hosted agents have no display), and publishes `**/surefire-reports/TEST-*.xml`.
2. **Kubernetes** (depends on Test): `Docker@2` builds and pushes `$(containerRegistry)/$(imageRepository):$(Build.BuildId)`. `Kubernetes@1` runs `login`. A bash step then deletes the old Job, applies the new one, polls `.status.succeeded` / `.status.failed`, prints pod logs and fails the run if the Job didn't succeed. Results are only in the logs, not the Tests tab.

The `variables:` block (`dockerRegistryServiceConnection`, `containerRegistry`, `kubernetesServiceConnection`, `k8sNamespace`) holds placeholders that must match real Azure DevOps service connections.

## Caveats

- Tests drive real browsers against live public sites (Hotstar, Amazon, Flipkart, Google, YouTube, naveenautomationlabs opencart). They need internet access, can be flaky when those sites change, and may be blocked from cloud/cluster IPs. Selenium Manager downloads drivers at runtime, which also needs internet.
- As of the last update, the Maven build, Docker image and Kubernetes Job had not yet been run on this machine. Verify with `mvn clean test-compile` first.

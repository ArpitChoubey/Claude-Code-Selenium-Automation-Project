# 🚀 Selenium WebDriver Java Test Automation | Selenium v4.11.0 | 🔵 Azure DevOps | CI/CD & Pipeline Automation |

<p align="center">

<img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk&logoColor=white"/>
<img src="https://img.shields.io/badge/Selenium-v4.11.0-43B02A?style=for-the-badge&logo=selenium&logoColor=white"/>
<img src="https://img.shields.io/badge/TestNG-Testing-red?style=for-the-badge"/>
<img src="https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven"/>
<img src="https://img.shields.io/badge/JavaScript-Executor-yellow?style=for-the-badge&logo=javascript"/>
<img src="https://img.shields.io/badge/Automation-QA-blue?style=for-the-badge"/>
<img src="https://img.shields.io/badge/Git-GitHub-black?style=for-the-badge&logo=github"/>
<img src="https://img.shields.io/badge/Open-Source-success?style=for-the-badge"/>
<img src="https://img.shields.io/badge/Azure%20DevOps-CI%2FCD-0078D4?style=for-the-badge&logo=azuredevops&logoColor=white"/>

</p>

---

# 🌟 Selenium WebDriver Java Automation Repository

A comprehensive Selenium WebDriver repository developed using **Java**, **Selenium WebDriver v4.11.0**, **TestNG**, and **Maven**, demonstrating practical UI automation techniques, browser automation, Selenium 4 features, synchronization strategies, TestNG execution, and reporting.

Originally implemented in **2023**, this repository serves as a practical reference for Selenium automation engineers, SDETs, QA professionals, and interview preparation.

---

# 🎯 Project Purpose

This repository was built to strengthen practical Selenium automation skills through real-world examples and reusable automation utilities.

It covers everything from Selenium basics to advanced browser interactions, making it suitable for both beginners and experienced automation engineers.

---

# 🚀 Technology Stack

| Technology | Purpose |
|------------|----------|
| ☕ Java | Programming Language |
| 🌐 Selenium WebDriver v4.11.0 | UI Automation |
| ✅ TestNG | Test Framework |
| 📦 Maven | Build Management |
| 🌍 ChromeDriver | Browser Automation |
| 📊 TestNG Reports | Execution Reporting |
| 🔧 Eclipse IDE | Development Environment |
| 🔄 Git & GitHub | Version Control |
| 🔵 Azure DevOps             | CI/CD & Pipeline Automation |
---

# ⭐ Highlights

✔ Selenium WebDriver 4 Examples

✔ Java Automation

✔ TestNG Framework

✔ Maven Project

✔ Selenium Relative Locators

✔ Selenium Wait Strategies

✔ Browser Commands

✔ Multiple Windows

✔ Frames & iFrames

✔ Alerts

✔ Drag & Drop

✔ Dropdown Handling

✔ File Upload

✔ Headless Browser

✔ Selenium Utilities

✔ TestNG Reports

✔ Interview Ready Repository

---

# 📂 Repository Structure

```
Selenium-Webdriver-Java-Test-Automation

│
├── src
│
│   ├── main
│   │
│   └── java
│        │
│        └── SeleniumSess
│              Selenium Programs
│
├── test
│
├── resources
│
├── TestRunner
│      Test.xml
│
├── test-output
│      TestNG Reports
│
├── pom.xml
│
└├── azure-pipelines.yml
│     
── README.md
```

---

# 🧪 Selenium Concepts Covered

## Browser Automation

- Launch Browser
- Browser Commands
- Browser Navigation
- Browser Close & Quit
- Current URL
- Page Title
- Browser Refresh

---

## WebElement Operations

- Click
- Send Keys
- Clear
- Submit
- Get Text
- Get Attribute
- Get CSS Value

---

## Selenium Locators

- ID
- Name
- Class Name
- Tag Name
- Link Text
- Partial Link Text
- CSS Selector
- XPath

---

## XPath

- Absolute XPath
- Relative XPath
- XPath Functions
- XPath Axes

---

## Selenium 4 Features

- Relative Locators
- Improved WebDriver APIs
- Latest Browser Support

---

## Wait Strategies

- Implicit Wait
- Explicit Wait
- WebDriverWait
- Expected Conditions

---

## Browser Navigation

- Back
- Forward
- Refresh
- Navigate To

---

## Browser Windows

- New Window
- Multiple Tabs
- Multiple Browser Windows
- Window Switching

---

## Alerts

- JavaScript Alert
- Confirmation Alert
- Prompt Alert

---

## Frames

- Frame Handling
- Nested Frames
- iFrame Switching

---

## Dropdown Handling

- Select Class
- Multi Select
- Deselect Options

---

## User Actions

Using Selenium Actions Class

- Mouse Hover
- Double Click
- Right Click
- Drag and Drop
- Keyboard Events

---

## File Upload

Automating file upload using Selenium.

---

## Headless Browser

Running browser automation without GUI for faster execution.

---

## Dynamic Web Elements

Examples demonstrating interaction with dynamic web components.

---

## Selenium Utilities

Reusable utility classes for automation.

---

# 📊 TestNG Concepts

Examples include

- TestNG XML

- Test Priority

- Depends On Methods

- Groups

- Parallel Execution Ready

- Assertions

- Test Suites

- Regression Suite

---

# 📈 Reports

Generated using TestNG.

Repository contains:

```
test-output/

AbzTest.html

XYZTest.html

JUnit Reports

Regression Suite Reports
```

Useful for

- Test Execution Summary

- Passed Tests

- Failed Tests

- Execution Statistics

---

# 🏗 Skills Demonstrated

This repository demonstrates practical experience in:

- Selenium WebDriver

- Selenium 4

- Java

- Maven

- TestNG

- Browser Automation

- XPath

- CSS Selectors

- Relative Locators

- Explicit Wait

- Implicit Wait

- JavaScript Alerts

- Window Handling

- File Upload

- Frames

- Dynamic Elements

- Test Automation

- UI Testing

---

# ▶ Getting Started

## Clone Repository

```bash
git clone https://github.com/ArpitChoubey/Selenium-Webdriver-Java-Test-Automation.git
```

---

## Import Project

Open Eclipse

Import

```
Existing Maven Project
```

---

## Install Dependencies

```bash
mvn clean install
```

---

## Execute TestNG Suite

```
src/test/resources/TestRunner/Test.xml
```

or

```bash
mvn test
```

## Run the TestNG suite with Docker

From the parent workspace directory, build and run the suite using the `tests` profile:

```powershell
docker compose --profile tests run --rm selenium-tests
```

The [Dockerfile](./Dockerfile) uses Java 17 and Maven, installs Google Chrome, and sets `BROWSER=chrome` and `HEADLESS=true` for container execution. Selenium Manager downloads the compatible WebDriver when the suite runs, so the container needs internet access both for dependency/driver downloads and for the live websites used by the tests. Failures caused by external site changes or network restrictions are possible.

The Compose service mounts `target` and `test-output` back to the host, making generated Maven and TestNG reports available in this project directory after the container exits. For the standalone root Java program, see the workspace-level [README](../README.md).

## Apache Kafka module

Reusable Kafka utilities live in `src/main/java/com/masterclass/kafka`:

| Class | Purpose |
|-------|---------|
| `config.KafkaConfig` | Broker address (`KAFKA_BOOTSTRAP_SERVERS` / `-Dkafka.bootstrap.servers`, default `localhost:9092`) and reliable client defaults |
| `admin.KafkaTopicManager` | Create (idempotent), list, describe and delete topics |
| `producer.KafkaMessageProducer` | Send objects or JSON strings, with optional schema validation before sending |
| `consumer.KafkaMessageConsumer` | Poll until a count, key or condition is met; deserialise JSON; read headers |
| `validation.JsonSchemaValidator` | Validate JSON against a draft-07 schema in `src/main/resources/schemas/` |

The Kafka tests have their own suite, so the Selenium suite above is unaffected:

```powershell
docker compose --profile kafka up -d        # from the workspace root: start the broker
mvn test "-Dtestng.suite=src/test/resources/TestRunner/KafkaTests.xml"
mvn test "-Dtest=JsonSchemaValidatorTest,KafkaClientsMockTest"   # unit tests only, no broker needed
```

---

# 🎯 Best Suited For

- QA Engineers

- Automation Engineers

- Selenium Developers

- SDET Interviews

- Freshers Learning Selenium

- Experienced Test Engineers

- Java Selenium Practice

- TestNG Learning

---

# 📚 Learning Outcomes

After completing this repository you will understand:

- Selenium WebDriver Fundamentals

- Selenium 4 Features

- Browser Automation

- Dynamic Element Handling

- Wait Strategies

- TestNG Execution

- Selenium Best Practices

- Automation Framework Basics

---

# 🚀 Future Enhancements

- Page Object Model (POM)

- Page Factory

- Data Driven Framework

- Hybrid Framework

- Apache POI

- Extent Reports

- Allure Reports

- Jenkins Integration

- Docker Execution

- Selenium Grid

- GitHub Actions CI

- Cross Browser Execution

---

# 🔗 GitHub Repository

https://github.com/ArpitChoubey/Selenium-Webdriver-Java-Test-Automation

---

# 👨‍💻 Author

## Arpit Choubey

**SDET | QA Automation Engineer | Selenium | Playwright | API Testing**

Passionate about building scalable automation frameworks, sharing practical QA knowledge, and creating interview-focused automation repositories.

### Connect With Me

🔗 GitHub

https://github.com/ArpitChoubey

🔗 LinkedIn

https://www.linkedin.com/in/arpitchoubey/

✍ Medium

https://medium.com/@ArpitChoubey9

---

# ⭐ Support

If this repository helped you in learning Selenium or interview preparation, please consider giving it a **Star ⭐**.

Your support encourages me to continue building practical automation repositories for the QA community.

---

## 💬 Quote

> **"Automation is not about replacing testers—it empowers them to deliver quality faster and with greater confidence."**

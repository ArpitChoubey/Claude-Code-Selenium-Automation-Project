# Selenium + TestNG test runner image: JDK 17, Maven, headless Google Chrome.
# Selenium Manager (built into Selenium 4.11) downloads the matching ChromeDriver at runtime.
FROM maven:3.9.6-eclipse-temurin-17

RUN apt-get update \
 && apt-get install -y --no-install-recommends wget ca-certificates \
 && wget -q -O /tmp/chrome.deb https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb \
 && apt-get install -y --no-install-recommends /tmp/chrome.deb \
 && rm -f /tmp/chrome.deb \
 && rm -rf /var/lib/apt/lists/*

# /workspace matches the volume mounts in ../docker-compose.yml (target/, test-output/)
WORKDIR /workspace

# Cache dependencies in their own layer so code changes don't re-download them
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q test-compile

ENV BROWSER=chrome \
    HEADLESS=true

# Runs the TestNG suite configured in pom.xml (src/test/resources/TestRunner/Test.xml)
CMD ["mvn", "-B", "test"]

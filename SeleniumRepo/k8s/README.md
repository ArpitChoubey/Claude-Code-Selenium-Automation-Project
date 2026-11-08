# Kubernetes manifests

Run the Selenium TestNG suite in Kubernetes.

| File | Kind | Purpose |
|------|------|---------|
| `namespace.yaml` | Namespace | `selenium-tests` namespace for everything below |
| `deployment.yaml` | Deployment | Selenium Grid (`selenium/standalone-chrome:4.11.0`), up to 4 parallel browser sessions |
| `service.yaml` | Service (ClusterIP) | Stable address `selenium-grid:4444` (WebDriver) and `:7900` (noVNC browser viewer) |
| `selenium-tests-job.yaml` | Job | Runs `mvn test` once from the project's Docker image, then stops |

There are two ways to run the tests:

- **Self-contained Job (default):** the Job's container has its own headless Chrome. You only need the Job.
- **Job + Grid:** the Job sends browser commands to the Grid. Uncomment `SELENIUM_REMOTE_URL` in `selenium-tests-job.yaml`. You can also point your own PC at the Grid.

## Prerequisites

- A cluster and `kubectl` (Docker Desktop's built-in Kubernetes, minikube or kind all work for local use)
- Docker, to build the test image from the project root `Dockerfile`
- Internet access from the cluster: the tests open live websites, and Selenium Manager downloads drivers

## Run locally (Docker Desktop Kubernetes)

All commands are run from the project root (`SeleniumRepo`).

```powershell
# 1. Build the test image (Docker Desktop's cluster can use local images directly)
docker build -t selenium-tests:local .

# 2. Create the namespace
kubectl apply -f k8s/namespace.yaml

# 3. (Optional) start the Selenium Grid
kubectl apply -f k8s/deployment.yaml -f k8s/service.yaml
kubectl rollout status deployment/selenium-grid -n selenium-tests

# 4. Point the Job at the local image (and optionally uncomment SELENIUM_REMOTE_URL), then run it
(Get-Content k8s/selenium-tests-job.yaml) -replace 'IMAGE_PLACEHOLDER','selenium-tests:local' |
  kubectl apply -n selenium-tests -f -

# 5. Follow the results
kubectl logs -f job/selenium-tests -n selenium-tests
kubectl get job selenium-tests -n selenium-tests     # COMPLETIONS 1/1 = passed
```

With minikube, build the image inside minikube's Docker first (`minikube image build -t selenium-tests:local .`); with kind, load it (`kind load docker-image selenium-tests:local`).

## Run again

A Job can't be edited after it is created, so delete it before re-applying:

```powershell
kubectl delete job selenium-tests -n selenium-tests
```

## Use the Grid from your own machine

```powershell
kubectl port-forward svc/selenium-grid -n selenium-tests 4444:4444 7900:7900
```

Then:
- Run the tests on the Grid: `mvn test -Dbrowser=chrome -Dselenium.remote.url=http://localhost:4444`
- Grid dashboard: http://localhost:4444/ui
- Watch the browser live: http://localhost:7900 (password `secret`)

## Settings read by the tests

`src/test/java/TestNGClass/DriverFactory.java` reads these as environment variables or `-D` system properties:

| Env var | System property | Default | Meaning |
|---------|-----------------|---------|---------|
| `BROWSER` | `browser` | `firefox` (`chrome` in the Docker image) | `chrome`, `firefox` or `edge` |
| `HEADLESS` | `headless` | `false` (`true` in the Docker image) | Run without a visible window |
| `SELENIUM_REMOTE_URL` | `selenium.remote.url` | not set | Grid URL; when set, uses `RemoteWebDriver` |

The Grid in `deployment.yaml` only has Chrome, so keep `BROWSER=chrome` when you use it.

## CI

`azure-pipelines.yml` builds and pushes the image, then applies `selenium-tests-job.yaml` into the namespace set by the `k8sNamespace` variable. It doesn't deploy the namespace, Grid or Service. To use them in CI, apply those three files to the cluster once, and set `k8sNamespace` to `selenium-tests`.

## Clean up

```powershell
kubectl delete namespace selenium-tests    # removes the Grid, Service and Job
```

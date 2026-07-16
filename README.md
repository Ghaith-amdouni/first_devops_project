# TaskManager DevOps Project

A Spring Boot TaskManager application deployed on Kubernetes with PostgreSQL, Docker, Prometheus, and Grafana monitoring.

## Features

- User registration and login with JWT authentication
- Project management
- Kanban task board with To Do, In Progress, and Done columns
- Project members
- Task assignment
- Due dates
- Task comments
- Prometheus metrics exposed by Spring Boot Actuator
- Grafana dashboard provisioned through the kube-prometheus-stack sidecar

## Stack

- Java 21
- Spring Boot
- Spring Security and JWT
- PostgreSQL
- Docker
- Kubernetes and Minikube
- Helm
- kube-prometheus-stack
- Prometheus
- Grafana

## Project Structure

```text
backend/                 Spring Boot application
docker/                  Dockerfile and Docker Compose config
k8s/                     Kubernetes manifests
k8s/monitoring/          Prometheus ServiceMonitor and Grafana dashboard
docs/                    Optional CI/CD workflow template
```

## Run With Docker Compose

```bash
docker compose -f docker/docker-compose.yml up --build
```

Open:

```text
http://localhost:8080
```

## Deploy To Minikube

Start Minikube:

```bash
minikube start
```

Build the app image inside Minikube:

```bash
minikube image build -t taskmanager:latest -f docker/Dockerfile .
```

Create Kubernetes secrets:

```bash
kubectl create secret generic db-secret \
  --from-literal=username=postgres \
  --from-literal=password=postgres

kubectl create secret generic jwt-secret \
  --from-literal=secret="$(openssl rand -base64 64)"
```

Apply the manifests:

```bash
kubectl apply -f k8s/postgres-pvc.yaml
kubectl apply -f k8s/postgres-deployment.yaml
kubectl apply -f k8s/postgres-service.yaml
kubectl apply -f k8s/app-deployment.yaml
kubectl apply -f k8s/app-service.yaml
```

Open the app:

```bash
kubectl -n default port-forward svc/taskmanager-app 8080:8080
```

Then visit:

```text
http://localhost:8080/projects
```

## Monitoring

Install kube-prometheus-stack:

```bash
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
helm upgrade --install monitoring prometheus-community/kube-prometheus-stack \
  -n monitoring \
  --create-namespace \
  -f k8s/monitoring/values.yaml
```

Apply the TaskManager monitoring resources:

```bash
kubectl apply -f k8s/monitoring/taskmanager-service-monitor.yaml
kubectl apply -f k8s/monitoring/taskmanager-dashboard.yaml
```

Open Grafana:

```bash
kubectl -n monitoring port-forward svc/monitoring-grafana 3000:80
```

Then visit:

```text
http://localhost:3000/d/taskmanager-app/taskmanager-app
```

Default local login:

```text
admin / admin
```

## Reopen Later

After Minikube is already installed and the stack is deployed:

```bash
minikube start
kubectl -n default port-forward svc/taskmanager-app 8080:8080
kubectl -n monitoring port-forward svc/monitoring-grafana 3000:80
```

App:

```text
http://localhost:8080/projects
```

Grafana:

```text
http://localhost:3000/d/taskmanager-app/taskmanager-app
```

If port `8080` shows an old Docker container, stop it:

```bash
docker stop taskmanager-app
```

## CI/CD Template

An example GitHub Actions workflow is available at `docs/ci-cd.yml.example`.
To enable it, copy it to `.github/workflows/ci-cd.yml` and configure the required repository secrets.

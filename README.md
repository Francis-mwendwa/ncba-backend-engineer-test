# Backend Engineer Test - Countries API

This Spring Boot service exposes APIs to search and retrieve country information. It pulls data from a public SOAP service, persists it to a relational database, and serves queries from the local database with filtering, pagination, and sorting.

- Language: Java (Spring Boot)
- Build: Maven
- Default port: 8080

Sections
1. Overview
2. Build and Run Locally
3. API Usage and Postman Collection
4. Configuration
5. Kubernetes: Deployment Scripts
6. Kubernetes: Deployment Guide
7. Kubernetes: Troubleshooting Guide
8. API Documentation
9. Docker Compose: Run Locally with MySQL


## 1. Overview
The service loads countries from the third‑party SOAP endpoint (on first query or via an explicit sync) and stores them in the local database. All subsequent client queries are served from the database.

Key capabilities:
- Search by country name
- Filter by continent, currency, language
- Retrieve country details by ISO code
- View countries sharing the same currency
- Pagination and sorting on list endpoints


## 2. Build and Run Locally
Prerequisites:
- Java 17+
- Maven 3.8+

Commands:
- Build: mvn -DskipTests package
- Run:  mvn spring-boot:run
  or:   java -jar target/backend-engineer-test-*.jar

The application listens on port 8080 by default.


## 3. API Usage and Postman Collection
- Base URL: http://localhost:8080/api/v1
- Import the Postman collection: postman/backend-engineer-test.postman_collection.json
- All API responses are wrapped in ApiResponse with statusCode (0=Success, 1=Failed) and statusMessage.

Example endpoints:
- GET /countries?name=ken&continent=AF&currency=KES&language=en&page=0&size=10&sortBy=name&sortDir=asc
- GET /countries/{isoCode}
- GET /countries/by-currency/{currency}


## 4. Configuration
Common configurations are kept in application.yaml. When running in containers/Kubernetes, see ConfigMap below.


## 5. Kubernetes: Deployment Scripts
This repository includes ready-to-use scripts for both PowerShell (Windows) and Bash (Linux/macOS) to build the container image and deploy to Kubernetes.

Scripts directory: scripts/
-  build-image.sh
  - Builds and tags the Docker image.
-  | k8s-deploy.sh
  - Applies the Kubernetes manifests under k8s/ (namespace, ConfigMap, Deployment, Service, optional Ingress).
-  | k8s-delete.sh
  - Deletes the deployed resources.
-  | k8s-status.sh
  - Shows rollout status, service details, and recent pod logs.

Kubernetes manifests directory: k8s/
- namespace.yaml
- configmap.yaml
- deployment.yaml
- service.yaml


## 6. Kubernetes: Deployment Guide
Prerequisites:
- Docker (or another OCI-compatible tool) to build/push images.
- Kubernetes cluster (Minikube, Kind, Docker Desktop, or a managed K8s like EKS/GKE/AKS).
- kubectl installed and configured to talk to your cluster.
- A container registry you can push to (Docker Hub, GHCR, ECR, etc.).

A. Build and tag the image
- Bash (Linux/macOS):
  ./scripts/build-image.sh -t latest -r <image_registry_name>/backend-engineer-test

This will build image <image_registry_name>/backend-engineer-test:latest.

B. Push the image to your registry
- docker push <image_registry_name>/backend-engineer-test:latest

C. Set the image in Kubernetes manifests (optional)
- By default, deployment.yaml references the image via an environment variable IMAGE (scripts can substitute), or you can edit k8s/deployment.yaml and set the image field to your pushed image.

D. Deploy to Kubernetes
- PowerShell (Windows):
  scripts\k8s-deploy.ps1 -Namespace ncba-countries -Image <image_registry_name>/backend-engineer-test:latest
- Bash (Linux/macOS):
  ./scripts/k8s-deploy.sh -n ncba-countries -i <image_registry_name>/backend-engineer-test:latest

E. Verify deployment
- PowerShell: scripts\k8s-status.ps1 -Namespace ncba-countries
- Bash: ./scripts/k8s-status.sh -n ncba-countries

F. Access the service
- Option 1: Port-forward
  kubectl -n ncba-countries port-forward svc/countries-svc 8080:8080
  Then open http://localhost:8080/api/v1/countries


## 7. Kubernetes: Troubleshooting Guide
Below are common issues and how to investigate and resolve them.

- Pods not starting (ImagePullBackOff)
  - kubectl -n ncba-countries get pods
  - kubectl -n ncba-countries describe pod <pod-name>
  - Check image name/tag and that it was pushed and is accessible from the cluster. If using a private registry, configure imagePullSecrets and reference them in the Deployment.

- CrashLoopBackOff (application keeps crashing)
  - kubectl -n ncba-countries logs deployment/countries-deploy --tail=200
  - kubectl -n ncba-countries describe pod <pod-name>
  - Review application logs for stack traces. Validate environment variables and ConfigMap values. Increase resources if OOMKilled (see below).

- Readiness/Liveness probe failures
  - kubectl -n ncba-countries describe pod <pod-name>
  - Ensure the app starts within the initialDelaySeconds and that the probe path (/actuator/health or /api/v1/countries) returns 200. Adjust probe timings in k8s/deployment.yaml as needed.

- OOMKilled (out-of-memory)
  - kubectl -n ncba-countries describe pod <pod-name>
  - Increase memory limits/requests in k8s/deployment.yaml or reduce JVM heap via JAVA_TOOL_OPTIONS (e.g., -Xms256m -Xmx512m). Ensure the node has enough free memory.

- Service not reachable
  - kubectl -n ncba-countries get svc countries-svc -o wide
  - If using port-forward, confirm the command is running. If using an Ingress, verify the ingress controller is installed and that DNS resolves the host to the ingress IP.

- Database considerations
  - This sample uses MySQL by default in docker-compose; for production you may use a managed DB (e.g., MySQL, PostgreSQL). Mount credentials via Secrets and connection settings via ConfigMap/Env Vars. Update application.yaml accordingly and set probes to match readiness.

Useful commands:
- kubectl get all -n ncba-countries
- kubectl describe deployment countries-deploy -n ncba-countries
- kubectl logs deployment/countries-deploy -n ncba-countries --tail=200
- kubectl get events -n ncba-countries --sort-by=.lastTimestamp

If you need further help, collect the output from the above commands and logs and share them for analysis.


## 9. Docker Compose: Run Locally with MySQL
This project includes a ready-to-use docker-compose.yml to run both MySQL and the Countries API locally.

Prerequisites:
- Docker Desktop (Windows/macOS) or Docker Engine + Docker Compose v2 (Linux)

What it does:
- Starts a MySQL 8 container with database rdas_db and root password pass001, persisted on a Docker volume.
- Builds and runs the Countries API container from the Dockerfile in this repo.
- Wires Spring Boot to use the MySQL service via environment variables.

Default ports:
- App: http://localhost:8080
- MySQL: localhost:3306

Quick start:
1) Build and start in background
   docker compose up -d --build

2) Check container status and logs
   docker compose ps
   docker compose logs -f app

3) Call the API
   curl "http://localhost:8080/api/v1/countries?page=0&size=5"

4) Stop and remove containers (data volume is preserved)
   docker compose down

5) Stop, remove, and also delete the MySQL data volume
   docker compose down -v

Environment overrides (compose):
- The app service is configured with these environment vars:
  - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/rdas_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
  - SPRING_DATASOURCE_USERNAME=root
  - SPRING_DATASOURCE_PASSWORD=pass001
  - SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
  - SPRING_JPA_HIBERNATE_DDL_AUTO=create
- To change credentials or DB name, edit docker-compose.yml (both mysql and app env).

Rebuild the app image after code changes:
- docker compose build app
  or
- docker compose up -d --build

Troubleshooting compose:
- If the app starts before MySQL is ready, the app depends_on waits for MySQL healthcheck to pass.
- If you changed ports and can’t access the app, ensure 8080:8080 is still mapped under the app service.
- To inspect the DB:
  docker exec -it countries-mysql mysql -uroot -ppass001 -e "SHOW DATABASES; USE rdas_db; SHOW TABLES;"

Using Postman with compose:
- Set baseUrl to http://localhost:8080 in the provided collection postman/backend-engineer-test.postman_collection.json

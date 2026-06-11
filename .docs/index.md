# Project Documentation Home (.docs)

Last updated: 2026-06-11

Welcome to the documentation hub for the Backend Engineer Test - Countries API project. This folder provides a simple, central entry point to the project documentation.

Quick Links
- Project README (getting started, build, run, Kubernetes, troubleshooting): ../README.md
- Countries API documentation (Markdown): ../docs/API.md
- Postman collection (ready to import): ../postman/backend-engineer-test.postman_collection.json
- Kubernetes manifests: ../k8s/
- Deployment and utility scripts: ../scripts/

What is this folder?
- Some platforms (CI/CD, doc generators, or repository browsers) look for a .docs folder as a documentation root. This index.md file acts as the landing page and links to the main docs already maintained in the repository.

How to use
- Open the links above directly from your code editor or repository browser.
- For API exploration, import the Postman collection and point it at your running service (default http://localhost:8080).

Notes
- The application exposes REST endpoints under /api/v1 and returns responses wrapped in ApiResponse with TransactionStatus mapping (0 = Success, 1 = Failed).
- On first query, country data is synchronized from a public SOAP service and then served from the local database thereafter.

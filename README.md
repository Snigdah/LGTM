# LGTM Observability Stack

A complete observability setup for Spring Boot applications using the **LGTM stack** — **L**oki, **G**rafana, **T**empo, and **M**imir — with **Grafana Alloy** as the OpenTelemetry collector and **Prometheus** for metrics scraping.

This repository contains the Docker Compose files and a sample Spring Boot application to spin up an end-to-end observability pipeline for logs, metrics, and traces.

## Architecture Overview

Logs, metrics, and traces flow from Spring Boot applications through Grafana Alloy into the backend systems, and are visualized in Grafana.

![LGTM Observability Stack - End to End Flow](https://raw.githubusercontent.com/Snigdah/images/main/LGTM-1.png)

## Components

| Component | Role |
|-----------|------|
| **Spring Boot Apps** | Generate logs, metrics, and traces (Order, Payment, Customer services) |
| **Alloy** | Collector/agent that collects, processes, and forwards telemetry |
| **Loki** | Log aggregation and indexing |
| **Prometheus** | Scrapes and collects time-series metrics |
| **Mimir** | Scalable long-term metrics storage with high availability |
| **Tempo** | Distributed tracing storage and request-flow visualization |
| **Grafana** | Unified visualization of logs, metrics, and traces |

## Deployment Topology

The stack is split across two VMs — one for the observability backend, one for the application.

![Deployment Topology](https://raw.githubusercontent.com/Snigdah/images/main/LGTM-2.png)

| Server | IP | Hosts |
|--------|-----|-------|
| **VM-1** (Observability Server) | `192.168.10.110` | Grafana, Loki, Tempo, Mimir |
| **VM-2** (Application Server) | `192.168.10.56` | Spring Boot App, Alloy |

Alloy on VM-2 collects telemetry (logs, metrics, traces) from the Spring Boot app and forwards it to the observability stack on VM-1.

## Getting Started

### Prerequisites

- Docker & Docker Compose
- Java 17+ and Maven/Gradle (to build the Spring Boot app)

### Run the Stack

```bash
# Clone the repository
git clone https://github.com/Snigdah/<your-repo-name>.git
cd <your-repo-name>

# Start the observability stack
docker compose up -d
```

### Access the Services

| Service | URL |
|---------|-----|
| Grafana | http://localhost:3000 |
| Prometheus | http://localhost:9090 |
| Loki | http://localhost:3100 |

## Benefits

- **Centralized Observability** — all logs, metrics, and traces in one place
- **Faster Troubleshooting** — correlate signals to find root cause quickly
- **Scalable & Reliable** — Mimir provides long-term metrics storage with high availability
- **Better Performance Visibility** — monitor real-time performance and user experience

## License

This project is licensed under the MIT License.

# Automated Dependency Update Risk Assessment and Batching Strategy

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.4-6DB33F?logo=springboot&logoColor=white)](https://spring.io/)
[![Java](https://img.shields.io/badge/Java-21_LTS-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![React](https://img.shields.io/badge/React-18.3-61DAFB?logo=react&logoColor=black)](https://react.dev/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-3.4-38B2AC?logo=tailwind-css&logoColor=white)](https://tailwindcss.com/)
[![OSV Security](https://img.shields.io/badge/Security-OSV.dev_API-red?logo=githubactions&logoColor=white)](https://osv.dev/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)

> **A B.Tech Engineering Capstone Project in the DevOps Domain**
> 
> *An explainable, risk-aware dependency update decision-support system that ingests GitHub repositories, analyzes multi-ecosystem manifests, correlates live CVE security advisories, calculates a 5-factor normalized risk score (0–100), and synthesizes safe, non-breaking update batches.*

---

## 🌟 Key Features

1. **GitHub Repository Direct Ingestion**
   - Provide any public or private GitHub repository URL (e.g. `https://github.com/spring-projects/spring-petclinic`).
   - Automatically validates repository ownership, tree hierarchy, and branches without manual dependency entry.
2. **Multi-Ecosystem Manifest Scanner**
   - **Java / Maven:** Parses `pom.xml` (handles `<properties>`, parent BOMs, and dependencyManagement).
   - **JavaScript / Node.js:** Parses `package.json` (resolves exact & semver ranges for `dependencies` and `devDependencies`).
   - **Python:** Parses `requirements.txt` and `pyproject.toml` (PEP 621 / Poetry).
   - **Gradle:** Parses `build.gradle` and `build.gradle.kts` (Groovy and Kotlin DSL coordinates).
3. **Live Package Registry Intelligence**
   - Queries official package registries in real time: npm Registry, PyPI API, and Maven Central.
   - Detects version drift: `MAJOR`, `MINOR`, `PATCH`, `UP_TO_DATE`.
   - Identifies package deprecations, official homepages, and changelogs.
4. **Live OSV & GitHub Security Advisory Integration**
   - Queries the open-source vulnerability database (`api.osv.dev`) for active CVEs and GHSAs.
   - Extracts CVSS scores, affected version boundaries, and remediation target versions.
5. **Explainable 5-Dimensional Risk Engine (0–100)**
   - Calculates mathematical composite scores across:
     - **Version Change Risk** ($0 - 25$ pts)
     - **Security Vulnerability Risk** ($0 - 55$ pts)
     - **Compatibility & Deprecation Risk** ($0 - 20$ pts)
     - **Dependency Impact & Blast Radius** ($0 - 12$ pts)
     - **Build Integrity & Conflict Risk** ($0 - 8$ pts)
   - Outputs itemized mathematical reasons (e.g., `+25 Major version jump`, `+55 Critical CVE-2023-xxxx`).
6. **Intelligent Batching Strategy Engine (Graph Optimization)**
   - Quarantines critical security vulnerabilities into immediate **Urgent Security Hotfixes**.
   - Clusters backward-compatible bugfixes into **Safe Automated Patches**.
   - Groups minor feature updates into **Staged Minor Updates**.
   - Isolates high-risk major version upgrades into **Isolated Major Upgrades** (1 per PR).
   - Generates executable CLI commands (`npm install`, `mvn versions:...`, `pip install`) and copyable Pull Request Markdown templates.
7. **Executive Audit & Scan Report**
   - Computes overall DevOps Health Index ($A/B/C/D/F$), prioritized remediation roadmap, and exports to Markdown or JSON.

---

## 🏗️ Architecture & Technology Stack

```text
Frontend (React 18 + Vite + Tailwind CSS + Recharts)
                      ↓ REST API
Backend (Spring Boot 3.3.4, Java 21, Spring Data JPA)
                      ↓
Analysis Engines:
 ├── Multi-Ecosystem Parsers (Maven, npm, Python, Gradle)
 ├── Registry Analyzers (npm, PyPI, Maven Central)
 ├── Security Engine (OSV.dev REST Client)
 ├── Explainable Risk Assessment Engine (0-100)
 └── Graph Batching Optimization Engine
                      ↓
Database (MySQL 8.0 / In-memory H2 Dev Mode)
```

---

## 🚀 Quick Start

### 1. Prerequisites
- **Java 21 LTS**
- **Node.js 18+ & npm**
- **Docker & Docker Compose** (Optional for containerized run)

---

### 2. Running Locally (Development Mode)

#### Step 1: Start Backend (Spring Boot)
```bash
cd backend
./mvnw spring-boot:run
```
*The backend starts at `http://localhost:8080` with in-memory H2 database (or MySQL if configured).*

#### Step 2: Start Frontend (React + Vite)
```bash
cd frontend
npm install
npm run dev
```
*The frontend dashboard opens at `http://localhost:5173`.*

---

### 3. Running with Docker Compose

To start the complete stack with MySQL 8.0, Spring Boot, and Nginx React frontend:

```bash
docker compose up --build
```
- **Frontend Dashboard:** `http://localhost:3000`
- **Backend API:** `http://localhost:8080`
- **MySQL Database:** `localhost:3306`

---

## 📡 REST API Documentation

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/repositories/analyze` | Ingests GitHub URL and executes full analysis pipeline |
| `GET` | `/api/repositories/recent` | Retrieves list of recently scanned repositories |
| `GET` | `/api/batches/repository/{id}` | Retrieves generated batch update plan and execution commands |
| `GET` | `/api/reports/{id}` | Retrieves executive audit report and health score |
| `GET` | `/api/presets` | Retrieves curated benchmark demo repositories |
| `GET` | `/api/health` | Service health status check |

### Example Analyze Request:
```json
POST /api/repositories/analyze
Content-Type: application/json

{
  "repositoryUrl": "https://github.com/spring-projects/spring-petclinic",
  "branch": "main"
}
```

---

## 📚 Academic Capstone Documentation (`docs/`)
Detailed architectural and engineering documentation is available in the `docs/` folder:
- [`docs/architecture.md`](docs/architecture.md): Layered architecture, component design, and data flows.
- [`docs/risk-model.md`](docs/risk-model.md): Mathematical 5-factor scoring model and threshold definitions.
- [`docs/batching-algorithm.md`](docs/batching-algorithm.md): Formal graph & greedy clustering algorithm, pseudocode, and complexity analysis.
- [`docs/use-case.md`](docs/use-case.md): UML Use Case specifications and actor interactions.
- [`docs/dfd.md`](docs/dfd.md): Level-0, Level-1, and Level-2 Data Flow Diagrams.
- [`docs/er-diagram.md`](docs/er-diagram.md): Entity-Relationship database model.
- [`docs/class-diagram.md`](docs/class-diagram.md): Object-oriented class hierarchy and design patterns.

---

## 📄 License
This project is developed as an academic capstone engineering project under the MIT License.

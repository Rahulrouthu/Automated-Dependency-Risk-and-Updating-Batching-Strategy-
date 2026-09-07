# Data Flow Diagrams (DFD)

## 1. Level-0 Context Data Flow Diagram

```mermaid
graph TD
    User["DevOps Engineer / CI System"] -->|1. Submit GitHub Repo URL| System["DepRisk Engine (Automated Risk Assessment & Batching)"]
    System -->|2. Ingest Manifest Files| GitHubAPI["GitHub REST / Raw Repository"]
    System -->|3. Query Latest Stable Releases| PackageRegistries["Package Registries (npm, PyPI, Maven Central)"]
    System -->|4. Query Security Advisories| OSVAPI["OSV.dev Vulnerability Database"]
    
    System -->|5. Return Dependency Matrix, Risk Scores & Batches| User
    System -->|6. Store Audit History & Batches| Database[("MySQL Database")]
```

---

## 2. Level-1 Data Flow Diagram (Functional Decomposition)

```mermaid
graph TD
    User["DevOps Engineer"] -->|Repository URL| P1["1.0 URL Validation & Manifest Ingestion"]
    P1 -->|Fetch Manifests| GitHub["GitHub Service"]
    GitHub -->|Raw Manifest Contents| P2["2.0 Multi-Ecosystem Manifest Parsing"]
    P2 -->|Normalized Dependencies| Store1[("D1: Extracted Dependencies")]

    Store1 --> P3["3.0 Live Version & Registry Lookup"]
    P3 -->|Query dist-tags| Registries["npm / PyPI / Maven Central"]
    Registries -->|Target Version & Deprecations| Store2[("D2: Version Intelligence")]

    Store1 --> P4["4.0 Security Vulnerability Scanner"]
    P4 -->|Query package & version| OSV["OSV.dev REST API"]
    OSV -->|CVE/GHSA & Severity| Store3[("D3: Active Vulnerabilities")]

    Store2 & Store3 --> P5["5.0 Explainable Risk Assessment Engine"]
    P5 -->|5-Factor Risk Score (0-100) & Reasons| Store4[("D4: Risk Assessments")]

    Store4 --> P6["6.0 Batching Strategy & Graph Optimization"]
    P6 -->|Quarantine Critical & Group Patches| Store5[("D5: Generated Batches")]

    Store4 & Store5 --> P7["7.0 Executive Audit & Report Generation"]
    P7 -->|Interactive UI Dashboard & PDF/MD Export| User
```

---

## 3. Level-2 Data Flow Diagram (Risk Assessment & Batching Engine)

```mermaid
graph TD
    Dep["Dependency Record"] --> S1["Calculate Version Drift Sub-score (0-25)"]
    Vuln["CVE / GHSA Records"] --> S2["Calculate Security Vulnerability Sub-score (0-55)"]
    Dep --> S3["Calculate Compatibility & Deprecation Sub-score (0-20)"]
    Dep --> S4["Calculate Topology & Blast Radius Sub-score (0-12)"]
    Dep --> S5["Calculate Build & Conflict Sub-score (0-8)"]

    S1 & S2 & S3 & S4 & S5 --> Sum["Normalized Composite Sum (0-100)"]
    Sum --> Classify["Classify Level (LOW, MEDIUM, HIGH, CRITICAL)"]
    Classify --> Explain["Assemble Itemized Explainability Reasons"]

    Explain --> Part1["Filter & Quarantine Critical Security Hotfixes"]
    Part1 --> Part2["Group Safe Low-Risk Patches by Ecosystem"]
    Part2 --> Part3["Group Medium-Risk Minor Releases"]
    Part3 --> Part4["Isolate Major Breaking Upgrades (1-per-batch)"]
    Part4 --> Out["Export Batch DTOs + Execution CLI Commands"]
```

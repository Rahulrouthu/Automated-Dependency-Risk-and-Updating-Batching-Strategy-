# Mathematical Risk Assessment Model

## 1. Objective & Design Philosophy
Traditional dependency update tools classify risks binary-wise (e.g., outdated vs current). The **DepRisk** assessment engine models dependency update risk across **5 orthogonal dimensions** with configurable weights, outputting an explainable composite risk score $R \in [0, 100]$:

$$R = \min\left(100, \max\left(0, \sum_{i=1}^{5} W_i \cdot S_i\right)\right)$$

where:
- $W_i$ represents the weight assigned to risk dimension $i$.
- $S_i \in [0, 1]$ represents the normalized sub-score of dimension $i$.

---

## 2. Risk Dimensions & Mathematical Formulations

### 2.1 Dimension 1: Version Change & API Drift Risk ($W_{ver} = 25$)
Captures the semantic version distance between the current version $V_{curr}$ and the latest available stable target version $V_{target}$:

$$S_{ver} = \begin{cases} 
1.00 & \text{if } V_{target} \text{ is a MAJOR version jump} \\
0.45 & \text{if } V_{target} \text{ is a MINOR version bump} \\
0.15 & \text{if } V_{target} \text{ is a PATCH version bump} \\
0.00 & \text{if } V_{target} = V_{curr} \text{ (Up-to-Date)}
\end{cases}$$

### 2.2 Dimension 2: Security Vulnerability Risk ($W_{sec} = 35$)
Quantifies the severity of unpatched security advisories active in the current version:

$$S_{sec} = \begin{cases}
1.57 & \text{if CRITICAL vulnerability detected (CVSS 9.0-10.0, e.g. RCE) } [\text{Yields } 55 \text{ pts}] \\
1.00 & \text{if HIGH vulnerability detected (CVSS 7.0-8.9)} [\text{Yields } 35 \text{ pts}] \\
0.57 & \text{if MODERATE advisory detected (CVSS 4.0-6.9)} [\text{Yields } 20 \text{ pts}] \\
0.23 & \text{if LOW notice detected (CVSS 0.1-3.9)} [\text{Yields } 8 \text{ pts}] \\
0.00 & \text{if no known vulnerabilities reported}
\end{cases}$$

### 2.3 Dimension 3: Compatibility & Deprecation Risk ($W_{comp} = 20$)
Evaluates ecosystem deprecation notices and major transitive interface breaking changes:

$$S_{comp} = \begin{cases}
1.00 & \text{if package is explicitly deprecated in package registry} \\
0.75 & \text{if package undergoes major framework overhaul (e.g. Spring 5 to 6, React 17 to 18)} \\
0.00 & \text{otherwise}
\end{cases}$$

### 2.4 Dimension 4: Dependency Impact & Blast Radius ($W_{dep} = 12$)
Reflects the architectural position and runtime footprint in the dependency topology:

$$S_{dep} = \begin{cases}
1.00 & \text{if Direct Runtime / Production Dependency} \\
0.35 & \text{if Development / Test Scope Dependency} \\
0.20 & \text{if Transitive Dependency}
\end{cases}$$

### 2.5 Dimension 5: Build Integrity & Conflict Risk ($W_{build} = 8$)
Reflects manifest inconsistencies, lockfile mismatches, or multi-version resolution conflicts:

$$S_{build} = \begin{cases}
1.00 & \text{if version conflict / lockfile mismatch detected} \\
0.00 & \text{if build and manifest state are consistent}
\end{cases}$$

---

## 3. Risk Level Classification Thresholds

| Score Range | Risk Level | Description & Strategy |
| :--- | :--- | :--- |
| **$0 - 24$** | `LOW` | **Safe Auto-Patch:** Group into automated batch PR, merge after CI test suite passes. |
| **$25 - 49$** | `MEDIUM` | **Staged Minor Upgrade:** Group into minor feature batch, verify staging tests. |
| **$50 - 74$** | `HIGH` | **Isolated Migration:** Isolate update into single PR, review changelog and migration guide. |
| **$75 - 100$** | `CRITICAL` | **Immediate Hotfix:** Urgent remediation for active CVEs or severe breaking conflicts. |

---

## 4. Explainability Architecture
For every score computed, the engine exports an itemized breakdown array:
```json
[
  "+25 Major version jump (5.3.18 -> 6.2.3) introduces breaking API changes",
  "+55 Critical security vulnerability detected (GHSA-j2ge-4vdv-vq5u: Log4Shell RCE)",
  "+12 Core runtime production dependency (high blast radius)"
]
```

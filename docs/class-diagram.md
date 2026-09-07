# Object-Oriented Class Diagram & Design Patterns

## 1. Class Hierarchy & Design Patterns

```mermaid
classDiagram
    class DependencyParser {
        <<interface>>
        +getSupportedEcosystem() EcosystemType
        +canHandle(String filePath) boolean
        +parse(String content, String filePath, DependencyFileEntity file) List~DependencyEntity~
    }

    class MavenPomParser {
        +parse()
        -parseProperties()
    }
    class NpmPackageJsonParser {
        +parse()
        -cleanSemVer()
    }
    class PythonRequirementsParser {
        +parse()
    }
    class GradleBuildParser {
        +parse()
    }
    class ParserFactory {
        -List~DependencyParser~ parsers
        +getParserForFile(String path) Optional~DependencyParser~
    }

    DependencyParser <|.. MavenPomParser
    DependencyParser <|.. NpmPackageJsonParser
    DependencyParser <|.. PythonRequirementsParser
    DependencyParser <|.. GradleBuildParser
    ParserFactory o-- DependencyParser

    class RiskEngine {
        -RiskWeightsConfig config
        +calculateRisk(DependencyEntity, List~VulnerabilityEntity~, boolean) RiskAssessmentEntity
        -generateRecommendation() String
    }

    class BatchOptimizationEngine {
        +generateOptimizedBatches(List~DependencyDetailDto~) List~BatchDto~
        -generateCommand() String
        -generatePrBody() String
    }

    class RepositoryScannerService {
        -GitHubApiClient gitHubClient
        -ParserFactory parserFactory
        -VersionAnalysisService versionService
        -VulnerabilityService vulnService
        -RiskEngine riskEngine
        -BatchingService batchingService
        +scanRepository(AnalyzeRequestDto) AnalysisResultDto
    }

    RepositoryScannerService --> ParserFactory
    RepositoryScannerService --> RiskEngine
    RepositoryScannerService --> BatchOptimizationEngine
```

---

## 2. Software Design Patterns Implemented
1. **Factory Pattern (`ParserFactory`)**: Encapsulates dynamic manifest parser instantiation and selection based on file extension and content structure.
2. **Strategy Pattern (`DependencyParser`, `BatchOptimizationEngine`)**: Isolates ecosystem-specific parsing and batch optimization algorithms, allowing new ecosystems (e.g. Go, Rust) to be added without modifying core pipeline controllers.
3. **Data Transfer Object (DTO) Pattern**: Decouples internal JPA entities from external REST serialization schemas to prevent over-fetching and recursive relationship cycles.
4. **Adapter / Client Pattern (`GitHubApiClient`, `OsvVulnerabilityClient`)**: Wraps heterogeneous external REST endpoints and normalizes external JSON payloads into unified internal domain models.

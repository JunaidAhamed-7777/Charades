# Project structure

Start with [README](README.md) for installation and [PRODUCT](PRODUCT.md) for reconciled game context. AI coding agents should also read [AGENTS](AGENTS.md).

```text
Charades/
├── AGENTS.md                    Agent workflow and product invariants
├── PRODUCT.md                   Current offline-game context
├── DESIGN.md                    Shared visual direction
├── UX-CONTRACT.md               Shared interaction behavior
├── README.md                    Installation and project entry point
├── LICENSE                      GNU GPL v3
├── app/
│   ├── build.gradle.kts          Android app configuration
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── assets/
│       │   │   ├── categories/  Complete versioned JSON catalog
│       │   │   ├── images/      Complete local clue-image collection
│       │   │   ├── privacy_policy.txt
│       │   │   ├── terms_of_service.txt
│       │   │   └── license.txt
│       │   ├── java/com/yourdomain/freecharades/
│       │   │   ├── data/        Local asset repository
│       │   │   ├── domain/      Models and session rules
│       │   │   ├── ui/          Compose screens/navigation/components/theme
│       │   │   └── util/        Shared helpers
│       │   └── res/             Android resources and launcher branding
│       └── test/                Local JVM tests
├── docs/                        Decisions, architecture, content, policies
├── scripts/                     Developer import/validation/documentation tools
├── gradle/                      Version catalog and Gradle wrapper
└── gradlew / gradlew.bat         Reproducible Gradle entry points
```

All sections/images are read from the APK. There is no remote catalog, content database, preferences service, or player content-install step. See [architecture](docs/ARCHITECTURE.md) and [content maintenance](docs/CONTENT.md).

# CSE465-assignment-1
CSE465: Mobile Computing | Spring 2026 | programming assignment 1
## Repository Structure

```text
.
├── CSE465_PA1.pdf
│   └── Assignment specification and requirements
│
├── feature_summary01.csv
│   └── Feature analysis results from collected sensor data
│
├── feature_summary2.csv
│   └── Additional feature analysis results
│
├── har_data/
│   └── Sensor recordings used for activity analysis and testing
│
├── har_data01/
│   └── Initial activity data collection sessions
│
├── har_data2/
│   └── Additional activity data collection sessions
│
├── mobile_pa1/
│   ├── app/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/
│   │   │   │   │   └── Kotlin source code
│   │   │   │   ├── res/
│   │   │   │   │   └── Layouts, strings, and UI resources
│   │   │   │   └── AndroidManifest.xml
│   │   │   │
│   │   │   ├── test/
│   │   │   │   └── Unit tests
│   │   │   │
│   │   │   └── androidTest/
│   │   │       └── Instrumentation tests
│   │   │
│   │   ├── build.gradle.kts
│   │   └── proguard-rules.pro
│   │
│   ├── gradle/
│   │   ├── wrapper/
│   │   ├── gradle-daemon-jvm.properties
│   │   └── libs.versions.toml
│   │
│   ├── build.gradle.kts
│   ├── gradle.properties
│   ├── settings.gradle.kts
│   ├── gradlew
│   └── gradlew.bat
│
└── README.md
    └── Project documentation
```

### Main Components

**Android Application (`mobile_pa1/`)**

* Data collection interface for recording labeled sensor data
* Real-time visualization of accelerometer and gyroscope signals
* Rule-based Human Activity Recognition system
* Real-time activity prediction display

**Sensor Datasets (`har_data*`)**

* Accelerometer and gyroscope recordings collected during experiments
* Data for standing still, walking, running, going upstairs, and going downstairs

**Feature Analysis (`feature_summary*.csv`)**

* Extracted sensor features used to analyze activity patterns
* Used to design classification rules and thresholds

**Assignment Documentation**

* Original assignment specification (`CSE465_PA1.pdf`)
* Project description and repository documentation (`README.md`)

```
```

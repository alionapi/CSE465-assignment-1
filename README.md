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


## Overview

This project implements a smartphone-based Human Activity Recognition (HAR) system using only the device's accelerometer and gyroscope sensors. Instead of using a pretrained machine learning model or Android's built-in activity recognition APIs, the system uses manually designed features and rule-based classification to recognize human activities in real time.

The application supports:

* Standing Still
* Walking
* Running
* Going Upstairs
* Going Downstairs

## Features

### Data Collection

The app provides a data collection interface that allows users to:

* View real-time accelerometer data
* View real-time gyroscope data
* Select an activity label
* Start and stop recording sessions
* Save labeled sensor data for later analysis

### Real-Time Activity Recognition

The app continuously processes sensor data and applies handcrafted rules based on extracted motion features to classify the user's current activity.

Detected activities are displayed directly on the screen in real time.

## Sensor Data

The system uses data from:

* Accelerometer
* Gyroscope

Multiple recordings were collected for each activity and used to analyze motion patterns and design classification rules.

## Feature Analysis

Several motion-related features were extracted from the sensor signals, including statistics derived from acceleration and rotational movement.

Feature analysis was used to identify characteristics that distinguish:

* Still vs. moving activities
* Walking vs. running
* Upstairs vs. downstairs movement

The resulting thresholds and rules were integrated into the real-time recognition pipeline.

## Screenshots

Example application screenshots can be found in the `Screenshots` directory.

## Technologies

* Android Studio
* Kotlin
* Android Sensor Framework
* Accelerometer Sensor
* Gyroscope Sensor
* Gradle

## Course Information

**Course:** Mobile Computing (CSE465)
**Institution:** Ulsan National Institute of Science and Technology (UNIST)

## Disclaimer

This project was developed for educational purposes as part of a university course assignment.

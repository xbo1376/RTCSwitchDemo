# RTCSwitchDemo - Tencent Cloud Real-Time Communication Live Streaming Application

## Project Overview

RTCSwitchDemo is an Android real-time audio and video live streaming application based on Tencent Cloud's LiteAVSDK_TRTC, supporting room creation, room list display, real-time audio and video calling, and other features.

## Features

- **Room Management**: Create live rooms, view room lists
- **Real-time Audio/Video**: Real-time audio and video calling based on Tencent Cloud TRTC SDK
- **Modular Architecture**: Multi-module design for easy feature extension and maintenance
- **Network Requests**: HTTP communication using OkHttp

## Project Structure

```
RTCSwitchDemo/
├── App/                 # Main application module
├── Basic/Live/          # Live streaming functionality module
├── Common/              # Common components module
├── Debug/               # Debug tools module
└── README.md            # Project documentation
```

## Technology Stack

- **Development Language**: Java
- **SDK**: Tencent Cloud LiteAVSDK_TRTC
- **Network Framework**: OkHttp
- **Build Tool**: Gradle
- **Target Platform**: Android API 21+

## Runtime Requirements

- Android 5.0+ (API Level 21+)
- Camera and microphone permissions
- Network connection (for audio/video transmission)

## Quick Start

### 1. Environment Setup

Ensure you have installed:
- Android Studio Arctic Fox+
- Android SDK Platform 34
- Java 8+

### 2. Project Configuration

1. Clone or download the project to your local machine
2. Open the project in Android Studio
3. Wait for Gradle sync to complete

### 3. Server Configuration

The project uses the following server address (can be modified in `NetworkManager.java`):
```java
private static final String BASE_URL = "http://127.0.0.1:8376";
```

### 4. Compile and Run

1. Connect an Android device or start an emulator
2. Select the App module
3. Click the Run button (▶️)

## Function Description

### Room List Interface
- Display all available live rooms
- Show room ID, creation time, room type, and other information
- Support clicking to join rooms

### Room Creation
- Enter user ID to create new rooms
- Support different room types
- Jump to live interface after successful creation

### Real-time Audio/Video
- Based on Tencent Cloud TRTC SDK
- Support high-definition audio and video calling
- Low-latency real-time transmission

## Permission Requirements

The application requires the following permissions:
- Camera permission (for video capture)
- Microphone permission (for audio capture)
- Network permission (for audio/video transmission)
- Storage permission (for log recording)

## Development Guide

### Main File Structure

```
Basic/Live/src/main/java/com/tencent/trtc/live/
├── RoomListActivity.java    # Room list main interface
├── RoomAdapter.java         # Room list adapter
├── net/NetworkManager.java  # Network request management
└── rtc/TRTCEngineImpl.java  # TRTC engine implementation
```

### Custom Configuration

You can modify the following configurations in `build.gradle`:
```gradle
ext {
    compileSdkVersion = 34
    minSdkVersion = 21
    targetSdkVersion = 34
    liteavSdk = "com.tencent.liteav:LiteAVSDK_TRTC:latest.release"
}
```

## Important Notes

1. Ensure the device supports camera and microphone functionality
2. Please use a stable network environment for testing
3. Server address may need to be modified based on actual deployment
4. Relevant permissions need to be authorized on first run

## Technical Support

For issues please refer to:
- Tencent Cloud TRTC official documentation
- Android development documentation
- Project code comments

## Version Information

- Version: v1.0
- Compile SDK: 34
- Minimum Support: API 21
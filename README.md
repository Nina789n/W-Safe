# Women Safety Box

<h2 style="margin-bottom: 0;" align="center">Women Safety Box</h2>

<p align="center">
<img src="https://user-images.githubusercontent.com/15268903/92101573-23b92100-edff-11ea-9a01-73df323c4020.png" height="100" width="100">
<h3 style="margin-top: 0;" align="center">Women safety app using Background Voice Recognition</h3>
<h5 style="margin-top: 0;" align="center">[ Modernized for Android 14 - 2024 </h5>
</p>

## 🚀 Modernized Features

This Android app has been **completely modernized** for current Android development standards:

### ✅ **Modernization Updates:**
- **AndroidX Migration**: All Support Library components updated to AndroidX
- **Target SDK**: Updated to Android 14 (API 34)
- **Gradle Plugin**: Updated to 8.1.4 (latest stable)
- **Dependencies**: All libraries updated to latest versions
- **Material Design**: Updated to Material Components 3
- **Permission Handling**: Modern Android permission system
- **Build Tools**: Latest Android build tools and optimizations

### 🔧 **Core Features:**
- **Voice Recognition**: Shouts "HELP" to trigger emergency
- **Emergency Calls**: Automatically calls registered contacts
- **SMS Alerts**: Sends location via SMS to guardians
- **GPS Tracking**: Real-time location sharing
- **Background Service**: 24/7 voice monitoring
- **Google Maps**: Nearby police stations and hospitals
- **Safety Tips**: First aid and self-defense information
- **Auto-start**: Ensures service runs after device restart

## 📱 App Screenshots

<table>
  <tr>
     <td align="center">Registration</td>
     <td align="center">Background Voice Recognition, Help Mode, Auto Call & SMS</td>
  </tr>
  <tr>
    <td valign="top"><img src="https://user-images.githubusercontent.com/15268903/44601093-3d313680-a7fd-11e8-9e94-ba3b77d0dfc3.gif"></td>
    <td valign="top" align="center"><img src="https://user-images.githubusercontent.com/15268903/44601241-ab75f900-a7fd-11e8-92a9-28bbce9630ca.gif"></td>
  </tr>
 </table>

## 🛠️ Build Requirements

### **System Requirements:**
- **Java JDK**: 11 or higher
- **Android Studio**: 2023.1.1 or higher
- **Android SDK**: API 34 (Android 14)
- **Gradle**: 8.4 (automatically managed)

### **Required Permissions:**
- Location access (GPS)
- Phone calls
- SMS sending
- Microphone access
- Internet connectivity
- System alert windows
- Foreground service
- Wake lock

## 🚀 Quick Start

### **Method 1: Android Studio (Recommended)**

1. **Install Android Studio** from [developer.android.com](https://developer.android.com/studio)
2. **Open Android Studio**
3. **File → Open** → Select this project folder
4. **Wait for Gradle sync** (first time may take 5-10 minutes)
5. **Build → Build Bundle(s) / APK(s) → Build APK(s)**
6. **Install on device** via USB or download APK

### **Method 2: Command Line**

```bash
# Set JAVA_HOME environment variable
export JAVA_HOME=/path/to/your/jdk

# Build the project
./gradlew clean build

# Install on connected device
./gradlew installDebug
```

### **Method 3: Direct APK Download**

The built APK will be available at:
```
app/build/outputs/apk/debug/app-debug.apk
```

## 📋 Setup Instructions

### **1. Install Java JDK**
```bash
# Download from: https://adoptium.net/
# Or use your system's package manager
```

### **2. Set Environment Variables**
**Windows:**
```cmd
set JAVA_HOME=C:\Program Files\Java\jdk-11.0.x
set PATH=%JAVA_HOME%\bin;%PATH%
```

**macOS/Linux:**
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.x/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
```

### **3. Install Android SDK**
- Download Android Studio
- Open SDK Manager
- Install Android SDK Platform 34
- Install Android Build Tools

## 🔧 Configuration

### **Google Maps API Key**
The app includes a Google Maps API key for development. For production:
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project
3. Enable Maps SDK for Android
4. Create API key
5. Replace in `app/src/debug/res/values/google_maps_api.xml`

### **Emergency Contacts**
1. Open the app
2. Go to "Register" section
3. Add your emergency contact numbers
4. Test the voice recognition with "HELP"

## 📁 Project Structure

```
Women_Safety_Box-master/
├── app/                          # Main application module
│   ├── src/main/
│   │   ├── java/com/womensafety/shajt3ch/
│   │   │   ├── MainActivity.java     # Main UI
│   │   │   ├── MyService.java        # Background service
│   │   │   ├── Safety.java           # Safety tips
│   │   │   ├── MapsActivity.java     # Google Maps
│   │   │   └── Register.java         # Contact registration
│   │   └── res/                      # Resources
│   └── build.gradle                  # App dependencies
├── speech/                         # Custom speech recognition
├── build.gradle                    # Project configuration
└── gradle.properties               # Build settings
```

## 🔄 Modernization Changes

| **Component** | **Before** | **After** | **Improvement** |
|---------------|------------|-----------|-----------------|
| **Target SDK** | 26 (Android 8.0) | 34 (Android 14) | ✅ Latest features |
| **Support Library** | `com.android.support` | `androidx` | ✅ Modern AndroidX |
| **Gradle Plugin** | 3.1.4 | 8.1.4 | ✅ Latest build tools |
| **Material Design** | AppCompat | Material Components 3 | ✅ Modern UI |
| **Permissions** | RxPermissions | Modern Android | ✅ Better security |
| **Testing** | JUnit 4.12 | JUnit 4.13.2 | ✅ Updated testing |

## 🚨 Important Notes

1. **Background Service**: The app runs a background service for voice recognition
2. **Permissions**: Grant all requested permissions for full functionality
3. **Auto-start**: Enable auto-start for the app in device settings
4. **Battery Optimization**: Disable battery optimization for the app
5. **Google Services**: Ensure Google Play Services is installed

## 📄 License

```
MIT License

Copyright (c) 2024 Women Safety Box

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📞 Support

For issues and questions:
- Create an issue on GitHub
- Check the troubleshooting section
- Ensure all requirements are met

---

**⚠️ Safety Notice**: This app is designed for emergency situations. Always test the functionality in a safe environment before relying on it in an emergency.




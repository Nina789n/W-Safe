<div align="center"><img src="screenshots/w-safe-logo.png" alt="W-SAFE logo" width="180">W-SAFE – Women Safety Box
A Women’s Safety and Emergency Assistance Android Application
A mobile application designed to make essential safety features easier to access, including emergency assistance, trusted contacts and location-based support.
</div>About the Project
W-SAFE (Women Safety Box) is an Android application developed to support women's personal safety. It brings key safety-related functions together in one place, allowing users to access emergency options, manage contacts and use location-related features through a simple mobile interface.

The application includes user registration and login, a home dashboard, emergency options, trusted-contact features and live-location access. It also provides access to information and nearby emergency services.
Screenshots
<div align="center">Login Screen	Create Account
<img src="screenshots/login-screen.png" alt="W-SAFE login screen" width="220">	<img src="screenshots/registration-screen.png" alt="W-SAFE create account screen" width="220">


Home Dashboard
<img src="screenshots/home-dashboard.png" alt="W-SAFE home dashboard" width="220"></div>Key Features
- User Login: Login interface with Admin and User account selection.
- User Registration: Create an account by entering personal and contact details.
- Emergency SOS: Quick access to the emergency SOS feature.
- Fake Call / SMS: Access to the fake-call and SMS safety option shown on the dashboard.
- Trusted Contacts: Access to trusted-contact functionality.
- My Contacts: Access to the user's saved contacts.
- Live Location: Access to location-related functionality.
- Safety Instructions: A dedicated section for safety information.
- Maps and Nearby Services: Location-based map functionality for viewing relevant nearby emergency services.
Technology Stack
Area	Technology
Platform	Android
Programming Language	Java
UI Design	XML
Development Environment	Android Studio
Local Data Storage	SQLite and SharedPreferences
Maps	Google Maps SDK
Location	Android Location Services / Fused Location Provider
Build System	Gradle


Application Modules
Module	Purpose
Login	Provides the login interface and account-type selection.
User Registration	Allows users to create an account.
Home Dashboard	Provides access to the main safety features.
Emergency SOS	Provides access to emergency assistance functionality.
Fake Call / SMS	Provides the fake-call and SMS safety option.
Trusted Contacts	Provides access to trusted-contact management.
My Contacts	Provides access to saved contacts.
Live Location	Provides access to location functionality.
Maps	Displays location information and relevant nearby services.
Admin Dashboard	Provides the application's administrative interface.


Getting Started
Prerequisites
- Android Studio
- Java Development Kit (JDK) compatible with the project
- Android SDK required by the project
- Android Emulator or a physical Android device
Setup
1. Clone or download this repository.
2. Open Android Studio.
3. Select File → Open and choose the W-SAFE project folder.
4. Allow Gradle sync to finish.
5. Configure the Google Maps API key if the Maps feature is enabled.
6. Connect an Android device or start an emulator.
7. Build and run the application.
Permissions
Depending on which features are enabled in the installed build, Android may request permissions such as:
- Location access
- Phone access for calling functionality
- SMS access if the app sends SMS directly
- Internet access for map services
Grant only the permissions required by the features you use. Permission availability and background-location behavior depend on the Android version and the app's configuration.
Project Structure
The following is a representative structure. Activity and resource filenames may differ in the source project.
W-SAFE/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── <package-name>/
│           │       ├── LoginActivity.java
│           │       ├── UserRegistrationActivity.java
│           │       ├── MainActivity.java
│           │       ├── SOSActivity.java
│           │       ├── TrustedContactsActivity.java
│           │       ├── AdminDashboardActivity.java
│           │       └── MapsActivity.java
│           ├── res/
│           │   ├── drawable/
│           │   ├── layout/
│           │   └── values/
│           └── AndroidManifest.xml
├── screenshots/
│   ├── w-safe-logo.png
│   ├── login-screen.png
│   ├── registration-screen.png
│   └── home-dashboard.png
├── build.gradle
├── gradle.properties
└── README.md
Future Enhancements
- Improve emergency notification and location-sharing workflows.
- Add additional safety resources and guidance.
- Extend testing across Android versions and device types.
- Improve accessibility and usability.
Safety Disclaimer
W-SAFE is an academic software project and should not be treated as a replacement for official emergency services. Test the application's functions in a safe environment. Always contact local emergency services directly when immediate assistance is required.
<div align="center">W-SAFE – Women Safety Box

Safety, support and empowerment through technology.
</div>




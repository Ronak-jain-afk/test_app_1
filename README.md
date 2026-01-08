# Permission Scanner

Permission Scanner is an Android application designed to help users identify and manage privacy risks on their devices. It scans installed applications, analyzes their granted permissions, and categorizes them by risk level (High, Medium, Low), empowering users to take control of their data privacy.

## Features

*   **Risk Dashboard**: Get an immediate overview of your device's security status, including the count of high-risk apps and any recently detected changes.
*   **Permission Analysis**: Scans all installed apps and identifies dangerous permissions such as Camera, Microphone, Location, Contacts, SMS, and Storage access.
*   **Risk Categorization**:
    *   🔴 **High Risk**: Apps with critical permissions (e.g., precise location, background microphone).
    *   🟠 **Medium Risk**: Apps with potentially sensitive permissions.
    *   🟢 **Low Risk**: Apps with few or no dangerous permissions.
*   **Detailed Breakdown**: View exactly which permissions an app has and understand *why* they are risky through clear, user-friendly explanations.
*   **Change Tracking**: Detects new risks if an app updates and requests more permissions than before.
*   **Sorting & Filtering**: Easily sort apps by Name or Risk Level to find what you're looking for.
*   **Quick Actions**: Long-press on an app to view details or navigate directly to system settings to revoke permissions.
*   **Modern UI**: Built with Material Design 3 components for a clean and intuitive experience.

## Tech Stack

*   **Language**: Java
*   **Architecture**: MVVM (Model-View-ViewModel)
*   **Local Database**: Room Persistence Library
*   **UI Components**:
    *   RecyclerView with `DiffUtil` for efficient list updates
    *   ViewPager2 & TabLayout
    *   Material Components (Cards, AppBars, Toolbars)
*   **Threading**: Asynchronous background scanning
*   **Lifecycle**: LiveData & ViewModel

## Installation

1.  Clone the repository:
    ```bash
    git clone https://github.com/yourusername/permission-scanner.git
    ```
2.  Open the project in **Android Studio**.
3.  Sync the project with Gradle files.
4.  Run the application on an Android Emulator or a physical device (Requires Android 7.0 / API Level 24 or higher).

## Usage

1.  **Launch the App**: The app will automatically perform an initial scan of your installed applications.
2.  **View Dashboard**: Check the summary of high-risk apps.
3.  **Browse Lists**: Swipe between tabs to see High, Medium, and Low risk lists.
4.  **Inspect an App**: Tap on any app card to see a detailed list of its permissions and a "Why is this risky?" breakdown.
5.  **Manage Permissions**: Click "Open App Settings" in the detail view to go to the Android system settings for that app, where you can manually revoke permissions.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

[MIT License](LICENSE)

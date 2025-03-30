# RC Car Android Control App (Modified SimpleBluetoothTerminal)

This project modifies the `SimpleBluetoothTerminal` Android application to act as a dedicated controller for an RC car using an HC-05 Bluetooth module connected to an Arduino.

Instead of a general terminal, the app provides W, A, S, D buttons that send specific commands ('F', 'B', 'L', 'R') over Bluetooth.

The modified Android project code resides in the `SimpleBluetoothTerminal` subdirectory.

## Prerequisites

1.  **Android Studio:** You need Android Studio installed on your computer (download from the [official Android Developers site](https://developer.android.com/studio)).
2.  **Android Device:** An Android phone or tablet running Android 5.0 (Lollipop) or newer.
3.  **Developer Options & USB Debugging:** Enable Developer Options and USB Debugging on your Android device. (Usually done by tapping the "Build number" in Settings -> About phone multiple times, then finding Developer Options in the main Settings menu).
4.  **HC-05 Paired:** Your HC-05 Bluetooth module must be paired with your Android device through the device's Bluetooth settings.
5.  **Arduino Sketch:** Your Arduino must be running a sketch configured to receive the 'F', 'B', 'L', 'R' commands via its serial connection to the HC-05 and control the RC car's motors accordingly. Ensure the baud rate matches (the app uses the default 9600).

## Build Instructions

1.  **Open Project:** Launch Android Studio.
2.  Select **File** > **Open** (or "Open an Existing Project").
3.  Navigate to *this* directory (`/Volumes/T7/hackathon`) and select the **`SimpleBluetoothTerminal`** folder. Click **Open**.
4.  **Sync Project:** Android Studio will likely need to sync the project with Gradle. This might take a few moments and require an internet connection. Wait for it to complete (check the status bar at the bottom).
5.  **Build:** Once synced, build the project by selecting **Build** > **Make Project** from the top menu.

## Install and Run on Your Phone

1.  **Connect Device:** Connect your Android device to your computer using a USB cable. Ensure USB Debugging is enabled and authorize the computer if prompted on your device.
2.  **Select Device:** In Android Studio, you should see your device name appear in the toolbar near the Run button (a green triangle). If multiple devices/emulators are connected, select your phone from the dropdown list.
3.  **Run App:** Click the **Run 'app'** button (the green triangle ▶️) in the Android Studio toolbar, or select **Run** > **Run 'app'** from the top menu.
4.  **Installation:** Android Studio will build the app (if needed), transfer the APK file to your phone, and install it.
5.  **Launch:** The app should launch automatically on your phone after installation.

## Controlling the RC Car

1.  **Scan/Select Device:** When the app launches, it will show the Bluetooth device scanning screen. Ensure your HC-05 module is powered on.
2.  Find your paired HC-05 module in the list and tap on it.
3.  **Connect:** The app will switch to the control screen (with the W/A/S/D buttons) and attempt to connect. You'll see brief "Connecting..." and then "Connected" pop-up messages (Toasts).
4.  **Control:** Once connected, the W, A, S, D buttons will become enabled. Tap the buttons to send the corresponding commands ('F', 'A', 'S', 'D' map to 'F', 'L', 'B', 'R') to your RC car.
5.  **Disconnect:** To disconnect, simply close the app or navigate back from the control screen. 
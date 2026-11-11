# Bluetooth Pad

Android touch gamepad with the same landscape layout as the supplied controller page. It sends a standard Bluetooth HID gamepad report directly from the phone; no PC server is used.

## Build APK on GitHub

1. Create a GitHub repository and upload the contents of this folder.
2. Open **Actions**, select **Build Android APK**, and run the workflow (or push to `main`).
3. Download `BluetoothPad-debug-apk` from the completed run. Install `app-debug.apk` on Android.
4. Launch the app. In Windows Bluetooth settings choose **Add device > Bluetooth**, then select the phone/gamepad entry if shown.

Requires Android 9+ and a phone that exposes Android's Bluetooth HID Device profile. Some manufacturers disable that profile. Windows games that require XInput may not accept a generic Bluetooth HID pad.

## Source

The UI is drawn natively so touch events can be sent directly to Android's public Bluetooth HID Device API. The original `controller.html` and `server.py` are not required.

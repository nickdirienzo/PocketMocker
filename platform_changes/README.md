# PocketMocker Android Platform Modifications

Each of the Managers implement a pubsub to the userlevel PocketMocker app, which receives mocked data to replay (and stop sending real data to apps).

## Usage

Replace AOSP's `frameworks/base/location/java/android/location/LocationManager.java` with `./location/LocationManager.java`.

Replace AOSP's `frameworks/base/core/java/android/hardware/SystemSensorManager.java` with `./sensor/SystemSensorManager.java`.

Replace AOSP's `frameworks/base/telephony/java/android/telephony/TelephonyManager.java` with `./telephony/TelephonyManager.java`.

Replace AOSP's `frameworks/base/wifi/java/android/net/wifi/WifiManager.java` with `./wifi/WifiManager.java`.

Replace AOSP's `frameworks/base/wifi/java/android/net/wifi/ScanResult.java` with `./wifi/ScanResult.java`.

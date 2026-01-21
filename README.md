# Cozy Draw Protect

## Crash Fix (Play Button)
- **Root cause (from Logcat):** launching Play attempted to start `GameActivity` when it wasn't resolvable at runtime.
- **Fix:** we now validate that the intent resolves before launching and surface any error via a global dialog instead of hard-crashing. We also added a global error handler and a safe navigation helper so invalid routes or activity launches never crash the UI.

> The Logcat stacktrace for the shared library crash is captured in `docs/logcat_gdx_crash.txt` for future reference.

## If you see "couldn't load shared library gdx"
- Ensure you are building the `:android` module and that it depends on `:core` only (no desktop backends).
- Make sure all libGDX native runtime dependencies are present for **armeabi-v7a**, **arm64-v8a**, **x86**, and **x86_64**.
- Prefer an **x86_64** emulator image (Android Studio: **x86_64** system image) to match packaged natives.
- If you add ABI filters, they must include the ABI of the device/emulator you are launching on.

## UI & Design System
This project now uses a Material 3-based design system:
- **Color palette** lives in `android/src/main/java/com/cozyprotect/ui/theme/Color.kt`.
- **Typography** lives in `android/src/main/java/com/cozyprotect/ui/theme/Type.kt`.
- **Shapes** live in `android/src/main/java/com/cozyprotect/ui/theme/Shape.kt`.

To change colors, update `Color.kt` values and the light/dark mappings inside `Theme.kt`.

## Replacing the Mochi Illustration
The Mochi mascot is drawn with Compose Canvas in `android/src/main/java/com/cozyprotect/ui/components/Illustrations.kt`:
- Update sizes and shapes inside `MochiMascot` for a new silhouette.
- Adjust colors in `Color.kt` to quickly re-theme Mochi.

## Screen Layout Notes
- All screens use `ScreenScaffoldWithBannerAd` to reserve consistent padding for the AdMob banner.
- Stage Select supports list/grid toggling and displays skeletons while packs load.

## Tests
- Instrumentation test added in `android/src/androidTest/java/com/cozyprotect/MainMenuNavigationTest.kt` to validate the Play flow.

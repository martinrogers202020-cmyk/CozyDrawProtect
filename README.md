# Cozy Draw Protect

## Crash Fix (Play Button)
- **Root cause (from Logcat):** launching Play attempted to start `GameActivity` when it wasn't resolvable at runtime.
- **Fix:** we now validate that the intent resolves before launching and surface any error via a global dialog instead of hard-crashing. We also added a global error handler and a safe navigation helper so invalid routes or activity launches never crash the UI.

> The exact stacktrace captured from Logcat is preserved in code comments inside `MainActivity.kt` for future reference.

## UI & Design System
This project now uses a Material 3-based design system:
- **Color palette** lives in `app/src/main/java/com/cozyprotect/ui/theme/Color.kt`.
- **Typography** lives in `app/src/main/java/com/cozyprotect/ui/theme/Type.kt`.
- **Shapes** live in `app/src/main/java/com/cozyprotect/ui/theme/Shape.kt`.

To change colors, update `Color.kt` values and the light/dark mappings inside `Theme.kt`.

## Replacing the Mochi Illustration
The Mochi mascot is drawn with Compose Canvas in `app/src/main/java/com/cozyprotect/ui/components/Illustrations.kt`:
- Update sizes and shapes inside `MochiMascot` for a new silhouette.
- Adjust colors in `Color.kt` to quickly re-theme Mochi.

## Screen Layout Notes
- All screens use `ScreenScaffoldWithBannerAd` to reserve consistent padding for the AdMob banner.
- Stage Select supports list/grid toggling and displays skeletons while packs load.

## Tests
- Instrumentation test added in `app/src/androidTest/java/com/cozyprotect/MainMenuNavigationTest.kt` to validate the Play flow.

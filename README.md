ClipSync - Minimal Android Foreground Service to POST clipboard to a server.

Instructions:
1. Upload (push) this repository to GitHub on branch 'main'.
2. GitHub Actions workflow (.github/workflows/android-build.yml) will install Android cmdline tools and Gradle on the runner, then build a Debug APK.
3. After workflow completes, download the artifact 'clip-sync-debug-apk' from Actions -> run -> Artifacts.

If a run fails because of network or runner issues, re-run the workflow. Debug APK is unsigned (debug) and safe to install for testing.

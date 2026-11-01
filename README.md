# yt-dlp Desktop App (Windows)

A desktop GUI for [yt-dlp](https://github.com/yt-dlp/yt-dlp), built with Java Swing and FlatLaf.

## Requirements
- Windows 10 or 11
- [JDK 25](https://adoptium.net/) installed (Gradle looks it up as a toolchain; it does not download it)
- Git

You don't need to install Gradle, yt-dlp or ffmpeg. The Gradle wrapper is included, and the build downloads yt-dlp and ffmpeg for you.

## Setup
```
git clone https://github.com/douglasfunkler/YoutubeDownloader.git
cd YoutubeDownloader
```

## Run from source
```
gradlew run
```

On the first run, the build downloads pinned versions of `yt-dlp.exe` (into the project root) and ffmpeg (into `ffmpeg/bin`), and checks their SHA-256 checksums. Files that already exist are left alone, so a yt-dlp updated through the app's **Update yt-dlp** button is kept. These binaries are not stored in git. To download them without running the app, use `gradlew downloadBinaries`.

## Build a standalone app
```
gradlew distWindows
```
This creates `build\dist\YouTube Downloader\`, which contains `YouTube Downloader.exe`, a bundled Java runtime, yt-dlp and ffmpeg. It downloads the binaries automatically if they're missing. Java does not need to be installed on the machine that runs the app.

- Copy the whole folder somewhere permanent (e.g. `C:\Apps\`). Rebuilding wipes `build\dist`, including any yt-dlp updates and theme settings made there.
- To get a desktop shortcut, right-click `YouTube Downloader.exe` and choose **Send to → Desktop (create shortcut)**.
- Don't put it under `C:\Program Files`. The app writes `yt-dlp.exe` updates and its theme setting into its own folder, which needs write access.
- `gradlew distWindowsZip` creates `build\dist\YouTube Downloader-windows.zip` for sharing.
- Close the app before rebuilding, or the build can't replace the old folder.

## Upgrading yt-dlp or ffmpeg versions
The pinned versions and their SHA-256 checksums are at the top of the "External Windows binaries" section in `build.gradle.kts`. Each GitHub release lists the checksums. After changing them, delete the local `yt-dlp.exe` or `ffmpeg` folder and run `gradlew downloadBinaries`.

## License & Attribution

### Icon
The application's icon (`icon.svg`) is from the **Youtube Video Icons** collection by **SVG Sprite**, licensed under [Creative Commons Attribution (CC BY)](https://creativecommons.org/licenses/by/4.0/).

**Attribution:** SVG Sprite - YouTube Video Icons  
**Source:** https://www.svgrepo.com/svg/421070/play-video-youtube

### ffmpeg
The ffmpeg binaries are builds from [gyan.dev](https://www.gyan.dev/ffmpeg/builds/) (via [GyanD/codexffmpeg](https://github.com/GyanD/codexffmpeg)), licensed under GPLv3. The license is included in `ffmpeg/LICENSE`.

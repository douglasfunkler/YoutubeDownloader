import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest

plugins {
    application
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.formdev:flatlaf:3.7")
    implementation("com.formdev:flatlaf-extras:3.7")
    implementation("org.jetbrains:annotations:26.0.2")
}

application {
    mainClass.set("com.example.app.Main")
}

// Stages the project jar + all runtime dependencies into one directory for jpackage
tasks.register<Copy>("collectJars") {
    dependsOn("jar")
    from(configurations.runtimeClasspath)
    from(tasks.named("jar"))
    into(layout.buildDirectory.dir("package-input"))
}

val appName = "YouTube Downloader"
val mainJar = tasks.named<Jar>("jar").map { it.archiveFileName.get() }

// Use jpackage from the Java 25 toolchain (not whatever is on PATH) so the bundled runtime
// matches the bytecode version the app is compiled for
val jpackageExecutable = javaToolchains.launcherFor(java.toolchain).map {
    val exe = if (System.getProperty("os.name").lowercase().contains("win")) "jpackage.exe" else "jpackage"
    it.metadata.installationPath.file("bin/$exe").asFile.absolutePath
}

fun Exec.configureJpackage(icon: String) {
    group = "distribution"
    dependsOn("collectJars")
    doFirst {
        listOf("dist/$appName", "dist/$appName.app").forEach { path ->
            val dir = layout.buildDirectory.dir(path).get().asFile
            // Clear read-only flags first; Windows refuses to delete read-only files
            dir.walkBottomUp().forEach { it.setWritable(true) }
            if (!dir.deleteRecursively()) {
                throw GradleException(
                    "Could not delete ${dir.absolutePath}. Close the app and any Explorer/terminal " +
                    "windows open in that folder, then try again."
                )
            }
        }
        executable = jpackageExecutable.get()
    }
    args(
        "--name", appName,
        "--input", layout.buildDirectory.dir("package-input").get().asFile.absolutePath,
        "--main-jar", mainJar.get(),
        "--main-class", "com.example.app.Main",
        "--type", "app-image",
        "--icon", layout.projectDirectory.file(icon).asFile.absolutePath,
        "--dest", layout.buildDirectory.dir("dist").get().asFile.absolutePath
    )
}

// --- External Windows binaries (not in git; fetched on demand) ---
// To upgrade: change the version and SHA-256 (published on each GitHub release), then delete
// the local yt-dlp.exe / ffmpeg folder so the new version is downloaded.
val ytDlpVersion = "2026.10.26"
val ytDlpSha256 = "f5389d3698ecde4ef06f4e6b1ce0294fe9ac1396228c8990b8d3416d6d177a7d"
val ffmpegVersion = "9.0.2"
val ffmpegZipSha256 = "60f467265b1e312373dbcd92200c2618a74850f98d3d078e94296bb3fa2047ba"

fun downloadVerified(url: String, target: File, expectedSha256: String) {
    target.parentFile.mkdirs()
    val tmp = File(target.parentFile, target.name + ".part")
    logger.lifecycle("Downloading $url")
    URI(url).toURL().openStream().use { input ->
        tmp.outputStream().use { input.copyTo(it) }
    }
    val digest = MessageDigest.getInstance("SHA-256")
    tmp.inputStream().use { input ->
        val buf = ByteArray(1 shl 16)
        while (true) {
            val n = input.read(buf)
            if (n < 0) break
            digest.update(buf, 0, n)
        }
    }
    val actual = digest.digest().joinToString("") { "%02x".format(it) }
    if (!actual.equals(expectedSha256, ignoreCase = true)) {
        tmp.delete()
        throw GradleException("Checksum mismatch for $url\n  expected $expectedSha256\n  actual   $actual")
    }
    Files.move(tmp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
}

// Skipped if yt-dlp.exe already exists, so a version updated by the app is kept
val downloadYtDlp = tasks.register("downloadYtDlp") {
    group = "setup"
    description = "Downloads yt-dlp.exe $ytDlpVersion into the project root if missing"
    val target = layout.projectDirectory.file("yt-dlp.exe").asFile
    onlyIf { !target.exists() }
    doLast {
        downloadVerified(
            "https://github.com/yt-dlp/yt-dlp/releases/download/$ytDlpVersion/yt-dlp.exe",
            target, ytDlpSha256
        )
    }
}

val ffmpegZip = layout.buildDirectory.file("downloads/ffmpeg-$ffmpegVersion-essentials_build.zip")
val ffmpegBin = layout.projectDirectory.dir("ffmpeg/bin")

val downloadFfmpegZip = tasks.register("downloadFfmpegZip") {
    val zip = ffmpegZip.get().asFile
    onlyIf { !ffmpegBin.file("ffmpeg.exe").asFile.exists() && !zip.exists() }
    doLast {
        downloadVerified(
            "https://github.com/GyanD/codexffmpeg/releases/download/$ffmpegVersion/" +
                "ffmpeg-$ffmpegVersion-essentials_build.zip",
            zip, ffmpegZipSha256
        )
    }
}

// Skipped if ffmpeg/bin/ffmpeg.exe already exists
val downloadFfmpeg = tasks.register<Copy>("downloadFfmpeg") {
    group = "setup"
    description = "Downloads ffmpeg $ffmpegVersion (essentials build) into ffmpeg/bin if missing"
    dependsOn(downloadFfmpegZip)
    onlyIf { !ffmpegBin.file("ffmpeg.exe").asFile.exists() }
    from({ zipTree(ffmpegZip) }) {
        include("*/bin/ffmpeg.exe", "*/bin/ffprobe.exe")
        eachFile { path = "bin/$name" }
    }
    from({ zipTree(ffmpegZip) }) {
        include("*/LICENSE")
        eachFile { path = name }
    }
    includeEmptyDirs = false
    into(layout.projectDirectory.dir("ffmpeg"))
}

tasks.register("downloadBinaries") {
    group = "setup"
    description = "Downloads yt-dlp.exe and ffmpeg if missing"
    dependsOn(downloadYtDlp, downloadFfmpeg)
}

// Run on Windows
tasks.register<Exec>("jpackageWindows") {
    configureJpackage("src/main/resources/icon.ico")
}

// Builds a standalone app folder with yt-dlp.exe and ffmpeg next to the launcher, since the app
// resolves them (and theme_config.properties) relative to its working directory
tasks.register<Copy>("distWindows") {
    group = "distribution"
    description = "Builds a standalone Windows app folder in build/dist/$appName"
    dependsOn("jpackageWindows", "downloadBinaries")
    from(layout.projectDirectory.file("yt-dlp.exe"))
    from(layout.projectDirectory.dir("ffmpeg/bin")) {
        include("ffmpeg.exe", "ffprobe.exe")
        into("ffmpeg/bin")
    }
    from(layout.projectDirectory.file("ffmpeg/LICENSE")) { into("ffmpeg") }
    into(layout.buildDirectory.dir("dist/$appName"))
}

// Zips the Windows app folder for copying to another machine
tasks.register<Zip>("distWindowsZip") {
    group = "distribution"
    dependsOn("distWindows")
    from(layout.buildDirectory.dir("dist/$appName")) { into(appName) }
    archiveFileName.set("$appName-windows.zip")
    destinationDirectory.set(layout.buildDirectory.dir("dist"))
}

// Run on Linux
tasks.register<Exec>("distLinux") {
    configureJpackage("src/main/resources/icon.png")
}

// Run on macOS
tasks.register<Exec>("distMac") {
    configureJpackage("src/main/resources/icon.icns")
}

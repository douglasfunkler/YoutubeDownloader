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
        layout.buildDirectory.dir("dist/$appName").get().asFile.deleteRecursively()
        layout.buildDirectory.dir("dist/$appName.app").get().asFile.deleteRecursively()
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

// Run on Windows
tasks.register<Exec>("jpackageWindows") {
    configureJpackage("src/main/resources/icon.ico")
}

// Builds a standalone app folder with yt-dlp.exe and ffmpeg next to the launcher, since the app
// resolves them (and theme_config.properties) relative to its working directory
tasks.register<Copy>("distWindows") {
    group = "distribution"
    description = "Builds a standalone Windows app folder in build/dist/$appName"
    dependsOn("jpackageWindows")
    from(layout.projectDirectory.file("yt-dlp.exe"))
    from(layout.projectDirectory.dir("ffmpeg/bin")) {
        include("ffmpeg.exe", "ffprobe.exe")
        into("ffmpeg/bin")
    }
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

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

fun Exec.configureJpackage(icon: String) {
    group = "distribution"
    dependsOn("collectJars")
    doFirst {
        layout.buildDirectory.dir("dist/$appName").get().asFile.deleteRecursively()
        layout.buildDirectory.dir("dist/$appName.app").get().asFile.deleteRecursively()
    }
    commandLine(
        "jpackage",
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
tasks.register<Exec>("distWindows") {
    configureJpackage("src/main/resources/icon.ico")
}

// Run on Linux
tasks.register<Exec>("distLinux") {
    configureJpackage("src/main/resources/icon.png")
}

// Run on macOS
tasks.register<Exec>("distMac") {
    configureJpackage("src/main/resources/icon.icns")
}

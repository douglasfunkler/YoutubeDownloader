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

// Produces a self-contained portable application (folder with bundled JRE + launcher EXE)
tasks.register<Exec>("distWindows") {
    group = "distribution"
    dependsOn("collectJars")

    doFirst {
        layout.buildDirectory.dir("dist/YouTube Downloader").get().asFile.deleteRecursively()
    }

    commandLine(
        "jpackage",
        "--name", "YouTube Downloader",
        "--input", layout.buildDirectory.dir("package-input").get().asFile.absolutePath,
        "--main-jar", tasks.named<Jar>("jar").get().archiveFileName.get(),
        "--main-class", "com.example.app.Main",
        "--type", "app-image",
        "--icon", layout.projectDirectory.file("src/main/resources/icon.ico").asFile.absolutePath,
        "--dest", layout.buildDirectory.dir("dist").get().asFile.absolutePath
    )
}

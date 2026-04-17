plugins {
    application
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.formdev:flatlaf:3.7")
    implementation("com.google.code.gson:gson:2.10.1")
}

application {
    mainClass.set("com.example.app.Main")
}

tasks.register<Exec>("packageApp") {
    dependsOn("build")

    commandLine(
        "jpackage",
        "--name", "YtDlpApp",
        "--input", "build/libs",
        "--main-jar", "yt-dlp-desktop.jar",
        "--main-class", "com.example.app.Main",
        "--type", "app-image"
    )
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.gradle) // Or your Gradle version
        classpath(libs.google.services) // Add this line
        classpath("com.google.gms:google-services:4.4.2")

    }
}

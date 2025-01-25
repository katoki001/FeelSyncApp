buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Use the latest version of the Gradle plugin
        classpath("com.android.tools.build:gradle:8.7.2")
        
        // Use a newer version of the Google Services plugin
        classpath("com.google.gms:google-services:4.4.2")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

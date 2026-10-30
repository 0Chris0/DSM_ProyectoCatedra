import java.io.File

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

val localRootBuildDir = File(System.getProperty("user.home"), ".gradle_builds/DSM_ProyectoCatedra/build")
layout.buildDirectory.set(localRootBuildDir)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    maven("https://repo.jpenilla.xyz/snapshots/") {
      mavenContent { snapshotsOnly() }
    }
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
      mavenContent { snapshotsOnly() }
    }
    maven("https://oss.sonatype.org/content/repositories/snapshots/") {
      mavenContent { snapshotsOnly() }
    }
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven("https://repo.spongepowered.org/repository/maven-snapshots/") {
      mavenContent { snapshotsOnly() }
    }
  }
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
    maven("https://repo.jpenilla.xyz/snapshots/") {
      mavenContent { snapshotsOnly() }
    }
    maven("https://repo.spongepowered.org/repository/maven-public/")
  }
  includeBuild("gradle/build-logic")
}

plugins {
  id("quiet-fabric-loom") version "1.6-SNAPSHOT"
  id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "TabTPS"

listOf(
  "common",
  "spigot",
  "sponge",
  "fabric"
).forEach { module ->
  include("tabtps-$module")
  project(":tabtps-$module").projectDir = file(module)
}

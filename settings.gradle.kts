rootProject.name = "wargroove"
include("config", "core", "utils", "desktop", "plugins")

pluginManagement {

    plugins {
        id("up.wargroove.mapLoader")
    }

    repositories {
        mavenCentral()
        mavenLocal()
    }
}

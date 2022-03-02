rootProject.name = "wargroove"
include("config", "core", "utils", "desktop", "mapLoader")

pluginManagement {

    plugins {
        id("up.wargroove.mapLoader")
    }

    repositories {
        mavenCentral()
        mavenLocal()
    }
}

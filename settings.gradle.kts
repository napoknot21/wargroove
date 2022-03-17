rootProject.name = "wargroove"
include("config", "core", "utils", "desktop", "plugins")

pluginManagement {

    plugins {
        id("up.wargroove.mapLoader")
        id("up.wargroove.exportMap")
        id("up.wargroove.importMap")
    }

    repositories {
        mavenCentral()
        mavenLocal()
    }
}

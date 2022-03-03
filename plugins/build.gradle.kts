plugins {
    `java-gradle-plugin`
    `maven-publish`
    checkstyle
}


var gdxVersion = "1.10.0"

sourceSets {
    main {
        java {
            srcDirs("src/")
        }
    }
}

gradlePlugin {
    plugins {
        create("mapLoader") {
            id = "up.wargroove.mapLoader"
            implementationClass = "up.wargroove.plugins.MapLoader"
            version = "0.0.1"
        }
        create("exportTexture") {
            id = "up.wargroove.exportTextures"
            implementationClass = "up.wargroove.plugins.ExportTexturesPlugin"
            version = "0.0.1"
        }
    }
}
plugins.apply("java-gradle-plugin")
plugins.apply("maven-publish")

dependencies {
    implementation(gradleApi())
    implementation(project(":core"))
    implementation(project(":utils"))
}


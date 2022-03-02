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
        resources {
            srcDirs("assets/")
        }
    }
}


gradlePlugin {
    plugins {
        create("mapLoader") {
            id = "up.wargroove.mapLoader"
            implementationClass = "up.wargroove.mapLoader.MapLoader"
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


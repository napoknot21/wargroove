import java.net.URL
import java.net.URLClassLoader

plugins {
    java
    application

}

application {

    mainClass.set("up.wargroove.desktop.WargrooveLauncher")
    applicationName = "Wargroove"

}

var pluginPath = "up.wargroove.plugins."

tasks.create("selectPlugin") {
    dependsOn(configurations.runtimeClasspath)
    val urls: Array<URL> = configurations.runtimeClasspath.get().map { it.toURI().toURL() }.toTypedArray()
    val loader: ClassLoader = URLClassLoader(urls, null)
    doLast {
        try {
            val c = loader.loadClass(pluginPath + "PluginSelector")
            val method = c.getDeclaredMethod("run")
            method.invoke(null)
        } catch (e: Exception) {
            throw e.cause!!
        }
    }

}

distributions {
    main {
        contents {
            from("../db") {
                into("db/")
            }
            from("../damageMatrix") {
                into("damageMatrix/")
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDirs("src/")
        }
    }
}

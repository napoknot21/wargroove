import java.net.URL
import java.net.URLClassLoader

plugins {
    java
    application

}

tasks.withType<Jar> {

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    dependsOn(configurations.runtimeClasspath)
    from(
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    )
    manifest {
        attributes["Main-Class"] = application.mainClass
    }

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
            e.printStackTrace()
        }
    }

}



application {

    mainClass.set("up.wargroove.desktop.WargrooveLauncher")

}
sourceSets {
    main {
        java {
            srcDirs("src/")
        }
    }
}

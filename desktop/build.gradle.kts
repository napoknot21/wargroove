plugins {
	java
	application
}

tasks.withType<Jar> {

	duplicatesStrategy = DuplicatesStrategy.EXCLUDE

	dependsOn(configurations.runtimeClasspath)
	from (
	configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
	)
	manifest {
		attributes["Main-Class"] = application.mainClass
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

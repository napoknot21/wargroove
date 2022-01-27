plugins {
	java
	application
}

tasks.withType<Jar> {

	duplicatesStrategy = DuplicatesStrategy.EXCLUDE

	from(files(sourceSets["main"].output.classesDirs))
	from(files(sourceSets["main"].output.resourcesDir))

	configurations["compileClasspath"].forEach { file: File ->
        	if(file.isDirectory) {
			from(file)
		} else {
			from(zipTree(file.absoluteFile))
    		}
	}	

	manifest {
		attributes["Main-Class"] = "up.wargroove.desktop.WargrooveLauncher"
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

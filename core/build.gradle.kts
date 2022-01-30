plugins.apply("java-library")

sourceSets {
	main {
		java {
			srcDirs("src/")
		}
		resources {
			srcDirs("resources/")
		}
	}

	test {
		java {
			srcDirs("test/java")
		}
		resources {
			srcDirs("test/ressources/")
		}
	}
}

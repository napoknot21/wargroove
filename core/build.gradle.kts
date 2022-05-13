plugins.apply("java-library")

sourceSets {
    main {
        java {
            srcDirs("src/")
        }
        resources {
            srcDirs("assets/")
        }
    }

    test {
        java {
            srcDirs("test/java")
        }
        resources {
            srcDirs("test/assets/")
        }
    }
}

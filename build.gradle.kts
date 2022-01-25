plugins {
    java
    checkstyle
}

version = "O.0.1"
group = "up"

allprojects {
    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }


    plugins.apply("java")
    plugins.apply("checkstyle")

    java.sourceCompatibility = JavaVersion.VERSION_11
}


dependencies {
    testImplementation("junit:junit:4.13.2")
}


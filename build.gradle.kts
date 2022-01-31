plugins {
	java
	checkstyle
}

version = "O.0.1"
group = "up"

var gdxVersion = "1.10.0"

allprojects {

	repositories {

		mavenLocal()
        	mavenCentral()
//		maven { url("https://oss.sonatype.org/content/repositories/releases/") }

	}

    	tasks.withType<JavaCompile> {
        	options.encoding = "UTF-8"
    	}


    	plugins.apply("java")
    	plugins.apply("checkstyle")

    	java.sourceCompatibility = JavaVersion.VERSION_11
}

project(":core") {

	dependencies {

		implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    		implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
		implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
		implementation(project(":utils"))

	}

}

project(":desktop") {

	dependencies {

		implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion")
        	implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
        	implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
		implementation(project(":core"))
	}

}

dependencies {
    testImplementation("junit:junit:4.13.2")
}


plugins {
	java
	checkstyle
	`maven-publish`

	id("up.wargroove.importMap") version "0.0.1"
	id("up.wargroove.exportTextures") version "0.0.1"
	id("up.wargroove.exportMap") version "0.0.1"
}

var gdxVersion = "1.10.0"

allprojects {
	version = "O.0.1"
	group = "up"

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
	    plugins.apply("maven-publish")
		plugins.apply("java-gradle-plugin")

    	java.sourceCompatibility = JavaVersion.VERSION_11
}

project(":core") {
	dependencies {

		implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
		implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
		implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")	
		implementation("javax.json:javax.json-api:1.0")
		implementation("org.glassfish:javax.json:1.0.4")	
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

project(":utils") {
	dependencies {

		implementation("javax.json:javax.json-api:1.0")
		implementation("org.glassfish:javax.json:1.0.4")	

	}
}

dependencies {
    testImplementation("junit:junit:4.13.2")
	implementation(project(":plugins"))
}


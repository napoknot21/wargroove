import java.util.concurrent.ExecutionException

plugins {
	java
	checkstyle
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
		try {
			plugins.apply("up.wargroove.importMap")
			plugins.apply("up.wargroove.exportTextures")
			plugins.apply("up.wargroove.exportMap")
		} catch(_: UnknownPluginException){}

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
		implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
		implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion")
		implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
		implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
		implementation(project(":core"))
		implementation(project(":plugins"))
	}

}


project(":utils") {
	dependencies {

		implementation("javax.json:javax.json-api:1.0")
		implementation("org.glassfish:javax.json:1.0.4")	

	}
}

project(":plugins") {
	dependencies {
		implementation(gradleApi())
		implementation(project(":core"))
		implementation(project(":utils"))
	}
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}


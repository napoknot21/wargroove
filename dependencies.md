## TensorFlow

```
var tensorflowVersion = "0.4.0"
```
```
implementation("org.tensorflow:tensorflow-core-platform:$tensorflowVersion")
implementation("org.tensorflow:tensorflow-core-platform-mkl:$tensorflowVersion")
implementation("org.tensorflow:tensorflow-core-platform-mkl-gpu:$tensorflowVersion")
implementation("org.tensorflow:tensorflow-framework:$tensorflowVersion")
```

##javaFX

```
id("org.openjfx.javafxplugin") version "0.0.10"
plugins.apply("org.openjfx.javafxplugin")
javafx {
        version = "17"
        modules("javafx.controls", "javafx.fxml", "javafx.graphics", "javafx.media")
    }
    
```

##libgdx
```
var gdxVersion = "1.10.0"
```
```
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
```


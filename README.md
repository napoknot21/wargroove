# Fire Emblem

A tactical RPG game based on the Fire Emblem game

## Usage

### Building the project

1. clone the repository
    ```
    git clone git@gaufre.informatique.univ-paris-diderot.fr:garnierk/fire-emblem.git
    ```
   or
    ```
    git clone https://gaufre.informatique.univ-paris-diderot.fr/garnierk/fire-emblem.git
    ```
2. Enter the project folder
    ```
    cd fire-emblem
    ```
4. run gradle wrapper (it will download all dependencies, including gradle itself)
    ```
    ./gradlew build
    ```

### Running the software

Currently, it can be run through gradle too. In order to pass program arguments, you need to pass them behind `--args`:


```
./gradlew run 
```

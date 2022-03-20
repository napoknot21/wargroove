# Fire Emblem

A tactical RPG game based on the Fire Emblem game

## Usage

### Building the project

1. clone the repository
    ```
    git clone git@gaufre.informatique.univ-paris-diderot.fr:garnierk/wargroove.git
    ```
   or
    ```
    git clone https://gaufre.informatique.univ-paris-diderot.fr/garnierk/wargroove
    ```
2. Enter the project folder
    ```
    cd wargroove
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

# Core Wargroove

## Sauvegarde
Tout au long de l'exécution du programme, une base de donnée est à disposition, voilà
un exemple de son utilisation.

```java
DBEngine engine = DBEngine.getInstance();
engine.connect();

Database database = engine.getDatabase("nom.db");
database.selectCollection("collection");

DbObject obj = database.get("requête/sur/plusieurs/niveaux");

if(obj.isData()) {

  String data = obj.get();

} else {

  // D'autres requêtes sur obj possibles

}

// Traitement des collections
db.createCollection("nom");
db.dropCollection("nom");

// Traitement des données
// Mis à jour de cette clé à 35
boolean status_a = db.update("nom/de/l'objet", 35);
// Insertion d'un booléen au nom de cette clé
boolean status_b = db.insert("nom/de/l'objet", false);

// Écrire les modifications sur le flux
db.flush();

engine.disconnect();

```

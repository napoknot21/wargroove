# Wargroove

A tactical RPG game based on the Wargroove game

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
3. run gradle wrapper (it will download all dependencies, including gradle itself)
   ```
    ./gradlew build
    ```

### Running the software

Currently, it can be run through gradle too.


```
./gradlew run 
```
To run the game in debug mode, you need to use the following command:

```
./gradlew run --args=--debug=on
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

# Plugins

Il existe plusieurs plugins d'aide au développement, notamment pour la creation de cartes.
Tous ces plugins sont accessibles à partir d'un interpréteur de commande qui est lui-même accessible grâce à

``./gradlew selectPlugin``

Le programme va se lancer et va vous demander de choisir entre les plugins suivants :

* ExportMap
* ImportMap
* ExportTextures

La syntaxe du nom des plugins n'est pas sensible à la case.

Voici un exemple de syntaxe :
``importMap --paths=core/assets/importedMap/test.csv``

Chaque plugin s'accompagne de leurs propres paramètres à entrer en même temps que le nom du plugin.

### ExportMap

Commande CLI : `exportTextures --name --path --overwrite --gen --r --n --s --dim`

```
\--path=path               depuis la racine du projet ou absolue = emplacement ou la map sera exportée

\--name=name               le nom de la map dans la db

\--gen=(Y/N ou y/n)        indique si la map doit être générée, si oui la map ne provient pas de la db.

--r=Interger               paramètre repartirion de la génération.

--n=Double               paramètre normalization de la génération.

--s=Double               paramètre smooth de la generation.

--overwrite=(Y/N)          indique si il y a déjà un fichier, si celui ci doit être modifié.

--dim=Interger,Interger    La dimension du monde généré (default= 20,20)
```

L'ordre des paramètres est indépendant

La map exportée est un fichier CSV modifiable facilement avec le logiciel Tiled

### ExportTextures

Commande CLI : ` exportTextures --path --biome --overwrite`

```
--path=path               path depuis la racine du projet ou absolue = emplacement ou la texture du biome sera copié

--biome=biome             le biome peut être facultatif (default=grass) ou précisé.

--overwrite=(Y/N)         overwrite indique si il y a déjà un fichier, si celui ci doit être modifié.
```
L'ordre des paramètres est indépendant

Pour assurer la compatibilité entre le plugin d'importation et Tiled, il faut utiliser ce plugin pour obtenir les textures

### ImportMap

Commande CLI : `importMap --paths --biome`

```
--paths="path1;paths2..."                    paths depuis la racine du projet ou absolue
--biome=biome                                le biome peut être facultatif (default=grass) ou precisé.
```
L'ordre des paramètres est indépendant
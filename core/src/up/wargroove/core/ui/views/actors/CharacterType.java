package up.wargroove.core.ui.views.actors;


import com.badlogic.gdx.utils.Null;
import up.wargroove.core.character.Entity;
import up.wargroove.core.ui.views.objects.TileType;
import up.wargroove.core.world.Tile;
import up.wargroove.utils.Log;

public enum CharacterType {
        VILLAGER("villager"),
        COMMANDER("commander"),
        SOLDIER( "soldier"),
        DOG("dog"),
        SPEARMAN("spearman"),
        MAGE("mage"),
        ARCHER("archer"),
        WAGON("wagon"),
        CAVALRY("cavalry"),
        BALLISTA( "ballista"),
        TREBUCHET("trebuchet"),
        GIANT("giant"),
        RIFLEMAN("rifleman"),
        THIEF("thief"),
        BALLON("ballon"),
        AERONAUT( "aeronaut"),
        SKYRAIDER("skyraider"),
        DRAGON("dragon"),
        BARGE("barge"),
        AMPHIBIAN("amphibian"),
        TURTLE("turtle"),
        HARPOONSHIP("harpoonship"),
        WARSHIP( "warship");


        private static final String extension = ".png";
        private static final String CHARACTER_PATH = "data/sprites/character/";
        private final String texture;


        CharacterType(String texture){
        this.texture = texture;
}

}
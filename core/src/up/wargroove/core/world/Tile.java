package up.wargroove.core.world;

import up.wargroove.core.character.Entity;
import up.wargroove.utils.DbObject;
import up.wargroove.utils.Constants;
import up.wargroove.utils.Savable;

import java.util.Optional;

public class Tile implements Savable {

    public static final int PRIMARY_TILE_TYPE = 5;
    private int textureVersion;

    /*
     * Encodage de la défense et des coûts sur 32 bits
     */

    private static final int MOUNTAIN_D_COST = 0x43001100;
    private static final int FOREST_D_COST = 0x32301104;
    private static final int PLAIN_D_COST = 0x11121102;
    private static final int SEA_D_COST = 0x10001121;
    private static final int DEEP_SEA_D_COST = 0x11111;
    private static final int BEACH_D_COST = 0x91101122;
    private static final int RIVER_D_COST = 0xA2401101;
    private static final int REEF_D_COST = 0x20001142;
    private static final int ROAD_D_COST = 0x1111102;
    private static final int BRIDGE_D_COST = 0x1111122;
    private static final int WALL_D_COST = 0x0;
    private static final int FLAGSTONE_D_COST = 0x21100102;
    private static final int CARPET_D_COST = 0x21100102;
    
    public Optional<Entity> entity;
    public Optional<Character> character;

    private Type type; 

    public Tile() {

        /*
         * Initialisé par défaut sur la plaine
         */

        this(Type.PLAIN);

    }

    public Tile(Type type) {

        setType(type); 
        entity = Optional.empty();
        textureVersion = 1;

    }

    public Tile(Type type, int textureVersion) {
        this(type);
        this.textureVersion = textureVersion;
    }

    public Tile(int textureVersion) {
        this(Type.PLAIN, textureVersion);
    }

    public Type getType() {

        return type;

    }

    public void setType(Type type) {

        this.type = type;

    }

    /*
    public void setEntity(Entity e) {

	    this.entity = Optional.of(e);

    }

    public Entity getEntity() {

	    return entity.get();

    }
    */

    public String toString() {

        return type.asciiFormat + "";

    }

    public void updateType(Biome biome) {
        if (type == Type.RIVER){
            if (biome == Biome.VOLCANO) {
                type.enc = SEA_D_COST;
            } else if (biome == Biome.ICE){
                type.enc = PLAIN_D_COST;
            } else {
                type.enc = RIVER_D_COST;
            }
        }
    }

    public int getTextureVersion() {
        return textureVersion;
    }

    @Override
    public void load(DbObject dbo) {
	String typeStr = dbo.get(Constants.WORLD_TILE_TYPE_DB_KEY).get();
	type = Type.valueOf(typeStr);
    textureVersion = Integer.parseInt(dbo.get(Constants.WORLD_TILE_TEXTURE_VERSION_DB_KEY).get());
    entity = Optional.ofNullable(Entity.loadEntity(dbo.get(Constants.WORLD_TILE_ENTITY_DB_KEY)));
    }

    @Override
    public DbObject toDBO() {

	DbObject dbo = new DbObject();
	dbo.put(Constants.WORLD_TILE_TYPE_DB_KEY, type.toString());
    dbo.put(Constants.WORLD_TILE_TEXTURE_VERSION_DB_KEY,textureVersion);
    dbo.put(Constants.WORLD_TILE_ENTITY_DB_KEY, entity.map(Entity::toDBO).orElse(new DbObject()));
	return dbo;

    } 

    public enum Type {

        /*
         * Types primaires de génération procédurale
         */

        MOUNTAIN('^', MOUNTAIN_D_COST),
        FOREST(':', FOREST_D_COST),
        PLAIN('/', PLAIN_D_COST),
        SEA('o', SEA_D_COST),
        DEEP_SEA('O', DEEP_SEA_D_COST),

        // =========================================

        BEACH('_', BEACH_D_COST),
        RIVER('s', RIVER_D_COST),
        REEF('r', REEF_D_COST),
        ROAD('-', ROAD_D_COST),
        BRIDGE('h', BRIDGE_D_COST),
        WALL('H', WALL_D_COST),
        FLAGSTONE('.', FLAGSTONE_D_COST),
        CARPET('c', CARPET_D_COST);

        private int enc;
        final char asciiFormat;

        Type(char asciiFormat, int enc) {

            this.asciiFormat = asciiFormat;
            this.enc = enc;

        }
        public int enc() {
            return enc;
        }

        public boolean availableForLoad() {
            return this != SEA && this != DEEP_SEA && this != REEF && this != WALL && this!=RIVER;
        }

    }
}

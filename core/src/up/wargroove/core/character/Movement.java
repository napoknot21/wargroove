package up.wargroove.core.character;

public class Movement {

    private int nbOfCases; //Max des cases qui peut parcourir

    public enum MoveType {

        NULL(Character.Component.GROUND), //No movement

        WALKING(Character.Component.GROUND),
        RIDING(Character.Component.GROUND),
        WHEELS(Character.Component.GROUND),

        FLYING(Character.Component.AIR),
        HOVER(Character.Component.AIR),

        WATER(Character.Component.SEA),
        AMPHIBIOUS(Character.Component.SEA);

        MoveType(Character.Component Component) {}
    };
    private MoveType moveType;

    enum TerrainType {
        ROAD,
        BRIDGE,
        PLAINS,
        MOUNTAIN,
        BEACH,
        SEA,
        DEEP_SEA,
        RIVER,
        REEF,
        WALL,
        FLAGSTONE,
        CARPET;
    }
    private TerrainType terrainType;

    public Movement(int nbOfCases, MoveType moveType, TerrainType terrainType) {
        this.nbOfCases = nbOfCases;
        this.moveType = moveType;
        this.terrainType = terrainType;
    }

    public int getNbOfCases() {
        return nbOfCases;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public TerrainType getTerrainType() {
        return terrainType;
    }
}

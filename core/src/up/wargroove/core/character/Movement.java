package up.wargroove.core.character;

public class Movement {

    private int nbOfCases; //Max des cases qui peut parcourir

    public enum MoveType {

        NULL(Character.TypeUnit.GROUND), //No movement

        WALKING(Character.TypeUnit.GROUND),
        RIDING(Character.TypeUnit.GROUND),
        WHEELS(Character.TypeUnit.GROUND),

        FLYING(Character.TypeUnit.AIR),
        HOVER(Character.TypeUnit.AIR),

        WATER(Character.TypeUnit.SEA),
        AMPHIBIOUS(Character.TypeUnit.SEA);

        MoveType(Character.TypeUnit typeUnit) {}
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

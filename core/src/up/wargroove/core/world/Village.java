package up.wargroove.core.world;

import up.wargroove.core.character.Faction;

public class Village extends Structure{

    /**
     * Constructor for the Village structure
     * @param faction Village faction
     */
    public Village(Faction faction) {
        super(Type.VILLAGE, faction);
        exhaust();
    }

    @Override
    public void nextTurn() {
        super.nextTurn();
        exhaust();
    }
}

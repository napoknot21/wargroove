package up.wargroove.core.world;

import up.wargroove.core.character.Faction;


public class Stronghold extends Structure {

    /**
     * Constructor for StrongHold
     * @param faction StrongHold faction
     */
    public Stronghold(Faction faction) {
        super(Type.STRONGHOLD, faction);
        exhaust();
    }

    @Override
    public void nextTurn() {
        super.nextTurn();
        exhaust();
    }
}

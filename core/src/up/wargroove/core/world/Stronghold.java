package up.wargroove.core.world;

import up.wargroove.core.character.Faction;
import up.wargroove.utils.DbObject;

public class Stronghold extends Structure{

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

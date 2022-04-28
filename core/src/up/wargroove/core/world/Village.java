package up.wargroove.core.world;

import up.wargroove.core.character.Faction;
import up.wargroove.utils.DbObject;

public class Village extends Structure{
    public Village(Faction faction) {
        super(Type.VILLAGE, faction, 100);
    }
}

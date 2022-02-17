package up.wargroove.core.character.entities;

import up.wargroove.core.character.Character;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.Movement;

public class Villager extends Character {

    public Villager(String name, Faction faction) {
        super(name, faction, Type.VILLAGER, 4, Movement.Type.WALKING);
        initStats();
    }

    public void initStats() {
        super.setAttack(0);
        super.setDefense(10);
        super.setHealth(100);
        super.setCapture(true);
        super.setSight(2);
        super.setRange(0);
        super.setCost(50);
    }

}
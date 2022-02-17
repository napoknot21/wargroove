package up.wargroove.core.character.entities;


import up.wargroove.core.character.Character;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.Movement;

public class Commander extends Character {

    public Commander (String name, Faction faction) {

        super(name, faction, Type.COMMANDER, 4, Movement.Type.WALKING);
        initStats();

    }

    public void initStats () {

        super.setAttack(20);
        super.setDefense(20);
        super.setHealth(100);
        super.setCapture(true);
        super.setSight(3);
        super.setRange(1);
        super.setCost(500);

    }


}

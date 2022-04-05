package up.wargroove.core.character.entities;


import org.gradle.internal.impldep.org.yaml.snakeyaml.Yaml;
import up.wargroove.core.character.Character;
import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.Movement;
import up.wargroove.utils.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InaccessibleObjectException;
import java.util.Locale;
import java.util.Map;

public class Commander extends Character {

	public Commander() {

		this("", Faction.OUTLAWS);

	}

    public Commander (String name, Faction faction) {

        super(name, Type.COMMANDER, faction);
        initialize();

    }

    @Override
    public void initialize() {

	    super.movement = Movement.WALKING;
	    super.movRange = 4;

	    stats.attack = 20;
	    stats.defense = 20;
	    stats.health = 100;
	    stats.capture = true;
	    stats.sight = 3;
	    stats.range = 1;
	    stats.cost = 500;

    }

/*

	public void attack (Character ch) {
		String name = this.getClass().getName();
		name = name.toUpperCase();
		try {
			File f = new File("../damageMatrix/"+name+".yml");
			InputStream is = new FileInputStream(f);

			Yaml yml = new Yaml();

			Map<String, Map<String, Pair <Integer,Integer>>> data = yml.load(is);

			String name_attack = name.toLowerCase();
			String name_defend = ch.getClass().getName().toLowerCase();

			var actions = data.get(name_defend);

			Pair<Integer,Integer> values_attack = actions.get("attacks");
			Pair<Integer,Integer> values_defend = actions.get("defends");

			ch.setHealth(ch.getHealth() - values_attack.first + values_defend.first);

		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}
*/

}

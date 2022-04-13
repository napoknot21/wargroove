package up.wargroove.core.character.entities;

import org.gradle.internal.impldep.org.yaml.snakeyaml.Yaml;
import up.wargroove.core.character.Character;
import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.Movement;
import up.wargroove.utils.Pair;

import javax.naming.ldap.HasControls;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InaccessibleObjectException;
import java.net.CacheRequest;
import java.util.HashMap;
import java.util.List;
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

	private HashMap<String, Map<String, List<Integer>>> getMatrixValues () {
		try {
			File f = new File("./core/src/up/wargroove/core/character/damageMatrix/commander.yml");
			InputStream is = new FileInputStream(f);
			Yaml yml = new Yaml();
			return yml.load((is));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Map<String,List<Integer>> getAttacksDefendsValues (Character ch) {
		var data = getMatrixValues();
		if (data == null || ch == null) return null;
		System.out.println("Hola1");
		return data.get(ch.getClass().getName().toUpperCase());
	}

	//FIXME corriger pb de valeur de retour null
	public Pair<Integer,Integer> getAttacksValues (Character ch) {
		var data = getAttacksDefendsValues(ch);
		System.out.println("Hola2");
		if (data == null) return null;
		System.out.println("Hola3");
		List<Integer> attacks = data.get("attacks");
		if (attacks.size() != 2) return null;
		Pair<Integer,Integer> pairAttack = new Pair<>();
		pairAttack.first = attacks.get(0);
		pairAttack.second = attacks.get(1);
		return pairAttack;
	}

	public Pair<Integer,Integer> getDefendsValues (Character ch) {
		var data =  getAttacksDefendsValues(ch);
		if (data == null) return null;
		List<Integer> defends = data.get("defends");
		if (defends.size() != 2) return null;
		Pair<Integer,Integer> pairDefend = new Pair<>();
		pairDefend.first = defends.get(0);
		pairDefend.second = defends.get(1);
		return pairDefend;
	}

	public void attack (Character ch) {
		//Normal values (premier integer de Pais)
		Pair<Integer, Integer> values_attack = getAttacksValues(ch);
		Pair<Integer, Integer> values_defend = getDefendsValues(ch);
		if (values_attack == null && values_defend == null) {
			values_attack = new Pair<>();
			values_attack.first = 0;
			values_attack.second = 0;
			values_defend = new Pair<>();
			values_defend.first = 0;
			values_defend.second = 0;
		} else if (values_attack == null) {
			values_attack = new Pair<>();
			values_attack.first = 0;
			values_attack.second = 0;
		} else if (values_defend == null) {
			values_defend = new Pair<>();
			values_defend.first = 0;
			values_defend.second = 0;
		} else {
			ch.setHealth(ch.getHealth() - values_attack.first + values_defend.first);
		}



	}

	public static void main(String[] args) {
		Commander cd = new Commander();
		Villager vl = new Villager();
		System.out.println("Hello world");
		//System.out.println(cd.getClass().getName());
		System.out.println(cd.getAttacksValues(vl).toString());

	}


	 */
}

package up.wargroove.core.world;

import up.wargroove.core.character.Entity;
import up.wargroove.core.character.EntityManager;
import up.wargroove.core.character.Faction;
import up.wargroove.utils.Constants;
import up.wargroove.utils.DbObject;

import java.util.List;
import java.util.Optional;
import java.util.Vector;

public class Recruitment extends Structure {

	public static List<Entity> landEntityClasses = new Vector<>(),
			navalEntityClasses = new Vector<>() ,
			airEntityClasses = new Vector<>();

	public static void clearAll() {
		landEntityClasses.clear();
		navalEntityClasses.clear();
		airEntityClasses.clear();
	}

	public static Structure loadRecruitment(DbObject from, Faction faction) {
		if (from == null) {
			return null;
		}
		Type type = Type.valueOf(from.get(Constants.RECRUITMENT_TYPE_DB_KEY).get());
		return new Recruitment(type,faction);
	}

	@Override
	public DbObject toDBO() {
		DbObject res =  super.toDBO();
		res.put(Constants.RECRUITMENT_TYPE_DB_KEY, type);
		return res;
	}

	public enum Type {

		BARRACKS,
		TOWER,
		PORT

	}

	private Type type;
	private List<Entity> current;

	public Recruitment(Type type, Faction faction) {

		super(Structure.Type.RECRUITMENT, faction);
		this.type = type;
		initialize();
	}

	@Override
	public int getBonus() {
		return 0;
	}

	public void initialize() {

		switch (type) {

			case BARRACKS:
				current = landEntityClasses;
				break;

			case TOWER:
				current = navalEntityClasses;
				break;

			case PORT:
				current = airEntityClasses;
				break;

			default:
				current = null;
				break;
		}

	}

	public List<Entity> trainableEntityClasses() {

		return current;

	}

	public Type getRecruitmentType() {
		return type;
	}

	public Optional<Entity> buy(Class<? extends Entity> what, double m, String name, Faction faction) {

		if (current == null) return Optional.empty();

		Entity entity = EntityManager.instantiate(what, name, faction);
		return Optional.of(entity);

	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Recruitment clone = (Recruitment) super.clone();
		clone.type = this.type;
		return clone;
	}
}

package up.wargroove.core.character;

public enum Faction {
	
	CHERRYSTONE_KINGDOM,
	FELHEIM_LEGION,
	FLORAN_TRIBES,
	HEAVENSONG_EMPIRE,
	OUTLAWS;

	public String prettyName() {
		String[] name = name().split("_");
		String ret = name[0].charAt(0) + name[0].substring(1).toLowerCase();
		if (name.length == 2) {
			ret += " " + name[1].charAt(0) + name[1].substring(1).toLowerCase();
		}
		return ret;
	}
}

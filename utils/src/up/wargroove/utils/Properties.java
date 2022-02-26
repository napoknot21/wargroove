package up.wargroove.utils;

import javax.json.JsonObject;

public interface Properties {

	public void load(DbObject from);	
	public DbObject toDBO();	

}

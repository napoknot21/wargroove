package up.wargroove.utils;

public interface Savable {

	public void load(DbObject from);	
	public DbObject toDBO();	

}

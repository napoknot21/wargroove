package up.wargroove.utils;

public interface Savable {

    void load(DbObject from);

    DbObject toDBO();

}

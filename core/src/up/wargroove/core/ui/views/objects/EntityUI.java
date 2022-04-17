package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import up.wargroove.core.character.Entity;
import up.wargroove.core.ui.Assets;
import up.wargroove.utils.Pair;
import up.wargroove.core.character.Character;

public abstract class EntityUI extends Actor {
    private static final int TILE_SIZE= 20;
    private Sprite stats;
    private Sprite sprite;
    private Pair<Integer,Integer> coordinates;
    private final Entity entity;
    private Pair<Integer,Integer> size;
    private boolean alive = true;
    private boolean injured = false;
    private float temps;


    public EntityUI(Pair<Integer,Integer> coord, Entity entity) {
        this.entity = entity;
        this.coordinates = coord;
        size = (entity instanceof Character)? new Pair<>(20,30) : new Pair<>(20,20);
    }


    protected void initialiseSprites() {
        this.sprite = new Sprite();
        sprite.setSize(size.first,size.second);
        this.stats= new Sprite(getPathSTATS(0));
        stats.setSize(sprite.getWidth()/4,sprite.getHeight()/4);
        setPosition(coordinates.first * TILE_SIZE,coordinates.second * TILE_SIZE);
        positionChanged();
    }

    public void actualiseStats(){
        if (entity.getHealth()<90){
            this.stats= new Sprite(getPathSTATS((int) ((entity.getHealth()/10)+1)));
        }
        if ((entity.getHealth()<=0)||(entity.getHealth()==90)){
            this.stats= new Sprite(getPathSTATS((int) ((entity.getHealth()/10))));
        }
        stats.setSize(TILE_SIZE/4f,TILE_SIZE/4f);
        stats.setPosition(coordinates.first * TILE_SIZE+TILE_SIZE-6,coordinates.second * TILE_SIZE+1);
    }

    private Texture getPathSTATS(int file) {
        return Assets.getInstance().get( Assets.AssetDir.STATS.getPath()+"Stats" + file + ".png", Texture.class);
    }

    public Pair<Integer, Integer> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Pair<Integer, Integer> coordinates) {
        this.coordinates = coordinates;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Sprite getStats() {
        return stats;
    }

    public void setStats(Sprite stats) {
        this.stats = stats;
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setInjured(boolean injured) {
        this.injured = injured;
    }

    public boolean isInjured() {
        return injured;
    }

    protected Texture getPath(String nameFile) {
        return Assets.getInstance().get(
                Assets.AssetDir.CHARACTER.getPath() + entity.getFaction() + "/" +
                        entity.getType() + "_" + nameFile, Texture.class);
    }

    protected void actualiseSprite(TextureRegion textureRegion) {
        sprite = new Sprite(textureRegion);
        sprite.setSize(size.first, size.second);
        positionChanged();
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    /**
     * Create a sequence of colors showing the character injured
     * ~That which does not kill us makes us stronger~ Friedrich Nietzsche
     */
    private void injure(){
        sprite.setColor(Color.RED);
        temps+=getTimeLapse();
        if (temps>3){
            temps=0;
            sprite.setColor(1,1,1,1);
            injured=false;
        }
    }

    /**
     * Puts the character with a dark filter.
     */
    private void exhaust(){
        if(entity.isExhausted()) {
            sprite.setColor(Color.GRAY);
        } else {
            sprite.setColor(1,1,1,1);
        }
    }

    protected boolean canMove(){
        return false;
    }

    public boolean isWaiting(){
        return true;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!canMove()) exhaust();
        if (injured) injure();
        sprite.draw(batch);
        if (isWaiting()) {
            actualiseStats();
            stats.draw(batch);
        }
        super.draw(batch, parentAlpha);
    }

    public static float getTimeLapse() {
        return Gdx.graphics.getDeltaTime()*19;
    }

    public float getTemps() {
        return temps;
    }

    public void addTemps(float temps) {
        this.temps += temps;
    }

    public void setTemps(float temps) {
        this.temps = temps;
    }

    public Pair<Integer, Integer> getSize() {
        return size;
    }

    public void setSize(Pair<Integer, Integer> size) {
        this.size = size;
    }

    public static int getTileSize() {
        return TILE_SIZE;
    }

    @Override
    public void moveBy(float x, float y) {
        coordinates.first += (int)x;
        coordinates.second += (int)y;
        setPosition(coordinates.first * TILE_SIZE,coordinates.second * TILE_SIZE);
    }
}
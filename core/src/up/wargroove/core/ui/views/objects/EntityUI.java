package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import up.wargroove.core.character.Character;
import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Model;
import up.wargroove.utils.Pair;

public abstract class EntityUI extends Actor {
    private static float TILE_SIZE = 20;
    private final Entity entity;
    public EntityUI victime;
    private Sprite stats;
    private Sprite sprite;
    private Pair<Integer, Integer> coordinates;
    private Pair<Integer, Integer> size;
    private boolean alive = true;
    private boolean injured = false;
    private boolean waiting = true;
    private boolean readyToAttack = true;
    private float temps;


    public EntityUI(Pair<Integer, Integer> coord, Entity entity, float scale) {
        this.entity = entity;
        this.coordinates = coord;
        TILE_SIZE = (Model.getTileSize() * scale);
        if (entity instanceof Character) {
            size = new Pair<>((int) (1 * TILE_SIZE), (int) (1 * TILE_SIZE));
        } else {
            size = new Pair<>((int) TILE_SIZE, (int) TILE_SIZE);
        }
    }

    protected void initialiseSprites() {
        this.sprite = new Sprite();
        sprite.setSize(size.first, size.second);
        this.stats = new Sprite(getPathSTATS(0));
        stats.setSize(sprite.getWidth() / 4, sprite.getHeight() / 4);
        setPosition(coordinates.first * TILE_SIZE, coordinates.second * TILE_SIZE);
        positionChanged();
    }

    public void actualiseStats() {
        if ((entity.getHealth() <= 0) || entity.getHealth() > 90) {
            this.stats = new Sprite(getPathSTATS(0));
        } else if (entity.getHealth() < 90) {
            this.stats = new Sprite(getPathSTATS((int) ((entity.getHealth() / 10) + 1)));
        } else {
            this.stats = new Sprite(getPathSTATS((int) ((entity.getHealth() / 10))));
        }
        stats.setSize(TILE_SIZE / 4f, TILE_SIZE / 4f);
        stats.setPosition(coordinates.first * TILE_SIZE + TILE_SIZE - 6, coordinates.second * TILE_SIZE + 1);
    }


    protected Texture getPath(String nameFile) {
        return Assets.getInstance().get(
                Assets.AssetDir.CHARACTER.path() + entity.getFaction() + "/" +
                        entity.getType() + "_" + nameFile, Texture.class);
    }

    /**
     * Change the texture of the sprite and actialise size and postion
     */

    public void actualiseSprite(TextureRegion textureRegion) {
        sprite = new Sprite(textureRegion);
        sprite.setSize(size.first, size.second);
        positionChanged();
    }

    /**
     * Create a sequence of colors showing the character injured
     */
    private void injure() {
        if (temps > 0) sprite.setColor(Color.RED);
        temps += getTimeLapse();
        if (temps > getTileSize() * getTimeLapse()) {
            actualiseStats();
            temps = 0;
            sprite.setColor(1, 1, 1, 1);
            injured = false;
            waiting = true;
            if (entity.getHealth() <= 0) {
                alive = false;
            }
        }
    }

    /**
     * Puts the character with a dark filter.
     */
    protected void exhaust() {
        if (entity.isExhausted()) {
            sprite.setColor(Color.GRAY);
        } else {
            sprite.setColor(1, 1, 1, 1);
        }
    }

    @Override
    public void moveBy(float x, float y) {
        coordinates.first += (int) x;
        coordinates.second += (int) y;
        setPosition(coordinates.first * TILE_SIZE, coordinates.second * TILE_SIZE);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (isWaiting() && !injured) {
            actualiseStats();
            stats.draw(batch);
        }
        if (injured) injure();
        sprite.draw(batch);


        super.draw(batch, parentAlpha);
    }

    public static float getTimeLapse() {
        return Gdx.graphics.getDeltaTime() * (getTileSize());

    }

    public static float getTileSize() {
        return TILE_SIZE;
    }

    protected boolean canMove() {
        return false;
    }

    public boolean isWaiting() {
        return waiting;
    }

    public void setWaiting(boolean b) {
        this.waiting = b;
    }

    public float getTemps() {
        return temps;
    }

    public void setTemps(float temps) {
        this.temps = temps;
    }

    public void addTemps(float temps) {
        this.temps += temps;
    }

    public Pair<Integer, Integer> getSize() {
        return size;
    }

    public void setSize(Pair<Integer, Integer> size) {
        this.size = size;
    }

    public boolean isFaction(Faction faction) {
        return entity.getFaction().equals(faction);
    }

    public void setReadyToAttack(boolean bool) {
        readyToAttack = bool;
    }

    public boolean getReadyAttack() {
        return readyToAttack;
    }

    private Texture getPathSTATS(int file) {
        return Assets.getInstance().get(Assets.AssetDir.STATS.path() + "Stats" + file + ".png", Texture.class);
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

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
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

    public boolean isInjured() {
        return injured;
    }

    public void setInjured(boolean injured) {
        this.injured = injured;
    }

    public void setVictime(EntityUI victime) {
        this.victime = victime;
        victime.setWaiting(false);
    }


}

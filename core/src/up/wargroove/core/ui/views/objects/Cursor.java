package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Model;

/**
 * Game view cursor.
 */
public class Cursor extends Sprite {

    /**
     * World Scale. <br>
     * Scale pixels for one Tile
     */
    private int worldScale;
    private boolean locked;

    /**
     * Creates a cursor with a Texture a start position and the world scale.
     *
     * @param texture    The cursor's texture.
     * @param worldScale The world scale.
     */
    public Cursor(Texture texture, int worldScale) {
        super(texture);
        this.worldScale = worldScale;
        setSize(worldScale, worldScale);
    }

    /**
     * Gets the cursor's screen position.
     *
     * @return the position as a vector.
     */
    public Vector3 getPosition() {
        return new Vector3(getX(), getY(), 0);
    }

    /**
     * Sets the vector screen position.
     *
     * @param vector the cursor's screen position.
     */
    public void setPosition(Vector3 vector) {
        if (!locked) {
            setPosition(vector.x, vector.y);
        }
    }

    /**
     * @param batch the drawer
     */
    @Override
    public void draw(Batch batch) {
        if (!locked) {
            super.draw(batch);
        }
    }

    /**
     * Gets the cursor's world position.
     *
     * @return the position as a vector.
     */
    public Vector3 getWorldPosition() {
        return new Vector3(getX() / worldScale, getY() / worldScale, 0);
    }

    /**
     * Removes this.getTexture()
     */
    public void dispose() {
        this.getTexture().dispose();
    }

    /***************** setters and getters *****************/

    public void setWorldScale(int worldScale) {
        this.worldScale = worldScale;
    }

    public void setLock(boolean locked) {
        this.locked = locked;
    }

}

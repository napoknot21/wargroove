package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.core.world.Player;

/**
 * The current player information box on screen.
 */
public class PlayerBox extends Table {
    /**
     * Indicate if this box is for a unique player.
     */
    private boolean isStatic;

    /**
     * The current player's name.
     */
    private Label name;

    /**
     * The current player's amount of money.
     */
    private Label money;

    /**
     * The current player's.
     */
    private Label income;

    /**
     * The game's round.
     */
    private Label round;

    /**
     * The current player's avatar.
     */
    private Rectangle avatar;

    /**
     * Inits an empty player box.
     */
    public PlayerBox() {
        Skin skin = Assets.getInstance().getSkin();
        name = new Label("Friendly", skin);
        name.setColor(Color.BLACK);
        money = new Label("300", skin);
        money.setColor(Color.BLACK);
        income = new Label("+500", skin);
        this.income.setColor(new Color(0.2f, 0.6f, 0.2f, 1));
        round = new Label("Round 1", skin);
        round.setColor(Color.BLACK);
        avatar = new Rectangle();
        isStatic = false;
        setup();
        background(skin.getDrawable("window"));
    }

    /**
     * builds the playerBox and manage its elements.
     */
    private void setup() {
        Table table = new Table(Assets.getInstance().getSkin());
        table.left().top();
        table.add(name).expandX().fillX().pad(0, 0, -2, 0);
        table.row();
        table.add(money).expandX().fillX().pad(0, 0, -2, 0);
        table.row();
        table.add(income).expandX().fillX().pad(0, 0, -2, 0);
        table.row();
        table.add(round).expandX().fillX();
        add(table).expand().padRight(10).padLeft(5);
        add(avatar).size(70);
    }

    /**
     * Sets the playerBox displayed information.
     *
     * @param player The current player
     */
    public void setInformations(Player player, int round) {
        setInformations(false, player, round);
    }

    public void setInformations(boolean isStatic, Player player, int round) {
        if (this.isStatic) {
            return;
        }
        name.setText(player.getName());
        money.setText(player.getMoney());
        income.setText("+" + player.getIncome());
        this.round.setText("Round " + round);
        Color color;
        switch (player.getFaction()) {  //Color based on the tile set
            case FLORAN_TRIBES:
                color = new Color(111 / 255f, 153 / 255f, 13 / 255f, 1);
                break;
            case FELHEIM_LEGION:
                color = new Color(13 / 255f, 76 / 255f, 153 / 255f, 1);
                break;
            case CHERRYSTONE_KINGDOM:
                color = new Color(153 / 255f, 23 / 255f, 13 / 255f, 1);
                break;
            case HEAVENSONG_EMPIRE:
                color = new Color(153 / 255f, 120 / 255f, 13 / 255f, 1);
                break;
            default:
                color = Color.CLEAR;
        }
        this.avatar.setColor(color);
        this.isStatic = isStatic;
    }


    public void dispose() {
        name = null;
        money = null;
        avatar = null;
        round = null;
        income = null;

    }

    /**
     * Drawable rectangle.
     */
    private static class Rectangle extends Actor {
        private ShapeRenderer renderer;

        private Rectangle() {
            super();
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.end();

            if (renderer == null) {
                renderer = new ShapeRenderer();
            }

            Gdx.gl.glEnable(GL20.GL_BLEND);
            renderer.setProjectionMatrix(batch.getProjectionMatrix());
            renderer.setTransformMatrix(batch.getTransformMatrix());
            renderer.setColor(getColor());
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.rect(getX(), getY(), getWidth(), getHeight());
            renderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            batch.begin();
        }
    }
}

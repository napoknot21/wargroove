package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private Image avatar;

    /**
     * Inits an empty player box.
     *
     */
    public PlayerBox() {
        Skin skin = Assets.getInstance().getSkin();
        name = new Label("Friendly", skin);
        money = new Label("300", skin);
        income = new Label("+500", skin);
        round = new Label("Round 1", skin);
        avatar = new Image(Assets.getInstance().getTest());
        isStatic = false;
        setup();
    }

    /**
     * builds the playerBox and manage its elements.
     */
    private void setup() {
        Table table = new Table();
        table.left().top();
        table.add(name).expandX().fillX().pad(0, 0, -2, 0);
        table.row();
        table.add(money).expandX().fillX().pad(0, 0, -2, 0);
        table.row();
        table.add(income).expandX().fillX().pad(0, 0, -2, 0);
        table.row();
        table.add(round).expandX().fillX();
        add(table).expand().pad(10);
        add(avatar).size(70);
    }

    /**
     * Sets the playerBox displayed information.
     *
     * @param player The current player
     */
    public void setInformations(Player player, int round) {
        setInformations(false,player,round);
    }

    public void setInformations(boolean isStatic, Player player, int round) {
        if (this.isStatic) {
            return;
        }
        name.setText(player.getName());
        money.setText(player.getMoney());
        String sign;
        if (player.getIncome() >= 0){
            sign = "+";
            this.income.setColor(Color.GREEN);
        } else {
            sign = "-";
            this.income.setColor(Color.RED);
        }
        income.setText(sign + player.getIncome());
        this.round.setText(round);
        Color color;
        switch (player.getFaction()) {
            case FLORAN_TRIBES: color = Color.GREEN; break;
            case FELHEIM_LEGION: color = Color.ROYAL; break;
            case CHERRYSTONE_KINGDOM: color = Color.FIREBRICK; break;
            case HEAVENSONG_EMPIRE: color = Color.WHITE; break;
            default: color = Color.CLEAR;
        }
        Sprite sprite = new Sprite(Assets.getInstance().getTest());
        sprite.setColor(color);
        this.avatar.setDrawable(new SpriteDrawable(sprite));
        this.isStatic = isStatic;
    }


    public void dispose() {
        name = null;
        money = null;
        avatar = null;
        round = null;
        income = null;

    }
}

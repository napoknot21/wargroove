package up.wargroove.core.ui.views.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.controller.Controller;

/**
 * Actions' buttons manager.
 */
public class MoveDialog extends Table {
    private final TextButton wait;
    private final TextButton attack;
    private final TextButton move;
    private final TextButton buy;

    /**
     * Create an empty MoveDialog.
     *
     * @param assets The app Assets manager.
     * @param controller The app controller.
     */
    public MoveDialog(Assets assets, Controller controller) {
        super();
        background(new SpriteDrawable(new Sprite(
                assets.get(Assets.AssetDir.WORLD.getPath() + "test.png", Texture.class)
        )));
        Skin skin = assets.get(Assets.AssetDir.SKIN.getPath() + "uiskin.json", Skin.class);
        wait = new TextButton("wait", skin);
        attack = new TextButton("attack", skin);
        move = new TextButton("move", skin);
        buy = new TextButton("buy", skin);
        initInput(controller);
    }

    private void initInput(Controller controller) {
        wait.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        controller.entityWait();
                        clear();
                    }
                });
        move.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.endMoving();
                clear();
            }

            /*@Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                controller.endMoving();
                moving = false;
                return super.touchDown(event, x, y, pointer, button);
            }*/
        });
        buy.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.openStructureMenu();
                clear();
            }
        });
        attack.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.endAttack();
                clear();
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (getChildren().isEmpty()) {
            return;
        }
        super.draw(batch, parentAlpha);
    }


    private void checkStatus() {


    }

    public void startMoving(boolean visible) {
    }

    public void update(boolean m) {

    }

    public void addWait() {
        addButton(wait);
    }

    public void addMove() {
        addButton(move);
    }

    private void addButton(Button b) {
        if (!getChildren().contains(b, true)) {
            add(b).pad(5);
            row();
        }
    }

    public void addBuy() {

        addButton(buy);
    }

    public void addAttack() {
        addButton(attack);
    }

}
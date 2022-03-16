package up.wargroove.core.ui.views.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.controller.Controller;

public class MoveDialog extends Table {
    private final TextButton wait;
    private final TextButton attack;
    private final TextButton move;
    private boolean moving;

    public MoveDialog(Assets assets, Controller controller) {
        moving = false;
        Skin skin = assets.get(Assets.AssetDir.SKIN.getPath() + "uiskin.json",Skin.class);
        wait = new TextButton("wait", skin);
        attack = new TextButton("attack",skin);
        move = new TextButton("move", skin);

        initDialog();
        initInput(controller);
    }

    private void initInput(Controller controller) {
        wait.addListener(
                new ClickListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        controller.entityWait();
                        moving = false;
                        return super.touchDown(event, x, y, pointer, button);
                    }
                });
        move.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.endMoving();
                moving = false;
            }

            /*@Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                controller.endMoving();
                moving = false;
                return super.touchDown(event, x, y, pointer, button);
            }*/
        });
    }

    private void initDialog() {
        add(move).expand().pad(5);
        row().expand();
        add(wait).left().pad(5);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!moving) {
            return;
        }
        super.draw(batch, parentAlpha);
    }

    public void startMoving(boolean visible) {
        moving = visible || moving;
    }

    public void update(boolean m) {

    }
}

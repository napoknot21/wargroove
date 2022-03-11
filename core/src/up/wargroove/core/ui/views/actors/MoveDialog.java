package up.wargroove.core.ui.views.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.controller.Controller;

public class MoveDialog extends Table {
    private final TextButton cancel;
    private final TextButton wait;
    private final TextButton move;
    private boolean moving;

    public MoveDialog(Assets assets, Controller controller) {
        setVisible(false);
        Skin skin = assets.get(Assets.AssetDir.SKIN.getPath() + "uiskin.json",Skin.class);
        cancel = new TextButton("Cancel", skin);
        wait = new TextButton("wait", skin);
        move = new TextButton("move", skin);

        initDialog();
        initInput(controller);
    }

    private void initInput(Controller controller) {
        move.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                         if(!moving) {
                             controller.startMoving();
                             moving = true;
                             move.setText("Move to");
                             setVisible(false);
                         } else {
                             controller.endMoving();
                             moving = false;
                             move.setText("Move");
                             setVisible(false);
                         }
                    }
                }
        );
    }

    private void initDialog() {
        center();
        add(cancel).pad(0).left();
        row();
        add(wait).pad(0).left();
        row();
        add(move).pad(0).left();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!isVisible()) {
            return;
        }
        super.draw(batch, parentAlpha);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }

    public void update(boolean m) {

    }
}

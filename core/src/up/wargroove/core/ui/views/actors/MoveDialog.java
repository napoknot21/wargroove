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
    private final TextButton wait;
    private boolean moving;

    public MoveDialog(Assets assets, Controller controller) {
        setVisible(false);
        Skin skin = assets.get(Assets.AssetDir.SKIN.getPath() + "uiskin.json",Skin.class);
        wait = new TextButton("wait", skin);

        initDialog();
        initInput(controller);
    }

    private void initInput(Controller controller) {
        wait.addListener(
                new ClickListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        controller.entityWait();
                        return super.touchDown(event, x, y, pointer, button);
                    }
                });
    }

    private void initDialog() {
        add(wait).left();
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

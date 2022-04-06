package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.core.ui.Assets;


public class Codex extends Table {
    private Dialog dialog;
    private final TextButton openCodex;

    public Codex(Assets assets, Controller controller) {
        super();
        Skin skin = assets.get(Assets.AssetDir.SKIN.getPath() + "uiskin.json", Skin.class);
        openCodex = new TextButton("Codex", skin);
        dialog= new Dialog("Codex",skin);
        dialog.hide();
        add(openCodex);
        initInput(controller);
    }
    private void initInput(Controller controller) {
        openCodex.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (controller.isSoundOn()) {
                            Assets.getInstance().getDefault(Sound.class).play();
                        }
                        dialog.show(controller.getScreen().getStage());
                        clear();
                    }
                });
    }

    public TextButton getOpenCodex() {
        return openCodex;
    }
}

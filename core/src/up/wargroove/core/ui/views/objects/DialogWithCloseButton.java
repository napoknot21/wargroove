package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Controller;

public class DialogWithCloseButton extends Dialog {
    private final CheckBox exitBox;

    /**
     * Creates a dialog with an exit button
     *
     * @param title the name of the dialog
     */
    public DialogWithCloseButton(String title, Controller controller) {
        super(title, Assets.getInstance().getSkin());
        exitBox = new CheckBox("", Assets.getInstance().getSkin());
        setClip(false);
        setTransform(true);
        getTitleTable().add(exitBox).padRight(10).padTop(-exitBox.getHeight());
        getTitleTable().row();
        exitBox.setChecked(true);
        exitBox.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        controller.playSound(Assets.getInstance().getSound());
                        hide();
                        exitBox.setChecked(true);
                    }
                });
        this.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (x < 0 || y < 0 || x > getWidth() || y > getHeight()) {
                    hide();
                    return true;
                }
                return false;
            }
        });
    }

    /***************** setters and getters *****************/

    public CheckBox getCloseButtonMenu() {
        return exitBox;
    }
}

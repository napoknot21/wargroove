package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import up.wargroove.core.ui.Assets;

public class DialogWithCloseButton extends Dialog {
    private CheckBox exitBox;

    public DialogWithCloseButton(String title, Skin skin) {
        super(title, skin);
        exitBox= new CheckBox("",skin);
        initialiseLisener();
    }


    private void initialiseLisener(){
        exitBox.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        hide();
                        exitBox.setChecked(true);

                    }
                });
    }

    public CheckBox getClosebuttonMenu(){
        return exitBox;
    }
}

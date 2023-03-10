package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Controller;

/**
 * Actions' buttons manager.
 */
public class MoveDialog extends Table {
    private final TextButton wait;
    private final TextButton attack;
    private final TextButton move;
    private final TextButton buy;
    private final TextButton endTurn;
    private final TextButton nextUnit;

    /**
     * Create an empty MoveDialog.
     *
     * @param assets     The app Assets manager.
     * @param controller The app controller.
     */
    public MoveDialog(Assets assets, Controller controller) {
        super(Assets.getInstance().getSkin());
        background(getSkin().getDrawable("window"));
        Skin skin = assets.getSkin();
        wait = new TextButton("Wait", skin);
        attack = new TextButton("Attack", skin);
        move = new TextButton("Move", skin);
        buy = new TextButton("Buy", skin);
        nextUnit = new TextButton("Next unit", skin);
        endTurn = new TextButton("End turn", skin);
        initInput(controller);

        addButton(endTurn);
        addButton(nextUnit);
    }

    /**
     * Initialise the actions listeners
     */
    private void initInput(Controller controller) {
        wait.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        controller.playSound(Assets.getInstance().getSound());
                        controller.entityWait();
                        clear();
                    }
                });
        move.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.playSound(Assets.getInstance().getSound());
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
                controller.playSound(Assets.getInstance().getSound());
                controller.openStructureMenu();
                clear();
            }
        });
        attack.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.playSound(Assets.getInstance().getSound());
                controller.endAttack();
                clear();
            }
        });

        endTurn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.playSound(Assets.getInstance().getSound());
                controller.endTurn();
                nextUnit.setVisible(true);
                clear();
            }
        });

        nextUnit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.playSound(Assets.getInstance().getSound());
                controller.nextUnit();
                clear();
            }
        });
    }

    /**
     * @param batch       the drawer
     * @param parentAlpha
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (getChildren().isEmpty()) {
            return;
        }
        super.draw(batch, parentAlpha);
    }

    /**
     * Add button in MoveDialog
     *
     * @param b The button
     */
    private void addButton(Button b) {
        if (!getChildren().contains(b, true)) {
            add(b).pad(5);
            row();
        }
    }


    @Override
    public void clear() {
        super.clear();
        add(endTurn).pad(5);
        row();
        add(nextUnit).pad(5);
        row();
    }

    /**
     * Removes MoveDialog
     */
    public void dispose() {
        clear(true);
    }

    /***************** setters and getters *****************/


    public void setEndTurnVisible(boolean visible) {
        endTurn.setVisible(visible);
    }

    public void setNextUnitVisible(boolean visible) {
        nextUnit.setVisible(visible);
    }

    /***************** addButton *****************/

    public void addBuy() {
        clear();
        addButton(buy);
    }

    public void addAttack() {
        addButton(attack);
    }

    public void addWait() {
        addButton(wait);
    }

    public void addMove() {
        addButton(move);
    }
}
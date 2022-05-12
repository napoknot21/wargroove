package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Controller;
import up.wargroove.core.world.Biome;
import up.wargroove.core.world.Tile;

import java.util.ArrayList;


public class Codex extends Table {
    private final DialogWithCloseButton dialog;
    private final TextButton openCodex;
    private final Label description;
    private final ScrollPane paneDescription;
    private final ArrayList<TextButton> subject = new ArrayList<>();
    private final ArrayList<TextButton> object = new ArrayList<>();
    private final Table tableObjects;
    private final ScrollPane paneObject;
    private final Assets assets;


    public Codex(Assets assets, Controller controller) {
        this.assets = assets;
        Skin skin = assets.getSkin();
        openCodex = new TextButton("Codex", skin);
        openCodex.setColor(Color.FIREBRICK);


        dialog = new DialogWithCloseButton("", controller) {
            @Override
            public float getPrefHeight() {
                return Math.max(Gdx.graphics.getHeight() / 1.5f, 300);
            }

            public float getPrefWidth() {
                return Math.max(Gdx.graphics.getWidth() / 2, 500);
            }

            @Override
            public void hide() {
                super.hide();
                description.setText("Welcome to the world\n of NAME_NOT_FOUND ");
            }
        };
        description = new Label("This is the codex\n of the game", skin);
        description.setColor(Color.BLACK);

        createSubject(skin);
        createObject("Entity", skin);
        tableObjects = new Table();
        actualiseTableObject();
        paneDescription = new ScrollPane(description, skin);
        paneObject = new ScrollPane(tableObjects, skin);
        paneObject.setSize(dialog.getPrefWidth() / 5, getPrefHeight());
        initialisePanel(paneObject);
        initialisePanel(paneDescription);

        dialog.getContentTable().row().expand();
        dialog.getContentTable().add(addSubject()).center().colspan(5).expand().fill();
        dialog.getContentTable().row();
        dialog.getContentTable().add(paneObject).expand().fill();
        dialog.getContentTable().add(paneDescription).expand().fill();
        initInput(controller);
        add(openCodex);
        resize();
    }

    private void initInput(Controller controller) {
        openCodex.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        controller.playSound(Assets.getInstance().getSound());
                        dialog.show(getStage());
                        resize();

                    }
                });
        for (TextButton b : object) {
            b.addListener(
                    new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            controller.playSound(Assets.getInstance().getSound());
                            setDescription("Entity", b.getLabel().getText().toString());
                            resize();

                        }
                    });
            for (TextButton a : subject) {
                a.addListener(
                        new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                controller.playSound(Assets.getInstance().getSound());
                                createObject(a.getLabel().getText().toString(), openCodex.getSkin());
                                actualiseTableObject();
                                description.setText(
                                        "Here you can find the\n description of \nthe different "
                                                + a.getLabel().getText().toString() + "s "
                                );
                                if (a.getLabel().getText().toString().equals("Game")) {
                                    description.setText("Here you can find \nthe description \n about the game");
                                }
                                resize();

                            }
                        });

            }
        }
    }

    private void initialisePanel(ScrollPane pane) {
        pane.setScrollbarsVisible(false);
        pane.setScrollingDisabled(true, false);
        pane.setScrollbarsVisible(true);
        pane.setCancelTouchFocus(false);
        pane.setSmoothScrolling(true);
    }


    public TextButton getOpenCodex() {
        return openCodex;
    }

    private Table addSubject() {
        Table table = new Table();
        for (TextButton textButton : subject) {
            table.add(textButton).expand().size(textButton.getMinWidth(), textButton.getMinHeight());
        }
        return table;
    }

    private void actualiseTableObject() {
        tableObjects.clear();
        for (TextButton textButton : object) {
            tableObjects.add(textButton);
            tableObjects.row();
        }
    }

    private void createSubject(Skin skin) {
        subject.add(new TextButton("Entity", skin));
        subject.add(new TextButton("Tile", skin));
        subject.add(new TextButton("Faction", skin));
        subject.add(new TextButton("Biome", skin));
        subject.add(new TextButton("Game", skin));
    }

    private void createObject(String s, Skin skin) {
        object.clear();
        Object[] values;
        switch (s) {
            case "Tile":
                values = Tile.Type.values();
                break;
            case "Faction":
                values = Faction.values();
                break;
            case "Biome":
                values = Biome.values();
                break;
            case "Game":
                values = Codex.Game.values();
                break;
            default:
                values = Entity.Type.values();
        }
        for (Object t : values
        ) {
            TextButton textButton = new TextButton(t.toString(), skin);
            object.add(textButton);
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    try {
                        setDescription(s, t.toString());
                        resize();
                    } catch (Exception e) {
                        description.setText("Unknown description");

                    }
                }
            });
        }

    }


    private void resize() {
        float x = dialog.getX();
        float y = dialog.getY();
        float h = dialog.getHeight();
        dialog.setPosition(x, y - (dialog.getPrefHeight() - h));
        paneDescription.setSize(1000, 1000);
        paneDescription.getActor().getParent().setSize(200, 100);

    }


    private void setDescription(String object, String name) {
        description.setText(getDescription(object, name));
    }

    private String getDescription(String object, String name) {
        Object type = null;

        switch (object) {
            case "Entity":
                type = Entity.Type.valueOf(name.toUpperCase());
                break;
            case "Tile":
                type = Tile.Type.valueOf(name.toUpperCase());
                break;
            case "Faction":
                type = Faction.valueOf(name.toUpperCase());
                break;
            case "Biome":
                type = Biome.valueOf(name.toUpperCase());
                break;
            case "Game":
                type = Codex.Game.valueOf(name.toUpperCase());
                break;
        }
        try {
            return assets.get(type, (dialog.getPrefWidth() == Gdx.graphics.getWidth() / 2f) ? 50 : 20);
        } catch (Exception e) {
            return "Unknown description";
        }
    }

    public enum Game {
        CREATORS,
        MUSIC,
        ART
    }
}

package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.world.Biome;
import up.wargroove.core.world.Tile;

import java.awt.*;
import java.awt.Button;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;


public class Codex extends Table {
    private final Dialog dialog;
    private final TextButton openCodex;
    private final TextButton exitCodex;
    private final Label description;
    private ArrayList<TextButton> subject= new ArrayList<TextButton>();
    private ArrayList<TextButton> object= new ArrayList<TextButton>();
    private Table tableObjects;
    private TextureRegion textureRegion;
    private final Assets assets;



    public Codex(Assets assets, Controller controller) {
        this.assets=assets;
        Skin skin = assets.getSkin();
        openCodex = new TextButton("Codex", skin);
        openCodex.setColor(Color.FIREBRICK);

        exitCodex = new TextButton("X", skin);
        exitCodex.setColor(Color.RED);
        dialog= new Dialog("Codex",skin);
        dialog.setBounds(500,500,50,50);
        description= new Label("This is the codex of the game",skin);
        ScrollPane pane= new ScrollPane(description,skin);
        createSubject(skin);
        createObject("",skin);
        tableObjects= new Table();
        actualiseTableObject();
        Table top = new Table();
        top.add();
        top.right().add(exitCodex).right().top();
        top.row();
        top.add();
        top.add(addSubject());
        top.row();
        top.add(tableObjects);
        top.add(pane);
        top.add();
        dialog.top().add(top);
        initInput(controller);
        add(openCodex);

    }
    private void initInput(Controller controller) {
        openCodex.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialog.setBounds(dialog.getX(),dialog.getY(), dialog.getPrefWidth(), dialog.getPrefHeight());
                        dialog.show(getStage());
                    }
                });
        exitCodex.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        dialog.hide();
                        description.setText("Welcome to the world of NAME_NOT_FOUND ");
                    }
                });
        for (TextButton b: object
             ) { b.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        setDescription(b.getLabel().getText().toString());
                        dialog.setBounds(dialog.getX(),dialog.getY(), dialog.getPrefWidth(), dialog.getPrefHeight());

                    }
                });
            for (TextButton a: subject
            ) { a.addListener(
                    new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            float x= dialog.getX();
                            float y= dialog.getY();
                            dialog.setBounds(x,y, dialog.getPrefWidth(), dialog.getPrefHeight());
                            dialog.setPosition(x,y);
                            object.removeAll(object);
                            tableObjects.clear();
                            createObject(a.getLabel().getText().toString(),openCodex.getSkin());
                            actualiseTableObject();
                            description.setText("Here you can find the description of the differents "+a.getLabel().getText().toString()+"s ");

                        }
                    });

            }
        }
    }



    public TextButton getOpenCodex() {
        return openCodex;
    }

    private Table addSubject(){
        Table table= new Table();
        for (int i=0;i<subject.size();i++){
            table.add(subject.get(i)).expand();
        }
        return table;
    }

    private void actualiseTableObject(){
        for (int i=0;i<object.size();i++){
            tableObjects.add(object.get(i));
            tableObjects.row();
        }
    }

    private void createSubject(Skin skin){
        subject.add(new TextButton("Entity",skin));
        subject.add(new TextButton("Tile",skin));
        subject.add(new TextButton("Faction",skin));
        subject.add(new TextButton("Biome",skin));
        subject.add(new TextButton("Game",skin));
    }

    private void createObject(String s, Skin skin){
        Object [] values=null;
        switch (s){
            case "Tile": values= Tile.Type.values(); break;
            case "Faction": values= Faction.values(); break;
            case  "Biome": values= Biome.values(); break;
            default: values= Entity.Type.values();
        }
        for (Object t: values
        ) {
            TextButton textButton=new TextButton(t.toString(),skin);
            object.add(textButton);
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    try {
                        setDescription(t.toString());
                        dialog.setBounds(dialog.getX(),dialog.getY(), dialog.getPrefWidth(), dialog.getPrefHeight());
                    } catch (Exception e) {
                        description.setText( "Unknown description");
                    }
                }
            });
        }

    }




    private void setDescription(String name){
        description.setText(getDescription(name));
    }

    //TODO comprendre le systeme, pourquoi pas get(/descriptions/name)?
    private String getDescription(String name) {
        /*

         switch (name){
            case "Tile": values= Entity.Type.valueOf(name.toUpperCase()); break;
            case "Faction": values= Faction.valueOf(name.toUpperCase()); break;
            case  "Biome": values= Biome.valueOf(name.toUpperCase()); break;
            default: values= Entity.Type.valueOf(name.toUpperCase());
        }
         */
        var type= Entity.Type.valueOf(name.toUpperCase());
        try {
            return assets.get(type,60);
        } catch (Exception e) {
            return "Unknown description";
        }
        //assets.getInstance
    }

}

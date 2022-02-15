package up.wargroove.core.ui.views.actors;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import up.wargroove.core.character.Character;
import up.wargroove.core.ui.views.objects.GameMap;
import up.wargroove.core.ui.views.objects.MapTile;
import up.wargroove.core.ui.views.scenes.GameView;
import up.wargroove.utils.Pair;

import java.io.File;
import java.util.Random;

public class CharacterUI extends Actor {
    Texture texture;
    Sprite sprite;
    GameMap gameMap;
    GameView gameView;
    Pair<Integer, Integer> coordinate;
    int coordX;
    int coordY;
    MapTile tile;
    Character character;


    private static String getPath() {
        // return "data/sprites/character/" + character.getTypeUnit + File.separatorChar + character.getFaction + File.separatorChar+ fileName;
        return "data/sprites/character/" + "GROUND" + File.separatorChar + "VILLAGER" + File.separatorChar + "1" + File.separatorChar + "DIE" + File.separatorChar +"tile260.png";

    }

/*
    public CharacterUI(Character character){
        this.texture= new Texture((Gdx.files.internal(getPath( character))));
    }
*/

    public Texture getTexture() {
        return texture;
    }
/*
    public CharacterUI(){
        this.texture= new Texture((Gdx.files.internal("data/sprites/character/test.png")));
        this.sprite= new Sprite(texture);
        setBounds(sprite.getX(),sprite.getY(),sprite.getWidth(),sprite.getHeight());
        this.setTouchable(Touchable.enabled);


        addListener(new InputListener(){
            public boolean move() {
                if (Gdx.input.isTouched()) {
                    MoveByAction mba = new MoveByAction();
                    mba.setAmount(sprite.getX() + Gdx.input.getX(), sprite.getY() + Gdx.input.getY());
                    mba.setDuration(5f);
                    CharacterUI.this.addAction(mba);
                }
                return true;
            }
        });
    }
*/

    /**
     * Represent a visual character.
     */


    public CharacterUI(GameMap gameMap, GameView view, Pair<Integer, Integer> coord, Character character){
        this.gameMap= gameMap;
        this.gameView = view;
        this.character= character;
        gameMap.getWorld().addEntity(coord,character);
        this.texture= new Texture((Gdx.files.internal("data/sprites/character/"+ character.getType().component + "/"+ character.getType()+"/" + character.getFaction() +"/LIFE.png")));
        this.sprite= new Sprite(texture);
        sprite.setSize(20,30);
        this.coordinate= coord;
        this.tile= (MapTile) gameMap.getTileLayer().getCell(coordinate.first,coordinate.second).getTile();

/*
        //gameMap.getLayers().get(0).getOffsetY()
        this.setTouchable(Touchable.enabled);


        addListener(new InputListener(){
            public boolean move() {
                if (Gdx.input.isTouched()) {
                    MoveByAction mba = new MoveByAction();
                    mba.setAmount(sprite.getX() + Gdx.input.getX(), sprite.getY() + Gdx.input.getY());
                    mba.setDuration(5f);
                    CharacterUI.this.addAction(mba);
                }
                return true;
            }
        });
*/
    }

    public CharacterUI(GameMap gameMap, GameView view, int x, int y, String faction) {
        this.gameMap = gameMap;
        this.gameView = view;
        this.texture = new Texture((Gdx.files.internal("data/sprites/character/GROUND/VILLAGER/" + faction + "/DIE/tile260.png")));
        this.sprite = new Sprite(texture);
        sprite.setSize(20, 30);
        tile = (MapTile) gameMap.getTileLayer().getCell(x, y).getTile();
        coordX = x * gameMap.getTileWidth();
        coordY = y * gameMap.getTileHeight();
    }

        @Override
    public void positionChanged() {
        sprite.setPosition(getX(), getY());
        super.positionChanged();
    }




    public CharacterUI(CharacterType characterType){
        this.texture= new Texture((Gdx.files.internal("data/sprites/character/test.png")));
        this.setTouchable(Touchable.enabled);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //sprite.setScale(gameMap.getScale());
        sprite.setPosition(coordX,coordY);
        sprite.draw(batch);
        super.draw(batch,parentAlpha);
        //batch.draw(texture, Gdx.graphics.getWidth() / 2f,Gdx.graphics.getHeight() / 2f);
    }


    public static Table createActors(GameMap gameMap, GameView view, int nb){
        Table table = new Table();
        for (int i= 0;i< nb;i++ ){
            table.add(new CharacterUI(gameMap,view, i, i, Integer.toString((i%4)+1)));
        }
        return table;
    }

    /* Tests with actions

   Problemes: Ça ne lis pas les actions, voir act() normalement à mettre sur le renderer
                Si après on change les coordonnées ça n'utilise pas les actions, donc si on utilise actions, apres on va pas pouvoir modifier les coords??
    * */

    public void moveNorth(){
        this.addAction(Actions.moveBy( 0,gameMap.getTileHeight(),5));
        this.addAction(Actions.color(Color.RED));
        this.sprite.setTexture(new Texture((Gdx.files.internal("data/sprites/character/"+ character.getType().component + "/"+ character.getType()+"/" + character.getFaction() +"/MOVE/tile008.png"))));
        coordY+= gameMap.getTileHeight();

    }

    public void moveEast(){
        MoveByAction mba = new MoveByAction();
        this.sprite.setTexture(new Texture((Gdx.files.internal("data/sprites/character/"+ character.getType().component + "/"+ character.getType()+"/" + character.getFaction() +"/MOVE/tile011.png"))));
        mba.setDuration(5f);
        mba.setAmount( gameMap.getTileWidth(),0);
        this.addAction(mba);
        coordY+= gameMap.getTileWidth();

    }

    public void moveSouth(){
        MoveByAction mba = new MoveByAction();
        this.sprite.setTexture(new Texture((Gdx.files.internal("data/sprites/character/"+ character.getType().component + "/"+ character.getType()+"/" + character.getFaction() +"/MOVE/tile010.png"))));
        mba.setDuration(5f);
        mba.setAmount( 0,-gameMap.getTileHeight());
        this.addAction(mba);
        coordY-= gameMap.getTileHeight();

    }

    public void moveWest(){
        MoveByAction mba = new MoveByAction();
        this.sprite.setTexture(new Texture((Gdx.files.internal("data/sprites/character/"+ character.getType().component + "/"+ character.getType()+"/" + character.getFaction() +"/MOVE/tile009.png"))));
        mba.setDuration(5f);
        mba.setAmount( -gameMap.getTileWidth(),0);
        this.addAction(mba);
        coordY-= gameMap.getTileWidth();

    }

    public void die(){
        this.sprite.setTexture(new Texture((Gdx.files.internal("data/sprites/character/"+ character.getType().component + "/"+ character.getType()+"/" + character.getFaction() +"DIE.png"))));

    }


}

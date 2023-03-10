package up.wargroove.core.world;

import java.util.Queue;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Null;
import up.wargroove.core.character.Character;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.Entity;
import up.wargroove.core.character.entities.Commander;

public class Player {

    private Queue<Entity> entities;
    private final Faction faction;
    private String name;
    private int money;
    private int income;
    private final float ratio;
    private final Color color;


    /**
     * Constructor for Player
     * @param faction player faction
     * @param ratio quotient
     */
    public Player(Faction faction, float ratio) {

        entities = new LinkedList<>();
        this.faction = faction;
        this.money = (int) (1000 * ratio);
        //addEntity(new Commander());
        this.ratio = ratio;
        switch (faction) {  //Color based on the tile set
            case FLORAN_TRIBES:
                color = new Color(111 / 255f, 153 / 255f, 13 / 255f, 1);
                break;
            case FELHEIM_LEGION:
                color = new Color(13 / 255f, 76 / 255f, 153 / 255f, 1);
                break;
            case CHERRYSTONE_KINGDOM:
                color = new Color(153 / 255f, 23 / 255f, 13 / 255f, 1);
                break;
            case HEAVENSONG_EMPIRE:
                color = new Color(153 / 255f, 120 / 255f, 13 / 255f, 1);
                break;
            default:
                color = Color.CLEAR;
        }

    }


    /**
     * entity management available to the player
     * @param character entity to add
     */
    public void addEntity(Entity character) {
        if (character instanceof Structure) {
            addIncome(((Structure) character).getBonus());
        }
        entities.add(character);
    }


    /**
     * Delete an entity (structure) from the player list of entities
     * @param character entity to delete
     */
    public void removeEntity(Entity character) {
        if (character instanceof Structure) {
            addIncome(-((Structure) character).getBonus());
        }
        entities.remove(character);

    }


    /**
     * check if there are still structures(characters) available to play
     * @return True if there are ones, else False
     */
    public boolean hasNext() {
        return !entities.element().isExhausted();
    }


    /**
     * Retrieves and removes the head of the queue of entities
     * @return the entity if it's available to play it
     */
    @Null
    public Entity next() {
        if (entities.isEmpty()) return null;

        Entity entity = entities.poll();
        entities.add(entity);

        return entity.isExhausted() ? null : entity;

    }


    /**
     * Retrieves the head of entities
     * @return the head of entities
     */
    public Entity peekEntity() {
        return entities.peek();
    }


    /**
     * Pass the player's turn
     * @return False if the commandant is not alive or if the StrongHold was destroyed, and else True
     */
    public boolean nextTurn() {

        boolean isCommanderAlive = false;
        boolean isStrongholdAlive = false;

        for (Entity c : entities) {

            c.nextTurn();
            isCommanderAlive |= c.getType() == Entity.Type.COMMANDER;
            isStrongholdAlive |= c instanceof Stronghold;

        }
        money = Math.max(money + income, 0);
        return isCommanderAlive && isStrongholdAlive;

    }


    /**
     * Browse the list of entities availables to play
     * @return Null if there is not more playable entities,
     */
    @Null
    public Entity nextPlayableEntity() {
        int size = entities.size();
        for (int i = 0; i < size; i++) {
            Entity e = next();
            if (e != null) {
                return e;
            }
        }
        return null;
    }

    /***************** setters and getters *****************/

    public Faction getFaction() {
        return faction;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getMoney() {
        return money;
    }

    public void addIncome(int income) {
        this.income += (income * ratio);
    }

    public int getIncome() {
        return income;
    }

    public Queue<Entity> getEntities() {
        return entities;
    }

    public void buy(int amount) {
        this.money -= amount;
    }

    public Color getColor() {
        return color;
    }
}

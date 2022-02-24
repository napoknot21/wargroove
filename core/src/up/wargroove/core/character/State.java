package up.wargroove.core.character; 

public class State {

    private boolean isAlive;
    private boolean waiting;
    private boolean played;
    
    /**
     * constructeur (provisoire) pour State (état courant du personnage)
     *
     */
    State (boolean isAlive, boolean waiting) {
        this.isAlive = isAlive;
        if (isAlive)
            if (waiting) {
                this.waiting = true;
                this.played = false;
            } else {
                this.waiting = false;
                this.played = true;
            }
        } else {
            this.waiting = false;
            this.played = false;
        }
    }
    
    
    public play () {
     
        if (this.waiting && !this.played) {
            this.waiting = false;
            this.played = true;
        }
    
    }

}
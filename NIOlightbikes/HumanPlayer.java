/**
 * A player controlled by a person using the arrow keys
 * @author Johan & Luther
 */
public class HumanPlayer extends Player {

    //=================
    //=== VARIABLES ===
    //=================

    // The player's ID
    private Long clientID;
    //Player score (~= number of seconds played)
    private int score;
    // Whether the player is watching the current game
    private boolean watching;

    //===================
    //=== CONSTRUCTOR ===
    //===================

    /**
     * Creates a new human player
     * @param clientID
     */
    public HumanPlayer(Long clientID) {
        super();
        setScore(0);
        setWatching(true);
        setClientID(clientID);
    }

    //=======================
    //=== SCORE FUNCTIONS ===
    //=======================

    /**
     * Increments the score by 1
     */
    public void incrementScore(){
        this.score ++;
    }

    //=========================
    //=== GETTERS & SETTERS ===
    //=========================

    /**
     * Returns the score
     * @return The score
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the score
     * @param score The new score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Returns the player ID
     * @return The player ID
     */
    public Long getClientID() {
        return clientID;
    }

    /**
     * Sets the player ID
     * @param clientID The player ID
     */
    private void setClientID(Long clientID) {
        this.clientID = clientID;
    }

    /**
     * Returns whether the player is watching the current game or not
     * @return Whether the player is watching the current game or not
     */
    public boolean isWatching() {
        return watching;
    }

    /**
     * Sets whether the player is watching the current game or not
     * @param watching True for watching, false for not watching
     */
    public void setWatching(boolean watching) {
        this.watching = watching;
    }
}

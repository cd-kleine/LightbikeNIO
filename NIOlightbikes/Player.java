/**
 * Assembles all the attributes of a player in an abstract class
 * @author Johan & Luther
 */
public abstract class Player {

    //=================
    //=== VARIABLES ===
    //=================

    private int ixCarPos; //Position of the players on the x axis (columns; 0 = left)
    private int iyCarPos; //Position of the players on the y axis (lines  ; 0 = top)
    private char cCarDir;  //Current player orientation; 'U'p  'D'own 'L'eft 'R'ight
    private boolean alive; //Are players still in play or are they eliminated?
    private String sName; //Current winner of the game

    //===================
    //=== CONSTRUCTOR ===
    //===================

    /**
     * Creates a new player
     */
    public Player(){
        setIxCarPos(0);
        setIyCarPos(0);
        setcCarDir(' ');
        setsName("");
        setAlive(false);
    }

    //==========================
    //=== POSITION FUNCTIONS ===
    //==========================

    /**
     * Increments the x position
     */
    public void incrementIxCarPos(){
        this.ixCarPos++;
    }

    /**
     * Decrements the x position
     */
    public void decrementIxCarPos(){
        this.ixCarPos--;
    }

    /**
     * Increments the y position
     */
    public void incrementIyCarPos(){
        this.iyCarPos++;
    }

    /**
     * Decrements the y position
     */
    public void decrementIyCarPos(){
        this.iyCarPos--;
    }

    //===========================
    //=== GETTERS AND SETTERS ===
    //===========================

    /**
     * Returns the x position
     * @return The x position
     */
    public int getIxCarPos() {
        return ixCarPos;
    }

    /**
     * Sets the x position
     * @param ixCarPos The new x position
     */
    public void setIxCarPos(int ixCarPos) {
        this.ixCarPos = ixCarPos;
    }

    /**
     * Returns the y position
     * @return The y position
     */
    public int getIyCarPos() {
        return iyCarPos;
    }

    /**
     * Sets the y position
     * @param iyCarPos The new y position
     */
    public void setIyCarPos(int iyCarPos) {
        this.iyCarPos = iyCarPos;
    }

    /**
     * Returns the current direction
     * @return The current direction
     */
    public char getcCarDir() {
        return cCarDir;
    }

    /**
     * Sets the direction
     * @param cCarDir The new direction
     */
    public void setcCarDir(char cCarDir) {
        this.cCarDir = cCarDir;
    }

    /**
     * Returns whether the player is still alive in the ongoing game
     * @return Whether the player is still alive in the ongoing game
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Sets whether the player is still alive in the ongoing game
     * @param alive True if the player is still alive in the ongoing game, false otherwise
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * Returns the name of the player
     * @return The name of the player
     */
    public String getsName() {
        return sName;
    }

    /**
     * Sets the name of the player
     * @param sName The new name of the player
     */
    public void setsName(String sName) {
        this.sName = sName;
    }
}

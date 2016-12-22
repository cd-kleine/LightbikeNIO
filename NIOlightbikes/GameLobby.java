import java.awt.*;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class handles the multiplayer games
 * @author Johan & Luther
 */
public class GameLobby {

    //=================
    //=== VARIABLES ===
    //=================

    // Timer vars
    private static final int TIMER_MAX_SECONDS = 10;
    private static final int TIMER_INTERVAL = 1000;
    private Timer timer;

    // Players
    private static final int MAX_PLAYERS = 4;
    private final HashMap<Long, HumanPlayer> players;

    // Parent server
    private ILobbyManager server;
    // Game core
    private Core core;

    // For debugging
    private final Long lobbyID;

    //===================
    //=== CONSTRUCTOR ===
    //===================

    /**
     * Creates a new game lobby
     * @param server The parent server
     * @param lobbyID The lobby ID
     */
    public GameLobby(ILobbyManager server, Long lobbyID){
        this.lobbyID = lobbyID;
        this.server = server;
        this.players = new HashMap<>(MAX_PLAYERS);
    }

    //======================
    //=== GAME FUNCTIONS ===
    //======================

    /**
     * Starts the game
     */
    public void startGame(){
        // Checks if the game is not already in progress
        if(core == null || !core.isGameInProgress()){
            // Initialize the core
            core = new Core(players.values());

            // Launches the core thread
            Thread coreThread = new Thread() {
                public void run() {
                    System.out.println("Start game");
                    // Run the game
                    core.runGame();
                    System.out.println("End game - Clear players");
                    // Removes the players from the lobby at the end of the game
                    players.clear();
                }
            };
            coreThread.start();

            // Launch an update thread for each player
            for (HumanPlayer player : players.values()){
                Thread gameThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // While the game is in progress or while the player is watching the game
                        // Update the player screen every 50ms
                        while (core.isGameInProgress() && player.isWatching()) {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                System.out.println("Exception in GameLobby " + lobbyID + " : " + e.getMessage());
                            }
                            if(player.isWatching()){
                                server.updatePlayer(player.getClientID(), player.getScore(), core.getGrid(),
                                        !core.isGameInProgress(), core.getsWinnerName());
                            }
                        }
                    }
                });
                gameThread.start();
            }
        }
    }

    //=======================
    //=== LOBBY FUNCTIONS ===
    //=======================

    /**
     * Starts the lobby countdown timer
     */
    private void startTimer() {
        // Stop the timer if it was already running
        if(timer != null) {
            timer.cancel();
        }

        // Determine what the timer has to do
        TimerTask timerTask = new TimerTask() {
            int counter = TIMER_MAX_SECONDS;
            @Override
            public void run() {
                // Update the lobby timer of all the players
                server.updateLobbyTimer(players.keySet(), counter);

                if(counter <= 0) {
                    if(players.size() > 1){
                        startGame();
                    }
                    else if(players.size() == 1){
                        startTimer();
                    }
                    cancel();
                }

                counter--;
            }
        };
        // Running timer task as daemon thread
        timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, TIMER_INTERVAL);
    }

    /**
     * Adds a player to the list if he can still join the game
     * @param clientID The player ID of the new player
     * @return Whether or not the new player could join
     */
    public boolean join(long clientID){
        // Check if the new player can join
        boolean canJoin =
                (core == null || !core.isGameInProgress()) &&
                getNbrOfPlayers() < MAX_PLAYERS &&
                !players.containsKey(clientID);

        // If he can join ...
        if(canJoin){
            // Check if he is the first player
            boolean firstPlayer = players.isEmpty();
            // Add him to the list of players
            players.put(clientID, new HumanPlayer(clientID));

            // Update the lobby player list of all the players
            server.updateLobbyPlayerList(players.keySet());

            // If he is the first player, start the countdown timer
            if(firstPlayer){
                startTimer();
            }
        }
        return canJoin;
    }

    /**
     * Removes a player from the lobby
     * @param clientID The player ID of the player that wants to leave
     */
    public void leave(long clientID){
        // If the player is really part of the lobby's players
        if(players.containsKey(clientID)){
            // The player doesn't want to see the game any longer
            players.get(clientID).setWatching(false);
            // Remove the player from the list
            players.remove(clientID);

            // If there are no players anymore
            if(players.isEmpty()){
                // End the game
                if(core != null){
                    core.endGame();
                }
                // Stop the timer
                if(timer != null) {
                    timer.cancel();
                }
            } // Update the lobby player list
            else if(core == null || !core.isGameInProgress()){
                server.updateLobbyPlayerList(players.keySet());
            }
        }
    }

    //=======================
    //=== CLIENT REQUESTS ===
    //=======================

    /**
     * Changes the current direction of the player
     * @param clientID The player ID of the player that wants to change direction
     * @param newDirection The new direction
     */
    public void setCurrentDirection(long clientID, char newDirection){
        if(players.containsKey(clientID)){
            players.get(clientID).setcCarDir(newDirection);
        }
    }

    /**
     * Returns the current direction of the player
     * @param clientID The player ID of the player
     * @return The current direction of the player
     */
    public char getCurrentDirection(long clientID){
        return players.containsKey(clientID) ? players.get(clientID).getcCarDir() : ' ';
    }

    /**
     * Returns whether the player is still alive or not
     * @param clientID The player ID of the player
     * @return Whether the player is still alive or not
     */
    public boolean isPlayerAlive(long clientID) {
        return players.containsKey(clientID) && players.get(clientID).isAlive();
    }

    //===============
    //=== GETTERS ===
    //===============

    /**
     * Returns the game lobby ID
     * @return The game lobby ID
     */
    public long getLobbyID(){
        return this.lobbyID;
    }

    /**
     * Returns the number of players in the game lobby
     * @return The number of players in the game lobby
     */
    public int getNbrOfPlayers(){
        return players.size();
    }
}

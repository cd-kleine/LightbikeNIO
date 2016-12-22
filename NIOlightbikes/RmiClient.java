import java.awt.*;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class implements the client interface
 * @author Johan & Luther
 */
public class RmiClient extends UnicastRemoteObject implements IClient, Serializable {

    //=================
    //=== VARIABLES ===
    //=================

    // The different screens
    private final GUI gGUI;
    private final GuiLogin guiLogin;
    private GuiMultiplayer guiMultiplayer;
    // The server stub
    private final IServer server;
    // The client ID given by the server
    private Long clientID;
    // The core used when playing solo games
    private Core soloCore;
    private final Collection<HumanPlayer> players;
    // The Human player representing the client when playing solo games
    private final HumanPlayer soloPlayer;
    // The number of games played (identifies/differentiates the games)
    private int gameCount = 0;
    // Whether the client chooses to play solo
    private boolean goingSolo;
    
    //login et pwd
    private String login;
    private String pwd;
    //===================
    //=== CONSTRUCTOR ===
    //===================

    /**
     * Creates an RMI client
     * @param server The server stub
     * @throws RemoteException
     * @throws InvocationTargetException
     * @throws éInterruptedException
     * @throws AlreadyLoggedInException
     */
    public RmiClient(IServer server) throws RemoteException, InvocationTargetException, InterruptedException {
        super();
        this.server = server;
        System.out.println("Registered to server");

        //This are the GUI objects, responsible for all the displays
        gGUI = new GUI(this);
        guiLogin = new GuiLogin(this);
        guiMultiplayer = new GuiMultiplayer(this, gGUI);

        // Create core for solo play
        players = new ArrayList<>(4);
        soloPlayer = new HumanPlayer(0L);
        players.add(soloPlayer);
        

        //Lauching the GUI, using Swing EDT to remain "thread-safe".
        java.awt.EventQueue.invokeAndWait(new Runnable() {
            public void run() {
                guiLogin.setLocation(100, 100);
                guiLogin.setVisible(true);
            }
        });
    }

    //======================
    //=== STUB FUNCTIONS ===
    //======================

    /**
     * Does nothing, used by the server to see if the client is still there
     * @throws RemoteException
     */
    @Override
    public void hello() throws RemoteException {}

    /**
     * Updates the game screen
     * @param score The current score
     * @param grid The current grid
     * @param isGameOver Whether the game is over
     * @param winnerName The name of the winner
     * @throws RemoteException
     */
    @Override
    public void updateGame(int score, int[][] grid, boolean isGameOver, String winnerName) throws RemoteException {
        gGUI.update(score, grid, isGameOver, winnerName);
    }

    /**
     * Updates the player list of lobby screen
     * @param playerList The new player list
     * @throws RemoteException
     */
    @Override
    public void updateLobbyPlayerList(Collection<String> playerList) throws RemoteException {
        guiMultiplayer.updatePlayerList(playerList.toArray(new String[playerList.size()]));
    }

    /**
     * Update the timer on the lobby screen
     * @param countDown The current timer value
     * @throws RemoteException
     */
    @Override
    public void updateLobbyTimer(int countDown) throws RemoteException {
        guiMultiplayer.updateTimer(countDown);
    }

    /**
     * Starts a solo game or joins a lobby for a multiplayer game
     * @param solo Whether the user wants to play solo or in multiplayer
     * @throws RemoteException
     */
    @Override
    public void joinGame(boolean solo) throws RemoteException {
        // End solo game if it was in progress
        if(soloCore != null && soloCore.isGameInProgress()){
            System.out.println("Please stop " + gameCount);
            soloCore.endGame();
        }

        quitGame();

        // Increment the game count
        gameCount++;

        if(solo){
            // Start solo game
            gGUI.ShowLoginGUI.setBackground(Color.RED);
            goingSolo = true;
            soloCore = new Core(players);
            // Lauch the solo core
            Thread coreThread = new Thread() {
                public void run() {
                    final int gameNumber = gameCount;
                    System.out.println("Start " + gameNumber);
                    soloCore.runGame();
                    System.out.println("End " + gameNumber);
                }
            };
            coreThread.start();
            // Launch the game update thread for the solo player
            Thread gameThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    final int gameNumber = gameCount;
                    System.out.println("Start updateGame " + gameNumber);
                    while (soloCore.isGameInProgress() && gameNumber == gameCount) {
                        try {
                            Thread.sleep(50);
                            if(gameNumber == gameCount){
                                updateGame(soloPlayer.getScore(), soloCore.getGrid(),
                                        !soloCore.isGameInProgress(), soloCore.getsWinnerName());
                            }
                        } catch (InterruptedException | RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("End updateGame " + gameNumber);
                }
            });
            gameThread.start();
        }
        else if(clientID != null){
            // Start multiplayer game
            goingSolo = false;
            server.joinGame(clientID);
            // Make the main gui unusable
            gGUI.setEnabled(false);
            gGUI.setVisible(false);
            guiMultiplayer.setLocation(gGUI.getLocation());
            guiMultiplayer.setVisible(true);
        }
    }

    /**
     * Leave the mutliplayer game / lobby
     * @throws RemoteException
     */
    @Override
    public void quitGame() throws RemoteException {
        if(clientID != null) {
            server.quitGame(clientID);
        }
    }

    /**
     * Leave the game and log out from the server
     * @throws RemoteException
     */
    @Override
    public void quitApp() throws RemoteException {
        quitGame();
        if(clientID != null) {
            server.logOut(clientID);
        }
    }

    /**
     * Change the move direction of the player
     * @param newDirection The new direction
     * @throws RemoteException
     */
    @Override
    public void setCurrentDirection(char newDirection) throws RemoteException {
        // If solo, act the solo way
        if(goingSolo && soloPlayer.isAlive()){
            soloPlayer.setcCarDir(newDirection);
        } // If in a multiplayer game, contact the server
        else if(clientID != null) {
            server.setCurrentDirection(clientID, newDirection);
        }
    }

    /**
     * Returns the current direction of the player
     * @return The current direction of the player
     * @throws RemoteException
     */
    @Override
    public char getCurrentDirection() throws RemoteException {
        char currentDirection = ' ';
        // If solo, act the solo way
        if(goingSolo && soloPlayer.isAlive()){
            currentDirection = soloPlayer.getcCarDir();
        } // If in a multiplayer game, contact the server
        else if(clientID != null){
            currentDirection = server.getCurrentDirection(clientID);
        }
        return currentDirection;
    }

    /**
     * Returns whether the player is alive in his current game session
     * @return Whether the player is alive in his current game session
     * @throws RemoteException
     */
    @Override
    public boolean isPlayerAlive() throws RemoteException {
        boolean isAlive = false;
        // If solo, act the solo way
        if(goingSolo){
            isAlive = soloPlayer.isAlive();
        } // If in a multiplayer game, contact the server
        else if(clientID != null){
            isAlive = server.isPlayerAlive(clientID);
        }
        return isAlive;
    }

    /**
     * Asks the server to create a new account
     * @param login The login of the new account
     * @param pwd The password of the new account
     * @return Whether the new account was successfully created
     * @throws RemoteException
     */
    @Override
    public void createAccount(String login, String pwd) throws RemoteException {
        this.login = login;
        this.pwd = pwd;
        server.createAccount(this,login, pwd);
    }

    /**
     * Attempts to log into the server
     * @param login The login
     * @param pwd The password
     * @return Whether the login was successful
     * @throws RemoteException
     * @throws AlreadyLoggedInException
     */
    @Override
    public boolean logIn(String login, String pwd) throws RemoteException, AlreadyLoggedInException {
        // Attempt to log in
        clientID = server.logIn(this,login, pwd);
        boolean loggedIn = clientID != null;
        // If successful, close the login screen and open the game screen
        if(loggedIn){
            guiLogin.setVisible(false);
            gGUI.setShowLoginGUI(login);
            gGUI.setLocation(guiLogin.getLocation());
            gGUI.setSize(500, 550);
            gGUI.setVisible(true);
        }
        return loggedIn;
    }

    @Override
    public void createAccountOrNot(boolean seccus) {
        guiLogin.confirSucess(seccus, login, pwd);
    }
}

/**
 * Groups the client ID, the account and the stub of a client/player
 * Also keeps track of the gameLobby in which the client plays
 * @author Johan & Luther
 */
public class ClientSession {

    //=================
    //=== VARIABLES ===
    //=================

    // Connection failure vars
    private static final int MAX_CONNECTION_FAILURES = 4;
    private int connectionFailures;

    // Client vars
    private IClient client;
    private long clientID;
    private Account account;
    private GameLobby gameLobby;

    //===================
    //=== CONSTRUCTOR ===
    //===================

    /**
     * Creates a new client gameLobby
     * @param client The
     * @param clientID
     * @param account
     */
    public ClientSession(IClient client, long clientID, Account account){
        setClient(client);
        setClientID(clientID);
        setAccount(account);
        this.account.setOnline(true);
        resetConnectionFailures();
    }

    //============================
    //=== CONNECTION FUNCTIONS ===
    //============================

    /**
     * Increments the connection failure count by 1
     */
    public void incrementConnectionFailures(){
        this.connectionFailures ++;
    }

    /**
     * Resets the connection failure count to 0
     */
    public void resetConnectionFailures(){
        this.connectionFailures = 0;
    }

    /**
     * Returns whether there were more connection failures that allowed
     * @return Whether there were more connection failures that allowed
     */
    public boolean isDeclaredDead(){
        return this.connectionFailures >= MAX_CONNECTION_FAILURES;
    }

    //============================
    //=== GAME LOBBY FUNCTIONS ===
    //============================

    /**
     * Leaves the current game lobby
     */
    public void leaveGameLobby(){
        if(this.gameLobby != null){
            System.out.println(account.getLogin() + " leaves gameLobby " + gameLobby.getLobbyID());
            this.gameLobby.leave(clientID);
            this.gameLobby = null;
        }
    }

    //=========================
    //=== GETTERS & SETTERS ===
    //=========================

    /**
     * Returns the client
     * @return The client
     */
    public IClient getClient() {
        return client;
    }

    /**
     * Sets the client
     * @param client The new client
     */
    private void setClient(IClient client) {
        this.client = client;
    }

    /**
     * Returns the client ID
     * @return The client ID
     */
    public long getClientID() {
        return clientID;
    }

    /**
     * Sets the client ID
     * @param clientID The new client ID
     */
    private void setClientID(long clientID) {
        this.clientID = clientID;
    }

    /**
     * Returns the client's account
     * @return The client's account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the client's account
     * @param account The new client's account
     */
    private void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Returns the current game lobby in which the player is
     * @return The current game lobby in which the player is
     */
    public GameLobby getGameLobby() {
        return gameLobby;
    }

    /**
     * Sets the game lobby in which the player will be
     * @param gameLobby The new game lobby in which the player will be
     */
    public void setGameLobby(GameLobby gameLobby) {
        this.gameLobby = gameLobby;
    }
}

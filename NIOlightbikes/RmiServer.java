import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class implements the remote interface
 * @author Johan & Luther
 */
public class RmiServer extends UnicastRemoteObject implements IServer, ILobbyManager {

    //=================
    //=== VARIABLES ===
    //=================

    // Hello service timer interval
    private static final int HELLO_INTERVAL = 3000;
    // Possible move directions
    private static final List possibleDirections = Arrays.asList('L', 'R', 'U', 'D');

    // Data lists
    private HashMap<String, Account> accountList;
    private HashMap<Long, ClientSession> clientList;
    private List<GameLobby> gameLobbyList;

    //===================
    //=== CONSTRUCTOR ===
    //===================

    /**
     * Creates a new RMI server
     * @throws RemoteException
     */
    public RmiServer() throws RemoteException {
        super();
        // Initialization
        accountList = new HashMap<>();
        clientList = new HashMap<>();
        gameLobbyList = new ArrayList<>();
        // Launches the hello service
        helloService();
    }

    //========================
    //=== SERVER FUNCTIONS ===
    //========================

    /**
     * Checks every interval if the logged in users are still connected
     */
    private void helloService(){
        // Determine what the timer has to do
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // Make a copy of the client list to avoid concurrency problems
                List<ClientSession> clientSessions = new ArrayList<>(clientList.values());
                // For each logged in client, say hello
                for (ClientSession clientSession : clientSessions) {
                    try {
                        // Say hello
                        clientSession.getClient().hello();
                        // If that worked, the user is still there
                        clientSession.resetConnectionFailures();
                    } catch (RemoteException e) {
                        // If not, report the incident
                        System.out.println(clientSession.getAccount().getLogin() + " is unreachable");
                        clientSession.incrementConnectionFailures();
                        // If the user hasn't responded a few times in a row, he's probably dead
                        if(clientSession.isDeclaredDead()){
                            // Remove the user from the client list
                            System.out.println(clientSession.getAccount().getLogin() + " is declared dead");
                            removeClient(clientSession.getClientID());
                        }
                    }
                }
            }
        };
        // Running timer task as daemon thread
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, HELLO_INTERVAL);
    }

    /**
     * Removes a client from the list
     * @param clientID The client ID of the client to be removed
     */
    private synchronized void removeClient(long clientID){
        // If this client exists
        if (clientList.containsKey(clientID)) {
            ClientSession client = clientList.get(clientID);
            // Indicate he isn't online anymore
            client.getAccount().setOnline(false);
            // Remove him from any ongoing game
            client.leaveGameLobby();
            System.out.println(client.getAccount().getLogin() + " logged out");
            // Remove him from the client list
            clientList.remove(clientID);
        } else {
            System.out.println("unregister: client wasn't registered");
        }
    }

    /**
     * Returns a random long between LONG_MIN and LONG_MAX
     * @return A random long between LONG_MIN and LONG_MAX
     */
    private long getRandomID(){
        return new Random().nextLong();
    }

    //============================
    //=== GAME LOBBY FUNCTIONS ===
    //============================

    /**
     * Tells the concerned clients to update their lobby player list
     * @param clientIDs The list of clients to be updated
     */
    @Override
    public void updateLobbyPlayerList(Collection<Long> clientIDs) {
        // Turn id list to a login list
        List<String> playerLogins = new ArrayList<>(4);
        playerLogins.addAll(clientIDs.stream()
                .filter(clientID -> clientList.containsKey(clientID))
                .map(clientID -> clientList.get(clientID).getAccount().getLogin())
                .collect(Collectors.toList()));

        // Send the login list to the users
        clientIDs.stream().filter(clientID -> clientList.containsKey(clientID)).forEach(clientID -> {
            ClientSession clientSession = clientList.get(clientID);
            try {
                clientSession.getClient().updateLobbyPlayerList(playerLogins);
            } catch (RemoteException e) {
                System.out.println(clientSession.getAccount().getLogin() + " is unreachable");
            }
        });
    }

    /**
     * Tells the concerned clients to update their lobby countdown timer
     * @param clientIDs The list of clients to be updated
     * @param countDown The time left before the game starts
     */
    @Override
    public void updateLobbyTimer(Collection<Long> clientIDs, int countDown) {
        // Send the current countdown to each users
        List<Long> clientIDList = new ArrayList<>(clientIDs);
        clientIDList.stream().filter(clientID -> clientList.containsKey(clientID)).forEach(clientID -> {
            ClientSession clientSession = clientList.get(clientID);
            try {
                clientSession.getClient().updateLobbyTimer(countDown);
            } catch (RemoteException e) {
                System.out.println(clientSession.getAccount().getLogin() + " is unreachable");
            }
        });
    }

    /**
     * Tells a client to update their game screen
     * @param clientID The client ID of the client that has to do the update
     * @param score The current score of the player
     * @param grid The current game grid
     * @param isGameOver Whether the game is over or not
     * @param winnerName The winner of the game
     */
    @Override
    public void updatePlayer(long clientID, int score, int[][] grid, boolean isGameOver, String winnerName) {
        // If the client is in the list
        if(clientList.containsKey(clientID)){
            ClientSession clientSession = clientList.get(clientID);
            try{
                // Send the update
                clientSession.getClient().updateGame(score, grid, isGameOver, winnerName);
            }
            catch (Exception ex){
                System.out.println(clientSession.getAccount().getLogin() + " is unreachable");
            }
        }
    }

    //======================
    //=== STUB FUNCTIONS ===
    //======================

    /**
     * Attempts to create an account
     * @param login The login of the new account
     * @param password The password of the new account
     * @return Whether or not the chosen login was already taken
     */
    @Override
    public synchronized void createAccount(IClient client, String login, String password){
        // Trim the parameters just in case
        String loginTrim = login.trim();
        String passTrim = password.trim();
        // Check if the login is already in use
        boolean loginTaken = accountList.containsKey(loginTrim);
        // If not
        if(!loginTaken){
            // Add the new account to the list
            accountList.put(loginTrim, new Account(loginTrim, passTrim));
        }
        client.createAccountOrNot (!loginTaken);

    }

    /**
     * Attempts to log in a user
     * @param client The client trying to log in
     * @param login The given login
     * @param password The given password
     * @return Whether or not the user could successfully log in
     * @throws RemoteException
     * @throws AlreadyLoggedInException
     */
    @Override
    public synchronized Long logIn(IClient client, String login, String password)
            throws RemoteException, AlreadyLoggedInException {
        // Trim the strings just to be sure
        String loginTrim = login.trim();
        String passTrim = password.trim();
        // Check if the login and password match an account
        Long id = null;
        if (accountList.containsKey(loginTrim) && accountList.get(loginTrim).getPassword().equals(passTrim)) {
            // If so, check if the user is not already logged in with another client
            if(accountList.get(loginTrim).isOnline()){
                // Get the logged in user
                Optional<ClientSession> loggedUser =
                        clientList.values().stream().findAny()
                                .filter(cs -> cs.getAccount().getLogin().equals(loginTrim));
                if(loggedUser.isPresent()){
                    ClientSession clientSession = loggedUser.get();
                    try{
                        // Check if the other client is still responding
                        clientSession.getClient().hello();
                        // If so, let the user know that someone is already connected through another client
                        throw new AlreadyLoggedInException(clientSession.getAccount().getLogin());
                    }
                    catch (RemoteException e) {
                        // If not, log out the unresponding client
                        logOut(clientSession.getClientID());
                    }
                }
            }

            // Get a random ID for the logged in client
            id = getRandomID();
            // Check if generated ID is already in use by chance
            while(clientList.containsKey(id)){
                id = getRandomID();
            }
            // Add the client to the client list, log in was successfull
            clientList.put(id, new ClientSession(client, id, accountList.get(login)));
            System.out.println(login + " logged in");
        }
        return id;
    }

    /**
     * Logs out a client form the server
     * @param clientID The client to log out
     * @throws RemoteException
     */
    @Override
    public synchronized void logOut(long clientID) throws RemoteException{
        removeClient(clientID);
    }

    /**
     * Lets a client join a game lobby
     * @param clientID The client that wants to join a lobby
     * @throws RemoteException
     */
    @Override
    public synchronized void joinGame(long clientID) throws RemoteException {
        GameLobby joinedGameLobby = null;
        // If the client is in the list
        if (clientList.containsKey(clientID)) {
            ClientSession client = clientList.get(clientID);
            // Leave your current lobby
            client.leaveGameLobby();

            // Look for a lobby you can join
            for (GameLobby gameLobby : gameLobbyList) {
                if (gameLobby.join(clientID)) {
                    joinedGameLobby = gameLobby;
                    System.out.println(client.getAccount().getLogin() + " joined gameLobby " + gameLobby.getLobbyID());
                    client.setGameLobby(joinedGameLobby);
                    break;
                }
                System.out.println(client.getAccount().getLogin() + " can't join gameLobby " + gameLobby.getLobbyID());
            }

            // If none were found, create one
            if (joinedGameLobby == null) {
                joinedGameLobby = new GameLobby(this, getRandomID());
                joinedGameLobby.join(clientID);
                client.setGameLobby(joinedGameLobby);
                gameLobbyList.add(joinedGameLobby);
                System.out.println(client.getAccount().getLogin() + " joined session " + joinedGameLobby.getLobbyID());
            }
        }
    }

    /**
     * Lets a client leave his current lobby
     * @param clientID The client that wants to leave his current lobby
     * @throws RemoteException
     */
    @Override
    public void quitGame(long clientID) throws RemoteException {
        if(clientList.containsKey(clientID)){
            clientList.get(clientID).leaveGameLobby();
        }
    }

    /**
     * Changes the moving direction of a player
     * @param clientID The client ID of the player
     * @param newDirection The new moving direction
     * @throws RemoteException
     */
    @Override
    public void setCurrentDirection(long clientID, char newDirection) throws RemoteException {
        // Checks if client exists and if the direction char is one of the 4 possible directions
        if(clientList.containsKey(clientID) && possibleDirections.contains(newDirection)){
            // Get the lobby of the player
            GameLobby gameLobby = clientList.get(clientID).getGameLobby();
            // Change his moving direction
            gameLobby.setCurrentDirection(clientID, newDirection);
        }
    }

    /**
     * Returns the current moving direction of a player
     * @param clientID The client ID of the player
     * @return The current moving direction of a player
     * @throws RemoteException
     */
    @Override
    public char getCurrentDirection(long clientID) throws RemoteException {
        char currentDirection = ' ';
        // Check if the client exists
        if(clientList.containsKey(clientID)){
            // Get his lobby
            GameLobby gameLobby = clientList.get(clientID).getGameLobby();
            // Get his direction
            currentDirection = gameLobby.getCurrentDirection(clientID);
        }
        return currentDirection;
    }

    /**
     * Returns whether a given player is still alive in his current game
     * @param clientID The client ID of the player
     * @return Whether a given player is still alive in his current game
     * @throws RemoteException
     */
    @Override
    public boolean isPlayerAlive(long clientID) throws RemoteException {
        boolean playerAlive = false;
        // Check if the client exists
        if(clientList.containsKey(clientID)){
            // Get the lobby
            GameLobby gameLobby = clientList.get(clientID).getGameLobby();
            // Check if the player is still alive
            playerAlive = gameLobby != null && gameLobby.isPlayerAlive(clientID);
        }
        return playerAlive;
    }

}

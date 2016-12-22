import java.rmi.RemoteException;
import java.util.Collection;

/**
 * The client interface (stub)
 * @author Johan & Luther
 */
public interface IClient extends java.rmi.Remote{
    void hello() throws RemoteException;

    void updateGame(int score, int[][] grid, boolean isGameOver, String winnerName) throws RemoteException;

    void updateLobbyPlayerList(Collection<String> playerList) throws RemoteException;

    void updateLobbyTimer(int countDown) throws RemoteException;

    void joinGame(boolean isSolo) throws RemoteException;

    void quitGame() throws RemoteException;

    void quitApp() throws RemoteException;

    void setCurrentDirection(char currentDirection) throws RemoteException;

    boolean logIn(String login, String pwd) throws RemoteException, AlreadyLoggedInException;

    char getCurrentDirection() throws RemoteException;

    boolean isPlayerAlive() throws RemoteException;

    void createAccount(String login, String pwd) throws RemoteException;

    void createAccountOrNot(boolean seccus);
}

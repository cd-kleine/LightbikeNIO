import java.rmi.*;
import java.util.List;

/**
 * The server interface (stub)
 * @author Johan & Luther
 */

public interface IServer extends Remote {

    void createAccount(IClient client, String login, String password) throws RemoteException;

    Long logIn(IClient client, String login, String password) throws RemoteException, AlreadyLoggedInException;

    void logOut(long clientID) throws RemoteException;

    void joinGame(long clientID) throws RemoteException;

    void quitGame(long clientID) throws RemoteException;

    void setCurrentDirection(long clientID, char currentDirection) throws RemoteException;

    char getCurrentDirection(long clientID) throws RemoteException;

    boolean isPlayerAlive(long clientID) throws RemoteException;
}

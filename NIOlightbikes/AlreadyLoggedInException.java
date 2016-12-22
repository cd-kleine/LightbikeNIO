/**
 * An exception that should be thrown when a user that is already logged in, tries to log in through another client
 * @author Johan & Luther
 */
public class AlreadyLoggedInException extends Exception {
    public AlreadyLoggedInException(String login){
        super(login + " is already logged in with another client!");
    }
}

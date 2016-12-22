/**
 * Contains the login and the password of a player
 * Keeps track of whether a player is online or not
 * @author Johan & Luther
 */
public class Account {

    //=================
    //=== VARIABLES ===
    //=================

    private String login;
    private String password;
    private boolean online;

    //===================
    //=== CONSTRUCTOR ===
    //===================

    /**
     * Creates a new account, offline by default
     * @param login The login of the new account
     * @param password The password of the new account
     */
    public Account(String login, String password){
        setLogin(login);
        setPassword(password);
        setOnline(false);
    }

    //=========================
    //=== GETTERS & SETTERS ===
    //=========================

    /**
     * Returns the login
     * @return The login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Sets the login
     * @param login The new login
     */
    private void setLogin(String login) {
        this.login = login;
    }

    /**
     * Returns the password
     * @return The password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password
     * @param password The new password
     */
    private void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns whether the player is online
     * @return Whether the player is online
     */
    public boolean isOnline() {
        return online;
    }

    /**
     * Sets the player online or offline
     * @param online True for online, false for offline
     */
    public void setOnline(boolean online) {
        this.online = online;
    }
}

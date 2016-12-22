import javax.swing.*;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Login frame
 * @author Luther & Johan
 */
public class GuiLogin extends javax.swing.JFrame {

    //===============================
    //=== ERROR MESSAGES & TITLES ===
    //===============================

    private static final String LOGIN_IN_USE_MESSAGE =
            "The given login is already in use. Try again with another login.";
    private static final String LOGIN_IN_USE_TITLE = "Login already in use";

    private static final String LOGIN_ERROR_MESSAGE = "Failed to login with the given credentials.";
    private static final String LOGIN_ERROR_TITLE = "Login error";

    private static final String SERVER_NOT_FOUND_MESSAGE = "Failed to connect to the server.";
    private static final String SERVER_NOT_FOUND_TITLE = "Server connection error";

    private static final String ALREADY_LOGGED_IN_MESSAGE = "Someone is already logged in with that account.";
    private static final String ALREADY_LOGGED_IN_TITLE = "Already logged in";

    private static final String EMPTY_FIELDS_MESSAGE = "Please fill in all the fields.";
    private static final String EMPTY_FIELDS_TITLE = "Empty fields";

    //=======================
    //=== OTHER VARIABLES ===
    //=======================

    private IClient client;

    //===================
    //=== CONSTRUCTOR ===
    //===================

    /**
     * Creates a new Login form
     */
    public GuiLogin(IClient client) {
        this.client = client;
        initComponents();
    }

    //======================
    //=== FORM FUNCTIONS ===
    //======================

    /**
     * Tries to sign in a user using the given credentials
     */
    private void signIn() {
        // Get the user input
        String login = jTextFieldLogin.getText();
        String pwd = Arrays.toString(jPasswordField1.getPassword());
        // Check whether the fields are filled
        if (isFormFilled(login, pwd)) {
            // Try to create an account and log in afterwards
            try {
                client.createAccount(login, pwd);
            } catch (RemoteException e) {
                showServerNotFoundDialog();
                System.out.println("Exception at Client login: " + e.getMessage());

            }
        }
    }

    public void confirSucess(boolean sucess,String login,String pwd) {
        if (!sucess) {
            showLoginInUseDialog();
        } else try {
            if (!client.logIn(login, pwd)) {
                showLoginFailedDialog();
            }
        } catch (RemoteException ex) {
                showServerNotFoundDialog();
                System.out.println("Exception at Client login: " + ex.getMessage());
        } catch (AlreadyLoggedInException ex) {
                showAlreadyLoggedInDialog();
                System.out.println("Exception at Client login: " + ex.getMessage());        }
    }

    /**
     * Tries to log in a user using the given credentials
     */
    private void logIn(){
        // Get the user input
        String login = jTextFieldLogin.getText();
        String pwd = Arrays.toString(jPasswordField1.getPassword());
        // Check whether the fields are filled
        if(isFormFilled(login, pwd)){
            // Try to log in
            try {
                if(!client.logIn(login,pwd)){
                    showLoginFailedDialog();
                }
            } catch (RemoteException e) {
                showServerNotFoundDialog();
                System.out.println("Exception at Client login: " + e.getMessage());
            } catch (AlreadyLoggedInException e) {
                showAlreadyLoggedInDialog();
                System.out.println("Exception at Client login: " + e.getMessage());
            }
        }

    }

    /**
     * Checks whether one of the given strings is empty
     * @param login The login input
     * @param pwd The password input
     * @return whether one of the given strings is empty, true if both are filled
     */
    private boolean isFormFilled(String login, String pwd){
        // Trim to take out spaces
        boolean empty = login.trim().equals("") || pwd.trim().equals("");
        if(empty) {
            showEmptyFieldsDialog();
        }
        return !empty;
    }

    //=====================
    //=== ERROR DIALOGS ===
    //=====================

    /**
     * Shows the 'login already in use' error dialog
     */
    private void showLoginInUseDialog(){
        showErrorDialog(LOGIN_IN_USE_MESSAGE, LOGIN_IN_USE_TITLE);
    }

    /**
     * Shows the 'login failed' error dialog
     */
    private void showLoginFailedDialog(){
        showErrorDialog(LOGIN_ERROR_MESSAGE, LOGIN_ERROR_TITLE);
    }

    /**
     * Shows the 'server not found' error dialog
     */
    private void showServerNotFoundDialog(){
        showErrorDialog(SERVER_NOT_FOUND_MESSAGE, SERVER_NOT_FOUND_TITLE);
    }

    /**
     * Shows the 'already loggin in' error dialog
     */
    private void showAlreadyLoggedInDialog(){
        showErrorDialog(ALREADY_LOGGED_IN_MESSAGE, ALREADY_LOGGED_IN_TITLE);
    }

    /**
     * Shows the 'empty fields' dialog
     */
    private void showEmptyFieldsDialog(){
        showErrorDialog(EMPTY_FIELDS_MESSAGE, EMPTY_FIELDS_TITLE);
    }

    /**
     * Shows an error dialog
     * @param message The message of the dialog
     * @param title The title of the dialog
     */
    private void showErrorDialog(String message, String title){
        JOptionPane.showMessageDialog(this,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }

    //=================
    //=== EVENTS ===
    //=================

    /**
     * Tries to sign in the user when pressing the sign in button
     * @param evt
     */
    private void ButSignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButSignActionPerformed
        signIn();
    }//GEN-LAST:event_ButSignActionPerformed

    /**
     * Tries to log in the user when pressing the log in button
     * @param evt
     */
    private void ButLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButLogActionPerformed
        logIn();
    }//GEN-LAST:event_ButLogActionPerformed

    //=================
    //=== GENERATED ===
    //=================

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldLogin = new javax.swing.JTextField();
        ButLog = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        ButSign = new javax.swing.JButton();
        jPasswordField1 = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        jLabel1.setText("Lightbikes");

        jLabel2.setText("Login");

        ButLog.setText("Log in");
        ButLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButLogActionPerformed(evt);
            }
        });

        jLabel3.setText("Password");

        ButSign.setText("Sign in");
        ButSign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButSignActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(138, 138, 138)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSeparator1)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3))
                                .addGap(37, 37, 37)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldLogin, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                                    .addComponent(jPasswordField1)))
                            .addComponent(jSeparator2))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(144, Short.MAX_VALUE)
                .addComponent(ButSign)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ButLog)
                .addGap(87, 87, 87))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ButLog)
                    .addComponent(ButSign))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButLog;
    private javax.swing.JButton ButSign;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTextFieldLogin;
    // End of variables declaration//GEN-END:variables
}

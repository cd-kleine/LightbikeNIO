
import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The game lobby frame
 * @author Luther & Johan
 */
public class GuiMultiplayer extends javax.swing.JFrame {

    //=================
    //=== VARIABLES ===
    //=================

    private final IClient client;
    private final GUI guiParent;
    private int playerNameListSize = 1;
    private List<String> playerNameList;

    //===============================
    //=== ERROR MESSAGES & TITLES ===
    //===============================

    private static final String SERVER_NOT_FOUND_MESSAGE = "Failed to connect to the server.";
    private static final String SERVER_NOT_FOUND_TITLE = "Server connection error";

    private static final String START_GAME_DELAYED_MESSAGE = "Game start delayed, waiting for other players ...";
    private static final String START_GAME_DELAYED_TITLE = "Game start delayed";

    //===================
    //=== CONSTRUCTOR ===
    //===================

    /**
     * Creates a new game lobby form
     */
    public GuiMultiplayer(IClient client, GUI guiParent) {
        this.client = client;
        this.guiParent = guiParent;
        this.playerNameList = new ArrayList<>();

        initComponents();

        // Setup frame behaviour
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setAlwaysOnTop(true);
    }

    //============================
    //=== GAME LOBBY FUNCTIONS ===
    //============================

    /**
     * Close the game lobby frame
     */
    public void close(){
        guiParent.setEnabled(true);
        guiParent.setVisible(true);
        this.setVisible(false);
    }

    /**
     * Leave the current game lobby
     */
    private void leaveGameLobby(){
        close();
        try {
            client.quitGame();
        } catch (RemoteException e) {
            showServerNotFoundErrorDialog();
            System.out.println("Exception in Client lobby " + e.getMessage());
        }
    }

    /**
     * Update the player list
     * @param playerNames The new player list
     */
    public void updatePlayerList(String[] playerNames){
        this.playerNameList = new ArrayList<String>(Arrays.asList(playerNames));
        playerNameListSize = playerNames.length;
        jList1.setListData(playerNames);
        jScrollPaneList.updateUI();
    }

    /**
     * Update the countdown timer
     * @param countDown The number of seconds left before the game starts
     */
    public void updateTimer(int countDown){
        // Close the lobby screen if the timer gets to 0
        if(countDown <= 0){
            if(playerNameListSize > 1){
                switch(playerNameList.indexOf(guiParent.ShowLoginGUI.getText())){
                    case 0:
                        guiParent.ShowLoginGUI.setBackground(Color.RED);
                        break;
                    case 1:
                        guiParent.ShowLoginGUI.setBackground(Color.BLUE);
                        break;
                    case 2:
                        guiParent.ShowLoginGUI.setBackground(Color.YELLOW);
                        break;
                    case 3:
                        guiParent.ShowLoginGUI.setBackground(Color.GREEN);
                        break;
                }
                close();
            }
            else if(playerNameListSize == 1){
                //TODO show text JLabel start game delayed
            }
        }
        Timer.setText(countDown + " seconds");
    }

    //=====================
    //=== ERROR DIALOGS ===
    //=====================

    /**
     * Shows the 'start game delayed' dialog
     */
    private void showStartGameDelayedDialog(){
        JOptionPane.showMessageDialog(this,
                START_GAME_DELAYED_MESSAGE,
                START_GAME_DELAYED_TITLE,
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows the 'server not found' error dialog
     */
    private void showServerNotFoundErrorDialog(){
        JOptionPane.showMessageDialog(this,
                SERVER_NOT_FOUND_MESSAGE,
                SERVER_NOT_FOUND_TITLE,
                JOptionPane.ERROR_MESSAGE);
    }

    //==============
    //=== EVENTS ===
    //==============

    /**
     * When we press the red X to close the window, leave the game lobby
     * @param evt
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        leaveGameLobby();
    }//GEN-LAST:event_formWindowClosing

    /**
     * When the home button is pressed, leave the game lobby
     * @param evt
     */
    private void ButHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButHomeActionPerformed
        leaveGameLobby();
    }//GEN-LAST:event_ButHomeActionPerformed

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
        Timer = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPaneList = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        ButHome = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        jLabel1.setText("Mutiplayer");

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel2.setText("The game start in ");

        Timer.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        Timer.setForeground(new java.awt.Color(255, 0, 51));
        Timer.setText("...");

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setEnabled(false);
        jScrollPaneList.setViewportView(jList1);

        ButHome.setText("Home");
        ButHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButHomeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(139, 139, 139)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jSeparator1)
                                .addComponent(jSeparator2)
                                .addComponent(jScrollPaneList, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(121, 121, 121)
                                .addComponent(Timer))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(160, 160, 160)
                        .addComponent(ButHome)))
                .addContainerGap(58, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(Timer))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPaneList, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(ButHome)
                .addGap(18, 18, 18))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButHome;
    private javax.swing.JLabel Timer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPaneList;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    // End of variables declaration//GEN-END:variables
}

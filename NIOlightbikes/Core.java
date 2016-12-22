import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is the core class. This class is responsible for keeping the state of the game as well
 * as asking each AI for its input.
 * @author Sam, Johan & Luther
 */
public class Core {

    //=================
    //=== VARIABLES ===
    //=================

    private static final int AVAILABLE_SPOTS = 4;
    private List<Player> players;

    private int runTime = 0; //Number of "game ticks"
    private int gameRunTime = 0; //Unused. Used to make a sync every X game ticks
    private int gameMaxRunTime = 1;	//Ibid.
    private String sWinnerName = "NOBODY"; //Current winner of the game
    private boolean bGameQuit = false;

    private int[][] iGrid = new int[100][100]; //Inner grid representation (0 = empty, any = player id)
    private int[][] iTimer = new int[100][100]; //Memorizes the "freshness" of paths, for AI to use.

    //===================
    //=== CONSTRUCTOR ===
    //===================

    /**
     * Creates a new game core
     * @param humanPlayers The human players that will participate in this game
     */
    public Core(Collection<HumanPlayer> humanPlayers){
        // Get the human players
        players = new ArrayList<>(humanPlayers);

        // Fill the remaining spots with ai
        int emptySpots = AVAILABLE_SPOTS - players.size();
        for(int i=emptySpots; i > 0; i--){
            IAI ai;
            switch (i){
                case 1:
                    ai = new HardAI();
                    break;
                case 2:
                    ai = new MediumAI();
                    break;
                default:
                    ai = new EasyAI();
                    break;
            }
            players.add(new AIPlayer(ai));
        }

        // Reset everything
        newGrid();

        //Initialization
        for(Player player : players){
            player.setAlive(true);
            if(player instanceof HumanPlayer){
                ((HumanPlayer) player).setScore(0);
            }
        }

        bGameQuit = false;
    }

    //======================
    //=== GAME FUNCTIONS ===
    //======================

    /**
     * Starts the game
     */
    public void runGame()
    {
        // Till the game is over
        while(!bGameQuit)
        {
            try
            {
                //Increase a game tick (one tick = 50ms; game plays at about 20fps)
                Thread.sleep(50);
                runTime++;
                gameRunTime++;
                
                //If any player is still "alive"
                if(gameRunTime%gameMaxRunTime == 0 && isGameInProgress())
                {
                	  //unused, don't bother
                    gameMaxRunTime = 1;
                    if(gameMaxRunTime < 1)
                        gameMaxRunTime = 1;
                        
                    //Update the freshness of paths (0 = quite old path)
                    for(int i = 0; i < 100; i++)
                    {
                        for(int j = 0; j < 100; j++)
                        {
                            if(iTimer[i][j] > 0)
                                iTimer[i][j]--;
                        }
                    }

                    Player winner = null;
                    int survivors = 0;

                    //Update position of each player
                    for(int i = 0; i < AVAILABLE_SPOTS; i++)
                    {
                        Player player = players.get(i);
                        int x = player.getIxCarPos();
                        int y = player.getIyCarPos();

                        //If this AI is still in play
                        if(player instanceof AIPlayer && player.isAlive())
                        {
                            //Request a decision from the AI
                            IAI ai = ((AIPlayer) player).getAi();
                            player.setcCarDir(ai.getNewDir(player.getcCarDir(),x, y, iGrid, iTimer));
                        }
                        
                        //If the player is still in play
                        if(player.isAlive())
                        {
                            //Update its position based on the previous position and the current direction
                            //If we hit the wall or the path of any player, it's game over
                            switch(player.getcCarDir())
                            {
                                case 'L' :
                                    if(x > 0 && iGrid[x-1][y] == 0) {
                                        player.decrementIxCarPos();
                                    }
                                    else {
                                        player.setAlive(false);
                                    }
                                    break;
                                case 'R' :
                                    if(x < 99 && iGrid[x+1][y] == 0) {
                                        player.incrementIxCarPos();
                                    }
                                    else {
                                        player.setAlive(false);
                                    }
                                    break;
                                case 'U' :
                                    if(y > 0 && iGrid[x][y-1] == 0) {
                                        player.decrementIyCarPos();
                                    }
                                    else {
                                        player.setAlive(false);
                                    }
                                    break;
                                case 'D' :
                                    if(y < 99 && iGrid[x][y+1] == 0) {
                                        player.incrementIyCarPos();
                                    }
                                    else {
                                        player.setAlive(false);
                                    }
                                    break;
                            }

                            // Apply changes
                            x = player.getIxCarPos();
                            y = player.getIyCarPos();
                            
                            //This particular tile is now no longer available
                            iGrid[x][y] = (i+1);
                            
                            //This tile gets a freshness of 10
                            iTimer[x][y] = 10;
                        }

                        // Check who is still alive
                        if(player.isAlive()){
                            winner = player;
                            survivors++;
                        }
                    }

                    //If "highlander" (there's only one left), this one wins, and the current game ends.
                    if(survivors == 1){
                        sWinnerName = winner.getsName();
                        winner.setAlive(false);
                        bGameQuit = true;
                    }
                    else if(survivors == 0){
                        bGameQuit = true;
                    }

                }
                
                //Update the score of the player if it's been 1 second (20 fps) since the last updateGame
                if(runTime == 20)
                {
                    runTime = 0;
                    for(Player player : players){
                        if(player instanceof HumanPlayer && player.isAlive()){
                            ((HumanPlayer) player).incrementScore();
                        }
                    }
                }

            }
            catch(Exception e) {
                System.out.println("Exception in Core " + e.getMessage());
            }
        }
    }

    /**
     * Sets the flag to end the game
     */
    public void endGame(){
        bGameQuit = true;
    }

    /**
     * Returns whether the game is still in progress
     * @return Whether the game is still in progress
     */
    public boolean isGameInProgress(){
        return !bGameQuit && (players.get(0).isAlive() || players.get(1).isAlive() ||
                players.get(2).isAlive() || players.get(3).isAlive());
    }

    /**
     * Resets everything
     */
    public void newGrid()
    {
        gameRunTime = 0;
        gameMaxRunTime = 1;
        for(int i = 0; i < 100; i++)
        {
            for (int j = 0; j < 100; j++)
            {
                iGrid[i][j] = 0;
                iTimer[i][j] = 0;
            }
        }

        //Every player starts in the middle of a border segment
        Player playerBottom = players.get(0);
        playerBottom.setIxCarPos(50);
        playerBottom.setIyCarPos(99);
        playerBottom.setcCarDir('U');
        playerBottom.setsName("RED");

        Player playerLeft = players.get(1);
        playerLeft.setIxCarPos(0);
        playerLeft.setIyCarPos(50);
        playerLeft.setcCarDir('R');
        playerLeft.setsName("BLUE");

        Player playerTop = players.get(2);
        playerTop.setIxCarPos(50);
        playerTop.setIyCarPos(0);
        playerTop.setcCarDir('D');
        playerTop.setsName("YELLOW");

        Player playerRight = players.get(3);
        playerRight.setIxCarPos(99);
        playerRight.setIyCarPos(50);
        playerRight.setcCarDir('L');
        playerRight.setsName("GREEN");

        for(int i = 0; i < 4; i++)
        {
            Player player = players.get(i);
            int x = player.getIxCarPos();
            int y = player.getIyCarPos();
            iGrid[x][y] = (i+1);
            iTimer[x][y] = 10;
        }
    }

    //===============
    //=== GETTERS ===
    //===============

    /**
     * Returns the name of the winner
     * @return The name of the winner
     */
    public String getsWinnerName(){
        return this.sWinnerName;
    }

    /**
     * Returns the representation of the grid
     * @return The representation of the grid
     */
    public int[][] getGrid()
    {
        return iGrid;
    }

}

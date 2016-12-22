/**
 * A player controlled by an AI
 * @author Johan & Luther
 */
public class AIPlayer extends Player {

    // The AI that controls the player
    private IAI ai;

    /**
     * Creates a new AI player
     * @param ai The AI that will control this player
     */
    public AIPlayer(IAI ai) {
        super();
        setAI(ai);
    }

    /**
     * Returns the AI that controls the player
     * @return The AI that controls the player
     */
    public IAI getAi() {
        return ai;
    }

    /**
     * Sets the AI that will control this player
     * @param ai The AI that will control this player
     */
    private void setAI(IAI ai) {
        this.ai = ai;
    }
}

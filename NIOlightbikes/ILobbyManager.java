import java.util.Collection;

/**
 * The lobby manager interface
 * @author Johan & Luther
 */
public interface ILobbyManager {
    void updateLobbyPlayerList(Collection<Long> playerIDs);
    void updateLobbyTimer(Collection<Long> playerIDs, int countDown);
    void updatePlayer(long playerID, int score, int[][] grid, boolean isGameOver, String winnerName);
}

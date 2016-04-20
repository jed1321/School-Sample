import java.util.*;

/**
 * Interface for any games the solver will solve
 *
 * @author Jeff DeSain
 */
public interface Game
{
    /**
     * Returns a list of all possible configurations given a particular configuration
     * @param n A particular configuration
     * @return An ArrayList containing all possible configurations resulting from a given configuration
     */
    ArrayList<Node> getNeighbors(Node n);

    /**
     * Determines whether a particular configuration is a goal configuration
     *
     * @param n A particular configuration
     * @return //True if the configuration is a goal, false otherwise
     */
    boolean isGoal(Node n);

    /**
     * Updates the game after a move has been made
     *
     * @param n The new configuration after a move has been made
     */
    void update(Node n);

    /**
     * Checks if the current game is over
     *
     * @return True if the game is over, false otherwise
     */
    boolean gameOver();

    /**
     * Getter for the current position
     *
     * @return A node containing the current position
     */
    Node getCurrentPosition();

    /**
     * Sets the score for a node
     * @param n The node
     * @param score 1 if maximizing player, -1 if minimizing player, 0 if tie
     */
    void setScore(Node n, int score);

}

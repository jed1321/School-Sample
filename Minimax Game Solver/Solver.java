import java.util.*;

/**
 * Solver that uses the minimax algorithm to find the best move for a game
 *
 * @author Jeff DeSain
 */
public class Solver
{
    private Game game; //The game being played

    /**
     * @param game The game being played
     */
    public Solver(Game game)
    {
        this.game = game;
    }

    /**
     * Gets the best move for the current game
     *
     * @return A node containing the configuration of the best move
     */
    public Node getMove()
    {
        return miniMax(game.getCurrentPosition(), true);
    }

    /**
     * Recursively searches for a best move
     *
     * @param n The starting node
     * @param max True if the maximizing player, false if the minimizing player
     * @return The node containing the best move according to the minimax algorithm
     */
    private Node miniMax(Node n, boolean max)
    {
        Node bestPosition = null; //The best move
        int bestValue; //The score of the best move
        ArrayList<Node> neighbors; //All possible moves after a given move
        int val; //The score of a particular move
        TreeSet<Integer> visitedConfigs = new TreeSet<Integer>(); //Ensures each configuration is visited at most once

        //Check if the game is over
        if(game.isGoal(n))
        {
            bestPosition = n;
            if(max)
                game.setScore(n , 1);
            else
                game.setScore(n, -1);
        }
        //Check moves if the maximizing player
        else if(max)
        {
            bestValue = Integer.MIN_VALUE;
            neighbors = game.getNeighbors(n);

            if(neighbors.size() == 0)
            {
                bestPosition = n;
                game.setScore(n, 0);
            }
            else {
                for (Node aNode : neighbors) {
                    if (bestValue < 1) //If a winning move is found, stop searching
                    {
                        if (!visitedConfigs.contains(aNode.hashCode())) {
                            visitedConfigs.add(aNode.hashCode());
                            val = miniMax(aNode, false).getScore();
                            if (val > bestValue) {
                                bestValue = val;
                                bestPosition = aNode;
                                bestPosition.setScore(bestValue);
                            }
                        }
                    }
                }
            }
        }
        //Check moves if the minimizing player
        else {
            bestValue = Integer.MAX_VALUE;
            neighbors = game.getNeighbors(n);
            if (neighbors.size() == 0) {
                bestPosition = n;
                game.setScore(n , 0);
            }
            {
                for (Node aNode : neighbors) {
                    if (bestValue > -1) //If a winning move is found, stop searching
                    {
                        if (!visitedConfigs.contains(aNode.hashCode())) {
                            visitedConfigs.add(aNode.hashCode());
                            val = miniMax(aNode, true).getScore();
                            if (val < bestValue) {
                                bestValue = val;
                                bestPosition = aNode;
                                bestPosition.setScore(bestValue);
                            }
                        }
                    }
                }
            }
        }


        return bestPosition;
    }
}

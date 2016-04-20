import java.util.*;

/**
 * Simulates playing the game take away. Players alternate taking 1, 2, or 3 coins. The player to remove the last coin
 * loses
 *
 * @author Jeff DeSain
 */
public class TakeAway implements Game
{
    private int coins; //Number of remaining coins
    private final static String USAGE = "Usage: java TakeAway [play][auto] num_pennies"; //Usage statement

    /**
     * @param coins starting number of coins
     */
    public TakeAway(int coins)
    {
        this.coins = coins;
    }

    /**
     * Returns a list of all possible configurations given a particular configuration
     * @param n A particular configuration
     * @return An ArrayList containing all possible configurations resulting from a given configuration
     */
    public ArrayList<Node> getNeighbors(Node n)
    {
        ArrayList<Node> neighbors = new ArrayList<Node>();
        for(int i = (Integer)n.getData() - 1; i >= 0 && neighbors.size() < 3; i--)
            neighbors.add(new Node<Integer>(i));

        return neighbors;
    }

    /**
     * Used to check if the game is over
     *
     * @param n A configuration
     * @return True if no coins remaining, false otherwise
     */
    public boolean isGoal(Node n)
    {
        return (Integer)n.getData() == 0;
    }

    /**
     * Updates the game configuration after a move is made
     *
     * @param n The new configuration
     */
    public void update(Node n)
    {
        this.coins = (Integer)n.getData();
    }

    /**
     * Checks to see if the game is finished
     *
     * @return True if the game is finished, false otherwise
     */
    public boolean gameOver()
    {
        return isGoal(new Node<Integer>(coins));
    }

    /**
     * Getter for coins
     *
     * @return Current number of remaining coins
     */
    public int getCoins()
    {
        return coins;
    }

    /**
     * Returns the current position
     *
     * @return A node with the current number of remaining coins
     */
    public Node getCurrentPosition()
    {
        return new Node<Integer>(coins);
    }

    public int hashCode()
    {
        return coins;
    }

    /**
     *
     * @param n The node
     * @param score 1 if maximizing player, -1 if minimizing player, 0 if tie
     */
    @Override
    public void setScore(Node n , int score)
    {
        n.setScore(score);
    }


    public static void main(String[] args) {
        Scanner in = new Scanner(System.in); //Reads user inputs
        TakeAway currentGame; //The game being played
        Solver solver; //Game solver
        int coins = 0;
        boolean play = false; //True if the player goes first
        boolean auto = false; //True if the AI plays against AI

        //Verifying program arguments
        if (args.length == 2) {
            if (args[0].equals("play")) {
                play = true;
            } else if (args[0].equals("auto")) {
                auto = true;
            } else {
                System.err.println(USAGE);
                System.exit(-1);
            }
            try {
                coins = Integer.parseInt(args[1]);
                if (coins < 1) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.err.println(USAGE);
                System.exit(-1);
            }
        } else if (args.length == 1) {
            try {
                coins = Integer.parseInt(args[0]);
                if (coins < 1) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.err.println(USAGE);
                System.exit(-1);
            }
        } else {
            System.err.println(USAGE);
            System.exit(-1);
        }


        currentGame = new TakeAway(coins);

        boolean valid; //False until the user inputs a valid command
        solver = new Solver(currentGame);
        int playerMove = 0; //Number of coins the player removes
        int cpuMove = 0; //Number of coins the AI removes
        int autoMove = 0; //Tracks the AI turns for auto mode

        while (!currentGame.gameOver()) {
            System.out.println("Coins left: " + currentGame.getCoins());
            if (!auto) {

                //Gets the player's move, confirms it is valid, and then makes the move
                if (play) {
                    valid = false;
                    while (!valid)
                    {
                        System.out.print("Your move: ");
                        try
                        {
                                playerMove = Integer.parseInt(in.nextLine());

                                if (!(playerMove == 1 || playerMove == 2 || playerMove == 3)) {
                                    System.out.println("Must remove 1, 2, or 3 coins");
                                } else if (playerMove > currentGame.getCoins()) {
                                    System.out.println("Cannot remove more coins than are remaining");
                                } else
                                    valid = true;
                        }
                        catch(NoSuchElementException e)
                        {
                            System.exit(0);
                        }
                        catch (Exception e)
                        {
                            System.out.println("Must remove 1, 2, or 3 coins");
                        }
                    }

                    currentGame.update(new Node<Integer>(currentGame.getCoins() - playerMove));
                }
                //Gets the AI move and then makes it
                else {
                    cpuMove = (Integer) solver.getMove().getData();
                    System.out.println("Computer takes " + (currentGame.getCoins() - cpuMove) + " coins");
                    currentGame.update(new Node<Integer>(cpuMove));
                }
                play = !play; //Take turns between player and AI
            }
            //For auto mode repeatedly get AI move and switch which computer's move it is
            else
            {
                cpuMove = (Integer) solver.getMove().getData();
                System.out.println("Computer " + (autoMove + 1) + " takes " + (currentGame.getCoins() - cpuMove) + " coins");
                currentGame.update(new Node<Integer>(cpuMove));
                autoMove = autoMove ^ 1;
            }
        }

        //Print the results of the game
        if (!auto) {
            if (play)
                System.out.println("You win!");
            else
                System.out.println("You lose!");
        }
        else
            System.out.println("Computer " + (autoMove + 1) + " wins!");


        System.exit(0);

    }

}

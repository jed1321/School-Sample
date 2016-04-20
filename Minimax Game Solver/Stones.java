import java.util.*;


/**
 * Simulates the game stones. Players alternate taking any number of stones from a single pile. The player who takes
 * the last stone loses. Uses the minimax algorithm to generate computer moves.
 *
 * @author Jeff DeSain
 */
public class Stones implements Game
{

    private ArrayList<Integer> stones; //Configuration of stones
    private static final String USAGE = "java Stones [play][auto] pile-1 pile-2 pile-N";


    public Stones(ArrayList<Integer> stones)
    {
        this.stones = new ArrayList<Integer>(stones);
    }

    /**
     * Returns a list of all possible configurations given a particular configuration
     *
     * @param n A particular configuration
     * @return An ArrayList containing all possible configurations resulting from a given configuration
     */
    @Override
    public ArrayList<Node> getNeighbors(Node n)
    {
        ArrayList<Node> neighbors = new ArrayList<Node>();
        ArrayList<Integer> data = (ArrayList<Integer>)n.getData();

        for(int i = 0; i < data.size(); i++)
        {
            int pileSize = data.get(i);

            /*
            for(int j = 1; j <= pileSize; j++)
            {
                ArrayList<Integer> newData = new ArrayList<Integer>(data);
                newData.set(i, newData.get(i) - j);
                neighbors.add(new Node<ArrayList<Integer>>(newData));
            }
            */
            //Taking more stones first reduces the total number of moves, and the subsequent number of neighbors
            if(pileSize > 0) {
                for (int j = 0; j < pileSize; j++) {
                    ArrayList<Integer> newData = new ArrayList<Integer>(data);
                    newData.set(i, j);
                    neighbors.add(new Node<ArrayList<Integer>>(newData));
                }
            }

        }

        return neighbors;
    }

    /**
     * Determines whether a particular configuration is a goal configuration
     *
     * @param n A particular configuration
     * @return True if the configuration is a goal, false otherwise
     */
    @Override
    public boolean isGoal(Node n)
    {
        boolean goal = true;
        ArrayList<Integer> nodeStones = (ArrayList<Integer>)n.getData();

        for(int i = 0; i < nodeStones.size() && goal; i++)
        {
            if(nodeStones.get(i) != 0)
                goal = false;
        }
        return goal;
    }

    /**
     * Updates the game after a move has been made
     *
     * @param n The new configuration after a move has been made
     */
    @Override
    public void update(Node n)
    {
        this.stones = new ArrayList<Integer>((ArrayList<Integer>)n.getData());
    }

    /**
     * Checks if the current game is over
     *
     * @return True if the game is over, false otherwise
     */
    @Override
    public boolean gameOver()
    {
        return isGoal(new Node<ArrayList<Integer>>(new ArrayList<Integer>(stones)));
    }

    /**
     * Getter for the current position
     *
     * @return A node containing the current position
     */
    @Override
    public Node getCurrentPosition()
    {
        return new Node<ArrayList<Integer>>(new ArrayList<Integer>(stones));
    }


    /**
     * Used to determine if a player move is valid
     *
     * @param pile The pile the player is removing stones from
     * @param move The number of stones removed
     * @return True if this is a valid move, false otherwise
     */
    public boolean isValid(int pile, int move)
    {
        boolean valid = true;

        if(pile < 0 || pile > stones.size())
            valid = false;

        if(move < 1 || move > stones.get(pile))
            valid = false;

        return valid;
    }

    /**
     *
     * @return hashcode for this object
     */
    public int hashCode()
    {
        ArrayList<Integer> hashStones = new ArrayList<Integer>(stones);
        Collections.sort(hashStones);
        return hashStones.hashCode();
    }

    /**
     *
     * @return A string representation of this object
     */
    public String toString()
    {
        String answer = "";
        for(int i : stones)
        {
            answer += i + " ";
        }
        return answer;
    }

    /**
     *
     * @param n The node
     * @param score 1 if maximizing player, -1 if minimizing player, 0 if tie
     */
    @Override
    public void setScore(Node n, int score)
    {
        n.setScore(score);
    }


    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in); //Used to read from keyboard
        Stones currentGame; //The current game state
        Solver solver; //Solver object
        ArrayList<Integer> initialPosition = new ArrayList<Integer>(); //Starting position
        boolean play = false;
        boolean auto = false;

        if(args.length < 1)
        {
            System.err.println(USAGE);
            System.exit(-1);
        }

        if(args[0].equals("play"))
        {
            play = true;
            for(int i = 1; i < args.length; i++)
            {
                try
                {
                    initialPosition.add(Integer.parseInt(args[i]));
                }
                catch(Exception e)
                {
                    System.err.println(USAGE);
                    System.exit(-1);
                }
            }
        }
        else if(args[0].equals("auto"))
        {
            auto = true;
            for(int i = 1; i < args.length; i++)
            {
                try
                {
                    initialPosition.add(Integer.parseInt(args[i]));
                }
                catch(Exception e)
                {
                    System.err.println(USAGE);
                    System.exit(-1);
                }
            }
        }
        else
        {
            for(int i = 0; i < args.length; i++)
            {
                try
                {
                    initialPosition.add(Integer.parseInt(args[i]));
                }
                catch(Exception e)
                {
                    System.err.println(USAGE);
                    System.exit(-1);
                }
            }
        }


        currentGame = new Stones(initialPosition);
        solver = new Solver(currentGame);
        boolean valid = false;
        int autoMove = 0;
        int playerMove = 0;
        int playerPile = 0;
        ArrayList<Integer> cpuMove;

        while(!currentGame.gameOver())
        {
            System.out.println(currentGame);
            if(!auto) {
                //Gets the player's move, confirms it is valid, and then makes the move
                if (play) {
                    valid = false;
                    while (!valid) {
                        System.out.print("Your move (pile): ");
                        try {
                            playerPile = Integer.parseInt(in.nextLine());

                            System.out.print("Your move (number of stones): ");
                            playerMove = Integer.parseInt(in.nextLine());

                            if (currentGame.isValid(playerPile, playerMove))
                                valid = true;
                            else
                                System.out.println("Must choose a valid pile and remove at least 1 stone");
                        } catch (NoSuchElementException e) {
                            System.exit(0);
                        } catch (Exception e) {
                            System.out.println("Must choose a valid pile and remove at least 1 stone");
                        }
                    }

                    ArrayList<Integer> newStones = new ArrayList<Integer>((ArrayList<Integer>) currentGame.getCurrentPosition().getData());
                    newStones.set(playerPile, newStones.get(playerPile) - playerMove);

                    currentGame.update(new Node<ArrayList<Integer>>(newStones));
                }
                //Gets the AI move and then makes it
                else {
                    cpuMove = (ArrayList<Integer>) solver.getMove().getData();
                    int cpuPile = 0;
                    int cpuStones = 0;
                    ArrayList<Integer> currentPosition = (ArrayList<Integer>) currentGame.getCurrentPosition().getData();

                    for (int i = 0; i < currentPosition.size(); i++) {
                        if (cpuMove.get(i) != currentPosition.get(i)) {
                            cpuPile = i;
                            cpuStones = currentPosition.get(i) - cpuMove.get(i);
                        }
                    }

                    System.out.println("CPU takes " + cpuStones + " stones from pile " + cpuPile);
                    currentGame.update(new Node<ArrayList<Integer>>(cpuMove));
                }
            }
            //For auto mode repeatedly get AI move and switch which computer's move it is
            else
            {
                cpuMove = (ArrayList<Integer>) solver.getMove().getData();
                int cpuPile = 0;
                int cpuStones = 0;
                ArrayList<Integer> currentPosition = (ArrayList<Integer>) currentGame.getCurrentPosition().getData();

                for (int i = 0; i < currentPosition.size(); i++) {
                    if (cpuMove.get(i) != currentPosition.get(i)) {
                        cpuPile = i;
                        cpuStones = currentPosition.get(i) - cpuMove.get(i);
                    }
                }
                System.out.println("CPU " + (autoMove + 1) + " takes " + cpuStones + " stones from pile " + cpuPile);
                currentGame.update(new Node<ArrayList<Integer>>(cpuMove));
                autoMove = autoMove ^ 1;
            }

            //Alternate turns
            play = !play;
        }

        if(!auto) {
            if (play)
                System.out.println("You win!");
            else
                System.out.println("You lose!");
        }
        else
        {
            System.out.println("Computer " + (autoMove + 1) + " wins!");
        }

    }
}

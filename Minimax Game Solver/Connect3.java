import java.io.*;
import java.util.*;
import java.util.zip.CheckedInputStream;

/**
 * Simulates the game connect 3. Players alternate dropping a piece into the game board. The first to connect three
 * in a row wins.
 *
 * @author Jeff DeSain
 */
public class Connect3 implements Game
{

    private static final String USAGE = "Usage: java Connect3 [play] -"; //Usage statement
    private static final char ONE = 'X'; //Character used for player
    private static final char TWO = 'O'; //Charcter used for AI
    private static final char EMPTY = '.'; //Character used for open space

    private static int playerStart; //Used to determine whose turn it is

    private char[][] board; //The board


    /**
     *
     * @param board The board
     */
    public Connect3(char[][] board)
    {
        this.board = new char[board.length][board[0].length];
        for(int i = 0; i < board.length; i++)
        {
            for(int j = 0; j < board[0].length; j++)
            {
                this.board[i][j] = board[i][j];
            }
        }
    }


    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        Connect3 currentGame;
        Solver solver;
        boolean play = false;
        char[][] initialPosition = new char[0][0];

        //Ensure valid command line arguments
        if(args.length == 1)
        {
            if(args[0].equals("-"))
            {
                initialPosition = readInput();
            }
            else
                initialPosition = readFile(args[0]);
        }
        else if(args.length == 2)
        {
            if(!args[0].equals("play"))
            {
                System.err.println(USAGE);
                System.exit(-1);
            }
            else
            {
                play = true;
                if(args[1].equals("-"))
                {
                    initialPosition = readInput();
                }
                else
                    initialPosition = readFile(args[1]);
            }
        }
        else
        {
            System.err.println(USAGE);
            System.exit(-1);
        }

        currentGame = new Connect3(initialPosition);

        char[][] startBoard = (char[][])currentGame.getCurrentPosition().getData();

        int xCount = 0;
        int oCount = 0;
        //Used to determine whose turn it is
        for(int i = 0; i < startBoard.length; i++)
        {
            for(int j = 0; j < startBoard[0].length; j++)
            {
                if(startBoard[i][j] == 'X')
                    xCount++;

                if(startBoard[i][j] == 'O')
                    oCount++;
            }
        }

        if(xCount > oCount)
            playerStart = -1;
        else if(oCount > xCount)
            playerStart = 1;
        else
        {
            if(play)
                playerStart = 2;
            else
                playerStart = -2;

        }


        solver = new Solver(currentGame);

        boolean nextMoveOnly = !play;
        boolean valid = false;
        int playerMove = 0;
        char[][] cpuMove;

        //If no 'play' argument
        if(nextMoveOnly)
        {
            System.out.println(currentGame);
            cpuMove = (char[][]) solver.getMove().getData();
            int cpuCol = 0;

            char[][] currentPosition = (char[][]) currentGame.getCurrentPosition().getData();

            //Determine where the AI went
            for (int i = 0; i < currentPosition.length; i++) {
                for (int j = 0; j < currentPosition[0].length; j++) {
                    if (cpuMove[i][j] != currentPosition[i][j]) {
                        cpuCol = i;
                    }
                }
            }

            System.out.println("CPU plays in column " + cpuCol);

            char[][] newBoard = new char[cpuMove.length][cpuMove[0].length];

            for (int i = 0; i < newBoard.length; i++)
                System.arraycopy(cpuMove[i], 0, newBoard[i], 0, newBoard[i].length);

            currentGame.update(new Node<char[][]>(newBoard));


            System.out.println(currentGame);
        }
        else
        {
            while (!currentGame.gameOver()) {
                System.out.println(currentGame);
                //Gets the player's move, confirms it is valid, and then makes the move
                if (play) {
                    valid = false;
                    while (!valid) {
                        System.out.print("Your move: ");
                        try {
                            playerMove = Integer.parseInt(in.nextLine());

                            if (currentGame.isValid(playerMove))
                                valid = true;
                            else
                                System.out.println("Must choose a valid pile column");
                        } catch (NoSuchElementException e) {
                            System.exit(0);
                        } catch (Exception e) {
                            System.out.println("Must choose a valid column");
                        }
                    }

                    char[][] currentBoard = (char[][]) currentGame.getCurrentPosition().getData();
                    char[][] newBoard = new char[currentBoard.length][currentBoard[0].length];

                    for (int i = 0; i < newBoard.length; i++)
                        System.arraycopy(currentBoard[i], 0, newBoard[i], 0, newBoard[i].length);

                    int index;
                    for(index = newBoard[0].length - 1; index >= 0 && newBoard[playerMove][index] != EMPTY; index--);

                    newBoard[playerMove][index] = ONE;

                    currentGame.update(new Node<char[][]>(newBoard));
                }
                //Gets the AI move and then makes it
                else {
                    cpuMove = (char[][]) solver.getMove().getData();
                    int cpuCol = 0;

                    char[][] currentPosition = (char[][]) currentGame.getCurrentPosition().getData();

                    for (int i = 0; i < currentPosition.length; i++) {
                        for (int j = 0; j < currentPosition[0].length; j++) {
                            if (cpuMove[i][j] != currentPosition[i][j]) {
                                cpuCol = i;
                            }
                        }
                    }

                    System.out.println("CPU plays in column " + cpuCol);

                    char[][] newBoard = new char[cpuMove.length][cpuMove[0].length];

                    for (int i = 0; i < newBoard.length; i++)
                        System.arraycopy(cpuMove[i], 0, newBoard[i], 0, newBoard[i].length);

                    currentGame.update(new Node<char[][]>(newBoard));
                }

                play = !play;
            }
            System.out.println(currentGame);
            if(currentGame.isGoal(currentGame.getCurrentPosition()))
            {
                if (!play)
                    System.out.println("You win");
                else
                    System.out.println("You lose");
            }
            else
            {
                System.out.println("Tie game");
            }
        }
    }

    /**
     * Creates a board from standard input
     * @return The board
     */
    private static char[][] readInput()
    {
        char[][] board;
        Scanner in = new Scanner(System.in);
        String line;

        int horz = 0;
        int vert = 0;
        System.out.println("Enter board size followed by the board");
        try
        {
            line = in.nextLine();
            String[] chars = line.split(" ");

            horz = Integer.parseInt(chars[0]);
            vert = Integer.parseInt(chars[1]);

            if(horz < 1 || vert < 1)
                throw new NumberFormatException();
        }
        catch(NumberFormatException e)
        {
            System.err.println("Invalid dimensions");
            System.exit(-1);
        }

        board = new char[horz][vert];

        for(int i = 0; i < vert; i++)
        {
            line = in.nextLine();

            String[] chars = line.split(" ");

            for(int j = 0; j < chars.length; j++)
            {
                try
                {
                    board[j][i] = chars[j].charAt(0);

                    if(board[j][i] != ONE && board[j][i] != TWO && board[j][i] != EMPTY)
                        throw new Exception();
                }
                catch(Exception e)
                {
                    System.err.println("Invalid line");
                    System.exit(-1);
                }
            }
        }


        return board;
    }

    /**
     * Creates a board from a file
     * @param fileName The file to be read
     * @return The board
     */
    private static char[][] readFile(String fileName)
    {
        char[][] board;
        Scanner in = null;
        String line;

        try
        {
            in = new Scanner(new File(fileName));
        }
        catch(FileNotFoundException e)
        {
            System.err.println("File " + fileName + " not found");
            System.exit(-1);
        }


        int horz = 0;
        int vert = 0;
        try
        {
            line = in.nextLine();
            String[] chars = line.split(" ");

            horz = Integer.parseInt(chars[0]);
            vert = Integer.parseInt(chars[1]);

            if(horz < 1 || vert < 1)
                throw new NumberFormatException();
        }
        catch(NumberFormatException e)
        {
            System.err.println("Invalid dimensions in " + fileName);
            System.exit(-1);
        }

        board = new char[horz][vert];

        for(int i = 0; i < vert; i++)
        {
            line = in.nextLine();

            String[] chars = line.split(" ");

            for(int j = 0; j < chars.length; j++)
            {
                try
                {
                    board[j][i] = chars[j].charAt(0);

                    if(board[j][i] != ONE && board[j][i] != TWO && board[j][i] != EMPTY)
                        throw new Exception();
                }
                catch(Exception e)
                {
                    System.err.println("Invalid line in " + fileName);
                    System.exit(-1);
                }
            }
        }


        return board;
    }


    /**
     * Determines if a player move is valid
     *
     * @param col Column chosen by player
     * @return True if the move is valid
     */
    public boolean isValid(int col)
    {
        boolean valid = false;

        //There must be an open space in the chosen column
        for(char c: board[col])
        {
            if (c == '.')
                valid = true;
        }

        return valid;
    }

    /**
     *
     * @return A string representation of the object
     */
    public String toString()
    {
        String answer = "";

        for(int j = 0; j < board[0].length; j++)
        {
            for(int i = 0; i < board.length; i++)
            {
                answer += board[i][j] + " ";
            }
            answer += System.lineSeparator();
        }

        return answer;
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
        char[][] aBoard = (char[][])n.getData();

        ArrayList<Node> neighbors = new ArrayList<Node>();
        ArrayList<Character> column;
        int index;
        boolean playerMove;

        int xCount = 0;
        int oCount = 0;

        for(int i = 0; i < aBoard.length; i++)
        {
            for(int j = 0; j < aBoard[0].length; j++)
            {
                if(aBoard[i][j] == 'X')
                    xCount++;

                if(aBoard[i][j] == 'O')
                    oCount++;
            }
        }

        //Figure out which symbol should be used
        if(xCount > oCount)
            playerMove = false;
        else if(xCount < oCount)
            playerMove = true;
        else {
            if (playerStart == 1)
                playerMove = false;
            else if(playerStart == -1)
                playerMove = true;
            else if(playerStart == 2)
                playerMove = true;
            else
                playerMove = false;
        }


        //For each column, if there is an empty space, place a piece
        for(int i = 0; i < aBoard.length; i++)
        {
            column = new ArrayList<Character>();

            for(int j = 0; j < aBoard[0].length; j++)
            {
                column.add(aBoard[i][j]);
            }

            index = column.lastIndexOf('.');
            if(index != -1)
            {
                Connect3 newPosition = new Connect3(aBoard);
                if(playerMove)
                {
                    newPosition.board[i][index] = 'X';
                }
                else
                    newPosition.board[i][index] = 'O';

                neighbors.add(new Node<char[][]>(newPosition.board));
            }
        }

        return neighbors;
    }

    /**
     * Determines whether a particular configuration is a goal configuration
     *
     * @param n A particular configuration
     * @return //True if the configuration is a goal, false otherwise
     */
    @Override
    public boolean isGoal(Node n)
    {
        boolean isGoal = false;

        char[][] aBoard = (char[][])n.getData();

        //check vertical
        for(int i = 0; i < aBoard.length && !isGoal; i++)
        {
            for(int j = 0; j < aBoard[0].length - 2 && !isGoal; j++)
            {
                if(aBoard[i][j] != EMPTY && aBoard[i][j] == aBoard[i][j+1] && aBoard[i][j] == aBoard[i][j+2])
                    isGoal = true;
            }
        }

        //check horizontal
        for(int j = 0; j < aBoard[0].length && !isGoal; j++)
        {
            for(int i = 0; i < aBoard.length - 2 && !isGoal; i++)
            {
                if(aBoard[i][j] != EMPTY && aBoard[i][j] == aBoard[i+1][j] && aBoard[i][j] == aBoard[i+2][j])
                    isGoal = true;
            }
        }

        //check diagonal
        for(int i = 0; i < aBoard.length - 2 && !isGoal; i++)
        {
            for(int j = 0; j < aBoard[0].length - 2 && !isGoal; j++)
            {
                if(aBoard[i][j] != EMPTY && aBoard[i][j] == aBoard[i+1][j+1] && aBoard[i][j] == aBoard[i+2][j+2])
                    isGoal = true;
            }
        }

        //check other diagonal
        for(int i = 0; i < aBoard.length - 2 && !isGoal; i++)
        {
            for(int j = aBoard[0].length - 1; j > 1 && !isGoal; j--)
            {
                if(aBoard[i][j] != EMPTY && aBoard[i][j] == aBoard[i+1][j-1] && aBoard[i][j] == aBoard[i+2][j-2])
                    isGoal = true;
            }
        }
        return isGoal;
    }


    /**
     * Updates the game after a move has been made
     *
     * @param n The new configuration after a move has been made
     */
    @Override
    public void update(Node n)
    {
        board = (char[][])n.getData();
    }

    /**
     * Checks if the current game is over
     *
     * @return True if the game is over, false otherwise
     */
    @Override
    public boolean gameOver() {
        int emptyCount = 0;
        boolean gameOver = false;

        //If there are no empty spaces, the game is a tie
        for(char[] chars: board)
        {
            for(char c : chars)
            {
                if (c == EMPTY)
                    emptyCount++;
            }
        }
        if(emptyCount == 0)
            gameOver = true;

        if(!gameOver)
            gameOver = isGoal(new Node<char[][]>(board));

        return gameOver;
    }

    /**
     * Getter for the current position
     *
     * @return A node containing the current position
     */
    @Override
    public Node getCurrentPosition() {
        char[][] aBoard = new char[board.length][board[0].length];

        for(int i = 0; i < board.length; i++)
            System.arraycopy(board[i], 0, aBoard[i], 0, aBoard[0].length);

        return new Node<char[][]>(aBoard);
    }

    /**
     *
     * @param n The node
     * @param score 1 if maximizing player, -1 if minimizing player, 0 if tie
     */
    @Override
    public void setScore(Node n, int score)
    {
        //The player who ends the game is the winner
        n.setScore(-score);
    }
}

Project Abstract:
The game class implements the Game interface to guarantee that it can be handled by Solver. The class serves two functions. The main method handles all UI for the game. The instance methods are used by the solver for analyzing positions.

The Solver takes an object of Game for a parameter. The Game object will have both the starting configuration and methods that are used to find additional configurations. These neighbor configurations are represented as generic Nodes that contain both information about the configuration and the score associated with the configuration.



TakeAway

Program Arguments:

java TakeAway num_coins
Go second against the AI with a starting number of coins num_coins

java TakeAway play num_coins
Go first against the AI with a starting number of coins num_coins

java TakeAway auto num_coins
Have two AI play each other with a starting number of coins num_coins


Player Moves:
The player must remove 1, 2, or 3 coins and cannot remove more coins than are remaining



Stones

Program Arguments:

java Stones pile-1 pile-2 pile-N
Go second against the AI with piles of stones pile-1, pile-2, ..., pile-N

java Stones play pile-1 pile-2 pile-N
Go first against the AI with piles of stones pile-1, pile-2, ..., pile-N

java Stones auto pile-1 pile-2 pile-N
Have two AI play each other with piles of stones pile-1, pile-2, ..., pile-N


Player Moves:
Each turn the player may pick as many stones as he/she wants from a single pile, but must pick at least one. This is done by first entering the pile index (starting from 0) and then entering the number of stones to remove.



Connect3

Program Arguments:

java Connect3 [play] -
The initial board is read in from standard input

java Connect3 [play] fileName
The initial board is read in from a space-delimited file


Player Moves:
Each turn the player chooses a column to drop a piece.
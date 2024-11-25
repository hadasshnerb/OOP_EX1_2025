import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameLogic implements PlayableLogic {
    private final int boardSize = 8; // Board size is 8x8
    private Disc[][] board = new Disc[boardSize][boardSize]; // The 8x8 board with Disc objects
    private boolean FirstPlayerTurn = true; // Start with the first player
    private Player firstPlayer;
    private Player secondPlayer;
    private Stack<Move> moveHistory = new Stack<>();

    /**
     * Constructs a new GameLogic object with default players and an empty board.
     * Initializes the game to start with the first player.
     */
    public GameLogic() {
        this.firstPlayer = new HumanPlayer(true);
        this.secondPlayer = new HumanPlayer(false);
        this.reset();
        this.moveHistory = new Stack<>();
    }

    /**
     * Attempts to place a disc on the board at the specified position.
     * Validates the move, flips opponent discs, updates move history, and switches turns.
     *
     * @param a    the position to place the disc
     * @param disc the disc to be placed
     * @return true if the move is valid and executed, false otherwise
     */
    @Override
    public boolean locate_disc(Position a, Disc disc) {
        // Check if the position is out of board boundaries or already occupied
        if (a.row() >= boardSize || a.row() < 0 || a.col() >= boardSize || a.col() < 0) {
            return false; // Out of bounds
        }
        if (board[a.row()][a.col()] != null) {
            return false; // Position is already occupied
        }

        // Check if placing the disc results in at least one disc flip
        if (countFlips(a) < 1) {
            return false; // No valid flips
        }
        // Check if the disc is an UnflippableDisc and if the player has enough available
        if (disc instanceof UnflippableDisc) {
            if (FirstPlayerTurn && firstPlayer.getNumber_of_unflippedable() <= 0) {
                return false; // No UnflippableDiscs left for player 1
            } else if (!FirstPlayerTurn && secondPlayer.getNumber_of_unflippedable() <= 0) {
                return false; // No UnflippableDiscs left for player 2
            }
        }

        // Check if the disc is a BombDisc and if the player has enough available
        if (disc instanceof BombDisc) {
            if (FirstPlayerTurn && firstPlayer.getNumber_of_bombs() <= 0) {
                return false; // No BombDiscs left for player 1
            } else if (!FirstPlayerTurn && secondPlayer.getNumber_of_bombs() <= 0) {
                return false; // No BombDiscs left for player 2
            }
        }

        // Place the disc
        board[a.row()][a.col()] = disc;
        System.out.println("Player " + (FirstPlayerTurn ? "1" : "2") + " placed a " + disc.getType() + " in (" + a.row() + ", " + a.col() + ")");

        // Reduce the count of special discs if used
        if (disc instanceof UnflippableDisc) {
            if (FirstPlayerTurn) {
                firstPlayer.reduce_unflippedable();
            } else {
                secondPlayer.reduce_unflippedable();
            }
        }

        if (disc instanceof BombDisc) {
            if (FirstPlayerTurn) {
                firstPlayer.reduce_bomb();
            } else {
                secondPlayer.reduce_bomb();
            }
        }

        // Flip opponent discs and track which discs are flipped
        List<Position> flippedPositions = flipDiscs(a, disc);

        // Create a Move object for storing the move details
        Move move = new Move(a, disc, flippedPositions);

        // Add the move to the history
        moveHistory.push(move);

        // Switch the turn
        FirstPlayerTurn = !FirstPlayerTurn;

        // Return true to indicate that the move was successful
        return true;
    }

    /**
     * Retrieves the disc at the specified position on the board.
     *
     * @param position the position to check
     * @return the disc at the position, or null if the position is empty
     */
    @Override
    public Disc getDiscAtPosition(Position position) {
        return board[position.row()][position.col()];
    }

    /**
     * Gets the size of the board.
     *
     * @return the size of the board (8)
     */
    @Override
    public int getBoardSize() {
        return boardSize;
    }

    /**
     * Calculates all valid move positions for the current player.
     *
     * @return a list of valid move positions
     */
    @Override
    public List<Position> ValidMoves() {
        List<Position> validMoves = new ArrayList<>();

        // Iterate through each position on the board
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Position position = new Position(row, col);

                // Check if the position is empty and if placing a disc there is a valid move
                if (board[row][col] == null && countFlips(position) > 0) {
                    validMoves.add(position); // Add the valid position to the list
                }
            }
        }
        return validMoves; // Return the list of valid moves
    }

    /**
     * Retrieves the first player.
     *
     * @return the first player
     */
    @Override
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    /**
     * Retrieves the second player.
     *
     * @return the second player
     */
    @Override
    public Player getSecondPlayer() {
        return secondPlayer;
    }

    /**
     * Sets the players for the game.
     *
     * @param player1 the first player
     * @param player2 the second player
     */
    @Override
    public void setPlayers(Player player1, Player player2) {
        this.firstPlayer = player1;
        this.secondPlayer = player2;
    }

    /**
     * Checks whether it's the first player's turn.
     *
     * @return true if it's the first player's turn, false otherwise
     */
    @Override
    public boolean isFirstPlayerTurn() {
        return FirstPlayerTurn;
    }

    /**
     * Checks if the game is finished. If no valid moves remain, prints the winner.
     *
     * @return true if the game is finished, false otherwise
     */
    @Override
    public boolean isGameFinished() {
        if (!ValidMoves().isEmpty()) {
            return false;
        } else printWinner();
        return true;
    }

    /**
     * Resets the game to its initial state, clearing the board and move history.
     */
    @Override
    public void reset() {
        board = new Disc[boardSize][boardSize]; // Clear the board
        FirstPlayerTurn = true; // Set to first player's turn
        board[4][4] = new SimpleDisc(firstPlayer);
        board[3][3] = new SimpleDisc(firstPlayer);
        board[3][4] = new SimpleDisc(secondPlayer);
        board[4][3] = new SimpleDisc(secondPlayer);
        moveHistory.clear();
        firstPlayer.reset_bombs_and_unflippedable();
        secondPlayer.reset_bombs_and_unflippedable();

        if (firstPlayer instanceof RandomAI) {
            ((RandomAI) firstPlayer).reset();
        }
        if (secondPlayer instanceof RandomAI) {
            ((RandomAI) secondPlayer).reset();
        }
    }

/**
     * Undoes the last move, restoring the previous game state.
     * Handles flipping discs back and restoring special disc counts.
     */
    @Override
    public void undoLastMove() {
        if (firstPlayer.isHuman() && secondPlayer.isHuman()) {
            if (moveHistory.isEmpty()) {
                System.out.println("Undoing last move:");
                System.out.println("\tNo previous move available to undo.");
                System.out.println(" ");
                return; // No moves to undo
            }

            // Pop the last move from the stack
            Move lastMove = moveHistory.pop();
            Position position = lastMove.position();
            board[position.row()][position.col()] = null; // Remove the last placed disc
            System.out.println("Undoing last move:");
            System.out.println("\tUndo: removing " + lastMove.disc().getType() + " from (" + position.row() + ", " + position.col() + ")");

            // Reverse the flipped discs
            for (Position flippedPosition : lastMove.flippedPositions) {
                Disc currentDisc = board[flippedPosition.row()][flippedPosition.col()];
                if (currentDisc != null) {
                    currentDisc.setOwner(currentDisc.getOwner() == firstPlayer ? secondPlayer : firstPlayer);
                    System.out.println("\tUndo: flipping back " + currentDisc.getType() + " in (" + flippedPosition.row() + ", " + flippedPosition.col() + ")");
                }
            }
            System.out.println(" ");

            // Restore the count of special discs if they were used
            if (lastMove.disc() instanceof UnflippableDisc) {
                if (FirstPlayerTurn) {
                    secondPlayer.number_of_unflippedable++;
                } else {
                    firstPlayer.number_of_unflippedable++;
                }
            }

            if (lastMove.disc() instanceof BombDisc) {
                if (FirstPlayerTurn) {
                    secondPlayer.number_of_bombs++;
                } else {
                    firstPlayer.number_of_bombs++;
                }
            }

            // Switch the turn back
            FirstPlayerTurn = !FirstPlayerTurn;
        }
    }

    /**
     * Counts the number of opponent discs that would be flipped if a disc
     * is placed at the specified position.
     *
     * @param a the position to check
     * @return the number of discs that would be flipped
     */
    @Override
    public int countFlips(Position a) {
        // Create a temporary disc to simulate placing it on the board
        Disc currentDisc = FirstPlayerTurn ? new SimpleDisc(firstPlayer) : new SimpleDisc(secondPlayer);
        List<Position> flippablePositions = getFlippablePositions(a, currentDisc);
        return flippablePositions.size();
    }

    /**
     * Determines and prints the winner based on the current disc counts on the board.
     */
    private void printWinner() {
        int playerOneDiscs = 0;
        int playerTwoDiscs = 0;

        // Count discs for each player to determine who has won the current match
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Disc disc = board[row][col];
                if (disc != null) {
                    if (disc.getOwner() == firstPlayer) {
                        playerOneDiscs++;
                    } else if (disc.getOwner() == secondPlayer) {
                        playerTwoDiscs++;
                    }
                }
            }
        }

        // Determine the winner of the current game
        if (playerOneDiscs > playerTwoDiscs) {
            firstPlayer.addWin();  // Increment win for Player 1
            System.out.println("Player 1 wins with " + playerOneDiscs + " discs! Player 2 had " + playerTwoDiscs + " discs.");
        } else if (playerTwoDiscs > playerOneDiscs) {
            secondPlayer.addWin();  // Increment win for Player 2
            System.out.println("Player 2 wins with " + playerTwoDiscs + " discs! Player 1 had " + playerOneDiscs + " discs.");
        }
    }

    /**
     * Flips the opponent's discs in all valid directions based on the move.
     * Includes handling bomb discs and their chain effects.
     *
     * @param position the position where the disc is placed
     * @param disc     the disc to be placed
     * @return a list of positions where discs were flipped
     */
    private List<Position> flipDiscs(Position position, Disc disc) {
        List<Position> discsToFlip = getFlippablePositions(position, disc);

        // Flip all the discs that need to be flipped
        for (Position pos : discsToFlip) {
            board[pos.row()][pos.col()].setOwner(disc.getOwner());
            // Print the flipping message
            System.out.println("Player " + (disc.getOwner() == firstPlayer ? "1" : "2") +
                    " flipped the " + board[pos.row()][pos.col()].getType() +
                    " in (" + pos.row() + ", " + pos.col() + ")");
        }
        System.out.println();

        return discsToFlip; // Return the list of flipped positions for move history
    }

    /**
     * Calculates the positions of discs that would be flipped if a disc is placed at the specified position.
     * This method does not modify the game board.
     *
     * @param position the position where the disc is placed
     * @param disc     the disc to be placed
     * @return a list of positions where discs would be flipped
     */
    private List<Position> getFlippablePositions(Position position, Disc disc) {
        List<Position> discsToFlip = new ArrayList<>();
        int[] rowDelta = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] colDelta = {-1, 0, 1, -1, 1, -1, 0, 1};

        // Loop over all 8 directions
        for (int i = 0; i < rowDelta.length; i++) {
            int row = position.row() + rowDelta[i];
            int col = position.col() + colDelta[i];
            List<Position> potentialFlips = new ArrayList<>();

            // Traverse in the current direction
            while (row >= 0 && row < boardSize && col >= 0 && col < boardSize) {
                Disc currentDisc = board[row][col];

                if (currentDisc == null) {
                    break; // Stop if we hit an empty cell
                } else if (currentDisc.getOwner() == disc.getOwner()) {
                    // Found our own disc, so flip all the discs in between
                    discsToFlip.addAll(potentialFlips);
                    break;
                } else if (currentDisc instanceof BombDisc || currentDisc instanceof SimpleDisc)
                {
                    // Opponent's flippable disc (SimpleDisc or BombDisc)
                    potentialFlips.add(new Position(row, col));
                }

                // Move further in the current direction
                row += rowDelta[i];
                col += colDelta[i];
            }
        }

        // Handle bomb disc effects
        for (Position pos : new ArrayList<>(discsToFlip)) {
            if (getDiscAtPosition(pos) instanceof BombDisc) {
                bombHelper(pos, discsToFlip, disc.getOwner());
            }
        }

        return discsToFlip;
    }

    /**
     * Handles the effect of a bomb disc by flipping all adjacent discs.
     * This method works recursively if another bomb disc is found.
     *
     * @param pos         The position of the current bomb.
     * @param discsToFlip The list of positions that will be flipped.
     * @param discOwner   The owner of the disc that initiated the bomb effect.
     */
    private void bombHelper(Position pos, List<Position> discsToFlip, Player discOwner) {
        // Check if the current position is on the board
        if (isDiscInBoard(pos)) {
            int[] rowDelta = {-1, -1, -1, 0, 0, 1, 1, 1};
            int[] colDelta = {-1, 0, 1, -1, 1, -1, 0, 1};

            // Traverse all 8 possible neighboring positions of the bomb disc
            for (int i = 0; i < rowDelta.length; i++) {
                int row = pos.row() + rowDelta[i];
                int col = pos.col() + colDelta[i];
                Position neighbor = new Position(row, col);

                // Check if the neighbor is within bounds, has a disc, and is not already flipped
                if (isDiscInBoard(neighbor) && getDiscAtPosition(neighbor) != null
                        && !discsToFlip.contains(neighbor)
                        && getDiscAtPosition(neighbor).getOwner() != discOwner) {

                    // Add the disc to the list of positions to flip
                    if (!(getDiscAtPosition(neighbor) instanceof UnflippableDisc)) {
                        discsToFlip.add(neighbor);
                    }

                    // If the neighboring disc is a BombDisc, handle it recursively
                    if (getDiscAtPosition(neighbor) instanceof BombDisc) {
                        bombHelper(neighbor, discsToFlip, discOwner);
                    }
                }
            }
        }
    }

    /**
     * Utility method to check if a given position is within board boundaries.
     *
     * @param position the position to check
     * @return true if the position is within the board, false otherwise
     */
    private boolean isDiscInBoard(Position position) {
        int row = position.row();
        int col = position.col();
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
    }
}

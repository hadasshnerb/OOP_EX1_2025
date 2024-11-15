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
    public GameLogic() {
        this.firstPlayer = new HumanPlayer(true);
        this.secondPlayer = new HumanPlayer(false);
        this.reset();
        this.moveHistory = new Stack<>();
    }


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


    @Override
    public Disc getDiscAtPosition(Position position) {
        return board[position.row()][position.col()];
    }

    @Override
    public int getBoardSize() {
        return boardSize;
    }

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

    @Override
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    @Override
    public Player getSecondPlayer() {
        return secondPlayer;
    }

    @Override
    public void setPlayers(Player player1, Player player2) {
        this.firstPlayer = player1;
        this.secondPlayer = player2;
    }

    @Override
    public boolean isFirstPlayerTurn() {
        return FirstPlayerTurn;
    }

    @Override
    public boolean isGameFinished() {
        if (!ValidMoves().isEmpty()) {
            return false;
        } else printWinner();
        return true;
    }


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
    }

    @Override
    public void undoLastMove() {
        if (firstPlayer.isHuman() && secondPlayer.isHuman()) {
            if (moveHistory.isEmpty()) {
                System.out.println("\tNo previous move available to undo.");
                return; // No moves to undo
            }

            // Pop the last move from the stack
            Move lastMove = moveHistory.pop();
            Position position = lastMove.position();
            board[position.row()][position.col()] = null; // Remove the last placed disc
            System.out.println("Undoing last move :");
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

    @Override
    public int countFlips(Position a) {
        int flipCount = 0;
        if (a.row() >= boardSize || a.row() < 0 || a.col() > boardSize || a.col() < 0) {
            return 0;
        }
        if (board[a.row()][a.col()] != null) {
            return 0;
        }
        Disc currentDisc = FirstPlayerTurn ? new SimpleDisc(firstPlayer) : new SimpleDisc(secondPlayer);

        int[] rowDelta = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] colDelta = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < rowDelta.length; i++) {
            flipCount += oneDIrectionFlips(a, currentDisc, rowDelta[i], colDelta[i]);
        }

        return flipCount;
    }


    // Helper methods:
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
            System.out.println("Player 1 wins this match with " + playerOneDiscs + " discs! Player 2 had " + playerTwoDiscs + " discs.");
        } else if (playerTwoDiscs > playerOneDiscs) {
            secondPlayer.addWin();  // Increment win for Player 2
            System.out.println("Player 2 wins this match with " + playerTwoDiscs + " discs! Player 1 had " + playerOneDiscs + " discs.");
        } else {
            System.out.println("It's a tie! Both players have " + playerOneDiscs + " discs.");
        }
    }

    private int oneDIrectionFlips(Position position, Disc disc, int rowDelta, int colDelta) {
        // start with 0 flips
        int count = 0;

        // to go to the pos I want to check
        int row = position.row() + rowDelta;
        int col = position.col() + colDelta;


        while (row < boardSize && row >= 0 && col < boardSize && col >= 0) {
            Disc current = board[row][col];

            if (current == null) {
                return 0;
            } else if (current.getOwner() == disc.getOwner()) {
                return count;
            } else {
                count++;
            }

            // Move further in the current direction
            row += rowDelta;
            col += colDelta;
        }
        // If we reach the edge of the board without finding our own disc, return 0 (invalid flip)
        return 0;
    }

    private List<Position> flipDiscs(Position position, Disc disc) {
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
                } else if (currentDisc instanceof UnflippableDisc) {
                    // Cannot flip UnflippableDisc, stop here
                    break;
                } else if (currentDisc instanceof BombDisc) {
                    // Handle BombDisc flipping logic (cascade flip effect)
                    potentialFlips.add(new Position(row, col));
                    discsToFlip.addAll(applyBombEffect(new Position(row, col))); // Trigger bomb effect to flip surrounding discs
                    discsToFlip.add(new Position(row, col)); // Bomb disc itself should also be flipped
                    break;
                } else {
                    // If it's an opponent's SimpleDisc, add to potential flips
                    potentialFlips.add(new Position(row, col));
                }

                // Move further in the current direction
                row += rowDelta[i];
                col += colDelta[i];
            }
        }

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

    private List<Position> applyBombEffect(Position bombPosition) {
            int[] rowDelta = {-1, -1, -1, 0, 0, 1, 1, 1};
            int[] colDelta = {-1, 0, 1, -1, 1, -1, 0, 1};
            List<Position> affectedPositions = new ArrayList<>();

            for (int i = 0; i < rowDelta.length; i++) {
                int row = bombPosition.row() + rowDelta[i];
                int col = bombPosition.col() + colDelta[i];

                if (row >= 0 && row < boardSize && col >= 0 && col < boardSize) {
                    Disc currentDisc = board[row][col];
                    if (currentDisc != null) {
                        affectedPositions.add(new Position(row, col));
                    }
                }
            }
            return affectedPositions;
        }
}

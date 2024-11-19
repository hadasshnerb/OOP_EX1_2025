import java.util.Comparator;
import java.util.List;

public class GreedyAI extends AIPlayer {

    /**
     * Constructs a GreedyAI player.
     *
     * @param isPlayerOne Indicates whether this AI is the first player or not.
     */
    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    /**
     * Determines the next move for the AI using a greedy algorithm.
     * The AI selects the move that maximizes the number of opponent discs flipped.
     * If multiple moves have the same number of flips, it chooses the move
     * that is further to the right and higher on the board (based on column and row comparison).
     *
     * @param gameStatus The current state of the game, providing game logic and valid moves.
     * @return A Move representing the selected move, or null if no valid moves are available.
     */
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        // Determine the current player
        Player currentPlayer = gameStatus.isFirstPlayerTurn() ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer();
        Disc discToPlace = new SimpleDisc(currentPlayer);

        // Get the list of valid moves
        List<Position> validMoves = gameStatus.ValidMoves();
        if (validMoves.isEmpty()) {
            return null; // No valid moves available
        }

        // Comparator to select the best move
        Comparator<Position> comparator = new Comparator<Position>() {
            @Override
            public int compare(Position p1, Position p2) {
                // Compare the number of flips
                int flipsComparison = Integer.compare(
                        gameStatus.countFlips(p1),
                        gameStatus.countFlips(p2)
                );
                if (flipsComparison != 0) {
                    return flipsComparison; // If the number of flips is different
                }

                // Compare columns - prefer the move further to the right (higher column number)
                int columnComparison = Integer.compare(p1.col(),p2.col());  // flipped to prefer higher column number
                if (columnComparison != 0) {
                    return columnComparison;
                }

                // Compare rows - prefer the move further down (higher row number)
                return Integer.compare( p1.row(),p2.row()); // flipped to prefer higher row number
            }
        };

        // Loop to find the best move
        Position bestMove = null;
        for (Position move : validMoves) {
            if (bestMove == null || comparator.compare(move, bestMove) > 0) {
                bestMove = move;
            }
        }

        // Return the best move
        return new Move(bestMove, discToPlace, null);
    }
}

import java.util.List;

/**
 * Represents a greedy AI player that selects the move maximizing the number of flipped discs.
 * If multiple moves result in the same maximum flips, it prioritizes moves by their position
 * (preferring the most right and top positions).
 */
public class GreedyAI extends AIPlayer {

    /**
     * Constructs a GreedyAI player, specifying whether the player is Player One.
     *
     * @param isPlayerOne Boolean indicating if this AI player is Player One.
     */
    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    /**
     * Makes a move for the AI player based on the current game status.
     * The move is chosen to maximize the number of discs flipped, following
     * a greedy strategy. In the case of ties, the move is selected based on position
     * preferences: first by column (rightmost) and then by row (topmost).
     *
     * @param gameStatus The current game status, providing information about valid moves
     *                   and players.
     * @return A {@link Move} object representing the selected move,
     *         or {@code null} if no valid moves are available.
     */
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        Player currentPlayer = gameStatus.isFirstPlayerTurn() ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer();
        Disc discToPlace = new SimpleDisc(currentPlayer);

        List<Position> validMoves = gameStatus.ValidMoves();
        if (validMoves.isEmpty()) {
            return null; // No valid moves available
        }

        Position bestMove = null;
        int maxFlips = Integer.MIN_VALUE;

        // Select a greedy valid move
        for (Position move : validMoves) {
            int flips = gameStatus.countFlips(move);
            if (flips > maxFlips) {
                maxFlips = flips;
                bestMove = move;
            } else if (flips == maxFlips) {
                // If multiple moves have the same number of flips, choose the most right move
                if (move.col() > bestMove.col()) {
                    bestMove = move;
                }
                if (move.row() < bestMove.row() && move.col() == bestMove.col()) {
                    bestMove = move;
                }
            }
        }

        return new Move(bestMove, discToPlace, null);
    }
}

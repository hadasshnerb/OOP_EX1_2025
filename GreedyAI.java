import java.util.List;

public class GreedyAI extends AIPlayer{
    public GreedyAI (boolean isPlayerOne) {
        super(isPlayerOne);
    }



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

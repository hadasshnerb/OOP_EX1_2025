import java.util.List;
import java.util.Random;

/**
 * Represents an AI player that makes random moves, with a limited use of special discs
 * (UnflippableDisc and BombDisc). The AI selects a valid move randomly and decides the
 * disc type based on availability and randomness.
 */
public class RandomAI extends AIPlayer {

    private final int initialBombLimit = 3; // Initial bomb limit
    private final int initialUnlipLimit = 2; // Initial unflippable disc limit

    private Random random = new Random(); // Random number generator for move selection and disc type
    private int bombLimit = initialBombLimit; // Current bomb limit
    private int unlipLimit = initialUnlipLimit; // Current unflippable disc limit

    /**
     * Constructs a RandomAI player, specifying whether the player is Player One.
     *
     * @param isPlayerOne Boolean indicating if this AI player is Player One.
     */
    public RandomAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    /**
     * Makes a move for the AI player based on the current game status.
     * The move is selected randomly from the list of valid moves, and the disc type
     * is determined by random selection, constrained by the remaining limits on
     * special discs.
     *
     * @param gameStatus The current game status, providing information about valid moves
     *                   and players.
     * @return A {@link Move} object representing the randomly selected move,
     *         or {@code null} if no valid moves are available.
     */
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        List<Position> validMoves = gameStatus.ValidMoves();
        if (validMoves.isEmpty()) {
            return null; // No valid moves available
        }

        // Select a random valid move
        int randomIndex = random.nextInt(validMoves.size());
        Position randomPos = validMoves.get(randomIndex);

        Player currentPlayer = isPlayerOne() ? gameStatus.getFirstPlayer() : gameStatus.getSecondPlayer();

        // Create a disc for the AI player based on availability and randomness
        Disc disc;
        int randomDisc = random.nextInt(3); // Randomly choose disc type (0, 1, 2)

        if (randomDisc == 1 && unlipLimit > 0) {
            // If random choice is UnflippableDisc and within limit
            unlipLimit--;
            disc = new UnflippableDisc(currentPlayer);
        } else if (randomDisc == 2 && bombLimit > 0) {
            // If random choice is BombDisc and within limit
            bombLimit--;
            disc = new BombDisc(currentPlayer);
        } else {
            // Default to SimpleDisc if special discs are not available or random choice was 0
            disc = new SimpleDisc(currentPlayer);
        }

        // Return the move
        return new Move(randomPos, disc, null);
    }

    /**
     * Resets the AI player's bomb and unflippable disc limits to their initial values.
     * This method should be called at the end of each game.
     */
    public void reset() {
        this.bombLimit = initialBombLimit;
        this.unlipLimit = initialUnlipLimit;
    }
}

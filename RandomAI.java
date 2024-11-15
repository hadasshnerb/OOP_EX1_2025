import java.util.List;
import java.util.Random;

public class RandomAI extends AIPlayer {

    Random random = new Random();
    int bombLimit = 3;       // Set initial bomb limit to 3
    int unlipLimit = 2;      // Set initial unflippable limit to 2

    public RandomAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

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
}

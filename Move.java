import java.util.List;

/**
 * Represents a move in the game, including the position where the move is made,
 * the disc being placed, and the list of positions of flipped discs.
 */
public class Move {
    private Position position; // Where the move is happening
    private Disc disc;         // The disc being placed
    public List<Position> flippedPositions; // List of discs flipped during this move

    /**
     * Constructs a Move object with the specified position, disc, and flipped positions.
     *
     * @param position The position where the move is happening.
     * @param disc The disc being placed.
     * @param flippedPositions The list of positions of discs flipped during this move.
     */
    public Move(Position position, Disc disc, List<Position> flippedPositions) {
        this.position = position;
        this.disc = disc;
        this.flippedPositions = flippedPositions;
    }

    /**
     * Returns the position where the move is happening.
     *
     * @return The position of the move.
     */
    public Position position() {
        return position;
    }

    /**
     * Returns the disc being placed during the move.
     *
     * @return The disc of the move.
     */
    public Disc disc() {
        return disc;
    }

    /**
     * Returns a string representation of the move, including its position,
     * the disc being placed, and the list of flipped positions.
     *
     * @return A string representation of the move.
     */
    @Override
    public String toString() {
        return "Move{" +
                "position=" + position +
                ", disc=" + disc +
                ", flippedPositions=" + flippedPositions +
                '}';
    }
}

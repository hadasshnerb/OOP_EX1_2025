import java.util.List;

public class Move {
    private  Position position; // Where the move is happening
    private  Disc disc;         // The disc being placed
    public   List<Position> flippedPositions; // List of discs flipped during this move

    // Constructor that initializes all fields
    public Move(Position position, Disc disc, List<Position> flippedPositions) {
        this.position = position;
        this.disc = disc;
        this.flippedPositions = flippedPositions;
    }

    // Getters to access fields
    public Position position() {
        return position;
    }

    public Disc disc() {
        return disc;
    }
    // To print the move for debugging purposes
    @Override
    public String toString() {
        return "Move{" +
                "position=" + position +
                ", disc=" + disc +
                ", flippedPositions=" + flippedPositions +
                '}';
    }
}

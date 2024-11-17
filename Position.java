/**
 * Represents a position on the game board, defined by a row and column.
 * The row and column values are typically in the range of 0 to 7 for an 8x8 board.
 */
public class Position {
    private int row;    // Represents the row on the board (0 to 7)
    private int column; // Represents the column on the board (0 to 7)

    /**
     * Constructs a Position object with the specified row and column.
     *
     * @param row The row of the position on the board (0-based index).
     * @param column The column of the position on the board (0-based index).
     */
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Returns the row of the position.
     *
     * @return The row of the position as an integer.
     */
    public int row() {
        return row;
    }

    /**
     * Returns the column of the position.
     *
     * @return The column of the position as an integer.
     */
    public int col() {
        return column;
    }

    /**
     * Compares this position to another object for equality.
     * Two positions are considered equal if they have the same row and column.
     *
     * @param obj The object to compare to this position.
     * @return {@code true} if the specified object is equal to this position,
     *         {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return row == position.row && column == position.column;
    }
}


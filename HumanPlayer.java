/**
 * Represents a human player in the game. This class extends the {@link Player}
 * class and overrides relevant methods to specify that the player is human.
 */
public class HumanPlayer extends Player {

    /**
     * Constructs a HumanPlayer object, specifying whether the player is Player One.
     *
     * @param isPlayerOne Boolean indicating if this player is Player One.
     */
    public HumanPlayer(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    /**
     * Indicates that this player is a human player.
     *
     * @return {@code true}, as this class represents a human player.
     */
    @Override
    boolean isHuman() {
        return true;
    }
}


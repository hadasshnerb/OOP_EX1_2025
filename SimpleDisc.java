/**
 * Represents a standard disc used in the game.
 * The SimpleDisc belongs to a specific player and has a default visual representation.
 */
public class SimpleDisc implements Disc {

    private Player owner; // The player who owns this disc.

    /**
     * Constructs a SimpleDisc object and assigns it to a player.
     *
     * @param currentPlayer The player who owns this SimpleDisc.
     */
    public SimpleDisc(Player currentPlayer) {
        this.owner = currentPlayer;
    }

    /**
     * Returns the player who owns this SimpleDisc.
     *
     * @return The Player object representing the owner of this SimpleDisc.
     */
    @Override
    public Player getOwner() {
        return this.owner;
    }

    /**
     * Sets the owner of this SimpleDisc.
     *
     * @param player The Player object to set as the owner of this SimpleDisc.
     */
    @Override
    public void setOwner(Player player) {
        this.owner = player;
    }

    /**
     * Returns the type of this disc as a string representation.
     * The type is represented by a simple disc icon ("⬤").
     *
     * @return A string representing the type of this disc.
     */
    @Override
    public String getType() {
        return "⬤"; // Simple Disc
    }
}



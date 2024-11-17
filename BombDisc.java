/**
 * Represents a special type of disc called "BombDisc" in the game.
 * The BombDisc belongs to a specific player and has a unique visual representation.
 */
public class BombDisc implements Disc {

    private Player owner; // The player who owns this disc.

    /**
     * Constructs a BombDisc object and assigns it to a player.
     *
     * @param currentPlayer The player who owns this BombDisc.
     */
    public BombDisc(Player currentPlayer) {
        this.owner = currentPlayer;
    }

    /**
     * Returns the player who owns this BombDisc.
     *
     * @return The Player object representing the owner of this BombDisc.
     */
    @Override
    public Player getOwner() {
        return this.owner;
    }

    /**
     * Sets the owner of this BombDisc.
     *
     * @param player The Player object to set as the owner of this BombDisc.
     */
    @Override
    public void setOwner(Player player) {
        this.owner = player;
    }

    /**
     * Returns the type of this disc as a string representation.
     * The type is represented by the bomb icon ("💣").
     *
     * @return A string representing the type of this disc.
     */
    @Override
    public String getType() {
        return "💣"; // Bomb Disc
    }
}

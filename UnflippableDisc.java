/**
 * Represents a special type of disc called "UnflippableDisc" in the game.
 * The owner of an UnflippableDisc cannot be changed once it is created.
 */
public class UnflippableDisc implements Disc {
    private Player owner; // The player who owns this disc.

    /**
     * Constructs an UnflippableDisc object with the specified owner.
     *
     * @param owner The player who owns this UnflippableDisc.
     */
    public UnflippableDisc(Player owner) {
        this.owner = owner;
    }

    /**
     * Returns the player who owns this UnflippableDisc.
     *
     * @return The Player object representing the owner of this UnflippableDisc.
     */
    @Override
    public Player getOwner() {
        return owner;
    }

    /**
     * This method does nothing as the owner of an UnflippableDisc cannot be changed.
     *
     * @param player The Player object (ignored as the owner cannot be changed).
     */
    @Override
    public void setOwner(Player player) {
    }

    /**
     * Returns the type of this disc as a string representation.
     * The type is represented by the unflippable disc icon ("⭕").
     *
     * @return A string representing the type of this disc.
     */
    @Override
    public String getType() {
        return "⭕"; // Symbol representing an Unflippable Disc
    }
}


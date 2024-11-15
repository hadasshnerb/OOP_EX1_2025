public class UnflippableDisc implements Disc {
    private Player owner;

    public UnflippableDisc(Player owner) {
        this.owner = owner;
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Player player) {
    }

    @Override
    public String getType() {
        return "⭕"; // Symbol representing an Unflippable Disc
    }
}


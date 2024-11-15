public class SimpleDisc implements Disc {

    private Player owner;

    public SimpleDisc(Player currentPlayer) {
        this.owner = currentPlayer;
    }

    @Override
    public Player getOwner() {
        return this.owner;
    }

    @Override
    public void setOwner(Player player) {
        this.owner = player;
    }

    @Override
    public String getType() {
        return  "⬤"; // Simple Disc
    }
}


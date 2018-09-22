package GameEngine;

public class TurnRecord {
    private Player player;
    private Integer column;
    private Boolean isPopOut;

    public Player getPlayer() {
        return player;
    }

    public Integer getColumn() {
        return column;
    }

    public Boolean getPopOut() { return isPopOut; }

    public TurnRecord(Player player, Integer column, Boolean isPopOut){
        this.player = player;
        this.column = column;
        this.isPopOut = isPopOut;
    }

    @Override
    public String toString() {
        if (!this.isPopOut)
            return String.format("GameEngine.Player <%s> have put a disc of type <%s> at column <%d>",
                    this.player.getId(),
                    this.player.getDiscType(),
                    this.column);
        return String.format("GameEngine.Player <%s> have popped out a disc of type <%s> at column <%d>",
                this.player.getId(),
                this.player.getDiscType(),
                this.column);
    }
}

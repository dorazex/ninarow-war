package GameEngine;

public class PlayerInfo {

    public enum PlayerType {
        Human,
        Computer
    }

    private final String name;
    private final String discType;
    private final PlayerType playerType;
    private int turnsCount = 0;
    private Board board; //TODO : copy


    PlayerInfo(String name, Board board, PlayerType playerType, String discType, int turnsCount) {
        this.name = name;
        this.board = board;
        this.playerType = playerType;
        this.discType = discType;
        this.turnsCount = turnsCount;
    }

    public PlayerInfo(String name, PlayerType playerType) {
        this.name = name;
        this.playerType = playerType;
        this.discType = "red";
    }

    int getTurnsCount() { return turnsCount;}
    public final String getName() {
        return  name;
    }
    final PlayerType getPlayerType(){
        return playerType;
    }
    public final Board getBoard() {
        return board;
    }

    @Override
    public String toString(){
        return String.format("%1s - %2s", playerType, name);
    }
}

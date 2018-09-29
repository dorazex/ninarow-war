package GameEngine;

class SimplePlayer {
    // just need name and type for player list
    // also players can be in more than one game

    private String name;
    private PlayerInfo.PlayerType playerType;

    SimplePlayer(String name, PlayerInfo.PlayerType playerType) {
        this.name = name;
        this.playerType = playerType;
    }

    public String getName() {
        return name;
    }
    public PlayerInfo.PlayerType getPlayerType() {
        return playerType;
    }
    public String getPlayerTypeString() {
        return playerType.toString();
    }
}

package GameEngine;

/**
 * Created by s on 03/10/2016.
 */
class SimplePlayer {
    // just need name and type for player list
    // also players can be in more than one game

    private String name;
    private PlayerManager.PlayerType playerType;

    SimplePlayer(String name, PlayerManager.PlayerType playerType) {
        this.name = name;
        this.playerType = playerType;
    }

    public String getName() {
        return name;
    }
    public PlayerManager.PlayerType getPlayerType() {
        return playerType;
    }
}

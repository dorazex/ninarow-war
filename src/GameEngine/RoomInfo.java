package GameEngine;

import com.google.gson.internal.Pair;

/**
 * Created by s on 04/10/2016.
 */
class RoomInfo {
    //simplified game manager info for converting to json

    private int spectators = 0;
    private int roomIdentifier;
    private String organizer;
    private String gameTitle;
    private int totalPlayers;
    private int onlinePlayers = 0;
    private int rounds;
    private int rows;
    private int columns;

    void addSpectator() { spectators++; }

    void removeSpectator() {
        spectators--;
    }

    void clearInfo() {
        onlinePlayers = 0;
        spectators = 0;
    }

    void setRoomIdentifier(int roomIdentifier) { this.roomIdentifier = roomIdentifier;}

    String getOrganizer() {
        return organizer;
    }

    void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    String getGameTitle() {
        return gameTitle;
    }

    void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    int getTotalPlayers() {
        return totalPlayers;
    }

    void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    int getOnlinePlayers() {
        return onlinePlayers;
    }

    void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    void increaseOnlinePlayers() {
        this.onlinePlayers++;
    }

    int getRounds() {
        return rounds;
    }

    void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    void decreaseOnlinePlayers() {
        onlinePlayers--;
    }
}

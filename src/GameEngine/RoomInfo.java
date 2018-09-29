package GameEngine;

import com.google.gson.internal.Pair;

/**
 * Created by s on 04/10/2016.
 */
public class RoomInfo {
    //simplified game manager info for converting to json

    private int roomIdentifier;
    private String organizer;
    private String gameTitle;
    private int totalPlayers;
    private int onlinePlayers = 0;
    private int rows;
    private int columns;
    public int target;
    public String variant;
    public Boolean isStarted;

    public int getRoomIdentifier() {
        return roomIdentifier;
    }

    void clearInfo() {
        onlinePlayers = 0;
        isStarted = false;
    }

    void setRoomIdentifier(int roomIdentifier) { this.roomIdentifier = roomIdentifier;}

    String getOrganizer() {
        return organizer;
    }

    void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getGameTitle() {
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

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    void decreaseOnlinePlayers() {
        onlinePlayers--;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public void setStarted(Boolean started) {
        isStarted = started;
    }
}

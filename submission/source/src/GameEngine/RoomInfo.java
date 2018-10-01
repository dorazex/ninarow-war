package GameEngine;

public class RoomInfo {
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

    String getOrganizer() {
        return organizer;
    }
    public int getRoomIdentifier() {
        return roomIdentifier;
    }
    public String getGameTitle() {
        return gameTitle;
    }
    int getTotalPlayers() {
        return totalPlayers;
    }
    int getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }
    public void setColumns(Integer columns) {
        this.columns = columns;
    }
    public void setTarget(int target) {
        this.target = target;
    }
    void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }
    public void setVariant(String variant) {
        this.variant = variant;
    }
    void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }
    void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }
    public void setStarted(Boolean started) {
        isStarted = started;
    }
    void setRoomIdentifier(int roomIdentifier) { this.roomIdentifier = roomIdentifier;}
    void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    void increaseOnlinePlayers() {
        this.onlinePlayers++;
    }

    void decreaseOnlinePlayers() {
        onlinePlayers--;
    }

    void clearInfo() {
        onlinePlayers = 0;
        isStarted = false;
    }
}

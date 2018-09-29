package GameEngine;
import javafx.beans.property.SimpleStringProperty;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class Game {

    public class GameDetails {
        //holds info to be sent with polling from game servlet
        //simple object for gsoning to client

        private boolean isActivePlayer = false;
        private String currentPlayerName;
        private int turnsCount; //relevant only to current player
        private List<PlayerInfo> playerList; //player type, name, score of player
        private boolean isGameOver;

        GameDetails(Game game, String username){

            if(game.getCurrentPlayer().getName().equals(username)){
                isActivePlayer = true;
            }

            turnsCount = game.getCurrentPlayer().getTurnsCount();
            currentPlayerName = game.getCurrentPlayer().getName();
            playerList = game.makePlayersList();

            isGameOver = game.isGameOver;
        }
    }


    private int target;
    private Board board;
    private List<Player> players;
    private Boolean isStarted;
    private Integer currentPlayerIndex;
    private Date startDate;
    private Player winnerPlayer;
    private History history;
    private SimpleStringProperty duration;
    private Date currentDate;
    private String variant;
    private Boolean registrationBlocked = false;
    private Boolean autoPlayInProgress = false;

    private final RoomInfo roomInfo = new RoomInfo();
    private ExecutorService computerMoveExecutor;
    private boolean isGameOver;
    private boolean onePlayerReady = false;

    private Stack<String> colorsList;

    public List<PlayerInfo> makePlayersList() {

        List<PlayerInfo> playersManagers = new ArrayList<>();
        for (Player player :
                this.players) {
            PlayerInfo.PlayerType playerType;
            if (player.getClass().getSimpleName().contains("Computer")){
                playerType = PlayerInfo.PlayerType.Computer;
            } else {
                playerType = PlayerInfo.PlayerType.Human;
            }
            PlayerInfo playerInfo = new PlayerInfo(player.getName(), getBoard(), playerType, player.getDiscType(), player.getTurnsCount());
            playersManagers.add(playerInfo);
        }
        this.board.addPlayers(this.players);

        return playersManagers;
    }

    private Integer getPlayerIndexOfUser(String username){
        Integer index = -1;
        for (Player player : this.players) {
            index++;
            if (player.getName().equals(username)) break;
        }
        return index;
    }

    public Boolean getAutoPlayInProgress() {
        return autoPlayInProgress;
    }

    public void setAutoPlayInProgress(Boolean autoPlayInProgress) {
        this.autoPlayInProgress = autoPlayInProgress;
    }

    public Player getPlayer(String organizer) {
        return this.players.get(this.getPlayerIndexOfUser(organizer));
    }

    private void removePlayerFromList(String username) {
        roomInfo.decreaseOnlinePlayers();
        Integer indexOfUserName = this.getPlayerIndexOfUser(username);
        if (indexOfUserName == -1){
            throw new RuntimeException("username not found for removal");
        }
        this.players.remove(indexOfUserName);
    }

    public synchronized boolean addPlayer(String organizer, PlayerInfo.PlayerType playerType) {
        if (this.registrationBlocked) return false;

        PlayerCommon player;
        if (playerType == PlayerInfo.PlayerType.Computer){
            player = new PlayerComputer(this.players.size(), organizer, this.colorsList.pop());
        } else{
            player = new PlayerWeb(this.players.size(), organizer, this.colorsList.pop());
        }

        if(roomInfo.getOnlinePlayers() < roomInfo.getTotalPlayers() && !isStarted){
            players.add(player);
            roomInfo.increaseOnlinePlayers();
            if(roomInfo.getOnlinePlayers() == 1){
                this.currentPlayerIndex = 0;
            }
            if(roomInfo.getOnlinePlayers() == roomInfo.getTotalPlayers()){
                roomInfo.setStarted(true);
                isStarted = true;
                isGameOver = false;
            }

            return true;
        }

        return false;
    }

    public synchronized boolean removePlayer(String organizer) {
        Player player = this.getPlayer(organizer);
        players.remove(player);
        board.removePlayer(player.getId());
        roomInfo.decreaseOnlinePlayers();
        this.registrationBlocked = true;
        if (this.getIsStarted() && player.getName().equals(this.getCurrentPlayer().getName())) {
            this.advanceToNextPlayer();
        }
        return false;
    }

    public Boolean getRegistrationBlocked() {
        return registrationBlocked;
    }

    public boolean checkUniqueUser(String username) { //TODO test this works
        return players
                .stream()
                .filter(player -> Objects.equals(player.getName(), username))
                .collect(Collectors.toList()).size() == 1;
    }

    public void setComputerMoveExecutor(ExecutorService computerMoveExecutor) {
        this.computerMoveExecutor = computerMoveExecutor;
    }

    RoomInfo getRoomInfo() {
        return roomInfo;
    }

    public void setOrganizer(String organizer) {
        roomInfo.setOrganizer(organizer);
    }

    public void setBoard(Board board){
        this.board = board;
        roomInfo.setRows(board.getRows());
        roomInfo.setColumns(board.getColumns());
    }

    public int getTarget() { return target; }

    public void setGameTitle(String gameTitle) {
        roomInfo.setGameTitle(gameTitle);
    }

    public void setVariant(String variant) {
        roomInfo.setVariant(variant);
    }

    public void setTarget(Integer target) {
        roomInfo.setTarget(target);
    }

    public void setStarted(Boolean isStarted) {
        roomInfo.setStarted(isStarted);
    }

    public void setTotalPlayers(int totalPlayers) {
        roomInfo.setTotalPlayers(totalPlayers);
    }

    public Boolean getGameRunning() {
        return isStarted;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public synchronized void setNextPlayer() {  //synced in case one player exits at the same time as another pressing turn done

        this.currentPlayerIndex++;
        if (this.currentPlayerIndex >= players.size()) {
            this.currentPlayerIndex = 0;
        }

        this.advanceToNextPlayer();

//        playAutoMoves(); // will play only if computer
    }

    public void resetGame() {
        roomInfo.setStarted(false);
        isStarted = false;
        onePlayerReady = false;
        if(players != null) {
            players.clear();
        }
        currentPlayerIndex = 0;
        registrationBlocked = false;
        roomInfo.clearInfo();
        // history, board, duration, winner
        this.history.clear();
        this.board.clear();
        isGameOver = false;
    }


    public Integer getTurnsCountOfUser(String username){
        return this.getPlayer(username).getTurnsCount();
    }

    public void onePlayerReady(){
        if (isStarted && this.getCurrentPlayer().getClass().getSimpleName().equals(PlayerInfo.PlayerType.Computer.toString())
                && !this.onePlayerReady){
//            playAutoMoves();    // start the game in case first player is computer
        }
        this.onePlayerReady = true; //so the above will happen only once
    }

    public GameDetails getGameDetails(String username) {
        return new GameDetails(this, username);
    }










    public Board getBoard(){ return board; }

    public List<Player> getPlayers() { return players; }

    public Boolean getIsStarted() {
        return isStarted;
    }

    public Integer getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public Player getWinnerPlayer(){
        return this.winnerPlayer;
    }

    public void setWinnerPlayer(Player winnerPlayer) {
        this.winnerPlayer = winnerPlayer;
    }

    public History getHistory() {
        return history;
    }

    public SimpleStringProperty durationProperty() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration.set(duration);
    }

    public String getVariant() { return variant; }

    public Player getCurrentPlayer(){
        return this.players.get(this.currentPlayerIndex);
    }

    public Game(){};

    public Game(int target, int rows, int columns, String variant){
        this.target = target;
        this.board = new Board(rows, columns);
        this.isStarted = false;
        this.currentPlayerIndex = 0;
        this.startDate = null;
        this.currentDate = new Date();
        this.winnerPlayer = null;
        this.history = new History();
        this.duration = new SimpleStringProperty();
        this.duration.set("00:00");
        this.variant = variant;
        this.players = new ArrayList<>();
        this.roomInfo.setColumns(this.board.getColumns());
        this.roomInfo.setRows(this.board.getRows());

        this.colorsList = new Stack<>();
        this.colorsList.push("#82b74b");
        this.colorsList.push("#034f84");
        this.colorsList.push("#6b5b95");
        this.colorsList.push("#feb236");
        this.colorsList.push("#d64161");
        this.colorsList.push("#ff7b25");

    }

    public Game(int target, Board board){
        this.target = target;
        this.board = board;
        this.isStarted = false;
        this.currentPlayerIndex = 0;
        this.startDate = null;
    }


    public String getDurationString() {
        if (startDate == null) return "00:00";
        currentDate = new Date();
        long diffInSeconds = (currentDate.getTime() - this.startDate.getTime()) / 1000;
        long seconds = Math.floorMod(diffInSeconds, 60);
        long minutes = Math.floorDiv(diffInSeconds, 60);

        return String.format("%02d:%02d", minutes, seconds);
    }

    public void advanceToNextPlayer(){
        this.currentPlayerIndex = (this.currentPlayerIndex+ 1) % this.players.size();
        for (int i = 0; i < this.players.size(); i++) {
            this.players.get(i).setCurrentTurn(i == this.currentPlayerIndex);
        }
    }

    public Boolean isEndWithWinner(){
        Boolean isTargetReached = this.board.isTargetReached(players, this.target, this.variant);
        this.isGameOver = isTargetReached;
        return isTargetReached;
    }

    public void start(ArrayList<Player> players){
        this.players = players;
        this.players.get(0).setCurrentTurn(true);
        this.board.addPlayers(this.players);

        this.isStarted = true;
        roomInfo.setStarted(true);
        this.startDate = new Date();
    }

    public Boolean makeTurn(){
        TurnRecord turnRecord = this.players.get(this.currentPlayerIndex).makeTurn(this.board);
        this.history.pushTurn(turnRecord);
        return finalizeTurn();
    }

    public Boolean finalizeTurn(){
        this.players.get(this.currentPlayerIndex).turnsCountProperty().set(
                this.players.get(this.currentPlayerIndex).getTurnsCount() + 1
        );
        if (this.isEndWithWinner()){
            this.winnerPlayer = this.players.get(this.currentPlayerIndex);
            this.isGameOver = true;
            return true;
        }
        this.advanceToNextPlayer();
        return this.board.isFull();
    }

    @Override
    public String toString() {
        String fullFormat =
                "%s\n" +
                "------------------------------------\n" +
                "Game started: %s\n" +
                "Target: %d\n" +
                "Turn of: %d\n" +
                "%s" +
                "\n" +
                "//////  GameEngine.Board  //////\n" +
                "%s\n" +
                "Time: %s\n\n" +
                "%s\n";

        String shortFormat =
                "------------------------------------\n" +
                "Game started: %s\n" +
                "Target: %d\n" +
                "//////  GameEngine.Board  //////\n" +
                "%s\n\n" +
                "%s\n";

        String menu =
                "Commands:\n" +
                "1 - LOAD config XML file\n" +
                "2 - START game\n" +
                "3 - SHOW game state\n" +
                "4 - PLAY turn\n" +
                "5 - SHOW history\n" +
                "6 - EXIT game\n";

        if (this.isStarted) {
            String playersBlock = "";
            for (Player player: this.players){
                playersBlock += player.toString();
            }

            String headerLine = String.format("Game of %d players, on a %dx%d board",
                    this.players.size(),
                    this.board.getRows(),
                    this.board.getColumns());

            Date currentTime = new Date();
            String durationString = this.getDurationString();
            String finalString =  String.format(fullFormat,
                    headerLine,
                    this.isStarted.toString(),
                    this.target,
                    this.currentPlayerIndex + 1,
                    playersBlock,
                    this.board,
                    durationString,
                    menu);
//            this.stringProperty.set(finalString);
            return finalString;
        } else {
            String finalString = String.format(shortFormat,
                    this.isStarted.toString(),
                    this.target,
                    this.board,
                    menu);
//            this.stringProperty.set(finalString);
            return finalString;
        }
    }
}

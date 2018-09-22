package GameEngine;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Game {
    private int target;
    private Board board;
    private ArrayList<Player> players;
    private Boolean isStarted;
    private Integer currentPlayerIndex;
    private Date startDate;
    private Player winnerPlayer;
    private History history;
    private SimpleStringProperty duration;
    private Date currentDate;
    private String variant;

    public int getTarget() { return target; }

    public Board getBoard(){ return board; }

    public ArrayList<Player> getPlayers() { return players; }

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
        return this.board.isTargetReached(players, this.target, this.variant);
    }

    public void start(ArrayList<Player> players){
        this.players = players;
        this.players.get(0).setCurrentTurn(true);
        this.board.addPlayers(this.players);

        this.isStarted = true;
        this.startDate = new Date();
    }

    public Boolean makeTurn(){
        TurnRecord turnRecord = this.players.get(this.currentPlayerIndex).makeTurn(this.board);
        this.history.pushTurn(turnRecord);
        if (this.isEndWithWinner()){
            this.winnerPlayer = this.players.get(this.currentPlayerIndex);
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

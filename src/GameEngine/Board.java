package GameEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Board {
    private int rows;
    private int columns;
    private HashMap<Integer, String> playersDiscTypeMap;
    private ArrayList<ArrayList<Integer>> cells;
    private Integer playersCount;

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public ArrayList<ArrayList<Integer>> getCells() {
        return cells;
    }

    public Board(int rows, int columns){
        this.rows = rows;
        this.columns = columns;

        this.playersDiscTypeMap = new HashMap<>();
        this.playersDiscTypeMap.put(0, "_");

        this.clearBoard();
    }

    private void clearBoard(){
        this.cells = new ArrayList<>();
        for (int i = 0; i < this.columns; i++) {
            ArrayList<Integer> column = new ArrayList<Integer>();
            for (int j = 0; j < this.rows; j++) {
                column.add(0);
            }
            this.cells.add(column);
        }
    }

    private Boolean canInsert(Integer column){
        return !this.getAvailableIndexInColumn(column).equals(-1);
    }

    private Boolean canPopOut(Integer column, Player player){
        return this.cells.get(column).get(this.cells.get(column).size() - 1).equals(player.getId());
    }

    private Integer getAvailableIndexInColumn(int column){
        return this.cells.get(column).lastIndexOf(0);
    }

    private Integer countAvailableCells(){
        Integer count = 0;
        for (ArrayList<Integer> column: this.cells){
            for (Integer cellContent: column){
                if (cellContent.equals(0)) count+=1;
            }
        }
        return count;
    }

    private Boolean isTargetInSequence(List<Player> players, String sequence, Integer target){
        for (Player player: players){
            if (sequence.matches(String.format(".*%s{%d}.*", this.playersDiscTypeMap.get(player.getId()), target))) {
                return true;
            }
        }
        return false;
    }

    private Integer getCellContent(Integer row, Integer column){
        return this.cells.get(column).get(row);
    }

    public HashMap<Integer, String> getPlayersDiscTypeMap() {
        return playersDiscTypeMap;
    }

    public Boolean isTargetReachedRegular(List<Player> players, Integer target){
        // check row
        String sequenceToCheck = "";
        for (int i = 0; i < this.rows; i++) {
            sequenceToCheck = "";
            for (ArrayList<Integer> column: this.cells){
                sequenceToCheck += this.playersDiscTypeMap.get(column.get(i));
            }
            if (this.isTargetInSequence(players, sequenceToCheck, target)) return true;
        }

        // check column
        for (ArrayList<Integer> column: this.cells){
            sequenceToCheck = "";
            for (Integer cellContent: column){
                sequenceToCheck += this.playersDiscTypeMap.get(cellContent);
            }
            if (this.isTargetInSequence(players, sequenceToCheck, target)) return true;
        }

        String boardAsLongString = "";
        for (int i = 0; i < this.rows; i++) {
            for (ArrayList<Integer> column: this.cells){
                boardAsLongString += this.playersDiscTypeMap.get(column.get(i));
            }
        }

        for (int i = 0; i < boardAsLongString.length(); i++) {
            sequenceToCheck = "";
            for (int j = i; j < boardAsLongString.length(); j+=this.columns+1) {
                sequenceToCheck += boardAsLongString.charAt(j);
            }
            if (this.isTargetInSequence(players, sequenceToCheck, target)) return true;
            sequenceToCheck = "";
            for (int j = i; j < boardAsLongString.length(); j+=this.columns-1) {
                sequenceToCheck += boardAsLongString.charAt(j);
            }
            if (this.isTargetInSequence(players, sequenceToCheck, target)) return true;
        }

        return false;
    }

    public Boolean isTargetReachedCircular(List<Player> players, Integer target){
        String sequenceToCheck = "";
        for (int i = 0; i < this.rows; i++) {
            sequenceToCheck = "";
            for (ArrayList<Integer> column: this.cells){
                sequenceToCheck += this.playersDiscTypeMap.get(column.get(i));
            }
            sequenceToCheck = sequenceToCheck + sequenceToCheck;
            if (this.isTargetInSequence(players, sequenceToCheck, target)) return true;
        }

        for (ArrayList<Integer> column: this.cells){
            sequenceToCheck = "";
            for (Integer cellContent: column){
                sequenceToCheck += this.playersDiscTypeMap.get(cellContent);
            }
            sequenceToCheck = sequenceToCheck + sequenceToCheck;
            if (this.isTargetInSequence(players, sequenceToCheck, target)) return true;
        }

        String boardAsLongString = "";
        for (int i = 0; i < this.rows; i++) {
            for (ArrayList<Integer> column: this.cells){
                boardAsLongString += this.playersDiscTypeMap.get(column.get(i));
            }
        }

        for (int i = 0; i < boardAsLongString.length(); i++) {
            sequenceToCheck = "";
            for (int j = i; j < boardAsLongString.length(); j+=this.columns+1) {
                sequenceToCheck += boardAsLongString.charAt(j);
            }
            if (this.isTargetInSequence(players, sequenceToCheck, target)) return true;
            sequenceToCheck = "";
            for (int j = i; j < boardAsLongString.length(); j+=this.columns-1) {
                sequenceToCheck += boardAsLongString.charAt(j);
            }
            if (this.isTargetInSequence(players, sequenceToCheck, target)) return true;
        }

        return false;
    }

    public Boolean isTargetReached(List<Player> players, Integer target, String variant){
        if (variant.equals("Circular")) return isTargetReachedCircular(players, target);
        return isTargetReachedRegular(players, target);
    }

    public Boolean isFull(){
        return this.countAvailableCells().equals(0);
    }

    public void addPlayers(List<Player> players){
        int i = 0;
        for (Player player: players){
//            String lastCharOfDiscType = Character.toString(player.getDiscType().charAt(player.getDiscType().length() - 1));
            this.playersDiscTypeMap.put(player.getId() + 1, String.format("%d", i + 1));
            i++;
        }
        this.playersCount = players.size();
    }

//    private Integer idToDigit(String playerId){
//
//    }

    public TurnRecord putDisc(Player player, int column){
        TurnRecord turnRecord = null;
        if (this.canInsert(column)){
            this.cells.get(column).set(this.getAvailableIndexInColumn(column), player.getId() + 1);
            turnRecord = new TurnRecord(player, column, false);
        }
        return turnRecord;
    }

    public TurnRecord popOut(Player player, int columnNumber){
        TurnRecord turnRecord = null;
        if (this.canPopOut(columnNumber, player)){
            ArrayList<Integer> column = this.cells.get(columnNumber);
            for (int i = column.size() - 1; i > 0; i--) {
                column.set(i,column.get(i-1));
            }
            column.set(0,0);
            turnRecord = new TurnRecord(player, columnNumber, true);
        }
        return turnRecord;
    }

    @Override
    public String toString() {
        String boardString = "";
        for (int i = 0; i < this.rows; i++) {
            String padding = "  ";
            if (this.rows - i > 9){
                padding = " ";
            }
            boardString += String.format("%d%s", this.rows - i, padding);
            for (ArrayList<Integer> column: this.cells){

                boardString += this.playersDiscTypeMap.get(column.get(i)) + "  ";
            }
            boardString += "\n";
        }
        boardString += "   ";
        for (int i = 0; i < this.columns; i++) {
            String padding = "  ";
            if (i + 1 > 9){
                padding = " ";
            }
            boardString += String.format("%d%s", i + 1, padding);
        }

        return boardString;
    }
}

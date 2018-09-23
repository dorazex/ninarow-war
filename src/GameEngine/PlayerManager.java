package GameEngine;


/**
 * Created by moran on 03/08/2016.
 */
public class PlayerManager {

    public enum PlayerType {
        Human,
        Computer,
        Spectator;
    }

    private final String name;
    private final PlayerType playerType;
    private int turnsCount = 0;
    private Board board; //TODO : copy


    PlayerManager(String name, Board board, PlayerType playerType) {
        this.name = name;
        this.board = board;
        this.playerType = playerType;
    }

    //region getters

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
    //endregion

    @Override
    public String toString(){
        return String.format("%1s - %2s", playerType, name);
    }

    //region package public methods


    boolean doMove(Integer column, Boolean isPopOut) {
        boolean retValue = false;
//        board.putDisc(this, column);
        turnsCount++;
        return  retValue;
    }

    void runComputerPlayerMove(){
//        RandomMoveGenerator.RandomChoice randomChoice = randomMoveGenerator.drawMoveOrUndo();
//        if(randomChoice == RandomMoveGenerator.RandomChoice.Move){
//            LinkedList<Triplet<Integer, Integer, Board.BoardSign>> moves = randomMoveGenerator.makeRandomMoves();
//            if(moves != null) {
//                doMove(moves);
//            }
//        }
//        else if(randomChoice == RandomMoveGenerator.RandomChoice.Undo){
//            undoMove();
//        }
    }

    //endregion

}

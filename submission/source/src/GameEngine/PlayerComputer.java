package GameEngine;

import java.util.Random;

import static java.lang.Thread.sleep;

public class PlayerComputer extends PlayerCommon {
    public PlayerComputer(Integer id, String name, String discType){
        super(id, name, discType);
    }

    @Override
    public TurnRecord makeTurn(Board board) {
        try {
            Thread.sleep(500);   // Delay thread
        } catch (Exception e) {
            // Do nothing with errors
        }


        Integer chosenColumn = new Random().nextInt(board.getColumns());
        TurnRecord turnRecord = board.putDisc(this, chosenColumn);
        Integer tries = 1;
        while (turnRecord == null && tries <= board.getColumns()){
            chosenColumn = (chosenColumn + 1) % board.getColumns();
            turnRecord = board.putDisc(this, chosenColumn);
            tries += 1;
        }
        return turnRecord;
    }

    @Override
    public String toString() {
        return String.format("%s%s", "Computer\t\t", super.toString());
    }
}

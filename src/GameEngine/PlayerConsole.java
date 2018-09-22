package GameEngine;

import java.util.Scanner;

public class PlayerConsole extends PlayerCommon {
    private Scanner scanner;

    public PlayerConsole(Integer id, String name, String discType, Scanner scanner){
        super(id, name, discType);
        this.scanner = scanner;
    }

    @Override
    public TurnRecord makeTurn(Board board) {
        System.out.print("Please choose column for disc (then hit <Enter>): ");
        Integer chosenColumn;
        try {
            chosenColumn = Integer.parseInt(this.scanner.nextLine()) - 1;
        } catch (Exception e){
            System.out.println("Input must be an integer. Please play again...");
            return makeTurn(board);
        }
        if (chosenColumn < 0 || chosenColumn > board.getColumns()){
            System.out.println("Column must be in board range. Please play again...");
            return makeTurn(board);
        }

        TurnRecord turnRecord = board.putDisc(this, chosenColumn);
        if (turnRecord == null){
            System.out.println("Chosen column is full. Please play again...");
            return this.makeTurn(board);
        }
        this.setTurnsCount(this.getTurnsCount() + 1);
        return turnRecord;
    }
}

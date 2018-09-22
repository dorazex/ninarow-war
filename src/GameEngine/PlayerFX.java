package GameEngine;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class PlayerFX extends PlayerCommon {
    private String id;
    private String discType;
    private String color;


    public PlayerFX(Integer id, String name, String color){
        super(id, name, color);
        this.color = color;
    }

    @Override
    public TurnRecord makeTurn(Board board) {
        throw new NotImplementedException();
    }

    public TurnRecord makeTurnFX(Board board, Integer column, Boolean isBottom) {
        TurnRecord turnRecord;
        if (!isBottom) {
            turnRecord = board.putDisc(this, column);
        } else {
            turnRecord = board.popOut(this, column);
        }
        return turnRecord;
    }

    @Override
    public String toString() {
        return String.format("%s%s", "Human\t\t", super.toString());
    }
}

package GameEngine;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class PlayerWeb extends PlayerCommon {
    private String id;
    private String discType;
    private String color;


    public PlayerWeb(Integer id, String name, String color){
        super(id, name, color);
        this.color = color;
    }

    @Override
    public TurnRecord makeTurn(Board board) {
        throw new NotImplementedException();
    }

    @Override
    public String toString() {
        return String.format("%s%s", "Web\t\t", super.toString());
    }
}

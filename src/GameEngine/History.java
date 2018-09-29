package GameEngine;

import java.util.ArrayList;

public class History {
    private ArrayList<TurnRecord> turnRecords;

    public History(){
        this.turnRecords = new ArrayList<TurnRecord>();
    }

    public void clear(){
        this.turnRecords.clear();
    }

    public void pushTurn(TurnRecord turnRecord){
        this.turnRecords.add(turnRecord);
    }

    public TurnRecord popTurn(){
        return this.turnRecords.remove(this.turnRecords.size() - 1);
    }

    @Override
    public String toString() {
        String returnString = "GameEngine.History:\n--------\n";
        for (TurnRecord turnRecord: this.turnRecords){
            returnString += turnRecord.toString() + "\n";
        }
        return returnString;
    }
}

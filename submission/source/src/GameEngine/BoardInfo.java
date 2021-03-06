package GameEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class BoardInfo {

    private ArrayList<ArrayList<Integer>> cells;
    private Integer rowsCount;
    private Integer columnsCount;
    private HashMap<Integer, String> playersDiscTypeMap;
    private Integer playersCount;
    private Boolean isPopOut;


    public BoardInfo(ArrayList<ArrayList<Integer>> cells, HashMap<Integer, String> playersDiscTypeMap, Boolean isPopOut) {
        this.cells = cells;
        this.columnsCount = this.cells.size();
        this.rowsCount = this.cells.get(0).size();
        this.playersDiscTypeMap = playersDiscTypeMap;
        this.playersCount = this.playersDiscTypeMap.size();
        this.isPopOut = isPopOut;
    }
}

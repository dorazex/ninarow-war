package GameEngine;

import com.google.gson.internal.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by moran on 10/7/2016.
 */
public class SimpleBoard {

    private ArrayList<ArrayList<Integer>> cells;
    private Integer rowsCount;
    private Integer columnsCount;
    private HashMap<Integer, String> playersDiscTypeMap;
    private Integer playersCount;


    public SimpleBoard(ArrayList<ArrayList<Integer>> cells, HashMap<Integer, String> playersDiscTypeMap) {
        this.cells = cells;
        this.columnsCount = this.cells.size();
        this.rowsCount = this.cells.get(0).size();
        this.playersDiscTypeMap = playersDiscTypeMap;
        this.playersCount = this.playersDiscTypeMap.size();
    }
}

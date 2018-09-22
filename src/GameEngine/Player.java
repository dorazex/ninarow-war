package GameEngine;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public interface Player {
    Integer getTurnsCount();
    SimpleIntegerProperty turnsCountProperty();
    SimpleStringProperty detailsProperty();
    Integer getId();
    String getDiscType();
    String getName();
    TurnRecord makeTurn(Board board);
    Boolean getIsCurrentTurn();
    void setCurrentTurn(Boolean currentTurn);
}

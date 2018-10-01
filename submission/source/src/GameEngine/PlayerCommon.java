package GameEngine;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public abstract class PlayerCommon implements Player {
    private SimpleIntegerProperty turnsCount;
    private SimpleStringProperty details;
    private Integer id;
    private String name;
    private String discType;
    private Boolean isCurrentTurn;

    public PlayerCommon(Integer id, String name, String discType){
        this.turnsCount = new SimpleIntegerProperty();
        this.turnsCount.set(0);
        this.id = id;
        this.name = name;
        this.discType = discType;
        this.isCurrentTurn = false;

        this.details = new SimpleStringProperty();
        this.details.set(this.toString());
    }

    @Override
    public Integer getTurnsCount() {
        return turnsCount.get();
    }

    @Override
    public Boolean getIsCurrentTurn() {
        return isCurrentTurn;
    }

    public void setCurrentTurn(Boolean currentTurn) {
        isCurrentTurn = currentTurn;
    }

    @Override
    public SimpleIntegerProperty turnsCountProperty() {
        return turnsCount;
    }

    @Override
    public SimpleStringProperty detailsProperty() {
        return details;
    }

    public void setTurnsCount(Integer turns) {
        turnsCount.set(turns);
        this.details.set(this.toString());
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDiscType() {
        return this.discType;
    }

    @Override
    public String toString() {
        return String.format("%s (#%d): \tdisc=%s, \t\tturns=%d\n", this.name, this.id, this.discType, this.getTurnsCount());
    }
}

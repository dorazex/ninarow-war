package GameEngine;

import com.google.gson.internal.Pair;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by moran on 8/28/2016.
 */
public class GameManager {

    public class GameDetails {
        //holds info to be sent with polling from game servlet
        //simple object for gsoning to client

        private boolean isActivePlayer = false;
        private String currentPlayerName;
        private int totalRounds;
        private int currentRound;
        private int numOfTurns; //relevant only to current player
        private List<PlayerManager> playerList; //player type, name, score of player
        private boolean isGameOver;

        GameDetails(GameManager gameManager, String username){

            if(gameManager.currentPlayer.getName().equals(username)){
                isActivePlayer = true;
            }

            totalRounds = roomInfo.getRounds();
            currentRound = gameManager.currentRound;
            numOfTurns = numOfOperationsInTurn;
            currentPlayerName = gameManager.currentPlayer.getName();
            //amountOfMoves = currentPlayer.getValue().getHistoryMoves().size(); //TODO this is a little stupid, you don't need this when not in your turn and it wasnt in ex2
            playerList = gameManager.makePlayerAndSpectatorList();

            isGameOver = gameManager.gameOver;
        }
    }

    //region data members
    private boolean gameRunningProperty = false;
    private String systemMessage;
    private PlayerManager currentPlayer;

    private Integer currentRound = 0;
    private Integer numOfOperationsInTurn = 0;

    private List<PlayerManager> players = new ArrayList<>();
    private int indexOfCurrentPlayer;

    // ex3 data
    private final RoomInfo roomInfo = new RoomInfo();
    private Board prototypeBoard;
    private List<String> spectators = new LinkedList<>();
    private ExecutorService computerMoveExecutor;
    private boolean gameOver;
    private boolean onePlayerReady = false;
    //endregion

    //region server related methods

    public List<PlayerManager> makePlayerAndSpectatorList() {

        Stream combinedStream = Stream.concat(players.stream(), spectators.stream());

        return (List<PlayerManager>) combinedStream
                .map(item -> {
                        return new PlayerManager(((PlayerManager) item).getName(), getBoard(), ((PlayerManager) item).getPlayerType());

                })
                .collect(Collectors.toList());

//        return players
//                .stream()
//                .map(player -> new Triplet<>(player.getPlayerType(), player.getName(), player.getScore()))
//                .collect(Collectors.toList());
    }


    public boolean addSpectator(String name){
        boolean spectatorAdded = false;
        if(getPlayer(name) == null){    //only if he isn't already playing
            spectators.add(name);
            spectatorAdded = true;
            roomInfo.addSpectator();
        }
        return spectatorAdded;
    }

    public void removeSpectator(String name){
        roomInfo.removeSpectator();
        spectators.removeIf(e -> Objects.equals(e, name));
    }

    public synchronized void removePlayer(String username) {  //sync to prevent race condition between two players that exit together
        if(!gameRunningProperty){
            removePlayerFromList(username);
        }
        else{
            //check if player is current player
            if(players.size() > 1){ // critical condition
                //leave one user, technical win for the other one
                if(Objects.equals(currentPlayer.getName(), username)){
                    setNextPlayer(); //move to the other player and then delete the previous player
                }
                removePlayerFromList(username);
                if (players.size() == 1) {
                    gameOver = true;
                    systemMessage = String.format("Game over. only %1s left.", currentPlayer.getName());
                }
            }
            else {
                //last user
                gameRunningProperty = false;
                removePlayerFromList(username);
                resetGame();    //reset the game when all players left
            }
        }
    }


    private void removePlayerFromList(String username) {
        roomInfo.decreaseOnlinePlayers();
        Iterator<PlayerManager> it = players.iterator();
        while(it.hasNext()){
            if(Objects.equals(it.next().getName(), username)){
                it.remove();
            }
        }
    }

    public synchronized boolean addPlayer(String organizer, PlayerManager.PlayerType playerType) {
        PlayerManager player = new PlayerManager(organizer, prototypeBoard, playerType);

        if(roomInfo.getOnlinePlayers() < roomInfo.getTotalPlayers() && !gameRunningProperty){
            players.add(player);
            roomInfo.increaseOnlinePlayers();
            if(roomInfo.getOnlinePlayers() == 1){
                currentPlayer = players.get(0);
            }
            if(roomInfo.getOnlinePlayers() == roomInfo.getTotalPlayers()){
                gameRunningProperty = true;
                gameOver = false;
                currentRound = 1;
            }

            return true;
        }

        return false;
    }

    public boolean checkUniqueUser(String username) { //TODO test this works
        return players
                .stream()
                .filter(player -> Objects.equals(player.getName(), username))
                .collect(Collectors.toList()).size() == 1;
    }

    //endregion

    //region get/setters

    public synchronized void onePlayerReady() {
        //if at least one player starts to ask for gamedetails then it's time to start the game in case the first player is a computer

        if (gameRunningProperty && currentPlayer.getPlayerType() == PlayerManager.PlayerType.Computer && !this.onePlayerReady){
            playAutoMoves();    // start the game in case first player is computer
        }
        this.onePlayerReady = true; //so the above will happen only once
    }

    public int getNmOfAllMoves(String nameOfPlayer) {
        return getPlayer(nameOfPlayer).getNumOfAllMoves();
    }

    void setComputerMoveExecutor(ExecutorService computerMoveExecutor) {
        this.computerMoveExecutor = computerMoveExecutor;
    }

    public Board getCurrentPlayerBoard() {
        return currentPlayer.getBoard();
    }

    public Board getPrototypeBoard() {
        return prototypeBoard;
    }

    public String getSystemMessage() { return systemMessage;}

    public GameDetails getGameDetails(String username) {
        return new GameDetails(this, username);
    }

    public PlayerManager getPlayer(String organizer) {
        return players.stream()				                // Convert to steam
                .filter(x -> organizer.equals(x.getName()))	// we want organizer only
                .findAny()									// If 'findAny' then return found
                .orElse(null);								// If not found, return null
    }

    RoomInfo getRoomInfo() {
        return roomInfo;
    }

    public void setOrganizer(String organizer) {
        roomInfo.setOrganizer(organizer);
    }

    void setPrototypeBoard(Board board){
        prototypeBoard = board;
        roomInfo.setBoardSize(new Pair<Integer, Integer>(board.getRows(), board.getColumns()));
    }

    public Board getBoard () { return prototypeBoard ;}

    void setGameTitle(String gameTitle) {
        roomInfo.setGameTitle(gameTitle);
    }

    void setTotalPlayers(int totalPlayers) {
        roomInfo.setTotalPlayers(totalPlayers);
    }

    public boolean getGameRunning() {
        return gameRunningProperty;
    }

    void setRounds(int moves) {
        roomInfo.setRounds(moves);
    }

    public List<PlayerManager> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerManager> players) {
        this.players = players;
    }

    public synchronized void setNextPlayer() {  //synced in case one player exits at the same time as another pressing turn done

        numOfOperationsInTurn = 0;
        indexOfCurrentPlayer++;
        if (indexOfCurrentPlayer >= players.size()) {
            indexOfCurrentPlayer = 0;
            currentRound++;
        }

        currentPlayer = players.get(indexOfCurrentPlayer);

        if (currentRound > roomInfo.getRounds()) {
            currentRound--;
            //gameRunningProperty.setValue(false);
            gameOver = true;
            systemMessage  = "Game over. Ran out of rounds.";
        }

        playAutoMoves(); // will play only if computer
    }

    //endregion

    //region main game controls

    public boolean doMove(Integer column, Boolean isPopOut) {
        if(numOfOperationsInTurn >= 2){
            return false;   //can get here if polling to disable the buttons is delayed
                            //so need to notify client that do move didn't succeed
        }

        boolean isPlayerWin = currentPlayer.doMove(column, isPopOut);

        if (numOfOperationsInTurn < 0)
            numOfOperationsInTurn = 1;
        else numOfOperationsInTurn++;

        if (isPlayerWin) {
            //gameRunningProperty.setValue(false);
            gameOver = true;
            systemMessage = String.format("Game over. %1s won.", currentPlayer.getName());
        }

        return true;
    }
    //endregion

    public boolean compareUserToCurrentPlayer(String username){
        return username.equals(currentPlayer.getName());
    }

    private void playAutoMoves(){
        if(currentPlayer.getPlayerType() == PlayerManager.PlayerType.Computer && gameRunningProperty && !gameOver){
            computerMoveExecutor.execute(() -> {
                Integer maxOperationsInTurn = 2;
                while(numOfOperationsInTurn < maxOperationsInTurn){
                    currentPlayer.runComputerPlayerMove();
                    numOfOperationsInTurn++;
                }
                setNextPlayer();
            });
        }
    }

    private void resetGame() {
        gameRunningProperty = false;
        gameOver = false;
        onePlayerReady = false;
        systemMessage = null;
        currentPlayer = null;
        currentRound = 0;
        if(players != null) {
            players.clear();
        }
        spectators.clear();
        indexOfCurrentPlayer = 0;
        numOfOperationsInTurn = 0;
        roomInfo.clearInfo();
    }
}

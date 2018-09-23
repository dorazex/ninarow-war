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
        private int turnsCount; //relevant only to current player
        private List<PlayerManager> playerList; //player type, name, score of player
        private boolean isGameOver;

        GameDetails(GameManager gameManager, String username){

            if(gameManager.currentPlayer.getName().equals(username)){
                isActivePlayer = true;
            }

            turnsCount = gameManager.currentPlayer.getTurnsCount();
            currentPlayerName = gameManager.currentPlayer.getName();
            playerList = gameManager.makePlayerAndSpectatorList();

            isGameOver = gameManager.isGameOver;
        }
    }

    //region data members
    private boolean gameRunningProperty = false;
    private String systemMessage;
    private PlayerManager currentPlayer;

    private Integer currentRound = 0;

    private List<PlayerManager> players = new ArrayList<>();
    private int indexOfCurrentPlayer;

    // ex3 data
    private final RoomInfo roomInfo = new RoomInfo();
    private Board board;
    private ExecutorService computerMoveExecutor;
    private boolean isGameOver;
    private boolean onePlayerReady = false;
    //endregion

    //region server related methods

    public List<PlayerManager> makePlayerAndSpectatorList() {

        Stream playersStream = players.stream();

        return (List<PlayerManager>) playersStream
                .map(item -> {
                        return new PlayerManager(((PlayerManager) item).getName(), getBoard(), ((PlayerManager) item).getPlayerType());

                })
                .collect(Collectors.toList());

//        return players
//                .stream()
//                .map(player -> new Triplet<>(player.getPlayerType(), player.getName(), player.getScore()))
//                .collect(Collectors.toList());
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
                    isGameOver = true;
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
        PlayerManager player = new PlayerManager(organizer, board, playerType);

        if(roomInfo.getOnlinePlayers() < roomInfo.getTotalPlayers() && !gameRunningProperty){
            players.add(player);
            roomInfo.increaseOnlinePlayers();
            if(roomInfo.getOnlinePlayers() == 1){
                currentPlayer = players.get(0);
            }
            if(roomInfo.getOnlinePlayers() == roomInfo.getTotalPlayers()){
                gameRunningProperty = true;
                isGameOver = false;
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
        return getPlayer(nameOfPlayer).getTurnsCount();
    }

    public void setComputerMoveExecutor(ExecutorService computerMoveExecutor) {
        this.computerMoveExecutor = computerMoveExecutor;
    }

    public Board getCurrentPlayerBoard() {
        return currentPlayer.getBoard();
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

    public void setBoard(Board board){
        this.board = board;
        roomInfo.setRows(board.getRows());
        roomInfo.setColumns(board.getColumns());
    }

    public Board getBoard () { return board;}

    public void setGameTitle(String gameTitle) {
        roomInfo.setGameTitle(gameTitle);
    }

    public void setTotalPlayers(int totalPlayers) {
        roomInfo.setTotalPlayers(totalPlayers);
    }

    public boolean getGameRunning() {
        return gameRunningProperty;
    }

    public void setRounds(int moves) {
        roomInfo.setRounds(moves);
    }

    public List<PlayerManager> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerManager> players) {
        this.players = players;
    }

    public synchronized void setNextPlayer() {  //synced in case one player exits at the same time as another pressing turn done

        indexOfCurrentPlayer++;
        if (indexOfCurrentPlayer >= players.size()) {
            indexOfCurrentPlayer = 0;
            currentRound++;
        }

        currentPlayer = players.get(indexOfCurrentPlayer);

        if (currentRound > roomInfo.getRounds()) {
            currentRound--;
            //gameRunningProperty.setValue(false);
            isGameOver = true;
            systemMessage  = "Game over. Ran out of rounds.";
        }

        playAutoMoves(); // will play only if computer
    }

    //endregion

    //region main game controls

    public boolean doMove(Integer column, Boolean isPopOut) {

        boolean isPlayerWin = currentPlayer.doMove(column, isPopOut);

        if (isPlayerWin) {
            //gameRunningProperty.setValue(false);
            isGameOver = true;
            systemMessage = String.format("Game over. %1s won.", currentPlayer.getName());
        }

        return true;
    }
    //endregion

    public boolean compareUserToCurrentPlayer(String username){
        return username.equals(currentPlayer.getName());
    }

    private void playAutoMoves(){
        if(currentPlayer.getPlayerType() == PlayerManager.PlayerType.Computer && gameRunningProperty && !isGameOver){
            computerMoveExecutor.execute(() -> {
                currentPlayer.runComputerPlayerMove();
                setNextPlayer();
            });
        }
    }

    private void resetGame() {
        gameRunningProperty = false;
        isGameOver = false;
        onePlayerReady = false;
        systemMessage = null;
        currentPlayer = null;
        currentRound = 0;
        if(players != null) {
            players.clear();
        }
        indexOfCurrentPlayer = 0;
        roomInfo.clearInfo();
    }
}

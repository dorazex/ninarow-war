package GameEngine;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by moran on 9/30/2016.
 */
@WebListener
public class RoomsManager implements ServletContextListener {

    private final Map<Integer, Game> games = new HashMap<>();
    private final List<SimplePlayer> onlinePlayers = new ArrayList<>();
    private final List<RoomInfo> roomList = new LinkedList<>(); //simplified game manager for converting to json
    private int count = 0;

    // will run the computer move one after the other
    // to prevent recursive calls if all players are computer
    private ExecutorService computerMoveExecutor = Executors.newSingleThreadExecutor();

    public boolean isPlayerExists(String name) {
        for (SimplePlayer player : onlinePlayers) {
            if(player.getName().equals(name))
                return true;
        }
        return false;
    }

    public boolean isPlayerExists(String name, PlayerManager.PlayerType type) {
        for (SimplePlayer player : onlinePlayers) {
            if(player.getName().equals(name) && player.getPlayerType() == type)
                return true;
        }
        return false;
    }

    public Map<Integer, Game> getGames() {return games;}

    public void addPlayer(String name, PlayerManager.PlayerType playerType) {
        onlinePlayers.add(new SimplePlayer(name, playerType));
    }

    public synchronized void addGame(Game game) {
        count++;
        games.put(count, game);
        game.setComputerMoveExecutor(computerMoveExecutor);
        roomList.add(game.getRoomInfo());
        game.getRoomInfo().setRoomIdentifier(count);
    }

    public List<SimplePlayer> getPlayerList() { return onlinePlayers;}

    public List<RoomInfo> getRoomList() {
        return roomList;
    }

    public void removePlayer(String organizer) {
        Iterator<SimplePlayer> it = onlinePlayers.iterator();
        while (it.hasNext()){
            if(Objects.equals(it.next().getName(), organizer)){
                it.remove();
            }
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        servletContextEvent.getServletContext().setAttribute("RoomsManager", this);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        computerMoveExecutor.shutdown();
        computerMoveExecutor.shutdownNow(); //clean up for the executor service when server is shutting down
    }
}

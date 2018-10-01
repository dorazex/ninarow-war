package GameEngine;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@WebListener
public class RoomsManager implements ServletContextListener {

    private final Map<Integer, Game> games = new HashMap<>();
    private final List<SimplePlayer> onlinePlayers = new ArrayList<>();
    private final List<RoomInfo> roomList = new LinkedList<>();
    private int count = 0;
    private ExecutorService computerMoveExecutor = Executors.newSingleThreadExecutor();

    public List<SimplePlayer> getPlayerList() { return onlinePlayers;}
    public List<RoomInfo> getRoomList() {
        return roomList;
    }
    public Map<Integer, Game> getGames() {return games;}

    public boolean isPlayerExists(String name) {
        for (SimplePlayer player : onlinePlayers) {
            if(player.getName().equals(name))
                return true;
        }
        return false;
    }
    public boolean isPlayerExists(String name, PlayerInfo.PlayerType type) {
        for (SimplePlayer player : onlinePlayers) {
            if(player.getName().equals(name) && player.getPlayerType() == type)
                return true;
        }
        return false;
    }

    public synchronized void addGame(Game game) {
        count++;
        games.put(count, game);
        game.setComputerMoveExecutor(computerMoveExecutor);
        roomList.add(game.getRoomInfo());
        game.getRoomInfo().setRoomIdentifier(count);
    }

    public void addPlayer(String name, PlayerInfo.PlayerType playerType) {
        onlinePlayers.add(new SimplePlayer(name, playerType));
    }

    public void removePlayer(String userName) {
        onlinePlayers.removeIf(simplePlayer -> Objects.equals(simplePlayer.getName(), userName));
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

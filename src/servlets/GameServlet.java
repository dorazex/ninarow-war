package servlets;

import GameEngine.*;
import com.google.gson.internal.Pair;
import servlets.utils.ServletUtils;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


@WebServlet(name = "GameServlet", urlPatterns = {"/game"})
public class GameServlet extends HttpServlet {

    private Gson gson = new Gson();
    private RoomsManager roomsManager;

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String requestType = request.getParameter("requestType");

        switch (requestType) {
            case Constants.GAMEDETAILS:
                handleGameDetails(request, response);
                break;
            case Constants.BOARD:
                handleBoard(request, response);
                break;
            case Constants.TURN:
                handleTurn(request, response);
                break;
            case Constants.COMPUTER_TURN:
                handleComputerTurn(request, response);
                break;
            case Constants.CHECK_GAME_START:
                handleCheckGameStart(request, response);
                break;
            case Constants.SYSTEM_MESSAGE:
                handleSystemMessage(request, response);
                break;
            case Constants.LEAVE_ROOM:
                handleLeaveRoom(request, response);
                break;
            case Constants.RESET_GAME:
                handleResetGame(request, response);
                break;
        }
    }

    private void handleResetGame(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Game game = getGame(request);
        game.resetGame();
        String responseString = gson.toJson("Redirecting to rooms page");
        response.getWriter().write(responseString);
    }

    private void handleSystemMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Game game = getGame(request);
        String responseString = gson.toJson("... And the winner is: " + game.getWinnerPlayer().getName());
        response.getWriter().write(responseString);
    }

    private void handleLeaveRoom(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Game game = getGame(request);
        Map<String, String> result = new HashMap<>();
        try {
            Boolean isGameOver = game.removePlayer(request.getParameter("organizer"));
            if (isGameOver){
                String responseString = gson.toJson("... And the winner is: " + game.getWinnerPlayer().getName());
                response.getWriter().write(responseString);
                return;
            }
        } catch (Exception e){
        }
        result.put("redirect", "rooms.html");
        String json = gson.toJson(result);
        response.getWriter().write(json);
    }

    private void handleTurn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Game game = getGame(request);

        Integer column = Integer.parseInt(request.getParameter("column"));
        String organizer = request.getParameter("organizer");
        String isPopout = request.getParameter("isPopOut");

        Player player = game.getPlayer(organizer);

        TurnRecord turnRecord;
        if (isPopout.equals("true")){
            turnRecord = game.getBoard().popOut(player, column);
            if (turnRecord==null){
                String responseString = gson.toJson("Cannot pop out: chosen column's bottom disc is not yours");
                response.getWriter().write(responseString);
                return;
            }
        } else{
            turnRecord = game.getBoard().putDisc(player, column);
            if (turnRecord==null){
                String responseString = gson.toJson("Cannot put disc: column is full");
                response.getWriter().write(responseString);
                return;
            }
        }
        game.getHistory().pushTurn(turnRecord);
        Boolean isGameOver = game.finalizeTurn();

        Board board = game.getBoard();
        SimpleBoard responseBoard = new SimpleBoard(board.getCells(), board.getPlayersDiscTypeMap(), game.getVariant().equals("Popout"));

        String boardJson = gson.toJson(responseBoard);
        PrintWriter out = response.getWriter();
        out.println(boardJson);
        out.flush();
    }

    private void handleComputerTurn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Game game = getGame(request);

        String organizer = request.getParameter("organizer");

        Player player = game.getPlayer(organizer);
        if (!player.getName().equals(game.getCurrentPlayer().getName())){
            if(request.getParameter("playerType").equals("Computer")){
                return;
            } else{
                String responseString = gson.toJson("Wait for your turn");
                response.getWriter().write(responseString);
                return;
            }
        }

        Board board = game.getBoard();
        TurnRecord turnRecord = player.makeTurn(board);
        game.getHistory().pushTurn(turnRecord);
        Boolean isGameOver = game.finalizeTurn();

        SimpleBoard responseBoard = new SimpleBoard(board.getCells(), board.getPlayersDiscTypeMap(), game.getVariant().equals("Popout"));
        String boardJson = gson.toJson(responseBoard);

        PrintWriter out = response.getWriter();
        out.println(boardJson);
        out.flush();
    }


    private void handleGameDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Game game = getGame(request);
        game.onePlayerReady();
        Game.GameDetails gameDetails = game.getGameDetails(request.getParameter("organizer"));
        String json = gson.toJson(gameDetails);
        response.getWriter().write(json);
    }


    private void handleCheckGameStart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Game game = getGame(request);
        Pair<Boolean, List<PlayerInfo>> result;
        result = new Pair<>(game.getGameRunning(), game.makePlayersList());
        String responseString = gson.toJson(result);
        response.getWriter().write(responseString);
    }

    private void handleBoard(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Game game = getGame(request);
        Board board;

        board = game.getBoard();

        SimpleBoard responseBoard = new SimpleBoard(board.getCells(), board.getPlayersDiscTypeMap(), game.getVariant().equals("Popout"));

        String boardJson = gson.toJson(responseBoard);
        PrintWriter out = response.getWriter();
        out.println(boardJson);
        out.flush();
    }

    private Game getGame(HttpServletRequest request){
        int roomId = Integer.parseInt(request.getParameter("roomid"));
        if(roomsManager == null){
            roomsManager = ServletUtils.getRoomsManager(getServletContext());
        }
        return roomsManager.getGames().get(roomId);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}



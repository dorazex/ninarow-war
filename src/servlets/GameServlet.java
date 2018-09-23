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
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */

    //region servlets requests and handlers

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
            case Constants.CHECK_GAME_START:
                handleCheckGameStart(request, response);
                break;
            case Constants.SYSTEM_MESSAGE:
                handleSystemMessage(request, response);
                break;
            case Constants.LEAVE_ROOM:
                handleLeaveRoom(request, response);
                break;
            case Constants.NUM_OF_ALL_MOVES:
                handlenNmOfAllMoves(request, response);
                break;
        }
    }


    private void handlenNmOfAllMoves(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Game game = getGame(request);
        String responseString = gson.toJson(game.getTurnsCountOfUser(request.getParameter("organizer")));
        response.getWriter().write(responseString);
    }

    private void handleSystemMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Game game = getGame(request);
//        String responseString = gson.toJson(game.getSystemMessage());
        String responseString = gson.toJson("STUB SYSTEM MESSAGE");
        //getGameManager(request).resetGame();
        response.getWriter().write(responseString);
    }

    private void handleLeaveRoom(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Game game = getGame(request);
        Map<String, String> result = new HashMap<>();
        result.put("redirect", "rooms.html");
        String json = gson.toJson(result);
        response.getWriter().write(json);
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
//        Pair<Boolean, List<Triplet<PlayerManager.PlayerType, String,  String>>> result = new Pair<>(gameManager.getGameRunning(), gameManager.makePlayerAndSpectatorList());
        Pair<Boolean, List<PlayerManager>> result;
        result = new Pair<>(game.getGameRunning(), game.makePlayerAndSpectatorList());
        String responseString = gson.toJson(result);
        response.getWriter().write(responseString);
    }

//    private void handleDoMove(HttpServletRequest request, HttpServletResponse response, Board.BoardSign sign) throws IOException {
//        String username = request.getParameter("organizer");
//        Game game = getGame(request);
//        Map<String, Boolean> resultParameter = new HashMap<>();
//
//        if(!gameManager.compareUserToCurrentPlayer(username)){
//            resultParameter.put("isSuccessful", false); //case for a different user than current sending request
//        }
//        else {
//            LinkedList<Triplet<Integer, Integer, Board.BoardSign>> moves = new LinkedList<>();
//            Pair[] selectedCoords = gson.fromJson(request.getParameter("selectedCoords"), Pair[].class);
//
//            for (Pair coordinate : selectedCoords) {
//                moves.add(new Triplet<>(Integer.parseInt((String) coordinate.getKey()), Integer.parseInt((String) coordinate.getValue()), sign));
//            }
//
//            if (gameManager.doMove(moves)) {
//                resultParameter.put("isSuccessful", true);
//            } else {
//                resultParameter.put("isSuccessful", false);
//            }
//        }
//
//        String json = gson.toJson(resultParameter);
//        response.getWriter().write(json);
//    }

    private void handleBoard(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Game game = getGame(request);
        Board board;

        board = game.getBoard();

        SimpleBoard responseBoard = new SimpleBoard(board.getCells());

        String boardJson = gson.toJson(responseBoard);
        PrintWriter out = response.getWriter();
        out.println(boardJson);
        out.flush();
    }
//endregion

    private Game getGame(HttpServletRequest request){

        String x = request.getParameter("roomid");

        int roomId = Integer.parseInt(request.getParameter("roomid"));
        if(roomsManager == null){
            roomsManager = ServletUtils.getRoomsManager(getServletContext());
        }
        return roomsManager.getGames().get(roomId);
    }



// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}



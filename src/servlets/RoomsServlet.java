package servlets;

import GameEngine.*;
import com.google.gson.Gson;
import servlets.utils.ServletUtils;
import servlets.utils.SessionUtils;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@WebServlet(name = "RoomsServlet", urlPatterns = {"/rooms"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class RoomsServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

    private final Gson gson = new Gson();
    private RoomsManager roomsManager;

    private void processPostRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        String requestType = request.getParameter("requestType");

        if(Objects.equals(requestType, "fileUpload")){
            handleXMLFile(request, response);
        }
    }

    private void processGetRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String requestType = request.getParameter("requestType");

        switch (requestType) {
            case "userList":
                handleUserList(request, response);
                break;
            case "roomList":
                handleRoomList(request, response);
                break;
            case "enterRoom":
                handleEnterRoom(request, response);
                break;
            case "logout":
                handleLogout(request, response);
                break;
            case "spectateRoom":
                handleSpectateRoom(request, response);
                break;
        }
     }

    //region handlers

    private void handleSpectateRoom(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int roomid = Integer.parseInt(request.getParameter("roomid"));
        GameManager gameManager = getGameManager(request);
        Map<String, String> result = new HashMap<>();

        if(gameManager.getGameRunning()){
            result.put("error", "You're already playing in this room");
        }
        else{
            result.put("error", "Game hasn't started yet");
        }

        String json = gson.toJson(result);
        response.getWriter().write(json);
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if(roomsManager == null){
            roomsManager = ServletUtils.getRoomsManager(getServletContext());
        }
        roomsManager.removePlayer(request.getParameter("organizer"));
        SessionUtils.clearSession(request);
        Map<String, String> result = new HashMap<>();
        result.put("redirect", "index.html");
        Cookie[] x = request.getCookies();
        for (Cookie cookie : x) {
            cookie.setMaxAge(0);
        }

        String json = gson.toJson(result);
        response.getWriter().write(json);
    }

    private void handleEnterRoom(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int roomid = Integer.parseInt(request.getParameter("roomid"));
        GameManager gameManager = getGameManager(request);
        Map<String, String> result = new HashMap<>();

        String username = request.getParameter("organizer");

        if(gameManager.checkUniqueUser(request.getParameter("organizer"))){
            //user already exist, so can't register them, just let them go to their board
            result.put("error", "You're already playing in this room.");
        }
        else {
            //user doesn't exist so register them
            if (gameManager.addPlayer(request.getParameter("organizer"), PlayerManager.PlayerType.valueOf(request.getParameter("playerType")))) {
                // room isn't full
                result.put("redirect", "boardPage.html");
                Cookie roomIdCookie = new Cookie("roomid", Integer.toString(roomid));
                roomIdCookie.setPath("/");
                response.addCookie(roomIdCookie); // so the client side will remember his room id after redirect
            }
            else if(gameManager.getGameRunning()){
                result.put("error", "Game is already running");
            }
            else {
                // room is full
                result.put("error", "Room is full");
            }
        }
        String json = gson.toJson(result);
        response.getWriter().write(json);
    }

    private void handleRoomList(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        Cookie[] x = request.getCookies();
        PrintWriter out = response.getWriter();
        if(roomsManager == null){
            roomsManager = ServletUtils.getRoomsManager(getServletContext());
        }
        String playerListJson = gson.toJson(roomsManager.getRoomList());
        out.println(playerListJson);
        out.flush();
    }

    private void handleUserList(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        PrintWriter out = response.getWriter();
        if(roomsManager == null){
            roomsManager = ServletUtils.getRoomsManager(getServletContext());
        }
        String playerListJson = gson.toJson(roomsManager.getPlayerList());
        out.println(playerListJson);
        out.flush();
    }

    private void handleXMLFile(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Part file = request.getPart("XMLFile");
        GameManager gameManager = new GameManager();
        try {
//            xmlReader.loadXML(file.getInputStream(), gameManager, ServletUtils.getRoomsManager(getServletContext()));
            gameManager.setGameTitle("Game#1");
            gameManager.setTotalPlayers(2);
            gameManager.setBoard(new Board(4,4));
            gameManager.setOrganizer(request.getParameter("organizer"));    // set the organizer of the room
            ServletUtils.getRoomsManager(getServletContext()).addGameManager(gameManager);
        } catch (Exception e) {
            response.getWriter().write(e.getMessage());
        }
    }

    //endregion

    private GameManager getGameManager(HttpServletRequest request){ //TODO fix this duplication

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
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processGetRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processPostRequest(request, response);
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

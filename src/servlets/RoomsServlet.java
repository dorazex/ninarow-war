package servlets;

import GameEngine.*;
import XmlLoader.XmlLoader;
import com.google.gson.Gson;
import servlets.utils.ServletUtils;
import servlets.utils.SessionUtils;

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
        }
     }

    //region handlers


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
        Game game = getGame(request);
        Map<String, String> result = new HashMap<>();

        String username = request.getParameter("organizer");

        if(game.checkUniqueUser(username)){
            //user already exist, so can't register them, just let them go to their board
            result.put("error", "You're already playing in this room.");
        }
        else {
            //user doesn't exist so register them
            if (game.addPlayer(username, PlayerManager.PlayerType.valueOf(request.getParameter("playerType")))) {
                // room isn't full
                result.put("redirect", "boardPage.html");
                Cookie roomIdCookie = new Cookie("roomid", Integer.toString(roomid));
                roomIdCookie.setPath("/");
                response.addCookie(roomIdCookie); // so the client side will remember his room id after redirect
            }
            else if(game.getGameRunning()){
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
        try {
            HashMap<String, Object> parametersMap = XmlLoader.getGameInitParameters(file.getInputStream());

            String variant = (String)parametersMap.get("variant");
            Integer target = (Integer)parametersMap.get("target");
            Integer rows = (Integer)parametersMap.get("rows");
            Integer columns = (Integer)parametersMap.get("columns");
            Game game = new Game(target, rows, columns, variant);
            game.setTotalPlayers((Integer)parametersMap.get("total-players"));
            game.setOrganizer(request.getParameter("organizer"));    // set the organizer of the room
            String gameTitle = (String)parametersMap.get("game-title");
            game.setGameTitle(gameTitle);
            game.setStarted(game.getGameRunning());
            game.setTarget(target);
            game.setVariant(variant);
            for (RoomInfo roomInfo :
                    roomsManager.getRoomList()) {
                if (gameTitle.equals(roomInfo.getGameTitle())){
                    response.getWriter().write("Specified game title already exists, please choose a unique name");
                    return;
                }
            }
            ServletUtils.getRoomsManager(getServletContext()).addGame(game);
        } catch (Exception e) {
            response.getWriter().write(e.getMessage());
        }
    }

    //endregion

    private Game getGame(HttpServletRequest request){ //TODO fix this duplication

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

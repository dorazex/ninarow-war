package servlets;

import GameEngine.PlayerInfo;
import GameEngine.RoomsManager;
import servlets.utils.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;


@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private RoomsManager roomsManager;

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String usernameFromSession = SessionUtils.getUsernameFromSession(request);
        roomsManager = ServletUtils.getRoomsManager(getServletContext());

        PlayerInfo.PlayerType playerTypeFromParameter = PlayerInfo.PlayerType.valueOf(request.getParameter(Constants.PLAYER_TYPE));

        if (usernameFromSession == null) {
            String usernameFromParameter = request.getParameter(Constants.USERNAME);
            if (usernameFromParameter.isEmpty()) {
                String errorMessage = "Must enter username";
                request.setAttribute(Constants.USER_NAME_ERROR, errorMessage);
                getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
            }
            else {
                //normalize the username value
                usernameFromParameter = usernameFromParameter.trim();
                if(Objects.equals(request.getParameter("loggedin"), "on") && roomsManager.isPlayerExists(usernameFromParameter, playerTypeFromParameter)){
                    handleLoginAttributes(request, response, playerTypeFromParameter, usernameFromParameter);   //case for the same user that wants to play from 2 browsers
                }
                else if (roomsManager.isPlayerExists(usernameFromParameter)) {
                    String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";
                    //username already exists, forward the request back to index.jsp
                    //with a parameter that indicates that an error should be displayed
                    request.setAttribute(Constants.USER_NAME_ERROR, errorMessage);
                    getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
                }
                else if(usernameFromParameter.isEmpty()){ //username was spaces only
                    String errorMessage = "Name has to contain characters.";
                    request.setAttribute(Constants.USER_NAME_ERROR, errorMessage);
                    getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
                }
                else if(!usernameFromParameter.matches("^[\u0000-\u0080]+$")){   //check english username
                    //then username isn't in English so give an error
                    String errorMessage = "Only English user names are allowed.";
                    request.setAttribute(Constants.USER_NAME_ERROR, errorMessage);
                    getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
                }
                else {
                    handleLoginAttributes(request, response, playerTypeFromParameter, usernameFromParameter);
                    roomsManager.addPlayer(usernameFromParameter, playerTypeFromParameter);
                }
            }
        }
        else {
            //user is already logged in (know from session)
            response.sendRedirect("rooms.html");
        }
    }

    private void handleLoginAttributes(HttpServletRequest request, HttpServletResponse response, PlayerInfo.PlayerType playerTypeFromParameter, String usernameFromParameter) throws IOException {
        //add the new user to the users list
        String playerType = request.getParameter(Constants.PLAYER_TYPE);
        request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);
        Cookie organizerCookie = new Cookie("organizer", usernameFromParameter);
        organizerCookie.setPath("/");
        Cookie playerTypeCookie = new Cookie("playerType", playerType);
        playerTypeCookie.setPath("/");
        response.addCookie(organizerCookie); // so the client side will remember his name
        response.addCookie(playerTypeCookie);
        //redirect the request to the rooms - in order to actually change the URL
        response.sendRedirect("rooms.html");
    }

    @Override
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
        return "LoginServlet";
    }
}

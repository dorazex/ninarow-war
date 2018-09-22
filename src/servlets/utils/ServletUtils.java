package servlets.utils;

import GameEngine.RoomsManager;
import javax.servlet.ServletContext;

public class ServletUtils {

    private static final String MANAGER_GAMES_ATTRIBUTE_NAME = "RoomsManager";

    public static RoomsManager getRoomsManager(ServletContext servletContext) {
		return (RoomsManager)servletContext.getAttribute(MANAGER_GAMES_ATTRIBUTE_NAME);
	}
}

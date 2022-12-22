import configuration.Configuration;
import jakarta.servlet.ServletContextEvent;

import java.io.IOException;

/**
 * @author Afonso Caniço
 * @author Afonso Sampaio
 * @author Gustavo Ferreira
 * @author Samuel Correia
 */
public class ServletContextListener implements jakarta.servlet.ServletContextListener {

    private static final String CONFIGURATION_FILE = "/WEB-INF/config.properties";

    public void contextInitialized(ServletContextEvent e) {
        System.out.println("Servlet context created: " + e.toString());
        try {
            Configuration.load(e.getServletContext().getResourceAsStream(CONFIGURATION_FILE));
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void contextDestroyed(ServletContextEvent e) {
        System.out.println("Servlet context destroyed: " + e.toString());
    }
}

import helper.OWLMaster;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.swrlapi.sqwrl.SQWRLResult;

import java.io.IOException;

@WebServlet("/resultToVowlServlet")
@MultipartConfig
public class ResultToVowlServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SQWRLResult result = (SQWRLResult) req.getSession().getAttribute("queryResultObject");
        if (result != null) {
            //idealmente neste redirect é usada uma nova janela.
            //para tal parece que a melhor opção é tentar colocar a parte do redirect no html em si, n sei como
            resp.sendRedirect("https://service.tib.eu/webvowl/#iri=http://paul.staroch.name/thesis/SmartHomeWeather.owl");
            return;
        }
        req.getSession().setAttribute("errorMessage", "There was an error visualizing your query!");
        resp.sendRedirect(req.getContextPath() + "/result.jsp");
    }
}
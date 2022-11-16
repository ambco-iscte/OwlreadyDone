import helper.OWLMaster;
import helper.OWLOntologyCreator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.swrlapi.sqwrl.SQWRLResult;

import java.io.IOException;

import static helper.OWLMaster.getOntologyFromFile;
import static helper.OWLOntologyCreator.resultToOntology;

@WebServlet("/resultToVowlServlet")
@MultipartConfig
public class ResultToVowlServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SQWRLResult result = (SQWRLResult) req.getSession().getAttribute("queryResultObject");
        if (result == null) {
            req.getSession().setAttribute("errorMessage", "It seems there are no query results to visualize!");
            resp.sendRedirect(req.getContextPath() + "/result.jsp");
            return;
        }
        String ontoKbPath = req.getSession().getAttribute("uploadedFilePath").toString();
        OWLOntology originalOntology = getOntologyFromFile(ontoKbPath);
        try {
            resultToOntology(result, originalOntology, true);
            //Fazer o upload da ontologia para algum lado para depois enviar para o vowl


            //Código temporário
            //idealmente neste redirect é usada uma nova janela.
            //para tal parece que a melhor opção é tentar colocar a parte do redirect no html em si, n sei como
            //resp.sendRedirect("https://service.tib.eu/webvowl/#iri=http://paul.staroch.name/thesis/SmartHomeWeather.owl");

        } catch (OWLOntologyCreationException e) {
            req.getSession().setAttribute("errorMessage", "There was an error visualizing your query!");
            resp.sendRedirect(req.getContextPath() + "/result.jsp");
        }
    }
}
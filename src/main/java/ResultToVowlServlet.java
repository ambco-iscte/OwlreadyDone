import helper.DirectoryHelper;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyAlreadyExistsException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.swrlapi.sqwrl.SQWRLResult;

import java.io.IOException;

import static helper.OWLMaster.getOntologyFromFile;
import static helper.OWLOntologyCreator.resultToOntology;

@WebServlet("/resultToVowlServlet")
@MultipartConfig
public class ResultToVowlServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SQWRLResult result = (SQWRLResult) req.getSession().getAttribute("queryResultObject");

        if (result == null) {
            req.getSession().setAttribute("errorMessage", "It seems there are no query results to visualize!");
            resp.sendRedirect(req.getContextPath() + "/result.jsp");
            return;
        }
        String ontoKbPath = req.getSession().getAttribute("uploadedFilePath").toString();
        OWLOntology originalOntology = getOntologyFromFile(ontoKbPath);
        try {
            if(resultToOntology(result, originalOntology, getServletContext(), true, DirectoryHelper.getFileName(ontoKbPath)) == null) {
                req.getSession().setAttribute("errorMessage", "There was an error visualizing your query!");
                resp.sendRedirect(req.getContextPath() + "/result.jsp");
            }
            else {
                //Fazer o upload da ontologia para algum lado para depois enviar para o vowl
                //Código temporário
                //idealmente neste redirect é usada uma nova janela.
                //para tal parece que a melhor opção é tentar colocar a parte do redirect no html em si, n sei como
                //resp.sendRedirect("https://service.tib.eu/webvowl/#iri=http://paul.staroch.name/thesis/SmartHomeWeather.owl");
                req.getSession().setAttribute("errorMessage", "It worked!");
                resp.sendRedirect(req.getContextPath() + "/result.jsp");
            }

        } catch (OWLOntologyAlreadyExistsException e) {
            req.getSession().setAttribute("errorMessage", "Ontology result has already been created!");
            resp.sendRedirect(req.getContextPath() + "/result.jsp");
            e.printStackTrace();
        }
        catch (OWLOntologyCreationException e) {
            req.getSession().setAttribute("errorMessage", "There was an error visualizing your query!");
            resp.sendRedirect(req.getContextPath() + "/result.jsp");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
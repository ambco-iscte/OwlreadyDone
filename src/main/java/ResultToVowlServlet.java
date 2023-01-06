import configuration.Configuration;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.semanticweb.owlapi.model.OWLOntologyAlreadyExistsException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.swrlapi.sqwrl.SQWRLResult;

import java.io.File;
import java.io.IOException;

import static helper.OWLOntologyCreator.resultToOntology;
import static helper.SubmitToGitHub.createFile;

/**
 * Servlet for handling the redirect to WebVOWL's page for visualisation of the results of a SQWRL query.
 */
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
        try {
            File f = resultToOntology(result, getServletContext(), ontoKbPath);
            if (f != null && !f.exists()) {
                req.getSession().setAttribute("errorMessage", "There was an error visualizing your query!");
                resp.sendRedirect(req.getContextPath() + "/result.jsp");
            }
            else if (f != null) {
                //Fazer o upload da ontologia para algum lado para depois enviar para o vowl
                //Código temporário
                //idealmente neste redirect é usada uma nova janela.
                //para tal parece que a melhor opção é tentar colocar a parte do redirect no html em si, n sei como
                req.getSession().setAttribute("errorMessage", "It worked!");
                createFile(f.getAbsolutePath());
                resp.sendRedirect(Configuration.getWebVowlURL() + f.getName() + "?raw=true");
                // a query que tenhoe estado a fazer nao funciona ?? tbox:cd(?x) -> sqwrl:select(?x), mas se testar com versoes antigas ja funciona
                // gostaria de nao ter isto hardcoded, mas tenho de entender melhor como funciona o get da rest api, nao percebo como ir buscar o path do ficheiro em especifico
            }

        } catch (OWLOntologyAlreadyExistsException e) {
            req.getSession().setAttribute("errorMessage", "Ontology result has already been created!");
            resp.sendRedirect(req.getContextPath() + "/result.jsp");
            e.printStackTrace();
        } catch (OWLOntologyCreationException e) {
            req.getSession().setAttribute("errorMessage", "There was an error visualizing your query!");
            resp.sendRedirect(req.getContextPath() + "/result.jsp");
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
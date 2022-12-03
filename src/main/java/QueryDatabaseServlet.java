import helper.OWLMaster;

import helper.OWLQueryManager;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.semanticweb.owlapi.model.OWLOntology;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

import java.io.IOException;

/**
 * @author Afonso Caniço
 * @author Afonso Sampaio
 * @author Gustavo Ferreira
 * @author Samuel Correia
 */
@WebServlet("/queryDatabaseServlet")
@MultipartConfig
public class QueryDatabaseServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String query = req.getParameter("queryString");
        if (query == null) {
            req.getSession().setAttribute("errorMessage", "Your query seems to be empty!");
            resp.sendRedirect(req.getContextPath() + "/query.jsp");
            return;
        }

        OWLOntology ontology = OWLMaster.getOntologyFromFile(req.getSession().getAttribute("uploadedFilePath").toString());
        OWLQueryManager queryManager = new OWLQueryManager(ontology);
        req.getSession().setAttribute("queryManager", queryManager);
        try {
            SQWRLResult result = queryManager.query(query);
            if (result == null) {
                req.getSession().setAttribute("errorMessage", "There was an error running your query!");
                resp.sendRedirect(req.getContextPath() + "/query.jsp");
                return;
            }
            req.getSession().removeAttribute("queryResultObject");
            req.getSession().setAttribute("queryString", query);
            req.getSession().setAttribute("queryResultObject", result);
            resp.sendRedirect(req.getContextPath() + "/result.jsp");

        } catch (SWRLParseException | SQWRLException e) {
            throw new RuntimeException(e);
        }




    }
}

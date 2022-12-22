import helper.DirectoryHelper;
import helper.OWLMaster;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.swrlapi.sqwrl.SQWRLResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

        if (query == null)
            query = req.getParameter("queryBuilderString");

        if (query != null) {
            String ontoKbPath = req.getSession().getAttribute("uploadedFilePath").toString();
            File kb = new File(ontoKbPath);

            if (kb.setLastModified(System.currentTimeMillis()))
                System.out.println("File " + kb.getName() + " has been modified (query).");

            SQWRLResult result = OWLMaster.query(ontoKbPath, query);
            if (result != null) {
                req.getSession().removeAttribute("queryResultObject");
                req.getSession().setAttribute("queryString", query);
                req.getSession().setAttribute("queryResultObject", result);
                writeQueryToFile(req.getServletContext(), ontoKbPath, query);

                resp.sendRedirect(req.getContextPath() + "/result.jsp");
                return;
            }
        }
        req.getSession().setAttribute("errorMessage", "There was an error running your query! ");
        resp.sendRedirect(req.getContextPath() + "/query.jsp");
    }

    /**
     * Writes a query to an ontology's query history file.
     * @param context The current servlet context.
     * @param ontologyKbPath The path to the ontology's knowledge base file.
     * @param query The SQWRL query string to write.
     */
    private void writeQueryToFile(ServletContext context, String ontologyKbPath, String query) {
        try {
            File history = DirectoryHelper.getMatchingHistoryFile(context, ontologyKbPath);
            if (history != null) {
                BufferedWriter queryHistoryWriter = new BufferedWriter(new FileWriter(history, true));
                queryHistoryWriter.write(query);
                queryHistoryWriter.newLine();
                queryHistoryWriter.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
}

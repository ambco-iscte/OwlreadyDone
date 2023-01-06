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
import java.nio.file.Files;

/**
 * Java servlet that receives a SQWRL query string from the user and redirects to the results page for that query.
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
            int limit = Integer.parseInt(context.getInitParameter("stored-query-limit"));
            File history = DirectoryHelper.getMatchingHistoryFile(context, ontologyKbPath);

            if (history != null) {
                BufferedWriter queryHistoryWriter = new BufferedWriter(new FileWriter(history, true));

                String[] lines = Files.readAllLines(history.toPath()).toArray(new String[0]);
                clearFileContent(history);

                queryHistoryWriter.write(query);
                queryHistoryWriter.newLine();

                for (int i = 0; i < Math.min(limit - 1, lines.length); i++) {
                    queryHistoryWriter.write(lines[i]);
                    queryHistoryWriter.newLine();
                }

                queryHistoryWriter.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Deletes the contents of a text file.
     * @param file The file to clear.
     */
    private void clearFileContent(File file) {
        if (file == null)
            return;
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, false));
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
}

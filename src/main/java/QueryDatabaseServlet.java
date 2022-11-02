import helper.OWLMaster;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.swrlapi.sqwrl.SQWRLResult;

import java.io.IOException;

@WebServlet("/queryDatabaseServlet")
@MultipartConfig
public class QueryDatabaseServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("queryString");
        if (query != null) {
            String ontoKbPath = req.getSession().getAttribute("uploadedFilePath").toString();
            SQWRLResult result = OWLMaster.query(ontoKbPath, query);
            if (result != null) {
                req.getSession().setAttribute("queryString", query);
                req.getSession().setAttribute("queryResultObject", result);
                resp.sendRedirect(req.getContextPath() + "/result.jsp");
                return;
            }
        }
        req.getSession().setAttribute("errorMessage", "There was an error running your query!");
        resp.sendRedirect(req.getContextPath() + "/query.jsp");
    }
}

import helper.DirectoryHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;

@WebServlet("/queryFormServlet")
@MultipartConfig
public class QueryFormServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String var1 = req.getParameter("antecedentTerm1-var1");
        String relation = req.getParameter("antecedentTerm1-rel");
        String var2 = req.getParameter("antecedentTerm1-var2");

        String builtQueryTerm = builtQueryTerm(var1, relation, var2);
        req.getSession().setAttribute("builtQueryTerm", builtQueryTerm);
        resp.sendRedirect(req.getContextPath() + "/query.jsp");
    }

    /**
     * Will be used to show multiple terms for the query
     * @return returns a string composed of the selected variable names.
     */
    private String builtQueryTerm(String var1, String relation, String var2) {
        return "var1: "+ var1 +" relation: "+ relation +" var2: "+ var2;
    }
}
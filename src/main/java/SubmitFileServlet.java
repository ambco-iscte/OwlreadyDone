import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.nio.file.Paths;

@WebServlet("/submitFileServlet")
@MultipartConfig
public class SubmitFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Part filePart = req.getPart("formFile");
        if (filePart != null) {
            HttpSession session = req.getSession();
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            session.setAttribute("submitOntologyFilename", fileName);

            resp.sendRedirect(req.getContextPath() + "/query.jsp");
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/index.jsp");
    }
}

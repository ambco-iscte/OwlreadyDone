import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/submitUrlServlet")
@MultipartConfig
public class SubmitURLServlet extends HttpServlet  {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getParameter("formUrl");
        if (url != null) {
            HttpSession session = req.getSession(false);
            session.setAttribute("submitOntologyUrl", url);

            resp.sendRedirect(req.getContextPath() + "/query.jsp");
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/index.jsp");
    }
}

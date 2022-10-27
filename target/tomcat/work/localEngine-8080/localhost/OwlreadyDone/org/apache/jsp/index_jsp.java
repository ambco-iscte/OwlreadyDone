package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class index_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.AnnotationProcessor _jsp_annotationprocessor;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_annotationprocessor = (org.apache.AnnotationProcessor) getServletConfig().getServletContext().getAttribute(org.apache.AnnotationProcessor.class.getName());
  }

  public void _jspDestroy() {
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html; charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("<link href=\"./static/css/bootstrap.css\" type=\"text/css\" rel=\"stylesheet\">\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("<html lang=\"en\">\n");
      out.write("    <head>\n");
      out.write("        <meta charset=\"UTF-8\">\n");
      out.write("        <title>OWL Query and Visualization Tool</title>\n");
      out.write("    </head>\n");
      out.write("    <body>\n");
      out.write("        <section class=\"px-4 py-5 my-5 text-center\">\n");
      out.write("            <h1 class=\"display-5 fw-bold\">Página Inicial</h1>\n");
      out.write("            <div class=\"col-lg-6 mx-auto\">\n");
      out.write("                <p class=\"lead mb-4\">Tool for the creation and visualization of\n");
      out.write("                    OWL queries with SQWRL\n");
      out.write("                </p>\n");
      out.write("            </div>\n");
      out.write("            <br><br>\n");
      out.write("            <form class=\"row g-3\" action=\"submitFileServlet\" enctype=\"multipart/form-data\" method=\"post\">\n");
      out.write("                <section class=\"container\">\n");
      out.write("                    <div class=\"mb-3\">\n");
      out.write("                        <h3><label for=\"formFile\" class=\"form-file-label\">OWL File</label></h3>\n");
      out.write("                        <input class=\"form-control w-auto mx-auto\" type=\"file\" id=\"formFile\" name=\"formFile\"\n");
      out.write("                               accept=\".owx, .owl, .rdf\" required>\n");
      out.write("                    </div>\n");
      out.write("                    <div class=\"col-12\">\n");
      out.write("                        <button class=\"btn btn-primary\" type=\"submit\">Submit File</button>\n");
      out.write("                    </div>\n");
      out.write("                </section>\n");
      out.write("            </form>\n");
      out.write("\n");
      out.write("            <br><br>\n");
      out.write("            <h2><b>Or</b></h2>\n");
      out.write("            <br><br>\n");
      out.write("\n");
      out.write("            <form class=\"row g-3\" action=\"submitUrlServlet\" enctype=\"multipart/form-data\" method=\"post\">\n");
      out.write("                <section class=\"container\">\n");
      out.write("                    <div class=\"mb-3\">\n");
      out.write("                        <h3><label for=\"formUrl\" class=\"form-url-label\">OWL Ontology URL</label></h3>\n");
      out.write("                        <input class=\"form-control w-auto mx-auto\" type=\"url\"\n");
      out.write("                               id=\"formUrl\" name=\"formUrl\" placeholder=\"OWL file url\" required>\n");
      out.write("                    </div>\n");
      out.write("                    <div class=\"col-12\">\n");
      out.write("                        <button class=\"btn btn-primary\" type=\"submit\">Submit URL</button>\n");
      out.write("                    </div>\n");
      out.write("                </section>\n");
      out.write("            </form>\n");
      out.write("        </section>\n");
      out.write("\n");
      out.write("\n");
      out.write("        <footer class=\"footer py-3 my-4\">\n");
      out.write("            <hr>\n");
      out.write("            <section class=\"container\">\n");
      out.write("                <ul class=\"list-inline text-center\">\n");
      out.write("                    <li class=\"list-inline-item\">Afonso Caniço</li>\n");
      out.write("                    <li class=\"list-inline-item\">Afonso Sampaio</li>\n");
      out.write("                    <li class=\"list-inline-item\">Gustavo Ferreira</li>\n");
      out.write("                    <li class=\"list-inline-item\">Samuel Correia</li>\n");
      out.write("                </ul>\n");
      out.write("                <p class=\"text-center text-muted\">2022</p><br/>\n");
      out.write("                <p class=\"text-center text-muted\">Server Version: ");
      out.print( application.getServerInfo() );
      out.write("</p>\n");
      out.write("                <p class=\"text-center text-muted\">Servlet Version: ");
      out.print( application.getMajorVersion() );
      out.write('.');
      out.print( application.getMinorVersion() );
      out.write("</p>\n");
      out.write("            </section>\n");
      out.write("        </footer>\n");
      out.write("    </body>\n");
      out.write("</html>\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else log(t.getMessage(), t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}

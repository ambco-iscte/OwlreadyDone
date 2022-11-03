<%@ page import="org.semanticweb.owlapi.model.OWLEntity" %>
<%@ page import="helper.OWLMaster" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<html lang="en">
    <head>
        <%@ include file="templates/head.jsp" %>

        <title>Owlready Done</title>
    </head>
    <body class="owl-bg">
        <section class="margin-top-3 text-center">
            <%@ include file="templates/top_decorations.jsp" %>
            <section>
                <div class="col-lg-6 mx-auto">
                    <% if (session.getAttribute("errorMessage") != null) {%>
                    <p class="oxanium-white"><b><%= session.getAttribute("errorMessage") %></b></p>
                    <% session.removeAttribute("errorMessage"); }%>
                </div>
                <br><br>
                <!-- Content Start --->
                <%if (session.getAttribute("uploadedFilePath") != null && session.getAttribute("uploadFileOriginalName") != null) {%>
                    <h1 class="oxanium-white">You provided this knowledge base:
                        <%= session.getAttribute("uploadFileOriginalName").toString()%></h1><br>
                    <h3 class="oxanium-white">Here's an overview of what's in this knowledge base:</h3>
                    <section class="onto-signature">
                        <ul>
                            <% for (OWLEntity entity : OWLMaster.getOntologySignature(session.getAttribute("uploadedFilePath").toString())) {%>
                            <li class="oxanium text-muted"><%= entity.toStringID()%></li>
                            <%}%>
                        </ul>
                    </section>
                    <p class="oxanium text-muted">Too technical? No worries!
                        You don't need to know all of this to interact with your knowledge base.</p>

                    <br><br>
                    <h3 class="oxanium-white">Build a query to your knowledge base using our interface:</h3>
                    <p class="oxanium text-muted">[Super cool intuitive interface]</p>

                    <br><br>
                    <h2 class="text-divider oxanium-white"><b>Or</b></h2>
                    <br><br>

                    <form class="no-right-margin margin-top-1rem row g-3" action="queryDatabaseServlet" enctype="multipart/form-data" method="post">
                        <section class="container mb-3">
                            <h3><label for="queryString" class="form-url-label oxanium-white">Query your knowledge base directly using SQWRL</label></h3>
                            <section class="d-inline-flex">
                                <input class="form-control w-auto mx-auto" type="text"
                                       id="queryString" name="queryString" placeholder="SQWRL query" required>
                                <div class="margin-left-1rem">
                                    <button class="owl-btn btn btn-primary" type="submit">Query</button>
                                </div>
                            </section>
                        </section>
                    </form>
                <%} else {%>
                    <h3 class="oxanium-white">Seems like we're missing something here...</h3>
                    <h3 class="oxanium-white">Are you sure you accessed this page the way you're supposed to?</h3>
                <%}%>
                <!-- Content End --->
            </section>
            <%@ include file="templates/footer.jsp" %>
        </section>
    </body>
</html>

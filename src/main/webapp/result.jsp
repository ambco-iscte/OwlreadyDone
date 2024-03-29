<%@ page import="org.swrlapi.sqwrl.SQWRLResult" %>
<%@ page import="org.swrlapi.sqwrl.exceptions.SQWRLException" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<html lang="en">
    <head>
        <%@ include file="templates/head.jsp" %>

        <title>OWLReady.Done</title>
    </head>
    <body class="owl-bg">
        <section class="margin-top-3 text-center">
            <%@ include file="templates/top_decorations.jsp" %>
            <section>
                <div class="col-lg-6 mx-auto">
                    <% if (session.getAttribute("errorMessage") != null) {%>
                        <br/>
                        <pre class="oxanium-white"><b><%= session.getAttribute("errorMessage") %></b></pre>
                    <% session.removeAttribute("errorMessage"); }%>
                </div>
                <br><br>
                <!-- Content Start --->
                <%if (session.getAttribute("uploadedFilePath") != null && session.getAttribute("uploadFileOriginalName") != null) {%>
                    <h1 class="oxanium-white">You provided this knowledge base:
                        <%= session.getAttribute("uploadFileOriginalName").toString()%></h1><br>
                    <br>
                    <h2 class="oxanium-white">You executed this query:</h2>
                    <h3 class="oxanium text-muted"><%= session.getAttribute("queryString").toString() %></h3>
                    <br>
                    <br>
                    <h3 class="oxanium-white">This is the result of executing this query:</h3>
                            <% SQWRLResult result = (SQWRLResult) session.getAttribute("queryResultObject");
                            try { %>
                                <section class="onto-signature">
                                    <ul>
                                        <%if (result.isEmpty()) { %>
                                            <p class="oxanium text-muted">Seems like the result is empty.</p>
                                        <%}
                                        result.reset();
                                        while (result.next()) {
                                            for (String column: result.getColumnNames()) { %>
                                                <li>
                                                    <!-- Para aceitar qualquer tipo de query e apresentar resultados -->
                                                    <% if (result.hasLiteralValue(column)) {%>
                                                        <p class="oxanium-white">Literal <%= column %>: <%= result.getLiteral(column) %>
                                                    <% } %></p>

                                                    <% if (result.hasNamedIndividualValue(column)) { %>
                                                        <p class="oxanium-white">Named Individual <%= column %>: <%= result.getNamedIndividual(column) %>
                                                    <% } %></p>

                                                    <% if (result.hasClassValue(column)) {%>
                                                        <p class="oxanium-white">Class <%= column %>: <%= result.getClass(column) %>
                                                    <% } %></p>
                                                </li>
                                            <%}%>
                                        <%}%>
                                    </ul>
                                </section>
                                <br><br>
                                <form class="no-right-margin margin-top-1rem row g-3" action="resultToVowlServlet" enctype="multipart/form-data" method="post">
                                    <section class="container mb-3">
                                        <h3><label for="visualizeButton" class="form-url-label oxanium-white">Visualize these results in VOWL</label></h3>
                                        <section class="d-inline-flex">
                                            <div class="margin-left-1rem">
                                                <button name="visualizeButton" id="visualizeButton" class="owl-btn btn btn-primary" type="submit">Visualize</button>
                                            </div>
                                        </section>
                                    </section>
                                </form>
                            <% } catch (SQWRLException ex) {
                                ex.printStackTrace(); %>
                                <h3 class="oxanium-white">An error has occured while displaying your query results</h3>
                            <%} %>
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

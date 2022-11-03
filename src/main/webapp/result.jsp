<%@ page import="org.swrlapi.sqwrl.SQWRLResult" %>
<%@ page import="org.swrlapi.sqwrl.exceptions.SQWRLException" %>
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
                    <br>
                    <h2 class="oxanium-white">You executed this query:</h2>
                    <h3 class="oxanium text-muted"><%= session.getAttribute("queryString").toString() %></h3>
                    <br>
                    <br>
                    <h2 class="oxanium-white">This is the result of executing this query:</h2>
                    <% SQWRLResult result = (SQWRLResult) session.getAttribute("queryResultObject");
                    try {
                        if (result.isEmpty()) { %>
                            <p class="oxanium text-muted">Seems like the result is empty.</p>
                    <%  }
                        while (result.next()) {%>
                            <p class="oxanium-white">x: <%= result.getLiteral("x").toString() %></p> <!--FIXME: literal types-->
                    <%  }
                    } catch (SQWRLException ex) { ex.printStackTrace(); } %>
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

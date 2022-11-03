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
                    <p><b><%= session.getAttribute("errorMessage") %></b></p>
                    <% session.removeAttribute("errorMessage"); }%>
                </div>
                <br><br>

                <!-- Main body goes here -->

            </section>
            <%@ include file="templates/footer.jsp" %>
        </section>
    </body>
</html>

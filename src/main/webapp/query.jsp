<link href="./static/css/bootstrap.css" type="text/css" rel="stylesheet">

<%@ page contentType="text/html; charset=UTF-8" %>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>OWL Query and Visualization Tool</title>
</head>
<body>
<section class="px-4 py-5 my-5 text-center">
    <h1>Hi</h1>
    <%if (session.getAttribute("submitOntologyFilename") != null) {%>
        <h2><%= session.getAttribute("submitOntologyFilename")%></h2>
        <%session.removeAttribute("submitOntologyFilename");%>

    <%} else if (session.getAttribute("submitOntologyUrl") != null) {%>
        <h2><%= session.getAttribute("submitOntologyUrl")%></h2>
        <%session.removeAttribute("submitOntologyUrl");%>

    <%} else {%>
        <h2>Seems like nothing was passed in the session...</h2>
    <%}%>
</section>

<footer class="footer py-3 my-4">
    <hr>
    <section class="container">
        <ul class="list-inline text-center">
            <li class="list-inline-item">Afonso Caniço</li>
            <li class="list-inline-item">Afonso Sampaio</li>
            <li class="list-inline-item">Gustavo Ferreira</li>
            <li class="list-inline-item">Samuel Correia</li>
        </ul>
        <p class="text-center text-muted">2022</p>
    </section>
</footer>
</body>
</html>

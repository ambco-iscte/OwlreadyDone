<link href="./static/css/bootstrap.css" type="text/css" rel="stylesheet">

<%@ page contentType="text/html; charset=UTF-8" %>

<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>OWL Query and Visualization Tool</title>
    </head>
    <body>
        <section class="px-4 py-5 my-5 text-center">
            <h1 class="display-5 fw-bold">Página Inicial</h1>
            <div class="col-lg-6 mx-auto">
                <p class="lead mb-4">Tool for the creation and visualization of
                    OWL queries with SQWRL
                </p>
            </div>
            <br><br>
            <form class="row g-3" action="submitFileServlet" enctype="multipart/form-data" method="post">
                <section class="container">
                    <div class="mb-3">
                        <h3><label for="formFile" class="form-file-label">OWL File</label></h3>
                        <input class="form-control w-auto mx-auto" type="file" id="formFile" name="formFile"
                               accept=".owx, .owl, .rdf" required>
                    </div>
                    <div class="col-12">
                        <button class="btn btn-primary" type="submit">Submit File</button>
                    </div>
                </section>
            </form>

            <br><br>
            <h2><b>Or</b></h2>
            <br><br>

            <form class="row g-3" action="submitUrlServlet" enctype="multipart/form-data" method="post">
                <section class="container">
                    <div class="mb-3">
                        <h3><label for="formUrl" class="form-url-label">OWL Ontology URL</label></h3>
                        <input class="form-control w-auto mx-auto" type="url"
                               id="formUrl" name="formUrl" placeholder="OWL file url" required>
                    </div>
                    <div class="col-12">
                        <button class="btn btn-primary" type="submit">Submit URL</button>
                    </div>
                </section>
            </form>
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
                <p class="text-center text-muted">2022 (Server Version: <%= application.getServerInfo() %>;
                    Servlet Version: <%= application.getMajorVersion() %>.<%= application.getMinorVersion() %>)</p><br/>
            </section>
        </footer>
    </body>
</html>

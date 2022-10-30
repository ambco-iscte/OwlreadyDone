<%@ page contentType="text/html; charset=UTF-8" %>

<html lang="en">
    <head>
        <meta charset="UTF-8">
        <link href="./static/css/bootstrap.css" type="text/css" rel="stylesheet">
        <link href="./static/css/custom.css" type="text/css" rel="stylesheet">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Oxanium:wght@300&display=swap" rel="stylesheet">
        <link rel="icon" type="image/png" href="./static/favicon.png"/>

        <title>Owlready Done</title>
    </head>
    <body class="owl-bg">
        <section class="margin-top-3 text-center">
            <figure>
                <img src="static/logo.png" alt="OwlreadyDone logo, with title and subtitle.">
                <img class="top-right" src="static/corner-up-right.png" alt="Top right decoration.">
            </figure>
            <section>
                <div class="col-lg-6 mx-auto">
                    <% if (session.getAttribute("errorMessage") != null) {%>
                    <p><b><%= session.getAttribute("errorMessage") %></b></p>
                    <% session.removeAttribute("errorMessage"); }%>
                </div>
                <br><br>

                <!-- Main body goes here -->

            </section>

            <footer class="footer">
                <section class="index-bottom-flex">
                    <p class="text-center oxanium-white absolute-center">Want to know how all of this works? Click
                        <a href="https://github.com/ambco-iscte/OwlreadyDone">here</a> to check us out on GitHub!</p>
                    <figure class="no-bottom-margin">
                        <img src="static/corner-bottom-left.png" alt="Bottom left decoration.">
                    </figure>
                </section>
                <section class="solid-purple-bg">
                    <hr class="no-top-margin">
                    <br/>
                    <section class="container">
                        <p class="oxanium-white">Made with ❤ by:</p>
                        <ul class="list-inline text-center">
                            <li class="list-inline-item oxanium-white">Afonso Caniço</li>
                            <li class="list-inline-item oxanium-white">Afonso Sampaio</li>
                            <li class="list-inline-item oxanium-white">Gustavo Ferreira</li>
                            <li class="list-inline-item oxanium-white">Samuel Correia</li>
                        </ul>
                        <p class="text-center oxanium-white">2022</p><br/>
                    </section>
                </section>
            </footer>
        </section>
    </body>
</html>

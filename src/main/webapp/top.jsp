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
                    <p><b><%= session.getAttribute("errorMessage") %>
                    </b></p>
                    <% session.removeAttribute("errorMessage");
                    }%>
                </div>
                <br><br>
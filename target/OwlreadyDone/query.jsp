<%@ page import="org.semanticweb.owlapi.model.OWLEntity" %>
<%@ page import="helper.OWLMaster" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<script type="text/javascript" src="./static/js/custom.js"></script>

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
            <p class="oxanium-white"><b><%= session.getAttribute("errorMessage") %></b></p>
            <% session.removeAttribute("errorMessage"); }%>
        </div>
        <br><br>

        <%if (session.getAttribute("uploadedFilePath") != null && session.getAttribute("uploadFileOriginalName") != null) {%>
        <%
            String kbPath = session.getAttribute("uploadedFilePath").toString();
            Set<String> classNames = OWLMaster.getOntologyClassNames(kbPath);
            Set<String> individualNames = OWLMaster.getOntologyIndividualNames(kbPath);
            Set<String> relationNames = OWLMaster.getAllRelationNames(kbPath);
        %>
            <h1 class="oxanium-white">You provided this knowledge base:
                <%= session.getAttribute("uploadFileOriginalName").toString()%></h1><br>
            <h3 class="oxanium-white">Here's an overview of what's in this knowledge base:</h3>
            <section class="onto-signature">
                <ul>
                    <% for (OWLEntity entity : OWLMaster.getOntologySignature(kbPath)) {%>
                    <li class="oxanium text-muted"><%= entity.toStringID()%></li>
                    <%}%>
                </ul>
            </section>
            <p class="oxanium text-muted">Too technical? No worries!
                You don't need to know all of this to interact with your knowledge base.</p>

            <br><br>
            <h3 class="oxanium-white">Build a query to your knowledge base using our interface:</h3>

            <section class="query-builder">
                <section class="query-builder-line">
                    <h3 class="oxanium-purple">If</h3>
                    <section class="query-builder-section"> <!-- Left side of query -->
                        <section class="query-builder-term"> <!-- Term proper -->
                            <section class="query-builder-term-input-fields"> <!-- Input fields -->
                                <input class="query-builder-input-text" type="text"
                                       id="antecedentTerm1-var1" name="antecedentTerm1-var1" placeholder="?var1" required>

                                <select class="query-builder-input-select" name="antecedentTerm1-rel" id="antecedentTerm1-rel">
                                    <% for (String relName : relationNames) {%>
                                    <option class="query-builder-input-select-option" value="<%=relName%>"><%=relName%></option>
                                    <%}%>
                                </select>

                                <input class="query-builder-input-text" type="text" list="antecedentTerm1-var2-datalist"
                                       id="antecedentTerm1-var2" name="antecedentTerm1-var2" placeholder="?var2" required>
                                <datalist id="antecedentTerm1-var2-datalist">
                                    <% for (String className : classNames) {%>
                                        <option class="query-builder-input-select-option" value="<%=className%>"><%=className%></option>
                                    <%}%>
                                </datalist>
                            </section>
                            <h3 class="oxanium-purple no-bottom-margin" style="display: none">and</h3>
                            <h3 class="oxanium-purple no-bottom-margin highlight-on-hover unselectable"
                                onclick="onClick(this.id, <%=classNames%>,<%=individualNames%>, <%=relationNames%>)"><b>+</b></h3>
                        </section>
                    </section>
                </section>
                <section class="query-builder-line">
                    <h3 class="oxanium-purple">Then</h3>
                    <section class="query-builder-section"> <!-- Right side of query (SELECT) -->

                    </section>
                </section>

                <!-- Query preview and submit buttom -->
                <form class="no-right-margin margin-top-1rem row g-3" action="queryDatabaseServlet" enctype="multipart/form-data" method="post">
                    <section class="container mb-3">
                        <section class="d-inline-flex">
                            <input class="form-control wide-30rem mx-auto" type="text" readonly
                                   id="queryBuilderString" name="queryBuilderString" placeholder="Your query will show up here!" required>
                            <div class="margin-left-1rem">
                                <button class="owl-btn btn btn-primary" type="submit">Query</button>
                            </div>
                        </section>
                    </section>
                </form>
            </section>

            <br><br>
            <h2 class="text-divider oxanium-white"><b>Or</b></h2>
            <br><br>

            <form class="no-right-margin margin-top-1rem row g-3" action="queryDatabaseServlet" enctype="multipart/form-data" method="post">
                <section class="container mb-3">
                    <h3><label for="queryString" class="form-url-label oxanium-white">Query your knowledge base directly using SQWRL</label></h3>
                    <section class="d-inline-flex">
                        <input class="form-control wide-30rem mx-auto" type="text"
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

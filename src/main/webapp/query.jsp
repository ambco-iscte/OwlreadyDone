<%@ page import="org.semanticweb.owlapi.model.OWLEntity" %>
<%@ page import="helper.OWLMaster" %>
<%@ page import="java.util.Set" %>
<%@ page import="helper.Helper" %>
<%@ page import="java.nio.file.Files" %>
<%@ page import="helper.DirectoryHelper" %>
<%@ page import="java.io.File" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<script src="./static/js/custom.js"></script>

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
                <%
                    String kbPath = session.getAttribute("uploadedFilePath").toString();
                    Set<String> classNames = OWLMaster.getOntologyClassNames(kbPath);
                    Set<String> individualNames = OWLMaster.getOntologyIndividualNames(kbPath);
                    Set<String> relationNames = OWLMaster.getAllRelationNames(kbPath);
                    Set<String> builtInNames = OWLMaster.getPrefixedBuiltInNames("sqwrl");
                %>
                <h1 class="oxanium-white">You provided this knowledge base:
                    <%= session.getAttribute("uploadFileOriginalName").toString()%>
                </h1><br>
                <h3 class="oxanium-white">Here's an overview of what's in this knowledge base:</h3>
                <section class="onto-signature">
                    <ul>
                        <% for (OWLEntity entity : OWLMaster.getOntologySignature(kbPath)) {%>
                        <li class="oxanium text-muted"><%= entity.toStringID()%>
                        </li>
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
                        <section class="query-builder-section" id="leftSideOfQuery"> <!-- Left side of query -->
                            <section class="query-builder-term"> <!-- Term proper -->
                                <section class="query-builder-term-input-fields"> <!-- Input fields -->
                                    <input class="query-builder-input-text" type="text"
                                           id="antecedentTerm1-var1" name="antecedentTerm1-var1" placeholder="?var1" required>

                                    <select class="query-builder-input-select" name="antecedentTerm1-rel"
                                            id="antecedentTerm1-rel">
                                        <% for (String relName : relationNames) {%>
                                        <option class="query-builder-input-select-option" value="<%=relName%>"><%=relName%>
                                        </option>
                                        <%}%>
                                    </select>

                                <input class="query-builder-input-text" type="text" list="antecedentTerm1-var2-datalist"
                                       id="antecedentTerm1-var2" name="antecedentTerm1-var2" placeholder="?var2" required>
                                <datalist id="antecedentTerm1-var2-datalist">
                                    <% for (String className : classNames) {%>
                                        <option class="query-builder-input-select-option" value="<%=className%>"><%=className%></option>
                                    <%}%>
                                    <% for (String indivName : individualNames) {%>
                                    <option class="query-builder-input-select-option" value="<%=indivName%>"><%=indivName%></option>
                                    <%}%>
                                </datalist>
                            </section>
                                <!--<h3 id="queryBuilderAntecedentAddTermToDisplayField-1" class="oxanium-purple no-bottom-margin highlight-on-hover unselectable" style="display: none"
                                onclick="antecedentShow('antecedentTerm1-var1', 'antecedentTerm1-rel', 'antecedentTerm1-var2')"><b>Show</b></h3>-->
                                <h3 class="oxanium-purple no-bottom-margin term-and-text" style="display: none;">and</h3>

                                <h3 id="queryBuilderAntecedentAddTermButton-1"
                                    class="oxanium-purple no-bottom-margin highlight-on-hover unselectable"
                                    onclick="antecedentAddNewTermClicked(this.id, <%=Helper.toJSStringList(classNames)%>,
                                        <%=Helper.toJSStringList(individualNames)%>,
                                        <%=Helper.toJSStringList(relationNames)%>)"><b>+</b></h3>
                            </section>
                        </section>
                    </section>
                    <section class="query-builder-line">
                        <h3 class="oxanium-purple">Then</h3>
                        <section class="query-builder-section" id="rightSideOfQuery"> <!-- Right side of query (SELECT) -->

                            <section class="query-builder-term">
                                <section class="query-builder-term-input-fields">
                                    <select class="query-builder-input-select" name="consequentTerm1-rel" id="consequentTerm1-rel">
                                        <% for (String builtInName : builtInNames) {%>
                                        <option class="query-builder-input-select-option"
                                                value="<%=builtInName%>"><%=builtInName%>
                                        </option>
                                        <%}%>
                                    </select>

                                    <input class="query-builder-input-text" type="text" id="consequentTerm1-var1"
                                           name="consequentTerm1-var1" placeholder="?var1, ?var2, ..." required>
                                </section>

                                <h3 class="oxanium-purple no-bottom-margin term-and-text" style="display: none">and</h3>

                                <h3 id="queryBuilderConsequentAddTermButton-1"
                                    class="oxanium-purple no-bottom-margin highlight-on-hover unselectable"
                                    onclick="consequentAddNewTermClicked(this.id, <%=Helper.toJSStringList(builtInNames)%>)"><b>+</b>
                                </h3>
                            </section>

                        </section>
                    </section>
                    <!-- Query preview and submit buttom -->
                    <h3 class="owl-btn btn btn-primary" onclick="onRefreshQueryFieldButtonClicked()">
                        <b>Refresh Query Field</b>
                    </h3>
                    <form class="no-right-margin margin-top-1rem row g-3" action="queryDatabaseServlet"
                          enctype="multipart/form-data" method="post">
                        <section class="container mb-3">
                            <section class="d-inline-flex">
                                <input class="form-control wide-30rem mx-auto" type="text" readonly
                                       id="queryBuilderString" name="queryBuilderString"
                                       placeholder="Your query will show up here!" required>
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

                <form class="no-right-margin margin-top-1rem row g-3" action="queryDatabaseServlet"
                      enctype="multipart/form-data" method="post">
                    <section class="container mb-3">
                        <h3><label for="directQueryString" class="form-url-label oxanium-white">Query your knowledge base directly
                            using SQWRL</label></h3>
                        <section class="d-inline-flex">
                            <input class="form-control wide-30rem mx-auto" type="text"
                                   id="directQueryString" name="queryString" placeholder="SQWRL query" required>
                            <div class="margin-left-1rem">
                                <button class="owl-btn btn btn-primary" type="submit">Query</button>
                            </div>
                        </section>
                    </section>
                </form>

                <%
                    File historyFile = DirectoryHelper.getMatchingHistoryFile(session.getServletContext(), kbPath);
                    if (historyFile != null && historyFile.exists()) {
                        List<String> historyQueries = Files.readAllLines(historyFile.toPath());
                        if (!historyQueries.isEmpty()) {
                %>
                <!-- These elements only appear if the ontology has a non-empty query history file. -->
                <br><br>
                <h2 class="text-divider oxanium-white"><b>Or</b></h2>
                <br><br>

                <form class="no-right-margin margin-top-1rem row g-3" action="queryDatabaseServlet" enctype="multipart/form-data" method="post">
                    <section class="container mb-3">
                        <h3><label for="historyQueryString" class="form-url-label oxanium-white">Use a query that's already been used for your ontology</label></h3>
                        <section class="d-inline-flex">
                            <select class="wide-30rem" name="queryString" id="historyQueryString">
                                <% for (String query : historyQueries) {%>
                                    <option value="<%=query%>"><%=query%></option>
                                <%}%>
                            </select>
                            <div class="margin-left-1rem">
                                <button class="owl-btn btn btn-primary" type="submit">Query</button>
                            </div>
                        </section>
                    </section>
                </form>

                <% } } %>

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

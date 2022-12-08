<%@ page import="helper.DirectoryHelper" %>
<%@ page import="java.io.File" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<html lang="en">
    <head>
        <%@ include file="templates/head.jsp" %>
        <title>OWLReady</title>
    </head>
    <body class="owl-bg">
        <section class="margin-top-3 text-center">
            <%@ include file="templates/top_decorations.jsp" %>
            <section>
                <div class="col-lg-6 mx-auto">
                    <% if (session.getAttribute("errorMessage") != null) {%>
                    <p class="oxanium-white"><b><%= session.getAttribute("errorMessage") %>
                    </b></p>
                    <% session.removeAttribute("errorMessage");
                    }%>
                </div>
                <br><br>
                <!-- Content Start --->
                <section class="container-fluid row">
                    <section class="col oxanium">
                        <aside>
                            <div class="d-flex flex-column align-items-stretch flex-shrink-0 bg-white" style="width: 380px;">
                                <div class="d-flex align-items-center flex-shrink-0 p-3 link-dark text-decoration-none border-bottom">
                                    <span class="fs-5 fw-semibold">Recent Ontologies</span>
                                </div>
                                <div class="list-group list-group-flush border-bottom scrollarea overflow-auto" style="max-height: 320px;">
                                    <%if(DirectoryHelper.getFiles(request.getSession().getServletContext(), "upload-dir") == null){%>
                                        <a class="list-group-item list-group-item-action py-3 lh-tight" aria-current="true">
                                            <div class="d-flex w-100 align-items-center justify-content-between">
                                                <strong class="mb-1">There are no recent files to display</strong>
                                            </div>
                                        </a>
                                    <%} else {%>
                                        <% for (File file : DirectoryHelper.getFiles(request.getSession().getServletContext(), "upload-dir")) {%>
                                            <a href="submitFileServlet?recentFile=<%=file.getName()%>" class="list-group-item list-group-item-action py-3 lh-tight" aria-current="true">
                                                <div class="d-flex w-100 align-items-center justify-content-between">
                                                    <strong class="mb-1"><%=file.getName()%>
                                                    </strong>
                                                    <small>Size: <%=file.length() / 1024%> KB</small>
                                                </div>
                                                <div class="mb-1 small">Last Used:
                                                    <%=new SimpleDateFormat("dd/MM/yyyy HH:mm").format(file.lastModified())%>
                                                </div>
                                            </a>
                                        <%}%>
                                    <%}%>
                                </div>
                            </div>
                        </aside>
                    </section>
                    <section class="col">
                        <form class="no-right-margin margin-top-1rem row g-3" action="submitFileServlet" enctype="multipart/form-data"
                              method="post">
                            <section class="container mb-3">
                                <h3><label for="formFile" class="form-file-label oxanium-white">Upload an OWL knowledge base from your
                                    computer</label></h3>
                                <section class="d-inline-flex">
                                    <input class="form-control wide-30rem mx-auto" type="file" id="formFile" name="formFile"
                                           accept=".owx, .owl, .rdf" required>
                                    <div class="margin-left-1rem">
                                        <button class="owl-btn btn btn-primary" type="submit">Upload</button>
                                    </div>
                                </section>
                            </section>
                        </form>

                        <br><br>
                        <h2 class="text-divider oxanium-white"><b>Or</b></h2>
                        <br><br>

                        <form class="no-right-margin margin-top-1rem row g-3" action="submitFileServlet" enctype="multipart/form-data"
                              method="post">
                            <section class="container mb-3">
                                <h3><label for="formUrl" class="form-url-label oxanium-white">Submit a link to an OWL knowledge
                                    base</label></h3>
                                <section class="d-inline-flex">
                                    <input class="form-control wide-30rem mx-auto" type="url"
                                           id="formUrl" name="formUrl" placeholder="OWL file url" required>
                                    <div class="margin-left-1rem">
                                        <button class="owl-btn btn btn-primary" type="submit">Submit</button>
                                    </div>
                                </section>
                            </section>
                        </form>
                    </section>

                    <section id="FillerColumn" class="col"></section>
                </section>
                <!-- Content End --->
            </section>
            <%@ include file="templates/footer.jsp" %>
        </section>
    </body>
</html>

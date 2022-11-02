<%@ include file="top.jsp" %>

    <form class="no-right-margin margin-top-1rem row g-3" action="submitFileServlet" enctype="multipart/form-data" method="post">
        <section class="container mb-3">
            <h3><label for="formFile" class="form-file-label oxanium-white">Upload an OWL knowledge base from your computer</label></h3>
            <section class="d-inline-flex">
                <input class="form-control w-auto mx-auto" type="file" id="formFile" name="formFile"
                       accept=".owx, .owl, .rdf" required>
                <div class="margin-left-1rem">
                    <button class="owl-btn btn btn-primary" type="submit">Upload</button>
                </div>
            </section>
        </section>
    </form>

    <br><br>
    <h2 class="text-divider oxanium-white">
        <b>Or</b>
    </h2>
    <br><br>

    <form class="no-right-margin margin-top-1rem row g-3" action="submitFileServlet" enctype="multipart/form-data" method="post">
        <section class="container mb-3">
            <h3><label for="formUrl" class="form-url-label oxanium-white">Submit a link to an OWL knowledge base</label></h3>
            <section class="d-inline-flex">
                <input class="form-control w-auto mx-auto" type="url"
                       id="formUrl" name="formUrl" placeholder="OWL file url" required>
                <div class="margin-left-1rem">
                    <button class="owl-btn btn btn-primary" type="submit">Submit</button>
                </div>
            </section>
        </section>
    </form>

<%@ include file="bottom.jsp" %>

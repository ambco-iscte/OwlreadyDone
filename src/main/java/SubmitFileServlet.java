import helper.DirectoryHelper;
import helper.OWLMaster;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Java servlet handling the uploading of ontology files by the user.
 * @author Afonso Caniço
 * @author Afonso Sampaio
 * @author Gustavo Ferreira
 * @author Samuel Correia
 */
@WebServlet("/submitFileServlet")
@MultipartConfig
public class SubmitFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Part filePart = req.getPart("formFile");
        String url = req.getParameter("formUrl");

        String fileName = null;
        String filePath = null;

        if (filePart != null) {
            fileName = filePart.getSubmittedFileName();
            filePath = getAbsoluteFilepath(getFileFromPart(filePart));
        }
        else if (url != null && isValidURL(url)) {
            fileName = FilenameUtils.getName(url);
            filePath = getAbsoluteFilepath(getFileFromURL(url));
        }

        showQueryPage(req, resp, fileName, filePath,"Couldn't find uploaded file or download from URL, or " +
                "the file was malformed.\nAre you sure you provided a valid OWL file or a direct link to one?");
    }

    /**
     * Gets the absolute file path of a given file.
     * @param file A file.
     * @return The absolute path of the given File instance, if it is non-null; Null, otherwise.
     */
    private String getAbsoluteFilepath(File file) {
        if (file == null)
            return null;
        return file.getAbsolutePath();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Set<File> files = DirectoryHelper.getFiles(getServletContext(), "upload-dir");
        if (files.isEmpty()) {
            req.getSession().setAttribute("errorMessage", "There are no recent files!");
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        String recentFileName = req.getParameter("recentFile");
        String filePath = null;

        for (File file : files) {
            if (file.getName().equals(recentFileName)) {
                filePath = file.getAbsolutePath();
                break;
            }
        }
        showQueryPage(req, resp, recentFileName, filePath, "Couldn't find the specified file.");
    }

    /**
     * Shows the query page for an ontology, if possible; Redirects to the home page, otherwise.
     * @param req The HTTP servlet request.
     * @param resp The HTTP servlet response.
     * @param fileName The file name of the uploaded ontology file.
     * @param filePath The file path of the uploaded ontology file.
     * @param errorMessage The error message to display when redirecting back to the home page.
     * @throws IOException If an error occurred when redirecting.
     */
    private void showQueryPage(HttpServletRequest req, HttpServletResponse resp, String fileName, String filePath, String errorMessage) throws IOException {
        if (filePath != null) {
            req.getSession().setAttribute("uploadedFilePath", filePath);
            req.getSession().setAttribute("uploadFileOriginalName", fileName);
            resp.sendRedirect(req.getContextPath() + "/query.jsp");
        } else {
            req.getSession().setAttribute("errorMessage", errorMessage);
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
        }
    }

    /**
     * @param part The Part instance, as uploaded using a multipart/form-data encoded form.
     * @return The file name (with extension) of the uploaded file.
     */
    private String getFilenameFromPart(Part part) {
        return Paths.get(part.getSubmittedFileName()).getFileName().toString();
    }

    /**
     * Finds a filename that avoids file collisions.
     * @param attemptedFilename The file name we want to attempt using.
     * @return A valid file name which avoids overwriting if a file with the attempted name already exists.
     */
    private String getValidUploadFilePath(String attemptedFilename) {
        String dirPath = DirectoryHelper.getDirectory(getServletContext(), "upload-dir").getAbsolutePath();
        String filePath = dirPath + File.separator + attemptedFilename;
        for (int i = 1; new File(filePath).exists(); i++) {
            filePath = dirPath + File.separator + i + "_" + attemptedFilename;
        }
        return filePath;
    }

    /**
     * Writes a Part instance to the application's upload directory.
     * @param part The Part instance, as uploaded using a multipart/form-data encoded form.
     * @return The File instance corresponding to the file where the Part was written.
     * @throws IOException Throws an exception if the Part couldn't be written to a file.
     */
    private File getFileFromPart(Part part) throws IOException {
        String filePath = getValidUploadFilePath(getFilenameFromPart(part));
        part.write(filePath);
        return validateOrDelete(new File(filePath));
    }

    /**
     * Downloads a file from a URL and writes it to the upload folder.
     * @param urlStr The URL where the file is located.
     * @return The File instance corresponding to the file where the downloaded file was written.
     * @throws IOException If there was an exception reading the file from the link or writing to a local file.
     */
    private File getFileFromURL(String urlStr) throws IOException {
        URL url = new URL(urlStr);

        String filePath = getValidUploadFilePath(FilenameUtils.getName(urlStr));

        ReadableByteChannel channel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        fileOutputStream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);

        channel.close();
        fileOutputStream.close();

        return validateOrDelete(new File(filePath));
    }

    /**
     * Is the given file a valid OWL file? If not, schedule it for deletion (see {@link DirectoryHelper#deleteOnExit}).
     * @param file A file.
     * @return The given file, if it is a valid OWL document; Null, otherwise.
     */
    private File validateOrDelete(File file) {
        if (!OWLMaster.isValidOntologyFile(file)) {
            DirectoryHelper.deleteOnExit(file);
            return null;
        }
        DirectoryHelper.purgeUploadDirectoryIfFull(getServletContext(), "stored-upload-limit");
        if (file.setLastModified(System.currentTimeMillis()))
            System.out.println("File " + file.getName() + " has been modified (upload).");
        return file;
    }

    /**
     * Does the given string constitute a valid URL?
     * @param url A string of a valid or invalid URL.
     * @return True if the URL is correctly formed; False otherwise.
     */
    private boolean isValidURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException ex) {
            return false;
        }
    }
}

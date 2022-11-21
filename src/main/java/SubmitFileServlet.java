import helper.DirectoryHelper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;

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
            filePath = getFileFromPart(filePart).getAbsolutePath();
        }
        else if (url != null && isValidURL(url)) {
            fileName = FilenameUtils.getName(url);
            filePath = getFileFromURL(url).getAbsolutePath();
        }

        showQueryPage(req, resp, fileName, filePath,"Couldn't find uploaded file or given URL.");
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        File[] files = DirectoryHelper.getFiles(getServletContext(), "upload-dir");
        if(files == null){
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

    private void showQueryPage(HttpServletRequest req, HttpServletResponse resp, String fileName, String filePath,
                               String errorMessage) throws IOException {
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

        DirectoryHelper.purgeUploadDirectoryIfFull(getServletContext(), "upload-dir", "stored-upload-limit");
        return new File(filePath);
    }

    /**
     * Downloads a file from a URL and writes it to the upload folder.
     * @param urlStr The URL where the file is located.
     * @return The File instance corresponding to the file where the downloaded file was written.
     */
    private File getFileFromURL(String urlStr) throws IOException {
        URL url = new URL(urlStr);

        String filePath = getValidUploadFilePath(FilenameUtils.getName(urlStr));

        ReadableByteChannel channel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        fileOutputStream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);

        channel.close();
        fileOutputStream.close();

        DirectoryHelper.purgeUploadDirectoryIfFull(getServletContext(), "upload-dir", "stored-upload-limit");
        return new File(filePath);
    }

    /**
     * Does the given string constitute to a valid URL?
     * @return True if the URL is correctly formed. False otherwise.
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

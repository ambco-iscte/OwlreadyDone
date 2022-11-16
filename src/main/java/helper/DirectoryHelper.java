package helper;

import jakarta.servlet.ServletContext;
import java.io.File;
import java.util.Objects;

/**
 * Contains helper methods for managing the upload directory of a ServletContext.
 */
public class DirectoryHelper {

    /**
     * @return A File instance corresponding to the given context's upload directory.
     */
    private static File getUploadDirectory(ServletContext context) {
        String uploadDir = context.getInitParameter("upload-dir");
        return new File(context.getRealPath("") + File.separator + uploadDir);
    }

    /**
     * How many files are currently stored in the given context's upload directory?
     * @return The number of files in the context's upload directory, if applicable; 0 otherwise.
     */
    public static int getUploadedFileCount(ServletContext context) {
        File[] files = getUploadedFiles(context);
        if (files != null)
            return files.length;
        return 0;
    }

    public static File[] getUploadedFiles(ServletContext context) {
        File dir = getUploadDirectory(context);
        if (dir.exists() && dir.isDirectory())
            return Objects.requireNonNull(dir.listFiles());
        return null;
    }

    /**
     * Is the context's upload directory full?
     * @return True if the number of stored uploads surpasses the limit defined in web.xml; False otherwise.
     */
    private static boolean isUploadDirectoryFull(ServletContext context) {
        return getUploadedFileCount(context) > Integer.parseInt(context.getInitParameter("stored-upload-limit"));
    }

    /**
     * @return The file with the oldest "last modified" timestamp in the given context's upload directory, if
     * the directory is valid. Null, otherwise.
     */
    private static File getOldestStoredFile(ServletContext context) {
        File[] files = getUploadDirectory(context).listFiles();
        if (files == null) return null;

        File oldest = files[0];
        for (File file : files) {
            if (file.lastModified() < oldest.lastModified())
                oldest = file;
        }
        return oldest;
    }

    /**
     * Deletes the oldest file in the context's upload directory.
     */
    private static void purgeUploadDirectory(ServletContext context) {
        File oldest = getOldestStoredFile(context);
        if (oldest != null) {
            if (oldest.delete()) {
                OWLMaster.purgeOntologyMap(oldest.getAbsolutePath());
                System.out.println("Successfuly deleted oldest file in upload directory and cleared ontology map.");
            }
        }
    }

    /**
     * Deletes the oldest file in the context's upload directory, but only if the number of files in this directory
     * surpasses the limit defined in web.xml; Otherwise, does nothing.
     */
    public static void purgeUploadDirectoryIfFull(ServletContext context) {
        if (isUploadDirectoryFull(context))
            purgeUploadDirectory(context);
    }
}

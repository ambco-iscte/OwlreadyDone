package helper;

import jakarta.servlet.ServletContext;
import java.io.File;
import java.util.Objects;

/**
 * Contains helper methods for managing the upload directory of a ServletContext.
 */
public class DirectoryHelper {


    /**
     * @return The full path of the upload directory of the application, as defined in web.xml
     */
    public static File getDirectory(ServletContext context, String dirInitParameter) {
        String uploadDir = context.getInitParameter(dirInitParameter);

        File dir = new File(context.getRealPath("") + File.separator + uploadDir);
        if (!dir.exists())
            if (dir.mkdirs())
                System.out.println("Upload directory not present, has been created.");

        return dir;
    }

    public static String getFileName(String path) {
        File file = new File(path);
        return file.getName();
    }

    /**
     * How many files are currently stored in the given context's upload directory?
     * @return The number of files in the context's upload directory, if applicable; 0 otherwise.
     */
    public static int getUploadedFileCount(ServletContext context, String dirInitParameter) {
        File[] files = getFiles(context, dirInitParameter);
        if (files != null)
            return files.length;
        return 0;
    }

    public static File[] getFiles(ServletContext context, String dirInitParameter) {
        File dir = getDirectory(context, dirInitParameter);
        if (dir.exists() && dir.isDirectory())
            return Objects.requireNonNull(dir.listFiles());
        return null;
    }

    /**
     * Is the context's upload directory full?
     * @return True if the number of stored uploads surpasses the limit defined in web.xml; False otherwise.
     */
    private static boolean isUploadDirectoryFull(ServletContext context, String dirInitParameter, String uploadLimitInitParameter) {
        return getUploadedFileCount(context, dirInitParameter) >
                Integer.parseInt(context.getInitParameter(uploadLimitInitParameter));
    }

    /**
     * @return The file with the oldest "last modified" timestamp in the given context's upload directory, if
     * the directory is valid. Null, otherwise.
     */
    private static File getOldestStoredFile(ServletContext context, String dirInitParameter) {
        File[] files = getDirectory(context, dirInitParameter).listFiles();
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
    private static void purgeUploadDirectory(ServletContext context, String dirInitParameter) {
        File oldest = getOldestStoredFile(context, dirInitParameter);
        if (oldest != null) {
            if (oldest.delete()) {
                OWLMaster.purgeOntologyMap(oldest.getAbsolutePath());
                System.out.println("Successfuly deleted oldest file in the directory and cleared ontology map.");
            }
        }
    }

    /**
     * Deletes the oldest file in the context's upload directory, but only if the number of files in this directory
     * surpasses the limit defined in web.xml; Otherwise, does nothing.
     */
    public static void purgeUploadDirectoryIfFull(ServletContext context, String dirInitParameter, String uploadLimitInitParameter) {
        if (isUploadDirectoryFull(context, dirInitParameter, uploadLimitInitParameter))
            purgeUploadDirectory(context, dirInitParameter);
    }
}

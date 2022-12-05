package helper;

import jakarta.servlet.ServletContext;
import org.semanticweb.owlapi.model.OWLOntology;

import java.io.File;
import java.util.Arrays;

/**
 * Contains helper methods for managing the directories of a ServletContext.
 * @author Afonso Caniço
 * @author Afonso Sampaio
 * @author Gustavo Ferreira
 * @author Samuel Correia
 */
public class DirectoryHelper {

    /**
     * @param context The current servlet context.
     * @param dirInitParameter The init parameter for the directory, as specified in web.xml.
     * @return The full path of the directory given by the init parameter, as defined in web.xml
     */
    public static File getDirectory(ServletContext context, String dirInitParameter) {
        String dirName = context.getInitParameter(dirInitParameter);

        File dir = new File(context.getRealPath("") + File.separator + dirName);
        if (!dir.exists() && dir.mkdirs())
            System.out.println("Directory '" + dirName + "' not present, has been created.");

        return dir;
    }

    /**
     * @param path The path to the file.
     * @return The name of the file stored at the given path.
     */
    public static String getFileName(String path) {
        if (path == null)
            return null;
        return new File(path).getName();
    }

    /**
     * How many files are currently stored in the given context's specified directory?
     * @param context The current servlet context.
     * @param dirInitParameter The init parameter for the directory, as specified in web.xml.
     * @return The number of files in the directory, if applicable; 0 otherwise.
     */
    public static int getDirectoryFileCount(ServletContext context, String dirInitParameter) {
        File[] files = getFiles(context, dirInitParameter);
        if (files != null)
            return files.length;
        return 0;
    }

    /**
     * @param context The current servlet context.
     * @param dirInitParameter The init parameter for the directory, as specified in web.xml.
     * @return An array containing all files in the given directory.
     */
    public static File[] getFiles(ServletContext context, String dirInitParameter) {
        File dir = getDirectory(context, dirInitParameter);
        if (dir.exists() && dir.isDirectory())
            return dir.listFiles();
        return new File[] { };
    }

    /**
     * Is the given context's directory full?
     * @param context The current servlet context.
     * @param dirInitParameter The init parameter for the directory, as specified in web.xml.
     * @param fileLimitInitParameter The init parameter specifying the maximum number of files in a directory,
     *                               as specified in web.xml.
     * @return True if the number of stored files surpasses the limit, as defined in web.xml; False otherwise.
     */
    private static boolean isDirectoryFull(ServletContext context, String dirInitParameter, String fileLimitInitParameter) {
        return getDirectoryFileCount(context, dirInitParameter) > Integer.parseInt(context.getInitParameter(fileLimitInitParameter));
    }

    /**
     * @param context The current servlet context.
     * @param dirInitParameter The init parameter for the directory, as specified in web.xml.
     * @return The file with the oldest "last modified" timestamp in the given context's directory, if
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
     * Deletes the oldest file stored in the upload directory and, if applicable, its matching query history file.
     * @param context The current servlet context.
     */
    private static void purgeOldestFileInUploadDirectory(ServletContext context) {
        File oldestUpload = getOldestStoredFile(context, "upload-dir");
        if (oldestUpload == null)
            return;

        File history = getMatchingHistoryFile(context, oldestUpload.getPath());

        if (oldestUpload.delete()) { // Delete oldest ontology file.
            OWLMaster.purgeOntologyMap(oldestUpload.getAbsolutePath());
            System.out.println("Successfuly deleted oldest file in directory '" + context.getInitParameter("upload-dir") +"' and cleared ontology map.");
        }

        if (history != null && history.delete())
            System.out.println("Successfuly deleted file'" + history.getName() + "' in directory '" + context.getInitParameter("query-history-dir") +"'.");
    }

    /**
     * Deletes the oldest file in the context's upload directory, but only if the number of files in this directory
     * surpasses the limit defined in web.xml; Otherwise, does nothing.
     * @param context The current servlet context.
     * @param fileLimitInitParameter The init parameter specifying the maximum number of files in a directory,
     *                               as specified in web.xml.
     */
    public static void purgeUploadDirectoryIfFull(ServletContext context, String fileLimitInitParameter) {
        if (isDirectoryFull(context, "upload-dir", fileLimitInitParameter))
            purgeOldestFileInUploadDirectory(context);
    }

    // TODO in the methods below:
    // I don't like that DirectoryHelper is using OWLAPI classes or OWLMaster methods
    // Matching with 'fileName == encodedOntologyID' is a quick hack. Find a better solution

    /**
     * Is the specified File the given ontology's query history file?
     * @return True if the filename is equal to the ontology's ID; False, otherwise.
     */
    private static boolean isMatchingHistoryFile(OWLOntology ontology, File file) {
        if (ontology == null)
            return false;
        return file.getName().equals(OWLMaster.getEncodedOntologyID(ontology));
    }

    /**
     * @param context The current servlet context.
     * @return The ontology's query history file, if present; Creates the file and returns it, otherwise.
     */
    public static File getMatchingHistoryFile(ServletContext context, String ontologyKbPath) {
        OWLOntology ontology = OWLMaster.getOntologyFromFile(ontologyKbPath);
        if (ontology == null)
            return null;

        return Arrays.stream(getFiles(context, "query-history-dir"))
                .filter(file -> isMatchingHistoryFile(ontology, file))
                .findFirst()
                .orElse(new File(getDirectory(context, "query-history-dir"), OWLMaster.getEncodedOntologyID(ontology)));
    }
}

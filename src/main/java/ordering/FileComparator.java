package ordering;

import java.io.File;
import java.util.Comparator;

/**
 * Compares two {@link File files} using their last modified timestamp.
 * @author Afonso Caniço
 * @author Afonso Sampaio
 * @author Gustavo Ferreira
 * @author Samuel Correia
 */
public class FileComparator implements Comparator<File> {

    @Override
    public int compare(File o1, File o2) {
        long comp = o2.lastModified() - o1.lastModified();
        if (comp > 0) // o1 is older
            return 1;
        else if (comp < 0) // o2 is older
            return -1;
        return 0; // Same modified timestamp
    }
}

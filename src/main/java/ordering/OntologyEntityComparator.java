package ordering;

import org.semanticweb.owlapi.model.OWLEntity;

import java.util.Comparator;

/**
 * Compares two {@link OWLEntity OWL entities} using their ID strings.
 * @author Afonso Caniço
 * @author Afonso Sampaio
 * @author Gustavo Ferreira
 * @author Samuel Correia
 */
public class OntologyEntityComparator implements Comparator<OWLEntity> {

    @Override
    public int compare(OWLEntity o1, OWLEntity o2) {
        return String.CASE_INSENSITIVE_ORDER.compare(o1.toStringID(), o2.toStringID());
    }
}

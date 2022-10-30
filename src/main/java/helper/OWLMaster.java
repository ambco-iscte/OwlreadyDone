package helper;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Contains helper methods for interacting with OWL knowledge bases.
 */
public class OWLMaster {

    private static final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();

    /**
     * Loads an OWL knowledge base from the file at the given path.
     * @param kbPath The path to the OWL knowledge base file.
     * @return The ontology present in the specified knowledge base.
     */
    public static OWLOntology getOntologyFromFile(String kbPath) {
        File file = new File(kbPath);
        try {
            if (file.exists())
                return ontologyManager.loadOntologyFromOntologyDocument(file);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    /**
     * @param ontology The OWL ontology you want to get the signature from.
     * @return The signature (set of entities) of the given ontology.
     */
    public static Set<OWLEntity> getOntologySignature(OWLOntology ontology) {
        if (ontology == null)
            return null;
        return ontology.getSignature();
    }

    /**
     * Loads an ontology from a given OWL knowledge base file and returns its signature.
     * @param kbPath The path to the OWL knowledge base file.
     * @return The signature (set of entities) of the loaded ontology.
     */
    public static Set<OWLEntity> getOntologySignature(String kbPath) {
        OWLOntology ontology = getOntologyFromFile(kbPath);
        if (ontology != null)
            return getOntologySignature(ontology);
        return new HashSet<>();
    }
}

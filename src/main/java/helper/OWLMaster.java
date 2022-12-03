package helper;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.swrlapi.builtins.AbstractSWRLBuiltInLibrary;

import java.io.File;
import java.util.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Contains helper methods for interacting with OWL knowledge bases.
 * @author Afonso Caniço
 * @author Afonso Sampaio
 * @author Gustavo Ferreira
 * @author Samuel Correia
 */
public class OWLMaster {

    private static final Map<String, OWLOntology> ontologies = new HashMap<>();

    /**
     * Deletes a mapping from the stored ontology map.
     * @param kbPath The knowledge base location of the ontology to be removed.
     */
    public static void purgeOntologyMap(String kbPath) {
        ontologies.remove(kbPath);
    }

    /**
     * Loads an OWL knowledge base from the file at the given path.
     * @param kbPath The path to the OWL knowledge base file.
     * @return The ontology present in the specified knowledge base.
     */
    public static OWLOntology getOntologyFromFile(String kbPath) {
        if (ontologies.containsKey(kbPath)) // Optimisation - only load each file once, store the ontology.
            return ontologies.get(kbPath);

        File file = new File(kbPath);
        try {
            if (file.exists()) {
                OWLOntology onto = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(file);
                ontologies.putIfAbsent(kbPath, onto);
                return onto;
            }
        } catch (Exception ex) {
            System.err.println("Couldn't load ontology from file: " + ex.getMessage());
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

    /**
     * @return The readable name of the OWL entity.
     */
    private static String getEntityReadableName(OWLEntity entity) {
        if (entity == null)
            return null;
        return entity.toStringID().split("#")[1];
    }

    /**
     * @param kbPath The path to the ontology's knowledge base.
     * @return A set of strings corresponding to the names of every OWLClass in the given ontology's signature.
     */
    public static Set<String> getOntologyClassNames(String kbPath) {
        return getOntologyClassNames(getOntologyFromFile(kbPath));
    }

    /**
     * @param kbPath The path to the ontology's knowledge base.
     * @return A set of strings corresponding to the names of every OWL Object Property in the given ontology's signature.
     */
    public static Set<String> getOntologyObjectPropertyNames(String kbPath) {
        return getOntologyObjectPropertyNames(getOntologyFromFile(kbPath));
    }

    /**
     * @param kbPath The path to the ontology's knowledge base.
     * @return A set of strings corresponding to the names of every OWL Data Property in the given ontology's signature.
     */
    public static Set<String> getOntologyDataPropertyNames(String kbPath) {
        return getOntologyDataPropertyNames(getOntologyFromFile(kbPath));
    }

    /**
     * @param kbPath The path to the ontology's knowledge base.
     * @return A set of strings corresponding to the names of every relation (object/data property, built-in, etc.)
     * in the given ontology.
     */
    public static Set<String> getAllRelationNames(String kbPath) {
        return getAllRelationNames(getOntologyFromFile(kbPath));
    }

    /**
     * @param kbPath The path to the ontology's knowledge base.
     * @return A set of strings corresponding to the names of every OWL Individual in the given ontology's signature.
     */
    public static Set<String> getOntologyIndividualNames(String kbPath) {
        return getOntologyIndividualNames(getOntologyFromFile(kbPath));
    }

    /**
     * @return A set of strings corresponding to the names of every OWLClass in the given ontology's signature.
     */
    private static Set<String> getOntologyClassNames(OWLOntology ontology) {
        if (ontology == null)
            return null;
        return Helper.map(ontology.getClassesInSignature(), OWLMaster::getEntityReadableName);
    }

    /**
     * @return A set of strings corresponding to the names of every OWL Data Property in the given ontology's signature.
     */
    private static Set<String> getOntologyDataPropertyNames(OWLOntology ontology) {
        if (ontology == null)
            return null;
        return Helper.map(ontology.getDataPropertiesInSignature(), OWLMaster::getEntityReadableName);
    }

    /**
     * @return A set of strings corresponding to the names of every OWL Object Property in the given ontology's signature.
     */
    private static Set<String> getOntologyObjectPropertyNames(OWLOntology ontology) {
        if (ontology == null)
            return null;
        return Helper.map(ontology.getObjectPropertiesInSignature(), OWLMaster::getEntityReadableName);
    }

    /**
     * @return A set of strings corresponding to the names of every OWL Individual in the given ontology's signature.
     */
    private static Set<String> getOntologyIndividualNames(OWLOntology ontology) {
        if (ontology == null)
            return null;
        return Helper.map(ontology.getIndividualsInSignature(), OWLMaster::getEntityReadableName);
    }

    /**
     * @return A set of strings corresponding to the names of every relation (object/data property, built-in, etc.)
     * in the given ontology.
     */
    private static Set<String> getAllRelationNames(OWLOntology ontology) {
        Set<String> relations = new HashSet<>();
        relations.add("is a");
        relations.add("is the same as");
        relations.add("is different from");
        relations.addAll(getOntologyObjectPropertyNames(ontology));
        relations.addAll(getOntologyDataPropertyNames(ontology));
        //relations.addAll(getPrefixedBuiltInNames("swrlb"));
        return relations;
    }

    /**
     * @param prefix The built-in prefix. E.g: swrlb, sqwrl, etc.
     * @return The set of all (prefixed) names of the built-ins associated with the given prefix.
     */
    public static Set<String> getPrefixedBuiltInNames(String prefix) {
        try {
            Class<?> library = Class.forName("org.swrlapi.builtins." + prefix + ".SWRLBuiltInLibraryImpl");
            if (AbstractSWRLBuiltInLibrary.class.isAssignableFrom(library)) {
                Set<String> names = ((AbstractSWRLBuiltInLibrary) library.getConstructor().newInstance()).getBuiltInNames();
                return Helper.map(names, x -> prefix + ":" + x);
            }
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
        return new HashSet<>();
    }
}

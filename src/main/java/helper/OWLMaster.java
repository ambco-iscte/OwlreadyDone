package helper;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        relations.add("isA");
        relations.add("isTheSameAs");
        relations.add("isDifferentFrom");
        relations.addAll(getOntologyObjectPropertyNames(ontology));
        relations.addAll(getOntologyDataPropertyNames(ontology));
        return relations;
    }

    /**
     * Queries an ontology using the given SQWRL query string.
     * @param ontology The ontology to query.
     * @param query The SQWRL query to execute.
     * @return The result (SQWRLResult instance) of the query execution.
     */
    public static SQWRLResult query(OWLOntology ontology, String query) {
        if (ontology == null || query == null)
            return null;

        try {
            SQWRLQueryEngine queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(ontology);
            for (IRI iri : queryEngine.getSWRLRuleEngine().getSWRLBuiltInIRIs())
                System.out.println(iri);
            return queryEngine.runSQWRLQuery("q1", query);
        } catch (SWRLParseException | SQWRLException ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    /**
     * Queries an ontology from a given knowledge base file using the given SQWRL query string.
     * @param kbPath The path to the desired ontology's knowledge base file.
     * @param query The SQWRL query to execute.
     * @return The result (SQWRLResult instance) of the query execution.
     */
    public static SQWRLResult query(String kbPath, String query) {
        return query(getOntologyFromFile(kbPath), query);
    }
}

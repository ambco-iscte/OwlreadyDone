package helper;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.swrlapi.builtins.AbstractSWRLBuiltInLibrary;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Contains helper methods for interacting with OWL knowledge bases.
 * @author Afonso Caniço
 * @author Afonso Sampaio
 * @author Gustavo Ferreira
 * @author Samuel Correia
 */
public class OWLMaster {

    private static final Map<String, OWLOntology> ontologies = new HashMap<>();
    private static final Map<OWLOntology, SQWRLQueryEngine> queryEngines = new HashMap<>();

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
            if (file.exists()) { // TODO: silence internal loadOntologyFromOntologyDocument exceptions?
                OWLOntology onto = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(file);
                ontologies.putIfAbsent(kbPath, onto);
                return onto;
            }
        } catch (OWLOntologyCreationException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    /**
     * Loads an OWL knowledge base from the given file.
     * @param kbFile An OWL knowledge base file.
     * @return The ontology present in the specified knowledge base.
     */
    public static OWLOntology getOntologyFromFile(File kbFile) {
        return getOntologyFromFile(kbFile.getPath());
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
        String[] tokens = entity.toStringID().split("#");
        if (tokens.length > 1)
            return tokens[1];
        return tokens[0];
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
        relations.add("sameAs");
        relations.add("differentFrom");
        relations.addAll(getOntologyObjectPropertyNames(ontology));
        relations.addAll(getOntologyDataPropertyNames(ontology));
        relations.addAll(getPrefixedBuiltInNames("abox"));
        relations.addAll(getPrefixedBuiltInNames("rbox"));
        relations.addAll(getPrefixedBuiltInNames("tbox"));
        relations.addAll(getPrefixedBuiltInNames("swrlb"));
        relations.addAll(getPrefixedBuiltInNames("swrlm"));
        relations.addAll(getPrefixedBuiltInNames("swrlx"));
        return relations;
    }

    /**
     * @param prefix The built-in prefix. E.g: 'swrlb', 'sqwrl', etc.
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
            ex.printStackTrace(System.err);
        }
        return new HashSet<>();
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
            return getQueryEngine(ontology).runSQWRLQuery(String.valueOf(System.currentTimeMillis()), query);
        } catch (SWRLParseException | SQWRLException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    /**
     * Gets the SQWRL Query Engine for the specified ontology. If none is present, a new one is created.
     * @param ontology An OWL ontology reference.
     * @return A SQWRL Query Engine associated with the given ontology.
     */
    private static SQWRLQueryEngine getQueryEngine(OWLOntology ontology) {
        if (ontology == null)
            return null;
        queryEngines.putIfAbsent(ontology, SWRLAPIFactory.createSQWRLQueryEngine(ontology));
        return queryEngines.get(ontology);
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

    /**
     * Is the specified file a valid OWL document?
     * @return True if the file is a valid, well-formed OWL file; False, otherwise.
     */
    public static boolean isValidOntologyFile(File file) {
        return file != null && !DirectoryHelper.isDeleteOnExit(file) && getOntologyFromFile(file.getPath()) != null;
    }

    /**
     * @return A filename-ready version of the given ontology's ID.
     */
    public static String getEncodedOntologyID(OWLOntology ontology) {
        if (ontology == null)
            return null;

        OWLOntologyID id = ontology.getOntologyID();
        String toUse = "";

        if (id.getVersionIRI().isPresent()) toUse = id.getVersionIRI().get().toString();
        else if (id.getOntologyIRI().isPresent()) toUse = id.getOntologyIRI().get().toString();

        // https://stackoverflow.com/questions/1184176/how-can-i-safely-encode-a-string-in-java-to-use-as-a-filename
        return "owl-" + URLEncoder.encode(toUse, StandardCharsets.UTF_8);
    }
}

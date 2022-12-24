package helper;

import ordering.OntologyEntityComparator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semarglproject.vocab.OWL;
import org.swrlapi.builtins.AbstractSWRLBuiltInLibrary;
import org.swrlapi.builtins.SWRLBuiltInLibraryManager;
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
import java.util.stream.Collectors;

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
    private static final Set<String> swrlApiPrefixes = getAllSWRLAPIPrefixedNames(); // Pre-built for optimization.
    private static final Set<IRI> owlVocabularyIRIs; // IRIs of OWL vocabulary-reserved classes, properties, etc.

    static { // Initialise list of OWL vocabulary-reserved IRIs from org.semarglproject.vocab.OWL static attributes.
        owlVocabularyIRIs = Arrays.stream(OWL.class.getDeclaredFields())
                .filter(x -> x.getType().equals(String.class))
                .map(x -> IRI.create(Objects.requireNonNull(Helper.getFieldValue(x, null, String.class))))
                .collect(Collectors.toSet());
    }

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
    public static SortedSet<OWLEntity> getOntologySignature(OWLOntology ontology) {
        if (ontology == null)
            return null;
        return Helper.collectionToSortedSet(ontology.getSignature(), new OntologyEntityComparator());
    }

    /**
     * Loads an ontology from a given OWL knowledge base file and returns its signature.
     * @param kbPath The path to the OWL knowledge base file.
     * @return The signature (set of entities) of the loaded ontology.
     */
    public static SortedSet<OWLEntity> getOntologySignature(String kbPath) {
        OWLOntology ontology = getOntologyFromFile(kbPath);
        if (ontology != null)
            return getOntologySignature(ontology);
        return new TreeSet<>();
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
    public static SortedSet<String> getOntologyClassNames(String kbPath) {
        return getOntologyClassNames(getOntologyFromFile(kbPath));
    }

    /**
     * @param kbPath The path to the ontology's knowledge base.
     * @return A set of strings corresponding to the names of every OWL Object Property in the given ontology's signature.
     */
    public static SortedSet<String> getOntologyObjectPropertyNames(String kbPath) {
        return getOntologyObjectPropertyNames(getOntologyFromFile(kbPath));
    }

    /**
     * @param kbPath The path to the ontology's knowledge base.
     * @return A set of strings corresponding to the names of every OWL Data Property in the given ontology's signature.
     */
    public static SortedSet<String> getOntologyDataPropertyNames(String kbPath) {
        return getOntologyDataPropertyNames(getOntologyFromFile(kbPath));
    }

    /**
     * @param kbPath The path to the ontology's knowledge base.
     * @return A set of strings corresponding to the names of every relation (object/data property, built-in, etc.)
     * in the given ontology.
     */
    public static SortedSet<String> getAllRelationNames(String kbPath) {
        return getAllRelationNames(getOntologyFromFile(kbPath));
    }

    /**
     * @param kbPath The path to the ontology's knowledge base.
     * @return A set of strings corresponding to the names of every OWL Individual in the given ontology's signature.
     */
    public static SortedSet<String> getOntologyIndividualNames(String kbPath) {
        return getOntologyIndividualNames(getOntologyFromFile(kbPath));
    }

    /**
     * @return A set of strings corresponding to the names of every OWLClass in the given ontology's signature.
     */
    private static SortedSet<String> getOntologyClassNames(OWLOntology ontology) {
        if (ontology == null)
            return null;
        return getOrderedEntityNames(ontology.getClassesInSignature());
    }

    /**
     * @return A set of strings corresponding to the names of every OWL Data Property in the given ontology's signature.
     */
    private static SortedSet<String> getOntologyDataPropertyNames(OWLOntology ontology) {
        if (ontology == null)
            return null;
        return getOrderedEntityNames(ontology.getDataPropertiesInSignature());
    }

    /**
     * @return A set of strings corresponding to the names of every OWL Object Property in the given ontology's signature.
     */
    private static SortedSet<String> getOntologyObjectPropertyNames(OWLOntology ontology) {
        if (ontology == null)
            return null;
        return getOrderedEntityNames(ontology.getObjectPropertiesInSignature());
    }

    /**
     * @return A set of strings corresponding to the names of every OWL Individual in the given ontology's signature.
     */
    private static SortedSet<String> getOntologyIndividualNames(OWLOntology ontology) {
        if (ontology == null)
            return null;
        return getOrderedEntityNames(ontology.getIndividualsInSignature());
    }

    /**
     * Constructs an ordered set of the OWL entities in the given collection.
     * @param collection A collection of {@link OWLEntity OWL entities}.
     * @return A {@link SortedSet sorted set} of the human-readable names of all the OWL entities in the collection.
     */
    private static SortedSet<String> getOrderedEntityNames(Collection<? extends OWLEntity> collection) {
        return Helper.mapSorted(collection, x -> {
            String readableName = getEntityReadableName(x);
            if (owlVocabularyIRIs != null && owlVocabularyIRIs.contains(x.getIRI()))
                readableName = "owl:" + readableName;
            return readableName;
        }, String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * @return A set of strings corresponding to the names of every relation (object/data property, built-in, etc.)
     * in the given ontology.
     */
    private static SortedSet<String> getAllRelationNames(OWLOntology ontology) {
        SortedSet<String> relations = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        relations.add("is a");
        relations.add("sameAs");
        relations.add("differentFrom");
        relations.addAll(getOntologyObjectPropertyNames(ontology));
        relations.addAll(getOntologyDataPropertyNames(ontology));
        relations.addAll(getAllValidAntecedentPrefixedNames());
        return relations;
    }

    /**
     * Gets all the prefixed operator names defined by SWRLAPI that can be used in the antecedent of a SQWRL query.
     * See {@link OWLMaster#getAllSWRLAPIPrefixedNames}.
     * @return A set of Strings corresponding to the prefixed operator names, excluding those with prefix "sqwrl:".
     */
    private static Set<String> getAllValidAntecedentPrefixedNames() {
        return swrlApiPrefixes.stream().filter(name -> !Objects.equals(name.split(":")[0], "sqwrl"))
                .collect(Collectors.toSet());
    }

    /**
     * Gets all the prefixed operator names defined by
     * <a href="https://github.com/protegeproject/swrlapi/wiki/SWRLAPIBuiltInLibraries">SWRLAPI</a>.
     * @return A set of Strings corresponding to the prefixed operator names.
     */
    private static Set<String> getAllSWRLAPIPrefixedNames() {
        SWRLBuiltInLibraryManager manager = new SWRLBuiltInLibraryManager();
        return manager.getSWRLBuiltInIRIs().stream().map(x -> manager.swrlBuiltInIRI2PrefixedName(x).orElse(""))
                .collect(Collectors.toSet());
    }

    /**
     * @param prefix The built-in prefix. E.g: 'swrlb', 'sqwrl', etc.
     * @return The set of all (prefixed) names of the built-ins associated with the given prefix.
     */
    public static SortedSet<String> getPrefixedBuiltInNames(String prefix) {
        try {
            Class<?> library = Class.forName("org.swrlapi.builtins." + prefix + ".SWRLBuiltInLibraryImpl");
            if (AbstractSWRLBuiltInLibrary.class.isAssignableFrom(library)) {
                Set<String> names = ((AbstractSWRLBuiltInLibrary) library.getConstructor().newInstance()).getBuiltInNames();
                return Helper.mapSorted(names, x -> prefix + ":" + x, String.CASE_INSENSITIVE_ORDER);
            }
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException ex) {
            ex.printStackTrace(System.err);
        }
        return new TreeSet<>();
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

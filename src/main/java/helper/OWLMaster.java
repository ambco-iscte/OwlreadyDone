package helper;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;
import org.swrlapi.sqwrl.values.SQWRLNamedIndividualResultValue;

import java.io.File;
import java.util.*;

/**
 * Contains helper methods for interacting with OWL knowledge bases.
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

    public static OWLOntology resultToOntology(SQWRLResult result, OWLOntology originalOntology) throws OWLOntologyCreationException {
        //TODO
        String document_iri = "http://www.semanticweb.org/owlreadyDone/ontologies/2022/10/result_template.owl";
        try {
            if (result.isEmpty()) {
                return null;
            }

            while (result.next()){
                if (result.hasNamedIndividualValue("x"))
                    result.getNamedIndividual("x");

                /*
                if (result.hasLiteralValue("x"))
                    result.getLiteral("x");

                if (result.hasClassValue("x"))
                    result.getClass("x");
                */
            }
        }
        catch(SQWRLException ex) { ex.printStackTrace(); }


        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLOntology ontology = manager.createOntology(IRI.create(document_iri));
        DefaultPrefixManager pm = new DefaultPrefixManager();
        pm.setDefaultPrefix(document_iri + "#");
        pm.setPrefix("var:", "urn:swrl#");

        //for each relevant class
        String className = "";
        OWLClass xClass = factory.getOWLClass(className, pm);
        manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(xClass));

        //for each individual
        String name = "";
        OWLNamedIndividual xIndividual = factory.getOWLNamedIndividual(name, pm);
        manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(xIndividual));


        //for relevant subclass axions
        String xAxiomIdentifier = "";
        OWLAnnotationProperty annotationProperty = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());
        OWLAnnotationValue value = factory.getOWLLiteral(xAxiomIdentifier);
        OWLAnnotation annotation = factory.getOWLAnnotation(annotationProperty, value);
        //being that 1st is subclass and 2nd is superclass
        OWLSubClassOfAxiom subClassOfAxiom = factory.getOWLSubClassOfAxiom(xClass, xClass, Collections.singleton(annotation));
        manager.addAxiom(ontology, subClassOfAxiom);


        //associate individuals
        manager.addAxiom(ontology, factory.getOWLClassAssertionAxiom(xClass, xIndividual));

        //for each object property
        String propertyName = "";
        OWLObjectProperty xProperty = factory.getOWLObjectProperty(propertyName, pm);
        manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(xProperty));
        //property, individual with property, to what the property relates.
        manager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(xProperty, xIndividual, xIndividual));


        //save to a file
        //como especificar o destino?
        try {
            FunctionalSyntaxDocumentFormat ontologyFormat = new FunctionalSyntaxDocumentFormat();
            ontologyFormat.copyPrefixesFrom(pm);
            manager.saveOntology(ontology, ontologyFormat, IRI.create(new File("target/OwlreadyDone/example.owl").toURI()));
        } catch (OWLOntologyStorageException e) {
            throw new RuntimeException(e);
        }

        return ontology;
    }
}

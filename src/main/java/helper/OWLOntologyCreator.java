package helper;

import jakarta.servlet.ServletContext;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;
import org.swrlapi.sqwrl.values.SQWRLNamedIndividualResultValue;

import java.io.File;

/**
 * Contains helper methods for the creation of a new ontology.
 */
public class OWLOntologyCreator {


    public static OWLOntology resultToOntology(SQWRLResult result, OWLOntology originalOntology, ServletContext context, Boolean saveFile) throws OWLOntologyCreationException {
        //TODO
        //String document_iri = "http://www.semanticweb.org/owlreadyDone/ontologies/2022/10/result.owl";

        //get if present
        IRI document_iri = originalOntology.getOntologyID().getOntologyIRI().get();
        //originalOntology. obtain the IRI?
        //obtain prefixes from ontology
        System.out.println(originalOntology.getOntologyID().getOntologyIRI());

        OWLDocumentFormat format =  originalOntology.getOWLOntologyManager().getOntologyFormat(originalOntology);
        //format.asPrefixOWLOntologyFormat().getpre
        //originalOntology.getOntologyID().

        try {
            if (result.isEmpty()) {
                return null;
            }

            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLDataFactory factory = manager.getOWLDataFactory();

            OWLOntology ontology = manager.createOntology(document_iri);
            DefaultPrefixManager pm = new DefaultPrefixManager();
            pm.setDefaultPrefix(document_iri + "#");
            pm.setPrefix("pizza:", "urn:swrl#");

            while (result.next()){
                if (result.hasNamedIndividualValue("x")) {
                    //a partir do iri do resultado pode ser mais fácil obter info
                    SQWRLNamedIndividualResultValue individual = result.getNamedIndividual("x");
                    //originalOntology.
                    System.out.println("Added named individual to query result ontology");
                    OWLNamedIndividual xIndividual = factory.getOWLNamedIndividual(individual.toString(), pm);
                    manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(xIndividual));

                    //System.out.println(xIndividual.asOWLClass().toString());
                }

                /*
                if (result.hasLiteralValue("x"))
                    result.getLiteral("x");

                if (result.hasClassValue("x"))
                    result.getClass("x");
                */
            }
            //Reset to first result row
            result.reset();

            if(saveFile){
                try {
                    FunctionalSyntaxDocumentFormat ontologyFormat = new FunctionalSyntaxDocumentFormat();
                    ontologyFormat.copyPrefixesFrom(pm);
                    File file = new File(DirectoryHelper.getDirectory(context, "result-dir")
                            + File.separator + "result.owl");
                    manager.saveOntology(ontology, ontologyFormat, IRI.create(file.toURI()));

                } catch (OWLOntologyStorageException e) {
                    throw new RuntimeException(e);
                }
            }

            return ontology;

        }
        catch(SQWRLException ex) { ex.printStackTrace(); }

        return null;
        /*

        //for each relevant class
        String className = "";
        OWLClass xClass = factory.getOWLClass(className, pm);
        manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(xClass));

        //for each individual

        //for relevant subclass axioms
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

         */
    }

}

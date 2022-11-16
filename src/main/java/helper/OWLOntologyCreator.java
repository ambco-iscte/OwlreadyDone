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
        String document_iri = "http://www.semanticweb.org/owlreadyDone/ontologies/2022/10/result_template.owl";


        try {
            if (result.isEmpty()) {
                return null;
            }

            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLDataFactory factory = manager.getOWLDataFactory();

            OWLOntology ontology = manager.createOntology(IRI.create(document_iri));
            DefaultPrefixManager pm = new DefaultPrefixManager();
            pm.setDefaultPrefix(document_iri + "#");
            pm.setPrefix("var:", "urn:swrl#");

            addAxiomsToManager(result, manager, factory, ontology, pm);

            if(saveFile){
                //save to a file
                //como especificar o destino?
                try {
                    FunctionalSyntaxDocumentFormat ontologyFormat = new FunctionalSyntaxDocumentFormat();
                    ontologyFormat.copyPrefixesFrom(pm);
                    //doesn't save ontologies yet, fix but do so the proper way.
                    manager.saveOntology(ontology, ontologyFormat, IRI.create(context.getInitParameter("upload-dir")
                            + File.separator + "example.owl"));

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

    private static void addAxiomsToManager(SQWRLResult result, OWLOntologyManager manager, OWLDataFactory factory, OWLOntology ontology, DefaultPrefixManager pm) throws SQWRLException {
        while (result.next()){
            if (result.hasNamedIndividualValue("x")) {
                //a partir do iri do resultado pode ser mais fácil obter info
                //SQWRLNamedIndividualResultValue res = result.getNamedIndividual("x");
                //originalOntology.
                //res.getIRI();
                OWLNamedIndividual xIndividual = factory.getOWLNamedIndividual(result.getNamedIndividual("x").toString(), pm);
                manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(xIndividual));
            }

            /*
            if (result.hasLiteralValue("x"))
                result.getLiteral("x");

            if (result.hasClassValue("x"))
                result.getClass("x");
            */
        }
    }
}

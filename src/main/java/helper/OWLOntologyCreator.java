package helper;

import com.google.common.base.Optional;
import jakarta.servlet.ServletContext;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitorEx;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;
import org.swrlapi.sqwrl.values.SQWRLClassResultValue;
import org.swrlapi.sqwrl.values.SQWRLNamedIndividualResultValue;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.io.IOException;

import static helper.SubmitToGitHub.createFile;

/**
 * Contains helper methods for the creation of a new ontology.
 */
public class OWLOntologyCreator {


    public static OWLOntology resultToOntology(SQWRLResult result, OWLOntology originalOntology, ServletContext context, Boolean saveFile, String fileName)
            throws OWLOntologyCreationException, ClassNotFoundException {
        //TODO
        //String document_iri = "http://www.semanticweb.org/owlreadyDone/ontologies/2022/10/result.owl";

        //get IRI if present
        Optional<IRI> document_iri_optional = originalOntology.getOntologyID().getOntologyIRI();
        IRI document_iri = null;
        if(document_iri_optional.isPresent())
             document_iri = document_iri_optional.get();
        if(document_iri == null) {
            //throw exceptions
            System.out.println("document_iri is null");
            return null;
        }

        OWLOntologyManager manager = originalOntology.getOWLOntologyManager();
        OWLDocumentFormat format = manager.getOntologyFormat(originalOntology);

        if(format == null) {
            System.out.println("format is null");
            return null;
        }

        try {
            if (result.isEmpty()) {
                System.out.println("result is null");
                return null;
            }

            document_iri = IRI.create(document_iri + "_result");

            OWLDataFactory factory = manager.getOWLDataFactory();
            OWLOntology ontology = manager.createOntology(document_iri);
            DefaultPrefixManager pm = new DefaultPrefixManager();
            pm.setDefaultPrefix(document_iri + "#");
            for (Map.Entry<String,String> entry : format.asPrefixOWLOntologyFormat().getPrefixName2PrefixMap().entrySet())
                pm.setPrefix(entry.getKey(), entry.getValue());

            //OWLReasonerFactory reasonerFactory = (OWLReasonerFactory) Class.forName(null).newInstance();
            //OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(originalOntology);

            while (result.next()){

                //for each relevant class
                String className = "resultClass";
                OWLClass xClass = factory.getOWLClass(className, pm);
                manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(xClass));

                if (result.hasNamedIndividualValue("x")) {
                    //x deve depender do target da query

                    SQWRLNamedIndividualResultValue individual = result.getNamedIndividual("x");
                    System.out.println("Added named individual to query result ontology");
                    OWLNamedIndividual xIndividual = factory.getOWLNamedIndividual(individual.toString(), pm);
                    manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(xIndividual));

                    //associate individual to class
                    manager.addAxiom(ontology, factory.getOWLClassAssertionAxiom(xClass, xIndividual));
                }

                /*
                if (result.hasLiteralValue("x"))
                    result.getLiteral("x");

                if (result.hasClassValue("x")) {
                    SQWRLClassResultValue classr = result.getClass("x");
                    OWLClass xclassr = factory.getOWLClass(classr.toString(), pm);
                    if(!reasoner.isSatisfiable(xclassr)) {
                        System.out.println("XXX: " + classr.toString());
                    }
                }
                */
            }
            //Reset to first result row
            result.reset();

            if(saveFile){
                try {

                    File file = new File(DirectoryHelper.getDirectory(context, "result-dir")
                            + File.separator + "result_" + fileName);
                    manager.saveOntology(ontology, format, IRI.create(file.toURI()));
                    createFile(file.getAbsolutePath());
                    // guardar no rep -> apagar este ficheiro criado maybe?

                } catch (OWLOntologyStorageException | IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            manager.removeOntology(ontology);
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


        //associate individual to class
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

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
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;
import org.swrlapi.sqwrl.values.*;

import java.io.File;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;
import java.io.IOException;


import static helper.SubmitToGitHub.createFile;

/**
 * Contains helper methods for the creation of a new ontology.
 */
public class OWLOntologyCreator {


    public static File resultToOntology(SQWRLResult result, OWLQueryManager queryManager, ServletContext context, Boolean saveFile, String fileName)
            throws OWLOntologyCreationException, ClassNotFoundException {
        //System.out.println(fileName);
        //TODO
        //String document_iri = "http://www.semanticweb.org/owlreadyDone/ontologies/2022/10/result.owl";

        OWLOntology originalOntology = queryManager.getOntology();
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
            for (Map.Entry<String, String> entry : format.asPrefixOWLOntologyFormat().getPrefixName2PrefixMap().entrySet())
                pm.setPrefix(entry.getKey(), entry.getValue());

            //Reset to first row of results
            result.reset();
            while (result.next()) {
                addResultsToOntology(result, queryManager, manager, factory, ontology, pm);
            }

            File file = null;
            if (saveFile) {
                try {
                    Timestamp ts = new Timestamp(System.currentTimeMillis());
                    file = new File(DirectoryHelper.getDirectory(context, "result-dir")
                            + File.separator + "result_" + ts.getTime() + "_" + fileName);
                    manager.saveOntology(ontology, format, IRI.create(file.toURI()));

                    // guardar no rep -> apagar este ficheiro criado maybe?

                } catch (OWLOntologyStorageException e) {
                    throw new RuntimeException(e);
                }
            }

            manager.removeOntology(ontology);
            return file;

        }
        catch(SQWRLException ex) { ex.printStackTrace(); }
        catch (SWRLParseException e) { e.printStackTrace();}

        return null;
        /*
        //for relevant subclass axioms
        String xAxiomIdentifier = "";
        OWLAnnotationProperty annotationProperty = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());
        OWLAnnotationValue value = factory.getOWLLiteral(xAxiomIdentifier);
        OWLAnnotation annotation = factory.getOWLAnnotation(annotationProperty, value);
        //being that 1st is subclass and 2nd is superclass
        OWLSubClassOfAxiom subClassOfAxiom = factory.getOWLSubClassOfAxiom(xClass, xClass, Collections.singleton(annotation));
        manager.addAxiom(ontology, subClassOfAxiom);

        //for each object property
        String propertyName = "";
        OWLObjectProperty xProperty = factory.getOWLObjectProperty(propertyName, pm);
        manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(xProperty));
        //property, individual with property, to what the property relates.
        manager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(xProperty, xIndividual, xIndividual));

         */

    }

    private static void addResultsToOntology(SQWRLResult result, OWLQueryManager queryManager, OWLOntologyManager manager,
                                             OWLDataFactory factory, OWLOntology ontology, DefaultPrefixManager pm) throws SQWRLException, SWRLParseException {
        int numOfColumns = result.getNumberOfColumns();
        for(int i = 0; i < numOfColumns; i++){
            if (result.hasNamedIndividualValue(i)) {
                //x deve depender do target da query

                SQWRLNamedIndividualResultValue individual = result.getNamedIndividual(i);
                System.out.println("Individual: " + individual);
                OWLNamedIndividual xindividual = factory.getOWLNamedIndividual(individual.toString(), pm);
                manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(xindividual));

                //associate individual to class, as well as superclasses of classes
                getAndAddClasses(queryManager, manager, factory, ontology, pm, individual, xindividual);

                //estes dois métodos requerem mais testes,
                // com cada pesquisa destas demora mais tempo a criação da ontologia final
                getAndAddDataProperties(queryManager, manager, factory, ontology, pm, individual, xindividual);
                getAndAddObjectProperties(queryManager, manager, factory, ontology, pm, individual, xindividual);

            }


            if (result.hasLiteralValue(i)) {
                SQWRLLiteralResultValue literalResultValue = result.getLiteral(i);
                System.out.println(literalResultValue);
            }

            if (result.hasClassValue(i)) {
                SQWRLClassResultValue classr = result.getClass(i);
                System.out.println(classr);
                //OWLClass xclassr = factory.getOWLClass(classr.toString(), pm);
            }
        }
    }

    private static void getAndAddClasses(OWLQueryManager queryManager, OWLOntologyManager manager, OWLDataFactory factory,
                                         OWLOntology ontology, DefaultPrefixManager pm, SQWRLNamedIndividualResultValue individual,
                                         OWLNamedIndividual xindividual) throws SWRLParseException, SQWRLException {
        SQWRLResult classResults = queryManager.query("abox:caa(?x, "+ individual +") -> sqwrl:select(?x)");
        while(classResults.next()){
            if(classResults.hasClassValue("x")){
                SQWRLClassResultValue classr = classResults.getClass("x");
                System.out.println("Found class: " +classr+" of individual: " + individual);
                OWLClass xclassr = factory.getOWLClass(classr.toString(), pm);
                manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(xclassr));
                manager.addAxiom(ontology, factory.getOWLClassAssertionAxiom(xclassr, xindividual));

                //getAndAddSuperclasses(queryManager, manager, factory, ontology, pm, classResults, classr, xclassr);
            }
        }
    }

    private static void getAndAddDataProperties(OWLQueryManager queryManager, OWLOntologyManager manager, OWLDataFactory factory,
                                                OWLOntology ontology, DefaultPrefixManager pm, SQWRLNamedIndividualResultValue individual,
                                                OWLNamedIndividual xindividual) throws SWRLParseException, SQWRLException {
        SQWRLResult dataPropertyResults = queryManager.query("abox:dpaa("+ individual +", ?p, ?v) -> sqwrl:select(?p, ?v)");

        while(dataPropertyResults.next()){
            if(dataPropertyResults.hasObjectPropertyValue("p") && dataPropertyResults.hasLiteralValue("v")){
                SQWRLDataPropertyResultValue property = dataPropertyResults.getDataProperty("p");
                SQWRLLiteralResultValue value = dataPropertyResults.getLiteral("v");
                System.out.println("Found data property: " +property+" with value: " + value);
                OWLDataProperty xproperty = factory.getOWLDataProperty(property.toString(), pm);
                OWLLiteral xvalue = factory.getOWLLiteral(value.toString());
                manager.addAxiom(ontology, factory.getOWLDataPropertyAssertionAxiom(xproperty, xindividual, xvalue));
            }
        }
    }

    //esta função pode ser problemática quando adiciona referências a objetos que não estão declarados por si só.
    private static void getAndAddObjectProperties(OWLQueryManager queryManager, OWLOntologyManager manager, OWLDataFactory factory,
                                                  OWLOntology ontology, DefaultPrefixManager pm, SQWRLNamedIndividualResultValue individual,
                                                  OWLNamedIndividual xindividual) throws SWRLParseException, SQWRLException {
        SQWRLResult objectPropertyResults = queryManager.query("abox:opaa("+ individual +", ?p, ?o) -> sqwrl:select(?p, ?o)");

        while(objectPropertyResults.next()){
            if(objectPropertyResults.hasObjectPropertyValue("p") && objectPropertyResults.hasNamedIndividualValue("o")){
                SQWRLObjectPropertyResultValue property = objectPropertyResults.getObjectProperty("p");
                SQWRLNamedIndividualResultValue object = objectPropertyResults.getNamedIndividual("o");
                System.out.println("Found property: " +property+" with individual: " + object);
                OWLObjectProperty xproperty = factory.getOWLObjectProperty(property.toString(), pm);
                OWLNamedIndividual xobject = factory.getOWLNamedIndividual(object.toString(), pm);
                manager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(xproperty, xindividual, xobject));
            }
        }
    }


    private static void getAndAddSuperclasses(OWLQueryManager queryManager, OWLOntologyManager manager, OWLDataFactory factory,
                                              OWLOntology ontology, DefaultPrefixManager pm, SQWRLClassResultValue classr,
                                              OWLClass xclassr) throws SWRLParseException, SQWRLException {
        SQWRLResult superClassResults = queryManager.query("tbox:sca("+ classr +", ?x) -> sqwrl:select(?x)");
        while(superClassResults.next()){
            if(superClassResults.hasClassValue("x")){
                SQWRLClassResultValue sclassr = superClassResults.getClass("x");
                System.out.println("Found superclass: " +sclassr+" of class: " + classr);
                OWLClass xsclassr = factory.getOWLClass(sclassr.toString(), pm);
                manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(xsclassr));
                manager.addAxiom(ontology, factory.getOWLSubClassOfAxiom(xclassr, xsclassr));
            }
        }
    }
}

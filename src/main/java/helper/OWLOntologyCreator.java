package helper;

import jakarta.servlet.ServletContext;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;
import org.swrlapi.sqwrl.values.*;

import java.io.File;
import java.sql.Timestamp;
import java.util.Map;

/**
 * Contains helper methods for the creation of a new ontology.
 * @author Samuel Correia
 */
public class OWLOntologyCreator {

    /**
     * Considering the query results, creates a new ontology, and saves it. To create this new ontology it may query the
     * original ontology to add additional information to this ontology, besides the results.
     * @param result Result from the query, used to create new ontology of result.
     * @param context Servlet context, used to obtain the path where results are to be stored.
     * @param ontoKbPath The path to the original ontology's knowledge base file.
     * @return The file containing the query result with additional relevant information.
     * @throws OWLOntologyCreationException If there was an error when creating the ontology.
     */
    public static File resultToOntology(SQWRLResult result, ServletContext context, String ontoKbPath)
            throws OWLOntologyCreationException {

        OWLOntology originalOntology = OWLMaster.getOntologyFromFile(ontoKbPath);
        if (originalOntology == null)
            return null;

        //get IRI if present
        IRI documentIRI = originalOntology.getOntologyID().getOntologyIRI().orNull();
        if (documentIRI == null) {
            //throw exceptions
            System.err.println("Document IRI is null.");
            return null;
        }

        OWLOntologyManager manager = originalOntology.getOWLOntologyManager();
        OWLDocumentFormat format = manager.getOntologyFormat(originalOntology);

        if (format == null) {
            System.err.println("Ontology format is null.");
            return null;
        }

        try {
            if (result.isEmpty()) {
                System.err.println("Result is empty.");
                return null;
            }

            documentIRI = IRI.create(documentIRI + "_result");

            OWLDataFactory factory = manager.getOWLDataFactory();
            OWLOntology ontology = manager.createOntology(documentIRI);
            DefaultPrefixManager pm = new DefaultPrefixManager();
            pm.setDefaultPrefix(documentIRI + "#");

            for (Map.Entry<String, String> entry : format.asPrefixOWLOntologyFormat().getPrefixName2PrefixMap().entrySet()) {
                pm.setPrefix(entry.getKey(), entry.getValue());
            }

            // Reset to first row of results
            result.reset();
            while (result.next()) {
                addResultsToOntology(result, manager, factory, originalOntology, ontology, pm);
            }

            File file;
            try {
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                file = new File(DirectoryHelper.getDirectory(context, "result-dir")
                        + File.separator + "result_" + ts.getTime() + "_" + DirectoryHelper.getFileName(ontoKbPath));
                manager.saveOntology(ontology, format, IRI.create(file.toURI()));
            } catch (OWLOntologyStorageException e) {
                throw new RuntimeException(e);
            }

            manager.removeOntology(ontology);
            return file;

        } catch (SQWRLException ex) {
            ex.printStackTrace();
        }

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

    /**
     * Method that adds all query results and additional information to the new ontology
     * @param result Result from the query, used to create new ontology of result.
     * @param manager Ontology manager used to correctly add ontology axioms.
     * @param factory OWL data factory that is used to create ontology axioms.
     * @param originalOntology The original ontology's knowledge base.
     * @param ontology The new ontology to which information is added.
     * @param pm Prefix manager that supplies correct prefixes when creating elements of OWL ontologies.
     * @throws SQWRLException If there were errors when accessing the SQWRL Result.
     */
    private static void addResultsToOntology(SQWRLResult result, OWLOntologyManager manager, OWLDataFactory factory,
                                             OWLOntology originalOntology, OWLOntology ontology, DefaultPrefixManager pm) throws SQWRLException {
        int numOfColumns = result.getNumberOfColumns();
        for (int i = 0; i < numOfColumns; i++) {
            if (result.hasNamedIndividualValue(i)) {
                //x deve depender do target da query

                SQWRLNamedIndividualResultValue individual = result.getNamedIndividual(i);
                System.out.println("Individual: " + individual);
                OWLNamedIndividual xindividual = factory.getOWLNamedIndividual(individual.toString(), pm);
                manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(xindividual));

                try {
                    //associate individual to class, as well as superclasses of classes
                    getAndAddClasses(manager, factory, originalOntology, ontology, pm, individual, xindividual);

                    //estes dois métodos requerem mais testes,
                    // com cada pesquisa destas demora mais tempo a criação da ontologia final
                    getAndAddDataProperties(manager, factory, originalOntology, ontology, pm, individual, xindividual);
                    getAndAddObjectProperties(manager, factory, originalOntology, ontology, pm, individual, xindividual);
                } catch (SQWRLException | OWLRuntimeException e) {
                    e.printStackTrace();
                }

            }


            if (result.hasLiteralValue(i)) {
                SQWRLLiteralResultValue literal = result.getLiteral(i);
                System.out.println("Literal: " + literal);
                OWLLiteral xliteral = factory.getOWLLiteral(literal.toString());
                OWLIndividual xindividual = factory.getOWLNamedIndividual("Individual", pm);
                OWLDataProperty xproperty = factory.getOWLDataProperty("LiteralValue", pm);
                manager.addAxiom(ontology, factory.getOWLDataPropertyAssertionAxiom(xproperty, xindividual, xliteral));
            }

            if (result.hasClassValue(i)) {
                SQWRLClassResultValue classr = result.getClass(i);
                System.out.println(classr);
                OWLClass xclassr = factory.getOWLClass(classr.toString(), pm);
                manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(xclassr));
                getAndAddSuperclassesAndSubclasses(manager, factory, originalOntology, ontology, pm, classr, xclassr);
            }
        }
    }

    /**
     * Auxiliary method that, for a given individual, discovers its classes and adds them to the ontology.
     * @param manager Ontology manager used to correctly add ontology axioms.
     * @param factory OWL data factory that is used to create ontology axioms.
     * @param originalOntology The original ontology's knowledge base.
     * @param ontology The new ontology to which information is added.
     * @param pm Prefix manager that supplies correct prefixes when creating elements of OWL ontologies.
     * @param individual Individual result value from the query result.
     * @param xindividual Named individual object corresponding to the individual in the results.
     * @throws SQWRLException If there was an error when getting the OWL Classes from the result.
     */
    private static void getAndAddClasses(OWLOntologyManager manager, OWLDataFactory factory, OWLOntology originalOntology,
                                         OWLOntology ontology, DefaultPrefixManager pm, SQWRLNamedIndividualResultValue individual,
                                         OWLNamedIndividual xindividual) throws SQWRLException {

        SQWRLResult classResults = OWLMaster.query(originalOntology, "abox:caa(?x, " + individual + ") -> sqwrl:select(?x)");
        while (classResults.next()) {
            if (classResults.hasClassValue("x")) {
                SQWRLClassResultValue classr = classResults.getClass("x");
                System.out.println("Found class: " + classr + " of individual: " + individual);
                OWLClass xclassr = factory.getOWLClass(classr.toString(), pm);
                manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(xclassr));
                manager.addAxiom(ontology, factory.getOWLClassAssertionAxiom(xclassr, xindividual));

                //getAndAddSuperclasses(queryManager, manager, factory, ontology, pm, classResults, classr, xclassr);
            }
        }
    }

    /**
     * Auxiliary method that, for a given individual, discovers its data properties and adds them to the ontology.
     * @param manager Ontology manager used to correctly add ontology axioms.
     * @param factory OWL data factory that is used to create ontology axioms.
     * @param originalOntology The original ontology's knowledge base.
     * @param ontology The new ontology to which information is added.
     * @param pm Prefix manager that supplies correct prefixes when creating elements of OWL ontologies.
     * @param individual Individual result value from the query result.
     * @param xindividual Named individual object corresponding to the individual in the results.
     * @throws SQWRLException If there was an error when getting the OWL Data Properties from the result.
     */
    private static void getAndAddDataProperties(OWLOntologyManager manager, OWLDataFactory factory, OWLOntology originalOntology,
                                                OWLOntology ontology, DefaultPrefixManager pm, SQWRLNamedIndividualResultValue individual,
                                                OWLNamedIndividual xindividual) throws SQWRLException {
        SQWRLResult dataPropertyResults = OWLMaster.query(originalOntology, "abox:dpaa(" + individual + ", ?p, ?v) -> sqwrl:select(?p, ?v)");

        while (dataPropertyResults.next()) {
            if (dataPropertyResults.hasDataPropertyValue("p") && dataPropertyResults.hasLiteralValue("v")) {
                SQWRLDataPropertyResultValue property = dataPropertyResults.getDataProperty("p");
                SQWRLLiteralResultValue value = dataPropertyResults.getLiteral("v");
                System.out.println("Found data property: " + property + " with value: " + value);
                OWLDataProperty xproperty = factory.getOWLDataProperty(property.toString(), pm);
                OWLLiteral xvalue = factory.getOWLLiteral(value.getValue());
                manager.addAxiom(ontology, factory.getOWLDataPropertyAssertionAxiom(xproperty, xindividual, xvalue));
            }
        }
    }

    /**
     * Auxiliary method that, for a given individual, discovers its object properties and adds them to the ontology.
     * @param manager Ontology manager used to correctly add ontology axioms.
     * @param factory OWL data factory that is used to create ontology axioms.
     * @param originalOntology The original ontology's knowledge base.
     * @param ontology The new ontology to which information is added.
     * @param pm Prefix manager that supplies correct prefixes when creating elements of OWL ontologies.
     * @param individual Individual result value from the query result.
     * @param xindividual Named individual object corresponding to the individual in the results.
     * @throws SQWRLException If there was an error when getting the OWL Object Properties from the result.
     */
    //esta função pode ser problemática quando adiciona referências a objetos que não estão declarados por si só.
    private static void getAndAddObjectProperties(OWLOntologyManager manager, OWLDataFactory factory, OWLOntology originalOntology,
                                                  OWLOntology ontology, DefaultPrefixManager pm, SQWRLNamedIndividualResultValue individual,
                                                  OWLNamedIndividual xindividual) throws SQWRLException {
        SQWRLResult objectPropertyResults = OWLMaster.query(originalOntology, "abox:opaa(" + individual + ", ?p, ?o) -> sqwrl:select(?p, ?o)");

        while (objectPropertyResults.next()) {
            if (objectPropertyResults.hasObjectPropertyValue("p") && objectPropertyResults.hasNamedIndividualValue("o")) {
                SQWRLObjectPropertyResultValue property = objectPropertyResults.getObjectProperty("p");
                SQWRLIndividualResultValue object = objectPropertyResults.getNamedIndividual("o");
                System.out.println("Found property: " + property + " with individual: " + object);
                OWLObjectProperty xproperty = factory.getOWLObjectProperty(property.toString(), pm);
                OWLIndividual xobject = factory.getOWLNamedIndividual(object.toString(), pm);
                manager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(xproperty, xindividual, xobject));
            }
        }
    }

    /*
        private static void getAndAddSubclasses(OWLQueryManager queryManager, OWLOntologyManager manager, OWLDataFactory factory,
                                                OWLOntology ontology, DefaultPrefixManager pm, SQWRLClassResultValue classr,
                                                OWLClass xclassr) throws SWRLParseException, SQWRLException {
            SQWRLResult subclassResults = queryManager.query("tbox:sca(?x, " + classr + ") -> sqwrl:select(?x)");
            while(subclassResults.next()){
                if(subclassResults.hasClassValue("x")){
                    SQWRLClassResultValue sclassr = subclassResults.getClass("x");
                    System.out.println("Found subclass: " +sclassr+" of class: " + classr);
                    OWLClass xsclassr = factory.getOWLClass(sclassr.toString(), pm);
                    manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(xsclassr));
                    manager.addAxiom(ontology, factory.getOWLSubClassOfAxiom(xsclassr, xclassr));
                }
            }
        }
    */

    /**
     * Auxiliary method that, for a given class, discovers its super/subclasses and adds them to the ontology.
     * @param manager Ontology manager used to correctly add ontology axioms.
     * @param factory OWL data factory that is used to create ontology axioms.
     * @param originalOntology The original ontology's knowledge base.
     * @param ontology The new ontology to which information is added.
     * @param pm Prefix manager that supplies correct prefixes when creating elements of OWL ontologies.
     * @param classr Class result value from the query result.
     * @param xclassr Class object corresponding to the class in the results.
     * @throws SQWRLException If there was an error when getting the OWL superclasses from the result.
     */
    private static void getAndAddSuperclassesAndSubclasses(OWLOntologyManager manager, OWLDataFactory factory, OWLOntology originalOntology,
                                                           OWLOntology ontology, DefaultPrefixManager pm, SQWRLClassResultValue classr,
                                                           OWLClass xclassr) throws SQWRLException {
        String[] args = {"?x, " + classr, classr + ", ?x",};
        for (int i = 0; i < args.length; i++) {
            SQWRLResult superclassResults = OWLMaster.query(originalOntology, "tbox:sca(" + args[i] + ") -> sqwrl:select(?x)");
            while (superclassResults.next()) {
                if (superclassResults.hasClassValue("x")) {
                    SQWRLClassResultValue sclassr = superclassResults.getClass("x");
                    OWLClass xsclassr = factory.getOWLClass(sclassr.toString(), pm);
                    manager.addAxiom(ontology, factory.getOWLDeclarationAxiom(xsclassr));
                    if (i == 0) {
                        System.out.println("Found subclass: " + sclassr + " of class: " + classr);
                        manager.addAxiom(ontology, factory.getOWLSubClassOfAxiom(xsclassr, xclassr));
                    } else {
                        System.out.println("Found superclass: " + sclassr + " of class: " + classr);
                        manager.addAxiom(ontology, factory.getOWLSubClassOfAxiom(xclassr, xsclassr));
                    }
                }
            }
        }
    }
}

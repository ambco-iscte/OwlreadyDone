package helper;

import org.semanticweb.owlapi.model.OWLOntology;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

import java.sql.Time;
import java.sql.Timestamp;

public class OWLQueryManager {

    private final OWLOntology ontology;
    private final SQWRLQueryEngine queryEngine;
    private int queryCounter;

    public OWLQueryManager(OWLOntology ontology) {
        this.ontology = ontology;
        this.queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(ontology);
        queryCounter = 0;
    }

    /**
     * Queries an ontology using the given SQWRL query string.
     * @param query The SQWRL query to execute.
     * @return The result (SQWRLResult instance) of the query execution.
     */
    public SQWRLResult query(String query) throws SWRLParseException, SQWRLException {
        if (ontology == null || query == null)
            return null;

        return queryEngine.runSQWRLQuery("q" + queryCounter++, query);
    }

    public OWLOntology getOntology() {
        return ontology;
    }
}

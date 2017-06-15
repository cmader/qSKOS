package at.ac.univie.mminf.qskos4j.issues.labels;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.conceptscheme.ConceptSchemes;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.URIImpl;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

public class MissingLabels extends Issue<CollectionResult<Resource>> {

    private final Logger logger = LoggerFactory.getLogger(MissingLabels.class);

    private Collection<Resource> unlabeledConceptsAndConceptSchemes;
    private AuthoritativeConcepts allAuthConcepts;
    private ConceptSchemes allConceptSchemes;

    public MissingLabels(AuthoritativeConcepts authConcepts, ConceptSchemes conceptSchemes) {
        super("ml",
            "Missing Labels",
            "Finds concepts and conceptschemes with missing labels",
            IssueType.ANALYTICAL,
            new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#missing-labels"));

        this.allAuthConcepts = authConcepts;
        this.allConceptSchemes = conceptSchemes;
    }

    @Override
    protected CollectionResult<Resource> invoke() throws RDF4JException {
        unlabeledConceptsAndConceptSchemes = new ArrayList<Resource>();

        unlabeledConceptsAndConceptSchemes.addAll(findUnlabeledConcepts());
        unlabeledConceptsAndConceptSchemes.addAll(findUnlabeledConceptSchemes());

        return new CollectionResult<Resource>(unlabeledConceptsAndConceptSchemes);
    }

    private Collection<Resource> findUnlabeledConcepts() throws RDF4JException {
        Collection<Resource> unlabeledConcepts = new ArrayList<Resource>();

        for (Resource authConcept : allAuthConcepts.getResult().getData()) {
            if (hasNoPrefLabel(authConcept)) unlabeledConcepts.add(authConcept);
        }

        return unlabeledConcepts;
    }

    private boolean hasNoPrefLabel(Value concept) {
        String prefLabelQuery = SparqlPrefix.SKOS + " ASK {<" +concept+ "> skos:prefLabel ?label} ";

        try {
            return !repCon.prepareBooleanQuery(QueryLanguage.SPARQL, prefLabelQuery).evaluate();
        }
        catch (RDF4JException e) {
            logger.error("Error finding preferred label of concept '" +concept.stringValue()+ "'");
            return false;
        }
    }

    private Collection<Resource> findUnlabeledConceptSchemes() throws RDF4JException {
        Collection<Resource> unlabeledConceptSchemes = new ArrayList<Resource>();

        for (Resource conceptScheme : allConceptSchemes.getResult().getData()) {
            if (hasNoRdfsLabelAndNoDcTitle(conceptScheme)) unlabeledConceptSchemes.add(conceptScheme);
        }

        return unlabeledConceptSchemes;
    }

    private boolean hasNoRdfsLabelAndNoDcTitle(Value conceptScheme) throws RDF4JException {
        String labelQuery = SparqlPrefix.RDFS +" "+ SparqlPrefix.DC +" "+ SparqlPrefix.DCTERMS+
            " ASK {" +
                "{<" +conceptScheme+ "> rdfs:label ?label} UNION " +
                "{<" +conceptScheme+ "> dc:title ?label} UNION " +
                "{<" +conceptScheme+ "> dcterms:title ?label}" +
            "}";

        return !repCon.prepareBooleanQuery(QueryLanguage.SPARQL, labelQuery).evaluate();
    }

    @Override
    public void setRepositoryConnection(RepositoryConnection repCon) {
        allAuthConcepts.setRepositoryConnection(repCon);
        allConceptSchemes.setRepositoryConnection(repCon);

        super.setRepositoryConnection(repCon);
    }
}

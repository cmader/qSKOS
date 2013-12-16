package at.ac.univie.mminf.qskos4j.issues.labels;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.conceptscheme.ConceptSchemes;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

public class MissingLabels extends Issue<Collection<Resource>> {

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
    protected Collection<Resource> computeResult() throws OpenRDFException {
        unlabeledConceptsAndConceptSchemes = new ArrayList<Resource>();

        unlabeledConceptsAndConceptSchemes.addAll(findUnlabeledConcepts());
        unlabeledConceptsAndConceptSchemes.addAll(findUnlabeledConceptSchemes());

        return unlabeledConceptsAndConceptSchemes;
    }

    @Override
    protected Report generateReport(Collection<Resource> preparedData) {
        return new CollectionReport<Resource>(unlabeledConceptsAndConceptSchemes);
    }

    private Collection<Resource> findUnlabeledConcepts() throws OpenRDFException {
        Collection<Resource> unlabeledConcepts = new ArrayList<Resource>();

        for (Resource authConcept : allAuthConcepts.getResult()) {
            if (hasNoPrefLabel(authConcept)) unlabeledConcepts.add(authConcept);
        }

        return unlabeledConcepts;
    }

    private boolean hasNoPrefLabel(Value concept) {
        String prefLabelQuery = SparqlPrefix.SKOS + " ASK {<" +concept+ "> skos:prefLabel ?label} ";

        try {
            return !repCon.prepareBooleanQuery(QueryLanguage.SPARQL, prefLabelQuery).evaluate();
        }
        catch (OpenRDFException e) {
            logger.error("Error finding preferred label of concept '" +concept.stringValue()+ "'");
            return false;
        }
    }

    private Collection<Resource> findUnlabeledConceptSchemes() throws OpenRDFException {
        Collection<Resource> unlabeledConceptSchemes = new ArrayList<Resource>();

        for (Resource conceptScheme : allConceptSchemes.getResult()) {
            if (hasNoRdfsLabelAndNoDcTitle(conceptScheme)) unlabeledConceptSchemes.add(conceptScheme);
        }

        return unlabeledConceptSchemes;
    }

    private boolean hasNoRdfsLabelAndNoDcTitle(Value conceptScheme) throws OpenRDFException {
        String labelQuery = SparqlPrefix.RDFS +" "+ SparqlPrefix.DC+
            " ASK {" +
                "{<" +conceptScheme+ "> rdfs:label ?label} UNION " +
                "{<" +conceptScheme+ "> dc:title ?label}" +
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

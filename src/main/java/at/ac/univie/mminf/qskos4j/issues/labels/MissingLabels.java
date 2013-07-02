package at.ac.univie.mminf.qskos4j.issues.labels;

import at.ac.univie.mminf.qskos.issues.RepairFailedException;
import at.ac.univie.mminf.qskos.issues.RepairableIssue;
import at.ac.univie.mminf.qskos.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos.issues.conceptscheme.ConceptSchemes;
import at.ac.univie.mminf.qskos.result.CollectionResult;
import at.ac.univie.mminf.qskos.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

public class MissingLabels extends RepairableIssue<CollectionResult<Value>> {

    private final Logger logger = LoggerFactory.getLogger(MissingLabels.class);

    private Literal defaultLabel;
    private AuthoritativeConcepts allAuthConcepts;
    private ConceptSchemes allConceptSchemes;
    private Collection<Value> unlabeledConcepts, unlabeledConceptSchemes;

    public MissingLabels(AuthoritativeConcepts authConcepts, ConceptSchemes conceptSchemes) {
        super("ml",
            "Missing Labels",
            "Finds concepts and conceptschemes with missing labels",
            "Injects default labels for all missing labels with a default language");

        this.allAuthConcepts = authConcepts;
        this.allConceptSchemes = conceptSchemes;
        qualityProperty = "hasMissingLabel";
    }

    public void setDefaultLabel(Literal defaultLabel) {
        this.defaultLabel = defaultLabel;
    }

    @Override
    protected void invokeRepair() throws RepairFailedException, OpenRDFException {
        if (!invoke().indicatesProblem()) return;

        if (defaultLabel == null) {
            throw new RepairFailedException("Default label literal not provided", this);
        }

        for (Value concept : unlabeledConcepts) {
            repairConcept(concept);
        }

        for (Value conceptScheme : unlabeledConceptSchemes) {
            repairConceptScheme(conceptScheme);
        }
    }

    private void repairConcept(Value concept) throws RepositoryException {
        repCon.add(new StatementImpl(
            (Resource) concept,
            new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "prefLabel"),
            defaultLabel)
        );
    }

    private void repairConceptScheme(Value conceptScheme) throws RepositoryException {
        repCon.add(new StatementImpl(
            (Resource) conceptScheme,
            new URIImpl(SparqlPrefix.RDFS.getNameSpace() + "label"),
            defaultLabel)
        );
        repCon.add(new StatementImpl(
            (Resource) conceptScheme,
            new URIImpl(SparqlPrefix.DC.getNameSpace() + "title"),
            defaultLabel));
    }

    @Override
    protected CollectionResult<Value> invoke() throws OpenRDFException {
        Collection<Value> unlabeledConceptsAndConceptSchemes = new ArrayList<Value>();

        unlabeledConcepts = findUnlabeledConcepts();
        unlabeledConceptSchemes = findUnlabeledConceptSchemes();
        unlabeledConceptsAndConceptSchemes.addAll(unlabeledConcepts);
        unlabeledConceptsAndConceptSchemes.addAll(unlabeledConceptSchemes);

        return new CollectionResult<Value>(unlabeledConceptsAndConceptSchemes).setQualityProperty(qualityProperty);
    }

    private Collection<Value> findUnlabeledConcepts() throws OpenRDFException {
        Collection<Value> unlabeledConcepts = new ArrayList<Value>();

        for (Value authConcept : allAuthConcepts.getResult().getData()) {
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

    private Collection<Value> findUnlabeledConceptSchemes() throws OpenRDFException {
        Collection<Value> unlabeledConceptSchemes = new ArrayList<Value>();

        for (Value conceptScheme : allConceptSchemes.getResult().getData()) {
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

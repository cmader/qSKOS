package at.ac.univie.mminf.qskos4j.issues.labels;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelType;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabeledConcept;
import at.ac.univie.mminf.qskos4j.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.IssueDescriptor;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UnprintableCharactersInLabels extends Issue<CollectionResult<LabeledConcept>> {

    private final Logger logger = LoggerFactory.getLogger(UnprintableCharactersInLabels.class);
    private AuthoritativeConcepts authoritativeConcepts;

    public UnprintableCharactersInLabels(AuthoritativeConcepts authoritativeConcepts) {
        super(new IssueDescriptor.Builder(
            "ucil",
            "Unprintable Characters in Labels",
            "Finds concepts having labels that contain unprintable characters",
            IssueDescriptor.IssueType.ANALYTICAL)
                .weblink("https://github.com/cmader/qSKOS/wiki/Quality-Issues#unprintable-characters-in-labels")
                .dependentIssue(authoritativeConcepts)
                .build());

        this.authoritativeConcepts = authoritativeConcepts;
    }


    @Override
    protected CollectionResult<LabeledConcept> invoke() throws RDF4JException {
        List<LabeledConcept> result = new ArrayList<>();

        Iterator<Resource> it = new MonitoredIterator<>(authoritativeConcepts.getResult().getData(), progressMonitor);
        while (it.hasNext()) {
            Resource concept = it.next();

            try {
                if (concept instanceof IRI) {
                    TupleQuery query = repCon.prepareTupleQuery(QueryLanguage.SPARQL,
                            createUnprintableCharsQuery((IRI) concept));

                    TupleQueryResult queryResult = query.evaluate();
                    while (queryResult.hasNext()) {
                        BindingSet binding = queryResult.next();

                        Value labelValue = binding.getValue("labelValue");
                        Value labelPropertyValue = binding.getValue("labelProperty");
                        try {
                            Literal labelValueLiteral = (Literal) labelValue;
                            IRI labelProperty = (IRI) labelPropertyValue;

                            String label = labelValue.stringValue();
                            if (!label.replaceAll("\\p{C}", "?").equals(label)) {
                                result.add(new LabeledConcept(concept, labelValueLiteral, LabelType.getFromUri(labelProperty)));
                            }
                        }
                        catch (ClassCastException e) {
                            logger.warn("Could not cast label value (" +labelValue.stringValue()+
                                ") or label property (" +labelPropertyValue.stringValue()+ ") value ");
                        }
                    }
                }
            }
            catch (RDF4JException e) {
                logger.error("Error finding labels of concept '" +concept+ "'");
            }
        }

        return new CollectionResult<>(result);
    }

    private String createUnprintableCharsQuery(IRI resource) {
        return SparqlPrefix.RDFS +" "+ SparqlPrefix.DC +" "+ SparqlPrefix.DCTERMS +" "+ SparqlPrefix.SKOS+
            "SELECT ?labelValue ?labelProperty WHERE {" +
                "<" +resource.stringValue()+ "> ?labelProperty ?labelValue. " +
                "FILTER (?labelProperty IN (skos:prefLabel,skos:altLabel,skos:hiddenLabel))"+
            "}";
    }

}

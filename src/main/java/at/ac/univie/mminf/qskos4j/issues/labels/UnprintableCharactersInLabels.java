package at.ac.univie.mminf.qskos4j.issues.labels;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelType;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabeledConcept;
import at.ac.univie.mminf.qskos4j.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UnprintableCharactersInLabels extends Issue<CollectionResult<LabeledConcept>> {

    private final Logger logger = LoggerFactory.getLogger(UnprintableCharactersInLabels.class);
    private AuthoritativeConcepts authoritativeConcepts;

    public UnprintableCharactersInLabels(AuthoritativeConcepts authoritativeConcepts) {
        super(authoritativeConcepts,
            "ucil",
            "Unprintable Characters in Labels",
            "Finds concepts having labels that contain unprintable characters",
            IssueType.ANALYTICAL,
            new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#unprintable-characters-in-labels"));

        this.authoritativeConcepts = authoritativeConcepts;
    }


    @Override
    protected CollectionResult<LabeledConcept> invoke() throws OpenRDFException {
        List<LabeledConcept> result = new ArrayList<>();

        Iterator<Resource> it = new MonitoredIterator<>(authoritativeConcepts.getResult().getData(), progressMonitor);
        while (it.hasNext()) {
            Resource concept = it.next();

            try {
                if (concept instanceof URI) {
                    TupleQuery query = repCon.prepareTupleQuery(QueryLanguage.SPARQL,
                            createUnprintableCharsQuery((URI) concept));

                    TupleQueryResult queryResult = query.evaluate();
                    while (queryResult.hasNext()) {
                        BindingSet binding = queryResult.next();

                        Value labelValue = binding.getValue("labelValue");
                        Value labelPropertyValue = binding.getValue("labelProperty");
                        try {
                            Literal labelValueLiteral = (Literal) labelValue;
                            URI labelProperty = (URI) labelPropertyValue;

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
            catch (OpenRDFException e) {
                logger.error("Error finding labels of concept '" +concept+ "'");
            }
        }

        return new CollectionResult<>(result);
    }

    private String createUnprintableCharsQuery(URI resource) {
        return SparqlPrefix.RDFS +" "+ SparqlPrefix.DC +" "+ SparqlPrefix.DCTERMS +" "+ SparqlPrefix.SKOS+
            "SELECT ?labelValue ?labelProperty WHERE {" +
                "<" +resource.stringValue()+ "> ?labelProperty ?labelValue. " +
                "FILTER (?labelProperty IN (skos:prefLabel,skos:altLabel,skos:hiddenLabel))"+
            "}";
    }

}

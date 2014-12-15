package at.ac.univie.mminf.qskos4j.issues.labels;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelWithUnprintableCharacters;
import at.ac.univie.mminf.qskos4j.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UnprintableCharactersInLabels extends Issue<CollectionResult<LabelWithUnprintableCharacters>> {

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
    protected CollectionResult<LabelWithUnprintableCharacters> invoke() throws OpenRDFException {
        List<LabelWithUnprintableCharacters> result = new ArrayList<>();

        Iterator<Resource> it = new MonitoredIterator<>(authoritativeConcepts.getResult().getData(), progressMonitor);
        while (it.hasNext()) {
            Resource concept = it.next();
            if (concept instanceof URI) {
                TupleQueryResult queryResult = repCon.prepareTupleQuery(QueryLanguage.SPARQL,
                        createUnprintableCharsQuery((URI) concept)).evaluate();
                while (queryResult.hasNext()) {
                    Literal labelValue = (Literal) queryResult.next().getValue("labelValue");
                    result.add(new LabelWithUnprintableCharacters(labelValue, concept));
                }
            }
        }

        return new CollectionResult<>(result);
    }

    private String createUnprintableCharsQuery(URI resource) {
        return SparqlPrefix.RDFS +" "+ SparqlPrefix.DC +" "+ SparqlPrefix.DCTERMS +" "+ SparqlPrefix.SKOS+
            "SELECT ?labelValue WHERE {" +
                "<" +resource.stringValue()+ "> ?labelProperty ?labelValue. " +
                "FILTER (?labelProperty IN (rdfs:label,dc:title,dcterms:title,skos:prefLabel,skos:altLabel,skos:hiddenLabel) &&" +
                "regex(?labelValue, '')" +
            "}";
    }

}

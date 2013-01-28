package at.ac.univie.mminf.qskos4j.issues.relations;

import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.query.TupleQueryResult;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ValuelessAssocRelationsResult extends Result<Collection<Pair<URI>>> {

    private VocabRepository repo;

    public ValuelessAssocRelationsResult(
        Result<Collection<Pair<URI>>> result,
        VocabRepository repo)
    {
		super(result.getData());
        this.repo = repo;
	}

	@Override
	public String getShortReport() {
		return "pair count: "+getData().size()+ "\nconcept count: " +getConceptCount();
	}

	private int getConceptCount() {
		return getDistinctConceptsFromPairs(getData()).size();
	}

    private Collection<URI> getDistinctConceptsFromPairs(Collection<Pair<URI>> conceptURIs) {
        Set<URI> distinctConcepts = new HashSet<URI>();

        for (Pair<URI> pair : conceptURIs) {
            distinctConcepts.add(pair.getFirst());
            distinctConcepts.add(pair.getSecond());
        }

        return distinctConcepts;
    }

	@Override
	public String getExtensiveReport() {
        StringBuilder extensiveReport = new StringBuilder();

		for (Pair<URI> relatedUris : getData()) {
			String label1 = getLabelForUri(relatedUris.getFirst());
            String label2 = getLabelForUri(relatedUris.getSecond());
			extensiveReport.append(
                relatedUris.getFirst()+
                " ("+ label1 +") related to "+ relatedUris.getSecond()+
                " ("+ label2+ ")\n");
		}
		
		return extensiveReport.toString();
	}

    private String getLabelForUri(URI uri) {
        String labelQuery = SparqlPrefix.SKOS +
            "SELECT * WHERE {<" +uri+ "> skos:prefLabel ?label}";

        try {
            TupleQueryResult result = repo.query(labelQuery);
            if (result.hasNext()) {
                return result.next().getValue("label").stringValue();
            }
        }
        catch (OpenRDFException e) {
            // fall through
        }

        return "unknown";
    }

}

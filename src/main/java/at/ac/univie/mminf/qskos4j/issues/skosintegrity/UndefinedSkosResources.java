package at.ac.univie.mminf.qskos4j.issues.skosintegrity;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Finds resources not defined in the SKOS ontology (
 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Undefined_SKOS_Resources">Undefined SKOS Resources</a>
 * ).
 */
public class UndefinedSkosResources extends Issue<CollectionReport<URI>> {

	private Map<URI, Collection<URI>> deprecatedProperties, illegalTerms;

    public UndefinedSkosResources(RepositoryConnection repCon) {
        super(repCon,
              "usr",
              "Undefined SKOS Resources",
              "Finds 'invented' new terms within the SKOS namespace or deprecated properties",
              IssueType.ANALYTICAL
        );
    }

    @Override
    protected CollectionReport<URI> invoke() throws OpenRDFException {
		findDeprecatedProperties();
		findIllegalTerms();
		
		return new CollectionReport<URI>(collectUndefinedResources());
	}
	
	private void findDeprecatedProperties() throws OpenRDFException 
	{
		TupleQuery query = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createDeprecatedPropertiesQuery());
		generateDeprecatedPropertiesMap(query.evaluate());
	}
	
	private String createDeprecatedPropertiesQuery() {
		return SparqlPrefix.SKOS +
			"SELECT DISTINCT ?iri ?deprProp "+
			"WHERE {{?iri ?deprProp ?o .} UNION "+
				"{?q ?deprProp ?iri .} "+
				"FILTER isIRI(?iri) "+
				"FILTER (?deprProp IN (" +
					"skos:symbol,"+ 
					"skos:prefSymbol,"+
					"skos:altSymbol,"+
					"skos:CollectableProperty,"+
					"skos:subject,"+
					"skos:isSubjectOf,"+
					"skos:primarySubject,"+
					"skos:isPrimarySubjectOf,"+
					"skos:subjectIndicator))}";
	}
	
	private void generateDeprecatedPropertiesMap(TupleQueryResult result) 
		throws QueryEvaluationException 
	{
		deprecatedProperties = new HashMap<URI, Collection<URI>>();

		while (result.hasNext()) {
			BindingSet queryResult = result.next();
			URI resource = (URI) queryResult.getValue("iri");
			URI deprProperty = (URI) queryResult.getValue("deprProp");
			
			Collection<URI> resources = deprecatedProperties.get(deprProperty);
			if (resources == null) {
				resources = new HashSet<URI>();
				deprecatedProperties.put(deprProperty, resources);
			}
			resources.add(resource);
		}
	}
	
	private void findIllegalTerms() throws OpenRDFException 
	{
        TupleQuery query = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createIllegalTermsQuery());
		generateIllegalTermsMap(query.evaluate());
	}
	
	private String createIllegalTermsQuery() throws OpenRDFException {
		return SparqlPrefix.SKOS+ 
			"SELECT DISTINCT ?illTerm ?s ?o "+
			"WHERE {" +
                "{?illTerm ?p ?o . } UNION "+
			    "{?s ?illTerm ?o . } UNION "+
			    "{?s ?p ?illTerm . } "+
			    "FILTER isIRI(?illTerm) "+
			    "FILTER STRSTARTS(str(?illTerm), \"" +SparqlPrefix.SKOS.getNameSpace()+ "\") "+
                createSkosSubjectsFilter()+
            "} ";
	}

    private String createSkosSubjectsFilter() throws OpenRDFException {
        RepositoryConnection skosRepoConn = SkosOntology.getInstance().getRepository().getConnection();
        try {
            TupleQuery skosSubjectsQuery = skosRepoConn.prepareTupleQuery(QueryLanguage.SPARQL, createSkosSubjectsQuery());
            return TupleQueryResultUtil.getFilterForBindingName(skosSubjectsQuery.evaluate(), "illTerm", true);
        }
        finally {
            skosRepoConn.close();
        }
    }

    private String createSkosSubjectsQuery() {
        return "SELECT ?illTerm WHERE {" +
                    "?illTerm ?p ?o . " +
                    "FILTER isIRI(?illTerm)"+
                "}";
    }
	
	private void generateIllegalTermsMap(TupleQueryResult result) 
		throws QueryEvaluationException 
	{
		illegalTerms = new HashMap<URI, Collection<URI>>();
		
		while (result.hasNext()) {
			BindingSet queryResult = result.next();
			URI illegalTerm = (URI) queryResult.getValue("illTerm");
			URI subject = (URI) queryResult.getValue("s");
			Value object = queryResult.getValue("o");

			if (!illegalTerm.getLocalName().isEmpty()) {
				addTermToMap(illegalTerm, subject, object);
			}
		}
	}
	
	private void addTermToMap(URI term, URI subject, Value object) {
		Collection<URI> resources = illegalTerms.get(term);
		if (resources == null) {
			resources = new HashSet<URI>();
			illegalTerms.put(term, resources);
		}
		
		if (subject != null) {
			resources.add(subject);
		}
		else if (object != null && object instanceof URI) {
			resources.add((URI) object);
		}		
	}
	
	private Collection<URI> collectUndefinedResources() {
		Collection<URI> undefRes = new HashSet<URI>();
		
		undefRes.addAll(deprecatedProperties.keySet());
		undefRes.addAll(illegalTerms.keySet());
		
		return undefRes;
	}

}

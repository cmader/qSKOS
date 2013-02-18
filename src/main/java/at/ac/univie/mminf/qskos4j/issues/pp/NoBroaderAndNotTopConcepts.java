package at.ac.univie.mminf.qskos4j.issues.pp;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.*;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class NoBroaderAndNotTopConcepts extends Issue<CollectionReport<Value>> implements Repairable {

    private URI absorbingConceptSchemeUri;
    private Literal absorbingConceptSchemeLabel;

    private final static String QUERY =  SparqlPrefix.SKOS +" "+
        "SELECT DISTINCT ?concept WHERE {"+
            "?concept a skos:Concept . "+
            "OPTIONAL "+
            "{"+
                "?y skos:narrower|^skos:broader ?concept."+
            "}."+
            "OPTIONAL "+
            "{"+
                "?s skos:hasTopConcept ?concept. "+
            "}."+
            "FILTER (!bound(?y) && !bound(?s))"+
        "}";

    public NoBroaderAndNotTopConcepts(VocabRepository vocabRepository) {
        super(vocabRepository,
            "nbantc",
            "No Broader And No Top Concepts",
            "Finds concepts that don't have a broader concept defined and are not top concepts",
            IssueType.ANALYTICAL);
    }

    /**
     * Sets a scheme that absorbs all concepts that comply to this issue and could not be assigned otherwise
     * @param uri URI with namespace of the ConceptScheme
     * @param label Label of the ConceptScheme
     */
    public void setAbsorbingConceptScheme(URI uri, Literal label) {
        absorbingConceptSchemeUri = uri;
        absorbingConceptSchemeLabel = label;
    }

    @Override
    protected CollectionReport<Value> invoke() throws OpenRDFException {
        TupleQueryResult result = vocabRepository.query(QUERY);
        Set<Value> foundConcepts = TupleQueryResultUtil.getValuesForBindingName(result, "concept");

        return new CollectionReport<Value>(foundConcepts);
    }

    @Override
    public void repair() throws RepairFailedException
    {
        try {
            for (Value concept : getReport().getData()) {
                Collection<Resource> containingSchemes = getContainingConceptSchemes((Resource) concept);
                if (!containingSchemes.isEmpty()) {
                    setConceptAsTopConcept((Resource) concept, containingSchemes);
                }
                else {
                    addToAbsorbingConceptScheme(concept);
                }
            }
        }
        catch (Exception e) {
            throw new RepairFailedException(e);
        }
    }

    private Collection<Resource> getContainingConceptSchemes(Resource concept) throws OpenRDFException
    {
        RepositoryResult<Statement> containingConceptSchemesResult = vocabRepository.getRepository().getConnection().getStatements(
            concept,
            new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "inScheme"),
            null,
            true);

        Collection<Resource> containingConceptSchemes = new ArrayList<Resource>();

        while (containingConceptSchemesResult.hasNext()) {
            containingConceptSchemes.add((Resource) containingConceptSchemesResult.next().getObject());
        }

        return containingConceptSchemes;
    }

    private void setConceptAsTopConcept(Resource concept, Collection<Resource> containingSchemes)
        throws RepositoryException
    {
        for (Resource conceptScheme : containingSchemes) {
            vocabRepository.getRepository().getConnection().add(
                conceptScheme,
                new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "hasTopConcept"),
                concept);
            vocabRepository.getRepository().getConnection().add(
                concept,
                new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "topConceptOf"),
                conceptScheme);
        }
    }

    private void addToAbsorbingConceptScheme(Value concept) throws RepairFailedException
    {

    }

}

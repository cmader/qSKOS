package at.ac.univie.mminf.qskos4j.issues.pp.onimport;

import at.ac.univie.mminf.qskos4j.issues.pp.RepairFailedException;
import at.ac.univie.mminf.qskos4j.issues.pp.RepairableIssue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.*;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class NoBroaderAndNotTopConcepts extends RepairableIssue<CollectionReport<Value>> {

    private URI absorbingConceptSchemeUri;
    private Literal absorbingConceptSchemeLabel;
    private RepositoryConnection repCon;

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
    public void invokeRepair() throws RepairFailedException, RepositoryException
    {
        repCon = vocabRepository.getRepository().getConnection();
        repCon.setAutoCommit(false);

        try {
            repairConcepts();
            repCon.commit();
        }
        catch (Exception e) {
            throw new RepairFailedException(e);
        }
        finally {
            repCon.close();
        }
    }

    private void repairConcepts() throws OpenRDFException, RepairFailedException
    {
        for (Value concept : getReport().getData()) {
            Collection<Resource> containingSchemes = getContainingConceptSchemes((Resource) concept);
            if (!containingSchemes.isEmpty()) {
                setConceptAsTopConcept((Resource) concept, containingSchemes);
            }
            else {
                addToAbsorbingConceptScheme((Resource) concept);
            }
        }
    }

    private Collection<Resource> getContainingConceptSchemes(Resource concept) throws OpenRDFException
    {
        RepositoryResult<Statement> containingConceptSchemesResult = repCon.getStatements(
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
            repCon.add(conceptScheme, new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "hasTopConcept"), concept);
            repCon.add(concept, new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "topConceptOf"), conceptScheme);
        }
    }

    private void addToAbsorbingConceptScheme(Resource concept) throws RepairFailedException, RepositoryException
    {
        checkAbsorbingConceptSchemeDataProvided();
        createAbsorbingConceptScheme();

        Collection<Resource> containingSchemes = new ArrayList<Resource>();
        containingSchemes.add(absorbingConceptSchemeUri);
        setConceptAsTopConcept(concept, containingSchemes);
    }

    private void checkAbsorbingConceptSchemeDataProvided() throws RepairFailedException
    {
        if (absorbingConceptSchemeUri == null || absorbingConceptSchemeLabel == null)
            throw new RepairFailedException("Absorbing ConceptScheme data not provided");
    }

    private void createAbsorbingConceptScheme() throws RepositoryException {
        if (!isAbsorbingConceptSchemeDefined()) {
            for (Statement statement : absorbingConceptSchemeDefinitions()) {
                repCon.add(statement);
            }
        }
    }

    private boolean isAbsorbingConceptSchemeDefined() throws RepositoryException
    {
        boolean isDefined = true;
        for (Statement statement : absorbingConceptSchemeDefinitions()) {
            isDefined &= repCon.hasStatement(statement, false);
        }
        return isDefined;
    }

    private Collection<Statement> absorbingConceptSchemeDefinitions() {
        Collection<Statement> definitionStatements = new ArrayList<Statement>();

        definitionStatements.add(new StatementImpl(absorbingConceptSchemeUri, RDF.TYPE, new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "ConceptScheme")));
        definitionStatements.add(new StatementImpl(absorbingConceptSchemeUri, RDFS.LABEL, absorbingConceptSchemeLabel));

        return definitionStatements;
    }

}

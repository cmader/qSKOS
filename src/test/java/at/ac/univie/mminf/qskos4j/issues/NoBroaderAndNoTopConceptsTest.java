package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.pp.RepairFailedException;
import at.ac.univie.mminf.qskos4j.issues.pp.onimport.NoBroaderAndNotTopConcepts;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.io.IOException;
import java.util.Collection;

public class NoBroaderAndNoTopConceptsTest {

    private final String ABSORBING_SCHEME_URI = "http://myvocab.at/absorbingScheme";
    private final String ABSORBING_SCHEME_LABEL = "NewAbsorbingScheme";

    private NoBroaderAndNotTopConcepts noBroaderAndNotTopConcepts;
    private RepositoryConnection repCon;

    @Before
    public void setUp() throws IOException, OpenRDFException {
        Repository repository = VocabRepository.setUpFromTestResource("noBroaderAndNotTopConcepts.rdf").getRepository();
        repCon = repository.getConnection();
        noBroaderAndNotTopConcepts = new NoBroaderAndNotTopConcepts(repository);
        noBroaderAndNotTopConcepts.setAbsorbingConceptScheme(
            new URIImpl(ABSORBING_SCHEME_URI),
            new LiteralImpl(ABSORBING_SCHEME_LABEL));
    }

    @After
    public void tearDown() throws RepositoryException
    {
        repCon.close();
    }

    @Test
    public void testConceptCount() throws OpenRDFException
    {
        Collection<Value> concepts = noBroaderAndNotTopConcepts.getReport().getData();
        Assert.assertEquals(3, concepts.size());
    }

    @Test
    public void repairConceptInConceptScheme() throws RepairFailedException, RepositoryException
    {
        noBroaderAndNotTopConcepts.repair();
        Assert.assertTrue(isDefinedAsTopConcept("http://localhost/myonto/resourceK", "http://localhost/myonto/myConceptScheme"));
    }

    private boolean isDefinedAsTopConcept(String concept, String conceptScheme) throws RepositoryException
    {
        URI conceptUri = new URIImpl(concept);
        URI conceptSchemeUri = new URIImpl(conceptScheme);

        return
            repCon.hasStatement(
                conceptUri,
                new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "topConceptOf"),
                conceptSchemeUri,
                false)

            &&

            repCon.hasStatement(
                    conceptSchemeUri,
                    new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "hasTopConcept"),
                    conceptUri,
                    false);
    }

    @Test
    public void allRepairsSuccessful() throws OpenRDFException, RepairFailedException
    {
        Assert.assertTrue(noBroaderAndNotTopConcepts.getReport().getData().size() > 0);
        noBroaderAndNotTopConcepts.repair();
        Assert.assertTrue(noBroaderAndNotTopConcepts.getReport().getData().size() == 0);
    }

    @Test
    public void repairConceptNonInConceptScheme() throws RepairFailedException, RepositoryException
    {
        noBroaderAndNotTopConcepts.repair();

        Assert.assertTrue(isDefinedAsTopConcept("http://localhost/myonto/resourceG", ABSORBING_SCHEME_URI));
        Assert.assertTrue(isDefinedAsTopConcept("http://localhost/myonto/resourceD", ABSORBING_SCHEME_URI));
        Assert.assertTrue(isAbsorbingConceptSchemeDefined());
    }

    private boolean isAbsorbingConceptSchemeDefined() throws RepositoryException
    {
        return
            repCon.hasStatement(new URIImpl(ABSORBING_SCHEME_URI), RDF.TYPE, null, false) &&
            repCon.hasStatement(new URIImpl(ABSORBING_SCHEME_URI), RDFS.LABEL, new LiteralImpl(ABSORBING_SCHEME_LABEL), false);
    }

    @Test(expected = RepairFailedException.class)
    public void repairConceptsNoAbsorbingSchemeProvided() throws RepairFailedException, RepositoryException
    {
        noBroaderAndNotTopConcepts.setAbsorbingConceptScheme(null, null);
        noBroaderAndNotTopConcepts.repair();
    }

    @Test
    public void multipleRepairsDontAddStatements() throws RepositoryException, RepairFailedException
    {
        try {
            long statementsBeforeRepair = repCon.size();
            Assert.assertTrue(statementsBeforeRepair > 0);

            noBroaderAndNotTopConcepts.repair();
            long statementsAfterRepair = repCon.size();
            Assert.assertTrue(statementsAfterRepair > statementsBeforeRepair);

            noBroaderAndNotTopConcepts.repair();
            long statementsAfterRepeatedRepair = repCon.size();
            Assert.assertTrue(statementsAfterRepeatedRepair == statementsAfterRepair);
        }
        finally {
            repCon.close();
        }
    }
}

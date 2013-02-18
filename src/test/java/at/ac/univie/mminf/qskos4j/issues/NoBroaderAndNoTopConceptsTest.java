package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.pp.NoBroaderAndNotTopConcepts;
import at.ac.univie.mminf.qskos4j.issues.pp.RepairFailedException;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;

import java.io.IOException;
import java.util.Collection;

public class NoBroaderAndNoTopConceptsTest {

    private NoBroaderAndNotTopConcepts noBroaderAndNotTopConcepts;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        noBroaderAndNotTopConcepts = new NoBroaderAndNotTopConcepts(VocabRepository.setUpFromTestResource("noBroaderAndNotTopConcepts.rdf"));
    }

    @Test
    public void testConceptCount() throws OpenRDFException
    {
        Collection<Value> concepts = noBroaderAndNotTopConcepts.getReport().getData();
        Assert.assertEquals(3, concepts.size());
    }

    @Test
    public void repairConcepts() throws RepairFailedException, RepositoryException
    {
        noBroaderAndNotTopConcepts.setAbsorbingConceptScheme(
            new URIImpl("http://myvocab.at/absorbingScheme"),
            new LiteralImpl("NewAbsorbingScheme"));
        noBroaderAndNotTopConcepts.repair();

        Assert.assertTrue(
                noBroaderAndNotTopConcepts.getVocabRepository().getRepository().getConnection().hasStatement(
                        new URIImpl("http://localhost/myonto#resourceK"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "topConceptOf"),
                        new URIImpl("http://localhost/myonto#myConceptScheme"),
                        false
                )
        );


    }

    @Test(expected = RepairFailedException.class)
    public void repairConceptsNoAbsorbingSchemeProvided() throws RepairFailedException
    {
        noBroaderAndNotTopConcepts.repair();
    }
}

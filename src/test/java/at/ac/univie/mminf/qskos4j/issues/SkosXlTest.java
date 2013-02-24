package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.labels.LexicalRelations;
import at.ac.univie.mminf.qskos4j.issues.labels.OverlappingLabels;
import at.ac.univie.mminf.qskos4j.issues.language.IncompleteLanguageCoverage;
import at.ac.univie.mminf.qskos4j.issues.language.OmittedOrInvalidLanguageTags;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;

import java.io.IOException;

public class SkosXlTest {

	private LexicalRelations lexicalRelations;
    private OmittedOrInvalidLanguageTags omittedOrInvalidLanguageTags;
    private IncompleteLanguageCoverage incompleteLanguageCoverage;
    private OverlappingLabels overlappingLabels;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
        RepositoryBuilder repositoryBuilder = new RepositoryBuilder();
        Repository repo = repositoryBuilder.setUpFromTestResource("skosxl.rdf");
        repositoryBuilder.enableSkosXlSupport();

        InvolvedConcepts involvedConcepts = new InvolvedConcepts(repo.getConnection());
        lexicalRelations = new LexicalRelations(involvedConcepts);
        omittedOrInvalidLanguageTags = new OmittedOrInvalidLanguageTags(repo.getConnection());
        incompleteLanguageCoverage = new IncompleteLanguageCoverage(involvedConcepts);
        overlappingLabels = new OverlappingLabels(involvedConcepts);
	}
	
	@Test
	public void lexicalRelationsCountTest() throws OpenRDFException {
		Assert.assertEquals(5, lexicalRelations.getPreparedData().intValue());
	}
	
	@Test
	public void omittedLangTagCount() throws OpenRDFException {
		Assert.assertEquals(2, omittedOrInvalidLanguageTags.getPreparedData().size());
	}
	
	@Test
	public void incompleteLangCovCount() throws OpenRDFException {
		Assert.assertEquals(2, incompleteLanguageCoverage.getPreparedData().size());
	}

	@Test
	public void labelConflictCount() throws OpenRDFException {
		Assert.assertEquals(1, overlappingLabels.getPreparedData().size());
	}

}

package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.labels.LexicalRelations;
import at.ac.univie.mminf.qskos4j.issues.labels.OverlappingLabels;
import at.ac.univie.mminf.qskos4j.issues.language.IncompleteLanguageCoverage;
import at.ac.univie.mminf.qskos4j.issues.language.OmittedOrInvalidLanguageTags;
import at.ac.univie.mminf.qskos4j.issues.language.util.LanguageCoverage;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.repository.Repository;

import java.io.IOException;

public class SkosXlTest {

	private LexicalRelations lexicalRelations;
    private OmittedOrInvalidLanguageTags omittedOrInvalidLanguageTags;
    private IncompleteLanguageCoverage incompleteLanguageCoverage;
    private OverlappingLabels overlappingLabels;
	
	@Before
	public void setUp() throws RDF4JException, IOException {
        RepositoryBuilder repositoryBuilder = new RepositoryBuilder();
        Repository repo = repositoryBuilder.setUpFromTestResource("skosxl.rdf");
        repositoryBuilder.enableSkosXlSupport();

        lexicalRelations = new LexicalRelations(new InvolvedConcepts());
        lexicalRelations.setRepositoryConnection(repo.getConnection());

        omittedOrInvalidLanguageTags = new OmittedOrInvalidLanguageTags();
        omittedOrInvalidLanguageTags.setRepositoryConnection(repo.getConnection());

        incompleteLanguageCoverage = new IncompleteLanguageCoverage(new LanguageCoverage(new InvolvedConcepts()));
        incompleteLanguageCoverage.setRepositoryConnection(repo.getConnection());

        overlappingLabels = new OverlappingLabels(new InvolvedConcepts());
        overlappingLabels.setRepositoryConnection(repo.getConnection());
    }
	
	@Test
	public void lexicalRelationsCountTest() throws RDF4JException {
		Assert.assertEquals(5, lexicalRelations.getResult().getData().intValue());
	}
	
	@Test
	public void omittedLangTagCount() throws RDF4JException {
		Assert.assertEquals(2, omittedOrInvalidLanguageTags.getResult().getData().size());
	}
	
	@Test
	public void incompleteLangCovCount() throws RDF4JException {
		Assert.assertEquals(2, incompleteLanguageCoverage.getResult().getData().size());
	}

	@Test
	public void labelConflictCount() throws RDF4JException {
		Assert.assertEquals(1, overlappingLabels.getResult().getData().size());
	}

}

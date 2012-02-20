package at.ac.univie.mminf.qskos4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

@Ignore
public class QSkosTestCase {
	
	protected QSkos qSkosConcepts, qSkosComponents, qSkosCycles, 
		qSkosExtResources, qSkosDeprecatedAndIllegal, qSkosAmbiguousLabels,
		qSkosRedundantAssociativeRelations, qSkosRankConcepts,
		qSkosOmittedInverseRelations, qSkosSolitaryTransitiveRelations,
		qSkosDocumentedConcepts, qSkosMissingTopConcepts,
		qSkosTopConceptsHavingBroaderConcept, qSkosReferenceIntegrityRelations,
		qSkosHierarchicalAndAssociativeRelations, qSkosRelatedConcepts;

	@Before
	public void setUp() throws RepositoryException, RDFParseException, IOException {
		qSkosConcepts = setUpInstance("concepts.rdf");
		qSkosComponents = setUpInstance("components.rdf");
		qSkosCycles = setUpInstance("cycles.rdf");
		qSkosExtResources = setUpInstance("resources.rdf");
		qSkosDeprecatedAndIllegal = setUpInstance("deprecatedAndIllegalTerms.rdf");
		qSkosAmbiguousLabels = setUpInstance("ambiguousLabels.rdf");
		qSkosRedundantAssociativeRelations = setUpInstance("redundantAssociativeRelations.rdf");
		qSkosRankConcepts = setUpInstance("rankConcepts.rdf");
		qSkosOmittedInverseRelations = setUpInstance("omittedInverseRelations.rdf");
		qSkosSolitaryTransitiveRelations = setUpInstance("solitaryTransitiveRelations.rdf");
		qSkosDocumentedConcepts = setUpInstance("documentedConcepts.rdf");
		qSkosMissingTopConcepts = setUpInstance("missingTopConcepts.rdf");
		qSkosTopConceptsHavingBroaderConcept = setUpInstance("topConceptsHavingBroaderConcept.rdf");
		qSkosReferenceIntegrityRelations = setUpInstance("skosReferenceIntegrity.rdf");
		qSkosHierarchicalAndAssociativeRelations = setUpInstance("hierarchicalAndAssociativeRelations.rdf");
		qSkosRelatedConcepts = setUpInstance("relatedConcepts.rdf");
	}
	
	private QSkos setUpInstance(String testFileName) 
		throws RepositoryException, RDFParseException, IOException 
	{
		URL conceptsUrl = getClass().getResource("/"+ testFileName);
		File conceptsFile = new File(conceptsUrl.getFile());
		Assert.assertNotNull(conceptsFile);
		
		return new QSkos(conceptsFile, RDFFormat.RDFXML);
	}
	
}

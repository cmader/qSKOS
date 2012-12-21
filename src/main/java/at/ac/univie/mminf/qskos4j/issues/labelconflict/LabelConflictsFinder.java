package at.ac.univie.mminf.qskos4j.issues.labelconflict;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.labelconflict.LabeledConcept.LabelType;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

public class LabelConflictsFinder extends Issue {

	private final Logger logger = LoggerFactory.getLogger(LabelConflictsFinder.class);

	private Set<LabelConflict> labelConflicts;
	private Map<Literal, Set<LabeledConcept>> conceptLabels;
	
	public LabelConflictsFinder(VocabRepository vocabRepository) 
	{
		super(vocabRepository);
	}
	
	public CollectionResult<LabelConflict> findLabelConflicts(Collection<URI> concepts) 
		throws OpenRDFException
	{
		if (labelConflicts == null) {
			generateConceptsLabelMap(concepts);
            generateLabelConflictResults();
		}
		return new CollectionResult<LabelConflict>(labelConflicts);
	}
	
	private void generateConceptsLabelMap(Collection<URI> concepts)
	{
		conceptLabels = new HashMap<Literal, Set<LabeledConcept>>();

        progressMonitor.setTaskDescription("Finding label conflicts");
        Iterator<URI> it = new MonitoredIterator<URI>(concepts, progressMonitor);

		while (it.hasNext()) {
            URI concept = it.next();

            try {
			    TupleQueryResult resultLabels1 = vocabRepository.query(createConceptLabelQuery(concept));
                Set<LabeledConcept> labeledConcepts = createLabeledConceptsFromResult(concept, resultLabels1);
                addToLabelsMap(labeledConcepts);
            }
            catch (OpenRDFException e) {
                logger.error("Error finding labels of concept '" +concept+ "'");
            }
		}
	}

    private void addToLabelsMap(Set<LabeledConcept> labels) {
        for (LabeledConcept label : labels) {
            SimilarityLiteral literal = new SimilarityLiteral(label.getLiteral());

            Set<LabeledConcept> affectedConcepts = conceptLabels.get(literal);
            if (affectedConcepts == null) {
                affectedConcepts = new HashSet<LabeledConcept>();
            }
            affectedConcepts.add(label);
            conceptLabels.put(literal, affectedConcepts);
        }
    }

	private String createConceptLabelQuery(URI concept) {
		return SparqlPrefix.SKOS+ 
			"SELECT ?prefLabel ?altLabel ?hiddenLabel "+
				"FROM <" +vocabRepository.getVocabContext()+ "> "+
				"WHERE {{<"+concept.stringValue()+"> skos:prefLabel ?prefLabel .} UNION "+
				"{<"+concept.stringValue()+"> skos:altLabel ?altLabel .} UNION "+
				"{<"+concept.stringValue()+"> skos:hiddenLabel ?hiddenLabel .}}";
	}
	
	private Set<LabeledConcept> createLabeledConceptsFromResult(URI concept, TupleQueryResult result)
		throws QueryEvaluationException 
	{
		Set<LabeledConcept> ret = new HashSet<LabeledConcept>();
		
		while (result.hasNext()) {
			BindingSet queryResult = result.next();

			for (String bindingName : queryResult.getBindingNames()) {
                try {
                    Literal literal = (Literal) queryResult.getValue(bindingName);

                    LabeledConcept skosLabel = new LabeledConcept(
                        concept,
                        literal,
                        getLabelTypeForBindingName(bindingName));

                    ret.add(skosLabel);
                }
                catch (ClassCastException e) {
                    logger.info("literal label expected for concept " +concept.toString()+ ", " +e.toString());
                }
			}			
		}
		
		return ret;
	}	
	
	private LabelType getLabelTypeForBindingName(String bindingName) {
		if (bindingName.equals("prefLabel")) {
			return LabelType.PREF_LABEL;
		}
		if (bindingName.equals("altLabel")) {
			return LabelType.ALT_LABEL;
		}
		if (bindingName.equals("hiddenLabel")) {
			return LabelType.HIDDEN_LABEL;
		}
		return null;
	}

    private void generateLabelConflictResults() {
        labelConflicts = new HashSet<LabelConflict>();

        for (Entry<Literal, Set<LabeledConcept>> entry : conceptLabels.entrySet()) {
            if (entry.getValue().size() > 1) {
                labelConflicts.add(new LabelConflict(entry.getKey(), entry.getValue()));
            }
        }

    }
	
}

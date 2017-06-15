package at.ac.univie.mminf.qskos4j.issues.labels;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelConflict;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelType;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabeledConcept;
import at.ac.univie.mminf.qskos4j.issues.labels.util.SimilarityLiteral;
import at.ac.univie.mminf.qskos4j.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.URIImpl;
import org.eclipse.rdf4j.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Finds concepts having the same preferred labels (
 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Overlapping_Labels">Overlapping Labels</a>
 * ).
 */
public class OverlappingLabels extends Issue<CollectionResult<LabelConflict>> {

	private final Logger logger = LoggerFactory.getLogger(OverlappingLabels.class);

	private Set<LabelConflict> labelConflicts;
	private Map<Literal, Set<LabeledConcept>> conceptLabels;
    private InvolvedConcepts involvedConcepts;

    public OverlappingLabels(InvolvedConcepts involvedConcepts) {
        super(involvedConcepts,
            "ol",
            "Overlapping Labels",
            "Finds concepts with similar (identical) labels",
            IssueType.ANALYTICAL,
            new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#overlapping-labels"));

        this.involvedConcepts = involvedConcepts;
    }

    @Override
    protected CollectionResult<LabelConflict> invoke() throws RDF4JException {
        generateConceptsLabelMap();
        generateLabelConflictResults();

		return new CollectionResult<LabelConflict>(labelConflicts);
	}

    private void generateConceptsLabelMap() throws RDF4JException
	{
		conceptLabels = new HashMap<Literal, Set<LabeledConcept>>();
        Iterator<Resource> it = new MonitoredIterator<Resource>(involvedConcepts.getResult().getData(), progressMonitor);

        progressMonitor.setTaskDescription("Collecting resource labels");
		while (it.hasNext()) {
            Resource concept = it.next();

            try {
                TupleQuery query = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createConceptLabelQuery(concept));
                Set<LabeledConcept> labeledConcepts = createLabeledConceptsFromResult(concept, query.evaluate());
                addToLabelsMap(labeledConcepts);
            }
            catch (RDF4JException e) {
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

            // We're not interested in conflicts within the same concept
            addIfResourceUnique(label, affectedConcepts);

            conceptLabels.put(literal, affectedConcepts);
        }
    }

    private void addIfResourceUnique(LabeledConcept newLabeledResource, Set<LabeledConcept> otherLabeledResources) {
        for (LabeledConcept labeledResource : otherLabeledResources) {
            if (labeledResource.getConcept().equals(newLabeledResource.getConcept())) return;
        }
        otherLabeledResources.add(newLabeledResource);
    }

	private String createConceptLabelQuery(Value concept) {
		return SparqlPrefix.SKOS+ 
			"SELECT ?prefLabel ?altLabel ?hiddenLabel "+
				"WHERE {{<"+concept.stringValue()+"> skos:prefLabel ?prefLabel .} UNION "+
				"{<"+concept.stringValue()+"> skos:altLabel ?altLabel .} UNION "+
				"{<"+concept.stringValue()+"> skos:hiddenLabel ?hiddenLabel .}}";
	}
	
	private Set<LabeledConcept> createLabeledConceptsFromResult(Resource concept, TupleQueryResult result)
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
                    logger.error("Literal label expected for concept " +concept.toString()+ ", " +e.toString());
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

        for (Set<LabeledConcept> conflicts : conceptLabels.values()) {
            if (conflicts.size() > 1) {
                labelConflicts.add(new LabelConflict(conflicts));
            }
        }
    }

}

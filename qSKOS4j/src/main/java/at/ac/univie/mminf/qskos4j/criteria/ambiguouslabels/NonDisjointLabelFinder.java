package at.ac.univie.mminf.qskos4j.criteria.ambiguouslabels;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;

class NonDisjointLabelFinder {

	private URI concept;
	private Map<String, Set<String>> prefLabels, altLabels, hiddenLabels;
	
	NonDisjointLabelFinder(URI concept) {
		this.concept = concept;
		
		prefLabels = new HashMap<String, Set<String>>(); 
		altLabels = new HashMap<String, Set<String>>();
		hiddenLabels = new HashMap<String, Set<String>>();
	}
	
	URI getConcept() {
		return concept;
	}
	
	boolean addPrefLabelAndCheckIfDisjoint(Literal prefLabel) {
		addToLabelMap(prefLabels, prefLabel);
		return isDisjoint(altLabels, prefLabel) && isDisjoint(hiddenLabels, prefLabel);
	}
	
	boolean addAltLabelAndCheckIfDisjoint(Literal altLabel) {
		addToLabelMap(altLabels, altLabel);
		return isDisjoint(prefLabels, altLabel) && isDisjoint(hiddenLabels, altLabel);
	}
	
	boolean addHiddenLabelAndCheckIfDisjoint(Literal hiddenLabel) {
		addToLabelMap(hiddenLabels, hiddenLabel);
		return isDisjoint(altLabels, hiddenLabel) && isDisjoint(prefLabels, hiddenLabel);
	}
	
	private void addToLabelMap(
		Map<String, Set<String>> map,
		Literal label)
	{
		if (label != null) {
			String lang = label.getLanguage();
			if (lang == null) lang = "";
			
			Set<String> entriesForLang = map.get(lang);
			
			if (entriesForLang == null) {
				entriesForLang = new HashSet<String>();
				map.put(lang, entriesForLang);
			}
			entriesForLang.add(label.stringValue());
		}
	}
	
	private boolean isDisjoint(Map<String, Set<String>> map, Literal label)
	{
		if (label != null) {
			String lang = label.getLanguage();
			if (lang == null) lang = "";
			
			Set<String> labelsForLang = map.get(lang);
			if (labelsForLang == null) {
				return true;
			}
			return !labelsForLang.contains(label.stringValue());
		}
		return true;
	}

}
=begin rdoc
Finds conflicting label definitions such as multiple (e.g. prefLabel) label definitions in the same language or identlical labels for prefLabel, altLabel and hiddenLabel (violating the label integrity conditions defined in the SKOS reference section 5.3)
=end

class AmbiguousLabelFinder

	def initialize(loggingRdfReader, log, allConcepts)
		log.info("collecting SKOS labels")

		@reader = loggingRdfReader
		@allConcepts = allConcepts

		@disjointLabels = {SKOS.prefLabel => :prefLabel, SKOS.altLabel => :altLabel, SKOS.hiddenLabel => :hiddenLabel}
		@conceptLabels = Hash.new do |hash1, concept|
			hash1[concept] = Hash.new do |hash2, language|
				hash2[language] = Hash.new do |hash3, label|
					hash3[label] = []
				end
			end
		end

		collectLabels
	end

	def getAmbiguouslyLabeledConcepts
		ret = Hash.new do |hash1, concept|
			hash1[concept] = {}
		end

		@conceptLabels.each_pair do |concept, language|
			language.each_pair do |langSymbol, labelLists|
				prefLabels = labelLists[:prefLabel]

				ret[concept][:prefLabel] = prefLabels if prefLabels.size > 1
				ret[concept][:notDisjoint] = findDuplicates(labelLists) if !listsDisjoint?(labelLists)
			end
		end

		ret
	end

	private

	def collectLabels
		@reader.loopStatements do |statement|
			if @allConcepts.include?(statement.subject) && @disjointLabels.keys.include?(statement.predicate)
				addLabel(statement.subject, @disjointLabels[statement.predicate], statement.object)
			end
		end
	end

	def addLabel(concept, label, object)
		@conceptLabels[concept][object.language][label] << object.value
		@conceptLabels[concept][object.language][label].uniq!
	end

	def listsDisjoint?(lists)
		lists.values.flatten.size == lists.values.flatten.uniq.size
	end

	def findDuplicates(lists)
		allValues = lists.values.flatten

		duplicates = []
		allValues.each do |value|
			duplicates << value if allValues.count(value) > 1
		end

		duplicates.uniq
	end

end

